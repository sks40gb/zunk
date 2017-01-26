/* $Header: /home/common/cvsarea/ibase/dia/src/server/RefreshSqlText.java,v 1.3.6.1 2006/03/22 20:27:15 nancy Exp $ */
package server;

import common.Log;
import server.DiaDatabaseOpener;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * From a command prompt, parse files\sql_text.txt and update the sql_text table.
 */
final public class RefreshSqlText {

    private static String fileName;
    private static String database;
    private static int port = 3306;

    private RefreshSqlText() {}

    final public static void main(String[] args) throws Exception {
        checkParameters(args);
        mergeFileIntoDatabase();
    }

    private static void checkParameters(String[] args) {

        if (args.length != 2 && args.length != 3) {
            System.err.println(
                "usage: java RefreshSqlText <file_name> <database> [<port>]");
                System.exit(1);
        }

        fileName = args[0];
        database = args[1];
        if (args.length == 3) {
            try {
                port = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.err.println("Port number invalid");
                    System.exit(1);
            }
        }
    }

    private static void mergeFileIntoDatabase() throws Exception {

        Connection con = DiaDatabaseOpener.open(port, database);
        Statement st = con.createStatement();

        // One transaction, in case we find an error
        // Use consistent read -- we should never be running two of these
        con.setAutoCommit(false);

        // Get map of existing names
        // We will remove these as we read the file
        // Any leftovers will be deleted
        // Format of existing name in map is <name>:<sequence>
        Map nameMap = new HashMap();
        ResultSet rs1 = st.executeQuery(
            "select name, sequence, text from sql_text");
        while (rs1.next()) {
            nameMap.put(rs1.getString(1)+":"+rs1.getString(2),
                        rs1.getString(3));      
        }
        rs1.close();
        //Log.print("Map read: size="+nameMap.size());

        //java.util.Iterator itx = nameMap.entrySet().iterator();
        //for (int i = 0; i < 10; i++) {
        //    if (itx.hasNext()) {
        //        Map.Entry entry = (Map.Entry) itx.next();
        //        System.out.println(entry.getKey()+"-->"+entry.getValue());
        //    }
        //}

        // prepare statements for writing and deleting
        PreparedStatement stInsert = con.prepareStatement(
            "insert into sql_text (name, sequence, text) values (?, ?, ?)");
        PreparedStatement stUpdate = con.prepareStatement(
            "update sql_text set text=? where name = ? and sequence = ?");
        PreparedStatement stDelete = con.prepareStatement(
            "delete from sql_text where name = ? and sequence = ?");

        // Now read the file and merge it
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = readNonblankLine(reader);
        if (line == null) {
            Log.quit("File is empty");
        } else if (line.charAt(0) != '$') {
            Log.quit("First line does not start with '$'");
        }

        while (line != null) {
            String name = line.substring(1);
            String sequence = "0";
            int colonPos = name.indexOf(':');
            if (colonPos >= 0) {
                sequence = name.substring(colonPos + 1);
                name = name.substring(0, colonPos);
            }
            StringBuffer buffer = new StringBuffer();
            line = readNonblankLine(reader);
            if (line == null || line.charAt(0) == '$') {
                Log.quit("No SQL for: "+name+":"+sequence);
            }
            do {
                if (buffer.length() > 0) {
                    buffer.append('\n');
                }
                buffer.append(line);
                line = readNonblankLine(reader);
            } while (line != null && line.charAt(0) != '$');
            //System.out.println("name="+name+" sequence="+sequence);
            String oldText = (String) nameMap.remove(name+":"+sequence);
            String newText = buffer.toString();
            //System.out.println("oldText="+oldText);
            //System.out.println("newText="+newText);
            if (oldText == null) {
                System.out.println("Inserting: "+name+":"+sequence);
                stInsert.setString(1, name);
                stInsert.setString(2, sequence);
                stInsert.setString(3, buffer.toString());
                stInsert.executeUpdate();
            } else if (! newText.equals(oldText)) {
                System.out.println("Updating: "+name+":"+sequence);
                stUpdate.setString(1, buffer.toString());
                stUpdate.setString(2, name);
                stUpdate.setString(3, sequence);
                stUpdate.executeUpdate();
            }
        }
        reader.close();
        stInsert.close();
        stUpdate.close();

        Iterator it = nameMap.keySet().iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            int colonPos = name.indexOf(':');
            String sequence = name.substring(colonPos + 1);
            name = name.substring(0, colonPos);
            System.out.println("Deleting: "+name+":"+sequence);
            stDelete.setString(1, name);
            stDelete.setString(2, sequence);
            stDelete.executeUpdate();
        }

        // End the transaction
        con.commit();
        con.close();
    }

    private static String readNonblankLine(BufferedReader reader) throws IOException {
        String line = null;
        do {
            // read a line
            line = reader.readLine();
            //System.out.println("line="+line);

            // quit if it's null
            if (line == null) {
                break;
            }

            // trim trailing carriage return, if any (DOS format on Unix)
            int length = line.length();
            if (length > 0 && line.charAt(length - 1) == '\r') {
                line = line.substring(0, length - 1);
            }

            // Check for empty lines and normalize '$' lines
            String trimLine = line.trim();
            if (trimLine.length() == 0 || trimLine.charAt(0) == '#') {
                line = null;
            } else if (trimLine.charAt(0) == '$') {
                if (trimLine.length() == 1) {
                    Log.quit("Name missing on '$' line");
                } else if (trimLine.charAt(1) == ' ' || line.charAt(0) != '$') {
                    // normalize '$' line
                    line = '$' + trimLine.substring(1).trim();
                }
            }
        } while (line == null);
        return line;
    }
}
