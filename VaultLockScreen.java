package cybervault;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class VaultLockScreen extends JDialog implements ActionListener {
    
    private JPasswordField pwdField;
    private JButton unlockBtn;
    private JLabel statusLabel;
    private boolean isUnlocked = false;
    private boolean isFirstTime;

    public VaultLockScreen(Frame owner) {
        super(owner, "CYBERVAULT // ACCESS CONTROL", true); 
        isFirstTime = !VaultFileManager.isMasterPasswordSet();
        
        setSize(400, 200);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(10, 10, 20));
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        
        String titleText = isFirstTime ? "// CREATE MASTER PASSWORD" : "// ENTER MASTER PASSWORD";
        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setForeground(new Color(0, 255, 231));
        title.setFont(new Font("Monospaced", Font.BOLD, 14));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);
        
        
        JPanel center = new JPanel(new FlowLayout());
        center.setBackground(new Color(10, 10, 20));
        pwdField = new JPasswordField(15);
        pwdField.setBackground(new Color(15, 15, 34));
        pwdField.setForeground(new Color(191, 90, 242));
        pwdField.setCaretColor(new Color(191, 90, 242));
        pwdField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        center.add(pwdField);
        add(center, BorderLayout.CENTER);
        
        
        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(new Color(10, 10, 20));
        
        unlockBtn = new JButton(isFirstTime ? "CREATE VAULT" : "UNLOCK");
        unlockBtn.setBackground(new Color(15, 15, 34));
        unlockBtn.setForeground(new Color(0, 255, 231));
        unlockBtn.setFont(new Font("Monospaced", Font.BOLD, 12));
        unlockBtn.addActionListener(this);
        south.add(unlockBtn, BorderLayout.CENTER);
        
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(255, 69, 58));
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        south.add(statusLabel, BorderLayout.SOUTH);
        
        add(south, BorderLayout.SOUTH);
        
        
        pwdField.addActionListener(this); 
    }
   
    public boolean showAndCheck() {
        setVisible(true); 
        return isUnlocked;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String pwd = new String(pwdField.getPassword());
        if (pwd.isEmpty()) {
            statusLabel.setText("ERROR: PASSWORD CANNOT BE EMPTY");
            return;
        }
        
        try {
            if (isFirstTime) {
                if (pwd.length() < 4) {
                    statusLabel.setText("ERROR: PASSWORD TOO SHORT (MIN 4)");
                    return;
                }
                VaultFileManager.saveMasterPassword(pwd);
                isUnlocked = true;
                dispose(); 
            } else {
                boolean match = VaultFileManager.checkMasterPassword(pwd);
                if (match) {
                    isUnlocked = true;
                    dispose(); 
                } else {
                    statusLabel.setText("ACCESS DENIED: INCORRECT PASSWORD");
                    pwdField.setText("");
                }
            }
        } catch (Exception ex) {
            statusLabel.setText("SYSTEM ERROR: " + ex.getMessage());
        }
    }
}