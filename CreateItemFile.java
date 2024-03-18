import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public class CreateItemFile {
    private static final int RECORD_SIZE = 23; // 3 (item number) + 20 (description)

    public static void main(String[] args) {
        String fileName = "items.txt";
        File file = new File(fileName);

        try {
            if (file.createNewFile()) {
                System.out.println("Item file created: " + fileName);
            } else {
                System.out.println("Item file already exists.");
            }

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            FileChannel fileChannel = randomAccessFile.getChannel();

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Enter item number (three digits): ");
                String itemNumber = scanner.nextLine();

                // Check if item number already exists in the file
                if (isItemNumberExists(fileChannel, itemNumber)) {
                    System.out.println("Error: Item number already exists.");
                    continue;
                }

                System.out.print("Enter item description (up to 20 characters): ");
                String description = scanner.nextLine();

                // Truncate the description if it exceeds 20 characters
                if (description.length() > 20) {
                    description = description.substring(0, 20);
                }

                // Write item record to the file
                writeItemRecord(fileChannel, itemNumber, description);

                System.out.print("Do you want to add another item? (y/n): ");
                String choice = scanner.nextLine();

                if (choice.equalsIgnoreCase("n")) {
                    break;
                }
            }

            fileChannel.close();
            randomAccessFile.close();
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isItemNumberExists(FileChannel fileChannel, String itemNumber) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(RECORD_SIZE);
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

    private static void writeItemRecord(FileChannel fileChannel, String itemNumber, String description) throws IOException {
        String record = String.format("%s, %s\n", itemNumber, description);
        byte[] recordData = record.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(recordData);
        fileChannel.position(fileChannel.size());
        fileChannel.write(buffer);
    }
}