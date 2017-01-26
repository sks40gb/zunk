/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_request_file.java,v 1.7 2004/03/27 20:27:02 weaston Exp $ */
package server;

import common.Log;
import common.Signature;
import common.SyncFile;
//import common.msg.MessageReader;
import common.msg.MessageWriter;

import java.util.*;
import java.sql.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;

final public class Handler_request_file extends Handler {

    final private static int INDEX_SIZE = 0x4000;
    final private static int BLOCK_LENGTH = SyncFile.BLOCK_LENGTH;

    /** 
     * This handler is read-only.
     * @return true
     */
    public boolean isReadOnly() {
        return true;
    }

    final private class MatchData {

        Signature[] signatures;
        int[] nextSignature;
        int[] firstSignature = new int[INDEX_SIZE];

        // used to return value from findNextMatch()
        int matchOffset;
    }

    public void run(ServerTask task, Element action) throws IOException, SQLException {

        String filename = action.getAttributeNode(A_NAME).getValue();
        int signatureCount = Integer.parseInt(action.getAttributeNode(A_COUNT).getValue());

        // read the signatures
        MatchData data = new MatchData();
        data.signatures = new Signature[signatureCount];
        DataInputStream inStream = task.getDataStream();
        for (int i = 0; i < data.signatures.length; i++) {
            int theFastSum = inStream.readInt();
            byte[] theStrongSum = new byte[16];
            inStream.read(theStrongSum);
            data.signatures[i] = new Signature(theFastSum, theStrongSum);
        }
        inStream.close();

        SyncFile src = null;
        int matchedBytes = 0;
        int unmatchedBytes = 0;

        // read the requested file
        String subPath = Handler_request_directory.DOWNLOAD_ROOT;
        src = new SyncFile(subPath, filename);
        final int length = src.getLength();

        // allow enough time (10 min) for dialup to receive the file without timeout
        task.getStatement().executeUpdate(
            "update session"
            +" set interaction_time = "+(System.currentTimeMillis() + 10*60000)
            +" where session_id="+task.getSessionId());
        task.commitTransaction();


        // create reply message
        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_FILE_DELTA);
        writer.writeAttribute(A_NAME, filename);
        writer.writeAttribute(A_TIME, Long.toString(src.getTimestamp()));
        writer.writeAttribute(A_LENGTH, Integer.toString(length));
        writer.endElement();
        writer.close();

        DataOutputStream dataStream = new DataOutputStream(task.getOutputStream());

        // write the full-file strong checksum
        byte[] strongSum = src.strongSum();
        dataStream.write(strongSum);

        //System.out.println("file read: "+ src);

        // build table of signatures.  firstSignature points to a chain
        // of signatures, with links in nextSignature (for fast search)

        data.nextSignature = new int[data.signatures.length];

        Arrays.fill(data.nextSignature, -1);
        Arrays.fill(data.firstSignature, -1);

        for (int i = 0; i < data.signatures.length; i++) {
            int hash = data.signatures[i].getFastSum() & (INDEX_SIZE - 1);
            //System.out.println("hash "+Integer.toHexString(hash)+" "
            //  +Integer.toHexString(signatures[i].getFastSum())+" "
            //  +Integer.toHexString(INDEX_SIZE-1));
            data.nextSignature[i] = data.firstSignature[hash];
            data.firstSignature[hash] = i;
        }

        // search for matching blocks
        int pos = 0;

        // location of match that hasn't been recorded
        // (waiting to see if multiple blocks)
        int lastMatchOffset = -1;
        int lastMatchLength = 0;
        int newPos;

        // loop, finding matched
        while (pos < length) {

            //Log.print("calling FindNextMatch "+pos+" "+length);

            newPos = findNextMatch(pos, src, data);
            //Log.print("findMatch: pos="+pos+", newPos="+newPos+", matchOffset="+data.matchOffset);

            // if there are unwritten blocks and we didn't extend match

            // Note.  We consolidate consecutive blocks.  However, a block that
            // matches more than one block will not necessarily be part of
            // a consecutive group--e.g. lots of all-zero blocks will match the last
            // such and be separate.  Would be interesting to think of a way
            // to maximize consolidation.
            if (lastMatchOffset >= 0
                && (newPos > pos || (lastMatchOffset + lastMatchLength) != data.matchOffset))
            {
                // add the unwritten blocks to the output
                dataStream.writeInt(- lastMatchLength);
                dataStream.writeInt(lastMatchOffset);
                matchedBytes += lastMatchLength;
                //Log.print("Match: "+" "+(pos - lastMatchLength) +" "+lastMatchOffset+" "+lastMatchLength);
                lastMatchOffset = -1;
                lastMatchLength = 0;
            }

            // if we found unmatched characters 
            if (newPos > pos) {
                // add them to the output
                dataStream.writeInt(newPos - pos);
                dataStream.write(src.get(pos, newPos - pos));
                unmatchedBytes += newPos - pos;
                //Log.print("Unmatched: "+pos+" "+(newPos - pos));
                pos = newPos;
            }

            // if there was a match, make it the last match
            if (data.matchOffset >= 0) {
                if (lastMatchOffset < 0) {
                    lastMatchOffset = data.matchOffset;
                    lastMatchLength = 0;
                }
                lastMatchLength += BLOCK_LENGTH;
                pos += BLOCK_LENGTH;
            }
        }

        // if there's a final match, write it
        if (lastMatchOffset >= 0) {
            //Log.print("final match: "+(pos - lastMatchLength)+" "+lastMatchOffset+" "+(length - pos + lastMatchLength));
            dataStream.writeInt(-(length - pos + lastMatchLength));
            dataStream.writeInt(lastMatchOffset);
            matchedBytes += length - pos + lastMatchLength;
        }

        // finish and close the stream
        dataStream.writeInt(0);
        dataStream.close();

        Log.write(filename+": "+matchedBytes+" matched, "+unmatchedBytes+" unmatched");
    }


    // find position of next match.  If none, returns file length
    // if match found, matchOffset is set to the offset; otherwise it is -1
    private int findNextMatch(int pos, SyncFile src, MatchData data) {

        data.matchOffset = -1;

        // initialize sums
        final int length = src.getLength();
        int fast = src.fastSum(pos);
        int fastHigh = fast >> 16;
        int fastLow = fast & 0xFFFF;

        // search for match beginning at newPos = pos, pos+1, ...
        // loop terminates when one is found or end of file reached
        for (;;) {

            // search hash chain 
            int pntr = data.firstSignature[fastLow & INDEX_SIZE - 1];
            while (pntr >= 0) {
                // if fast checksum matches
                if (((fastHigh << 16) | fastLow) == data.signatures[pntr].getFastSum())  {
                    // and if strong checksum matches, too
                    if (Arrays.equals(src.strongSum(pos), data.signatures[pntr].getStrongSum())) {
                        // match found
                        // indicate by setting offset
                        data.matchOffset = pntr * SyncFile.BLOCK_LENGTH;
                        //System.out.println("match: "+data.matchOffset);
                        return pos;
                    }
                }
                pntr = data.nextSignature[pntr];
            }

            // advance to next byte
            //System.out.println("fail "+pos);
            int ak = src.get(pos);
            int akplusL = src.get(pos + BLOCK_LENGTH);
            pos++;

            // return if beyond end of file
            if (pos >= length) {
                //System.out.println("beyond eof");
                return pos;
            } 

            // advance checksums
            fastHigh = fastHigh - ak + akplusL;
            fastLow = fastLow - ak * BLOCK_LENGTH + fastHigh;
        }
    }
}
