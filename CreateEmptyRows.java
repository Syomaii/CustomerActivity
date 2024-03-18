import java.io.*;
import java.nio.file.*;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import static java.nio.file.StandardOpenOption.*;
import java.util.*;
import javax.swing.*;
import java.text.*;

public class CreateEmptyRows {
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
        Path FilePath = Paths.get("CustomerEmptyRows.txt").toAbsolutePath();
        Create(FilePath);
    
        
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
