import java.io.*;
import java.nio.file.*;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import static java.nio.file.StandardOpenOption.*;
import java.util.*;

public class ReadEmptyRows {
    static Scanner scan = new Scanner(System.in);
    static final int CUSTOMER_SIZE = 2000; 
    static final String delimeter = ",";
    static final String custId = "0000";
    static final String custLastname = "-------";
    static final String custZipcode = "XXXXX";
    static final String lineSeparator = System.getProperty("line.separator");
    static final String REC_SIZE = custId + delimeter + custLastname + delimeter + custZipcode + lineSeparator;

    public static void main(String[] args) {
        Path readFile = Paths.get("CustomerEmptyRows.txt").toAbsolutePath();
        Path createFile = Paths.get("IndexRead.txt").toAbsolutePath();
        StringBuilder sb;
        
        createFile(createFile);

        int index = 1;

        try {
            FileChannel customerFile = FileChannel.open(readFile, READ);
            FileChannel readIndex = FileChannel.open(createFile, WRITE, CREATE);
            
            System.out.print("Enter Index to display: ");
            String temp = scan.nextLine();
            index = Integer.parseInt(temp);

            readByIndex(customerFile, readIndex, index);

            customerFile.close(); 
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void readByIndex(FileChannel readChannel, FileChannel writeChannel, int index) {
        try {
            int position = (index - 1) * REC_SIZE.length();
            ByteBuffer buff = ByteBuffer.allocate(REC_SIZE.length());
            String foundData = "Data in index " + index + ": ";
        
            readChannel.position(position);
            int bytesRead = readChannel.read(buff);
            if (bytesRead != -1) {
                buff.flip();
                byte[] data = new byte[buff.limit()];
                buff.get(data);
                String record = new String(data);
                String addString = new String(foundData);
                writeChannel.position(writeChannel.size());
                writeChannel.write(ByteBuffer.wrap(addString.getBytes()));
                writeChannel.write(ByteBuffer.wrap(record.getBytes()));
            } else {
                System.out.println("End of file reached.");
            }
        } catch (IOException e) {
            System.err.println("Error reading data: " + e.getMessage());
        }

        // try {
        //     int position = (index - 1) * REC_SIZE.length();
        //     ByteBuffer buff = ByteBuffer.allocate(REC_SIZE.length());
        
        //     readChannel.position(position);
        //     int bytesRead = readChannel.read(buff);
        //     if (bytesRead != -1) {
        //         buff.flip();
        //         byte[] data = new byte[buff.limit()];
        //         buff.get(data);
        //         String record = new String(data); // No need to trim
        //         System.out.println(record);
        //     } else {
        //         System.out.println("End of file reached.");
        //     }
        // } catch (IOException e) {
        //     System.err.println("Error reading data: " + e.getMessage());
        // }
        
        
    }
    
    public static void createFile(Path filePath){
        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
            }
        }
    }
}
