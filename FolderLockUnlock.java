import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class FolderLockUnlock extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JLabel label;
    private JButton lockButton, unlockButton;
    private String folderPath;

    public FolderLockUnlock() {
        super("Folder Lock/Unlock");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        label = new JLabel("Enter the path of the folder you want to lock/unlock:");
        panel.add(label);

        lockButton = new JButton("Lock");
        lockButton.addActionListener(this);
        panel.add(lockButton);

        unlockButton = new JButton("Unlock");
        unlockButton.addActionListener(this);
        panel.add(unlockButton);

        getContentPane().add(panel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == lockButton) {
            folderPath = JOptionPane.showInputDialog("Enter folder path:");
            if (folderPath != null) {
                File folder = new File(folderPath);
                if (!folder.exists() || !folder.isDirectory()) {
                    JOptionPane.showMessageDialog(this, "Invalid folder path!");
                    return;
                }
                try {
                    String command = "attrib +h " + folderPath;
                    Runtime.getRuntime().exec(command);
                    JOptionPane.showMessageDialog(this, "Folder locked successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error locking the folder: " + ex.getMessage());
                }
            }
        } else if (e.getSource() == unlockButton) {
            folderPath = JOptionPane.showInputDialog("Enter folder path:");
            if (folderPath != null) {
                File folder = new File(folderPath);
                if (!folder.exists() || !folder.isDirectory()) {
                    JOptionPane.showMessageDialog(this, "Invalid folder path!");
                    return;
                }
                try {
                    String command = "attrib -h " + folderPath;
                    Runtime.getRuntime().exec(command);
                    JOptionPane.showMessageDialog(this, "Folder unlocked successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error unlocking the folder: " + ex.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        new FolderLockUnlock();
    }

}