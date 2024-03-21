import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.nio.ByteBuffer;
import javax.swing.*;

public class CustomerItemOrder {
    static JTextField txtCustomerId;
    static JTextField txtItemId;
    private static final int CUSTOMER_RECORD_SIZE = 17; 
    private static final int ITEM_RECORD_SIZE = 32; 


    public static void main(String[] args) {
        Path customerFile = Paths.get("customer.txt").toAbsolutePath();
        Path itemFile = Paths.get("item.txt").toAbsolutePath();

        SwingUtilities.invokeLater(() -> {
            createGUI(customerFile, itemFile);
        });
    }

    public static String readByIndex(Path file, int index, int recordSize) {
        String record = "";
        try (FileChannel fc = FileChannel.open( file, StandardOpenOption.READ)) {
            int position = (index - 1) * recordSize;
            ByteBuffer buff = ByteBuffer.allocate(recordSize);
            fc.position(position);
            int bytesRead = fc.read(buff);
            if (bytesRead != -1) {
                record = new String(buff.array(), 0, bytesRead).trim();
            }
        } catch (IOException e) {
        	JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return record;
    }
    
    public static void createGUI(Path customerFile, Path itemFile) {
        JFrame frame = new JFrame("Display Customer Item");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(null);

        JLabel customerLabel = new JLabel("Customer Info");
        customerLabel.setBounds(160, 10, 100, 10);
        panel.add(customerLabel);

        JLabel customerIdLabel = new JLabel("Customer ID:");
        customerIdLabel.setBounds(50, 50, 100, 10);
        panel.add(customerIdLabel);

        txtCustomerId = new JTextField(10);
        txtCustomerId.setBounds(250, 45, 150, 20);
        panel.add(txtCustomerId);

        JLabel itemLabel = new JLabel("Item Info");
        itemLabel.setBounds(174, 80, 200, 10);
        panel.add(itemLabel);

        JLabel itemIdLabel = new JLabel("Item ID:");
        itemIdLabel.setBounds(50, 100, 150, 25);
        panel.add(itemIdLabel);

        txtItemId = new JTextField(10);
        txtItemId.setBounds(250, 105, 150, 20);
        panel.add(txtItemId);

        JButton btnSubmit = new JButton("Submit");
        btnSubmit.addActionListener(e -> {
            int customerId = Integer.parseInt(txtCustomerId.getText());
            int itemId = Integer.parseInt(txtItemId.getText());
            displayCustomerItemInfo(customerFile, itemFile, customerId, itemId);
        });
        btnSubmit.setBounds(165, 150, 75, 30);
        
        panel.add(btnSubmit);

        frame.setBounds(800, 400, 450, 250);
        frame.add(panel);
        frame.setTitle("Order Item");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }
    
    public static void displayCustomerItemInfo(Path customerFile, Path itemFile, int customerId, int itemId) {
        String customerInfo = readByIndex(customerFile, customerId, CUSTOMER_RECORD_SIZE);
        String itemInfo = readByIndex(itemFile, itemId, ITEM_RECORD_SIZE);

        if (customerInfo.isEmpty() || itemInfo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Record not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame displayFrame = new JFrame("Display Information");
        displayFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel customerInfoLabel = new JLabel("Customer Info");
        panel.add(customerInfoLabel);

        JLabel customerIdLabel = new JLabel("Customer ID: " + customerId);
        panel.add(customerIdLabel);

        String[] customerData = customerInfo.split(",");
        if (customerData.length >= 2) {
            JLabel customerLastNameLabel = new JLabel("Customer Lastname: " + customerData[1]);
            panel.add(customerLastNameLabel);
        }

        if (customerData.length >= 3) {
            JLabel customerZipCodeLabel = new JLabel("Customer Zip Code: " + customerData[2]);
            panel.add(customerZipCodeLabel);
        }

        panel.add(Box.createVerticalStrut(10));

        JLabel itemInfoLabel = new JLabel("Item Info");
        panel.add(itemInfoLabel);

        String[] itemData = itemInfo.split(",");
        if (itemData.length >= 2) {
            JLabel itemIdLabel = new JLabel("Item ID: " + itemId);
            panel.add(itemIdLabel);

            JLabel itemNameLabel = new JLabel("Item Name: " + itemData[1]);
            panel.add(itemNameLabel);
        }

        if (itemData.length >= 3) {
            JLabel itemDescriptionLabel = new JLabel("Item Description: " + itemData[2].replace('-', ' '));
            panel.add(itemDescriptionLabel);
        }

        displayFrame.add(panel);
        displayFrame.setBounds(800, 400, 450, 200);
        displayFrame.setLocationRelativeTo(null);
        displayFrame.setVisible(true);
    }


    
}
