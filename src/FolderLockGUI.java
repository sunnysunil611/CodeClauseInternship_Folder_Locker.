
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class FolderLockGUI extends JFrame {
    private JTextField folderPathField;
    private JButton lockButton;
    private JButton unlockButton;

    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };

    public FolderLockGUI() {
        super("Folder Lock/Unlock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 150);
        setLocationRelativeTo(null);

        folderPathField = new JTextField(30);
        lockButton = new JButton("Lock Folder");
        unlockButton = new JButton("Unlock Folder");

        lockButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String folderPath = folderPathField.getText();
                lockFolder(folderPath);
            }
        });

        unlockButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String folderPath = folderPathField.getText();
                unlockFolder(folderPath);
            }
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(255, 255, 204)); // Light yellow color (R, G, B)

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(new JLabel("Enter Folder Path:"), gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(folderPathField, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(lockButton, gbc);

        gbc.gridx = 1;
        mainPanel.add(unlockButton, gbc);

        getContentPane().add(mainPanel);
        getContentPane().setBackground(new Color(240, 240, 240)); // Light gray color for the JFrame background
    }

    private void lockFolder(String folderPath) {
        try {
            File folder = new File(folderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                JOptionPane.showMessageDialog(this,
                        "Invalid folder path.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            File[] files = folder.listFiles();
            if (files == null || files.length == 0) {
                JOptionPane.showMessageDialog(this,
                        "No files found in the folder.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JPasswordField passwordField = new JPasswordField();
            int option = JOptionPane.showConfirmDialog(this,
                    passwordField,
                    "Enter the password:",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.CANCEL_OPTION || passwordField.getPassword().length == 0) {
                return; // User canceled or did not provide a password
            }

            String password = new String(passwordField.getPassword());
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, password);

            for (File file : files) {
                if (file.isFile()) {
                    encryptFile(file, cipher);
                    file.delete();
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Folder locked successfully.",
                    "Folder Locked",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "An error occurred while locking the folder.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void unlockFolder(String folderPath) {
        try {
            File folder = new File(folderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                JOptionPane.showMessageDialog(this,
                        "Invalid folder path.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            File[] files = folder.listFiles();
            if (files == null || files.length == 0) {
                JOptionPane.showMessageDialog(this,
                        "No files found in the folder.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JPasswordField passwordField = new JPasswordField();
            int option = JOptionPane.showConfirmDialog(this,
                    passwordField,
                    "Enter the password:",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.CANCEL_OPTION || passwordField.getPassword().length == 0) {
                return; // User canceled or did not provide a password
            }

            String password = new String(passwordField.getPassword());
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE, password);

            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".locked")) {
                    decryptFile(file, cipher);
                    file.delete();
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Folder unlocked successfully.",
                    "Folder Unlocked",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Incorrect password.",
                    "Please enter correct password",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Cipher getCipher(int cipherMode, String password) throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey secretKey = keyFactory.generateSecret(keySpec);

        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        PBEParameterSpec parameterSpec = new PBEParameterSpec(SALT, 20);
        cipher.init(cipherMode, secretKey, parameterSpec);

        return cipher;
    }

    private void encryptFile(File file, Cipher cipher) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(file.getAbsolutePath() + ".locked");
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);

        byte[] buffer = new byte[1024];
        int read;

        while ((read = fis.read(buffer)) != -1) {
            cos.write(buffer, 0, read);
        }

        fis.close();
        cos.flush();
        cos.close();
    }

    private void decryptFile(File file, Cipher cipher) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(file.getAbsolutePath().replace(".locked", ""));
        CipherInputStream cis = new CipherInputStream(fis, cipher);

        byte[] buffer = new byte[1024];
        int read;

        while ((read = cis.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }

        cis.close();
        fos.flush();
        fos.close();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FolderLockGUI().setVisible(true);
            }
        });
    }
}
