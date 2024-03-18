import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import javax.swing.*;

public class CustomerItemOrder {
    private static final int CUSTOMER_RECORD_SIZE = 14; 
    private static final int ITEM_RECORD_SIZE = 23;     
    private static final int ORDER_RECORD_SIZE = 11; 

    public static void main(String[] args) {
        String customerFileName = "customer.txt";
        String itemFileName = "items.txt";
        String orderFileName = "orders.txt";

        File customerFile = new File(customerFileName);
        File itemFile = new File(itemFileName);
        File orderFile = new File(orderFileName);

        try {
            RandomAccessFile customerRandomAccessFile = new RandomAccessFile(customerFile, "r");
            FileChannel customerFileChannel = customerRandomAccessFile.getChannel();

            RandomAccessFile itemRandomAccessFile = new RandomAccessFile(itemFile, "r");
            FileChannel itemFileChannel = itemRandomAccessFile.getChannel();

            RandomAccessFile orderRandomAccessFile = new RandomAccessFile(orderFile, "rw");
            FileChannel orderFileChannel = orderRandomAccessFile.getChannel();

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Enter customer ID (three digits): ");
                String customerId = scanner.nextLine();

                if (!isIdExists(customerFileChannel, customerId)) {
                    System.out.println("Error: Customer ID does not exist.");
                    continue;
                }

                System.out.print("Enter item number (three digits): ");
                String itemNumber = scanner.nextLine();
                
                if (!isItemNumberExists(itemFileChannel, itemNumber)) {
                    System.out.println("Error: Item number does not exist.");
                    continue;
                }

                System.out.print("Enter quantity: ");
                String quantity = scanner.nextLine();

                writeOrderRecord(orderFileChannel, customerId, itemNumber, quantity);

                System.out.print("Do you want to add another order? (y/n): ");
                String choice = scanner.nextLine();

                if (choice.equalsIgnoreCase("n")) {
                    break;
                }
            }

            customerFileChannel.close();
            customerRandomAccessFile.close();

            itemFileChannel.close();
            itemRandomAccessFile.close();

            orderFileChannel.close();
            orderRandomAccessFile.close();

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isIdExists(FileChannel fileChannel, String id) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(CUSTOMER_RECORD_SIZE);
        while (fileChannel.read(buffer) != -1) {
            buffer.flip();
            String recordId = new String(buffer.array(), 0, 3);
            buffer.clear();
            if (recordId.equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isItemNumberExists(FileChannel fileChannel, String itemNumber) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(ITEM_RECORD_SIZE);
        while (fileChannel.read(buffer) != -1) {
            buffer.flip();
            String recordItemNumber = new String(buffer.array(), 0, 3);
            buffer.clear();
            if (recordItemNumber.equals(itemNumber)) {
                return true;
            }
        }
        return false;
    }

    private static void writeOrderRecord(FileChannel fileChannel, String CustomerId, String itemNumber, String quantity) throws IOException {
        String record = String.format("%s, %s, %s\n", CustomerId, itemNumber, quantity);
        byte[] recordData = record.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(recordData);
        fileChannel.position(fileChannel.size());
        fileChannel.write(buffer);
    }
}