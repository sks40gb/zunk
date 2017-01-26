package com.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author ssi150
 */
public class NIOFileReader {

    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        RandomAccessFile file = new RandomAccessFile("D:" + File.separator + "transaction.txt","r");
        //readByByteBuffer(file);
        readByMappedByteBuffer(file);
    }
    
    private static void readByByteBuffer(RandomAccessFile file) throws FileNotFoundException, IOException{
        FileChannel channel = file.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(10);
        while(channel.read(buffer) > 0 ){
            buffer.flip(); //The limit is set to the current position
            while(buffer.hasRemaining()){
                System.out.print((char)buffer.get());
            }
            buffer.clear();
        }
    }

    private static void readByMappedByteBuffer(RandomAccessFile file) throws IOException{
        FileChannel fChannel = file.getChannel();
        MappedByteBuffer buffer = fChannel.map(FileChannel.MapMode.READ_ONLY, 0, fChannel.size());
        //buffer.load(); // <-- imp
        for(int i =0; i < fChannel.size(); i++ ){
            System.out.print((char)buffer.get(i));
        }
        buffer.clear();

    }

}
