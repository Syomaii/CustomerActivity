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

public class CreateCustomerFile {

    static boolean errorDisplayed = false;
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

    static JTextField txtCustID;
    static JTextField txtCustLN;
    static JTextField txtCustZip;

    public static void main(String[] args) {
        Path FilePath = Paths.get("customer.txt").toAbsolutePath();
        CreateGUI(FilePath);
        Create(FilePath);
        AddData(FilePath);
    }

    public static void AddData(Path FilePath) {
        int iD;
        String Lname;
        String zip = "";
        String temp;

        final int QUIT = 0;

        try (FileChannel fc = (FileChannel) Files.newByteChannel(FilePath, READ, WRITE)) {
            while (true) {
                temp = txtCustID.getText();
                if (temp.isEmpty()) {
//                	JOptionPane.showMessageDialog(null, "Enter customer ID", "Error", JOptionPane.ERROR_MESSAGE);
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

                if (!array[0].equals(custId)) {
                    JOptionPane.showMessageDialog(null, "Record already exists for ID: " + iD, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    Lname = txtCustLN.getText();

                    if (Lname.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter the user's lastname for ID: " + iD, "Error", JOptionPane.ERROR_MESSAGE);
                        continue; 
                    }

                    int nameLength = Lname.length();
                    String record;
                    if (nameLength >= LNAME_SIZE) {
                        record = ID.format(iD) + delimeter + Lname + delimeter + zip; 
                    } else {
                        record = ID.format(iD) + delimeter + Lname;  
                        
                        for (int i = 0; i < LNAME_SIZE - nameLength; i++) {
                            record += " ";
                        }
                        record += delimeter;  
                    }

                    temp = txtCustZip.getText();
                    if (temp.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter the user's zip code for ID: " + iD, "Error", JOptionPane.ERROR_MESSAGE);
                        continue; 
                    } else {
                        zip = temp;  
                    }

                    record += zip;

                    data = record.getBytes();
                    buff = ByteBuffer.wrap(data);

                    fc.position((iD - 1) * REC_SIZE.length());
                    fc.write(buff);

                    JOptionPane.showMessageDialog(null, "Data successfully written for Customer ID: " + iD, "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                    txtCustID.setText("");
                    txtCustLN.setText("");
                    txtCustZip.setText("");
    
                }
            
        } catch (IOException | NumberFormatException e) {
            if (!errorDisplayed) {
                System.err.println("Error: " + e.getMessage());
                errorDisplayed = true;
            }
        }

        // Closing the GUI
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(txtCustID);
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
    
        JLabel labelTitle = new JLabel("Add a customer");
        labelTitle.setBounds(160, 10, 100, 10);

        JLabel createCustID = new JLabel("Enter customer ID or close to exit: ");
        createCustID.setBounds(20, 50, 200, 25);
    
        txtCustID = new JTextField(10);
        txtCustID.setBounds(250, 50, 150, 25);
    
        JLabel createCustLN = new JLabel("Enter customer Lastname: ");
        createCustLN.setBounds(20, 100, 200, 25);
    
        txtCustLN = new JTextField(7);
        txtCustLN.setBounds(250, 100, 150, 25);
    
        JLabel createCustZip = new JLabel("Enter customer zip code: ");
        createCustZip.setBounds(20, 150, 200, 25);
    
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
        panel.add(createCustID);
        panel.add(txtCustID);
        panel.add(createCustLN);
        panel.add(txtCustLN);
        panel.add(createCustZip);
        panel.add(txtCustZip);
        panel.add(btnSubmit);
    
        myFrame.setBounds(800, 400, 450, 300);
        myFrame.add(panel);
        myFrame.setTitle("Create Customer Item");
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setResizable(false);
        myFrame.setVisible(true);
    }
    

    
}
