import java.io.*;
import java.nio.file.*;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import static java.nio.file.StandardOpenOption.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.*;
import javax.swing.*;
import java.text.*;

public class CreateItemFile {

    static boolean errorDisplayed = false;
    static Scanner scan = new Scanner(System.in);
    static DecimalFormat ID = new DecimalFormat("####,0000");
    

    static final int CUSTOMER_SIZE = 2000;

    static final String delimeter = ",";
    static final String itemId = "0000";
    static final String itemName = "----------";
    static final String itemDesc = "--------------------";
    static final String lineSeparator = System.getProperty("line.separator");


    static final String REC_SIZE = itemId + delimeter + itemName + delimeter + itemDesc + lineSeparator;

    static final int ITEM_NAME_SIZE = 10;

    static JTextField txtItemId;
    static JTextField txtItemName;
    static JTextField txtCustZip;

    public static void main(String[] args) {
        Path FilePath = Paths.get("item.txt").toAbsolutePath();
        CreateGUI(FilePath);
        Create(FilePath);
        AddData(FilePath);
    }

    public static void AddData(Path FilePath) {
        int iD;
        String itemname;
        String itemdesc = "";
        String temp;

        final int QUIT = 0;

        try (FileChannel fc = (FileChannel) Files.newByteChannel(FilePath, READ, WRITE)) {
            while (true) {
                temp = txtItemId.getText();
                if (temp.isEmpty()) {
//                    JOptionPane.showMessageDialog(null, "Enter item ID", "Error", JOptionPane.ERROR_MESSAGE);
                    return; 
                } else {
                    iD = Integer.parseInt(temp);
                }

                if (iD == QUIT) {
                    break;
                }

                fc.position((iD - 1) * REC_SIZE.length());

                byte[] data = new byte[REC_SIZE.length()];
                ByteBuffer buff = ByteBuffer.wrap(data);

                fc.read(buff);
                String o = new String(data);
                String[] array = o.split(delimeter);

                if (!array[0].equals(itemId)) {
                    JOptionPane.showMessageDialog(null, "Record already exists for ID: " + iD, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    itemname = txtItemName.getText();

                    if (itemname.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter the item name for item: " + iD, "Error", JOptionPane.ERROR_MESSAGE);
                        ClearTextFields();
                        continue; 
                    }

                    int nameLength = itemname.length();
                    String record;
                    if (nameLength >= ITEM_NAME_SIZE) {
                        record = ID.format(iD) + delimeter + itemname + delimeter + itemdesc; 
                    } else {
                        record = ID.format(iD) + delimeter + itemname;  
                        
                        for (int i = 0; i < ITEM_NAME_SIZE - nameLength; i++) {
                            record += " ";
                        }
                        record += delimeter;  
                    }

                    temp = txtCustZip.getText();
                    if (temp.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter the item description for item: " + iD, "Error", JOptionPane.ERROR_MESSAGE);
                        ClearTextFields();
                        continue; 
                    } else {
                        itemdesc = temp;  
                    }

                    record += itemdesc;

                    data = record.getBytes();
                    buff = ByteBuffer.wrap(data);

                    fc.position((iD - 1) * REC_SIZE.length());
                    fc.write(buff);

                    JOptionPane.showMessageDialog(null, "Data successfully written for Item ID: " + iD, "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                    ClearTextFields();
    
                }
            
        } catch (IOException | NumberFormatException e) {
            if (!errorDisplayed) {
                System.err.println("Error: " + e.getMessage());
                errorDisplayed = true;
            }
        }

        
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(txtItemId);
        frame.dispose();
        System.exit(0);
    }

    public static void Create(Path FilePath) {
        boolean exist = true;
        try {
            FilePath.getFileSystem().provider().checkAccess(FilePath);
            System.out.println("File already exists");
        } catch (Exception e) {
            exist = false;
        }
        if (exist != true) {
            try {
                System.out.println("Creating File");
                OutputStream output = new BufferedOutputStream(Files.newOutputStream(FilePath, CREATE));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
                for (int i = 0; i < CUSTOMER_SIZE; i++) {
                    writer.write(REC_SIZE, 0, REC_SIZE.length());
                }
                writer.close();
                output.close();
            } catch (Exception e) {
                System.err.println("There is an error creating a file");
            }
        }
    }

    public static void CreateGUI(Path FilePath) {
        JFrame myFrame = new JFrame();
    
        JLabel labelTitle = new JLabel("Add an item");
        labelTitle.setBounds(160, 10, 100, 10);

        JLabel lblItemId = new JLabel("Enter item ID or close to exit: ");
        lblItemId.setBounds(20, 50, 200, 25);
    
        txtItemId = new JTextField(10);
        txtItemId.setBounds(250, 50, 150, 25);
    
        JLabel lblItemName = new JLabel("Enter item name: ");
        lblItemName.setBounds(20, 100, 200, 25);
    
        txtItemName = new JTextField(7);
        txtItemName.setBounds(250, 100, 150, 25);
    
        JLabel lblItemDesc = new JLabel("Enter item Description: ");
        lblItemDesc.setBounds(20, 150, 200, 25);
    
        txtCustZip = new JTextField(5);
        txtCustZip.setBounds(250, 150, 150, 25);
    
        JButton btnSubmit = new JButton("Submit");
        btnSubmit.setFocusable(false);
        btnSubmit.setBounds(160, 200, 100, 50);
        btnSubmit.addActionListener(e -> {
            AddData(FilePath);
        });
    
        JPanel panel = new JPanel(null);
        panel.add(labelTitle);
        panel.add(lblItemId);
        panel.add(txtItemId);
        panel.add(lblItemName);
        panel.add(txtItemName);
        panel.add(lblItemDesc);
        panel.add(txtCustZip);
        panel.add(btnSubmit);
    
        myFrame.setBounds(800, 400, 450, 300);
        myFrame.add(panel);
        myFrame.setTitle("Create Customer Item");
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setResizable(false);
        myFrame.setVisible(true);
    }
    
    public static void ClearTextFields(){
        txtItemId.setText("");
        txtItemName.setText("");
        txtCustZip.setText("");
    }
    
}
