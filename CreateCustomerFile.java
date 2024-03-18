import java.io.*;
import java.nio.file.*;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import static java.nio.file.StandardOpenOption.*;
import java.util.*;
import javax.swing.*;
import java.text.*;

public class CreateCustomerFile {
    static Scanner scan = new Scanner(System.in);
    static DecimalFormat ID = new DecimalFormat("####,0000");

    static final int CUSTOMER_SIZE = 2000;

    static final String delimeter = ",";
    static final String custId = "0000";
    static final String custLastname = "-------";
    static final String custZipcode = "XXXXX";
    static final String lineSeparator = System.getProperty("line.separator");
    
    static final String REC_SIZE = custId + delimeter + custLastname + delimeter + custZipcode + lineSeparator;

    static final int LNAME_SIZE = 7;

    public static void main(String[] args) {
        Path FilePath = Paths.get("customer.txt").toAbsolutePath();
        Create(FilePath);
    
        int iD = 1;
        String Lname;
        int zip;
        String temp;
    
        final int QUIT = 0;
    
        byte[] data;
        ByteBuffer buff;
        StringBuilder sb;
    
        boolean exist;
    
        try (FileChannel fc = (FileChannel) Files.newByteChannel(FilePath, READ, WRITE)) {
            while (true) {
                System.out.print("Enter id number or '0' to quit: ");
                temp = scan.nextLine();
                iD = Integer.parseInt(temp);
    
                if (iD == QUIT) {
                    break;
                }
    
                exist = false;

                while (true) {
                    fc.position((iD - 1) * REC_SIZE.length());

                    data = new byte[REC_SIZE.length()];
                    buff = ByteBuffer.wrap(data);

                    fc.read(buff);
                    String o = new String(data);
                    String[] array = o.split(delimeter);

                    if (array[0].equals(custId)) {
                        break;
                    } else {
                        System.out.println("Record already exists for ID " + iD);
                        exist = true;
                        break;
                    }
                }

    
                if (!exist) {
                    System.out.print("Enter LastName: ");
                    Lname = scan.nextLine();
    
                    if (Lname.length() < LNAME_SIZE) {
                        sb = new StringBuilder(Lname);
                        sb.setLength(LNAME_SIZE);
                        Lname = sb.toString();
                    }
    
                    System.out.print("Enter zip code: ");
                    temp = scan.nextLine();
                    zip = Integer.parseInt(temp);
    
                    String x = ID.format(iD) + delimeter + Lname + delimeter + zip;
    
                    data = x.getBytes();
                    buff = ByteBuffer.wrap(data);
    
                    fc.position((iD - 1) * REC_SIZE.length());
                    fc.write(buff);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
        

    

    public static void Create(Path FilePath){
        boolean exist = true;
        try{
            FilePath.getFileSystem().provider().checkAccess(FilePath);
            System.out.println("File already exists");
        } catch (Exception e){
            exist = false;
        }
        if(exist != true){
            try{
                System.out.println("Creating File");
                OutputStream output = new BufferedOutputStream(Files.newOutputStream(FilePath, CREATE));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
                for(int i= 0; i < CUSTOMER_SIZE; i++){
                    writer.write(REC_SIZE, 0, REC_SIZE.length());
                    writer.flush();
                }
                writer.close();
                output.close();
            } catch (Exception e){
                System.err.println("There is an error creating a file");
            }
        }

    }
}
