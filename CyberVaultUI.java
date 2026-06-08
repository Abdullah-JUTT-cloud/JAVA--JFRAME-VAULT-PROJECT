package cybervault;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class CyberVaultUI extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final Color BG_DARK       = new Color(10,  10,  20);
    private static final Color BG_PANEL      = new Color(11,  11,  26);
    private static final Color BG_INPUT      = new Color(15,  15,  34);
    private static final Color BORDER_COLOR  = new Color(30,  30,  53);
    private static final Color CYAN          = new Color(0,   255, 231);
    private static final Color PURPLE        = new Color(191, 90,  242);
    private static final Color YELLOW        = new Color(255, 214, 10);
    private static final Color RED_NEON      = new Color(255, 69,  58);
    private static final Color GREEN_NEON    = new Color(48,  209, 88);
    private static final Color GRAY_MID      = new Color(100, 100, 120);
    private static final Color TEXT_DIM      = new Color(80,  80,  100);

    static class EmptyFieldException extends Exception { public EmptyFieldException(String msg) { super(msg); } }
    static class WeakPasswordException extends Exception { public WeakPasswordException(String msg) { super(msg); } }
    static class DuplicateEntryException extends Exception { public DuplicateEntryException(String msg) { super(msg); } }
    static class InvalidServiceNameException extends Exception { public InvalidServiceNameException(String msg) { super(msg); } }
    static class NoSelectionException extends Exception { public NoSelectionException(String msg) { super(msg); } }
    static class EmptyVaultException extends Exception { public EmptyVaultException(String msg) { super(msg); } }

    private JTextField  serviceField, usernameField, searchField;
    private JPasswordField passwordField;
    private JTextField  notesField;
    private JComboBox<String> categoryBox;
    private JLabel      strengthLabel;
    private JPanel[]    strengthBars;

    private JTable          vaultTable;
    private VaultTableModel tableModel;
    private JLabel          statusLabel;
    private JLabel          statsLabel;

    private JButton addBtn, auditBtn, deleteBtn, resetBtn, saveBtn, loadBtn, viewPwdBtn;
    private JButton generatePwdBtn;

    
    private final PasswordValidator validator = new PasswordValidator();

    public static void main(String[] args) {
      
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { }

        EventQueue.invokeLater(() -> {
            try {
                VaultLockScreen lockScreen = new VaultLockScreen(null);
                boolean unlocked = lockScreen.showAndCheck();
                
                if (unlocked) {
                    CyberVaultUI frame = new CyberVaultUI();
                    frame.setVisible(true);
                    frame.loadVaultFromFile();
                } else {
                    System.exit(0); 
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

   
    public CyberVaultUI() {
        setTitle("CyberVault v2.0 — Identity Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 60, 960, 660);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        setContentPane(root);

        root.add(buildTitleBar(),  BorderLayout.NORTH);
        root.add(buildCenterArea(), BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
    }

    
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(8, 8, 18));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CYAN));
        bar.setPreferredSize(new Dimension(960, 52));

        JPanel dots = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        dots.setBackground(new Color(8, 8, 18));
        dots.setBorder(BorderFactory.createEmptyBorder(16, 14, 0, 0));
        dots.add(colorDot(RED_NEON));
        dots.add(colorDot(YELLOW));
        dots.add(colorDot(GREEN_NEON));
        bar.add(dots, BorderLayout.WEST);

        JLabel title = new JLabel("CYBERVAULT  —  CREDENTIAL & IDENTITY MANAGER");
        title.setFont(new Font("Monospaced", Font.BOLD, 14));
        title.setForeground(CYAN);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        bar.add(title, BorderLayout.CENTER);

        JLabel version = new JLabel("SCD PROJECT  ");
        version.setFont(new Font("Monospaced", Font.PLAIN, 11));
        version.setForeground(TEXT_DIM);
        bar.add(version, BorderLayout.EAST);

        return bar;
    }

    private JLabel colorDot(Color c) {
        JLabel dot = new JLabel("●");
        dot.setForeground(c);
        dot.setFont(new Font("Monospaced", Font.PLAIN, 14));
        return dot;
    }

    
    private JPanel buildCenterArea() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG_DARK);
        center.add(buildSidebar(), BorderLayout.WEST);
        center.add(buildTablePanel(), BorderLayout.CENTER);
        return center;
    }

    
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_PANEL);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));
        sidebar.setPreferredSize(new Dimension(290, 0));

        sidebar.add(sectionLabel(" NEW ENTRY", GRAY_MID));

        serviceField  = cyberField();
        usernameField = cyberField();
        passwordField = cyberPasswordField();
        notesField    = cyberField();

        sidebar.add(fieldBlock("ALIAS / SERVICE NAME", serviceField));
        sidebar.add(fieldBlock("USERNAME / EMAIL",     usernameField));
        sidebar.add(buildPasswordBlock());
        sidebar.add(buildCategoryBlock());
        sidebar.add(fieldBlock("NOTES (OPTIONAL)",     notesField));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(buildButtonBlock());
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JPanel buildPasswordBlock() {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(BG_PANEL);
        outer.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130)); 

        JLabel lbl = new JLabel("PASSWORD");
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lbl.setForeground(GRAY_MID);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(lbl);
        outer.add(Box.createVerticalStrut(3));

        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        outer.add(passwordField);
        outer.add(Box.createVerticalStrut(5));

        
        JPanel barsRow = new JPanel(new GridLayout(1, 5, 3, 0));
        barsRow.setBackground(BG_PANEL);
        barsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        barsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        strengthBars = new JPanel[5];
        for (int i = 0; i < 5; i++) {
            strengthBars[i] = new JPanel();
            strengthBars[i].setBackground(BORDER_COLOR);
            strengthBars[i].setOpaque(true);
            barsRow.add(strengthBars[i]);
        }
        outer.add(barsRow);
        outer.add(Box.createVerticalStrut(5));


        generatePwdBtn = new JButton("⚡ GENERATE SECURE PASSWORD");
        generatePwdBtn.setFont(new Font("Monospaced", Font.PLAIN, 10));
        generatePwdBtn.setBackground(BG_INPUT);
        generatePwdBtn.setForeground(YELLOW);
        generatePwdBtn.setFocusPainted(false);
        generatePwdBtn.setBorder(BorderFactory.createLineBorder(YELLOW));
        generatePwdBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        generatePwdBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        generatePwdBtn.addActionListener(e -> {
            String newPwd = PasswordGenerator.generate(14);
            passwordField.setText(newPwd);
            updateStrengthMeter();
            updateStatusBar("  ⚡ SECURE PASSWORD GENERATED", YELLOW);
        });
        outer.add(generatePwdBtn);
        outer.add(Box.createVerticalStrut(5));

        strengthLabel = new JLabel("STRENGTH: —");
        strengthLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        strengthLabel.setForeground(GRAY_MID);
        strengthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(strengthLabel);
        outer.add(Box.createVerticalStrut(8));


        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { updateStrengthMeter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { updateStrengthMeter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateStrengthMeter(); }
        });

        return outer;
    }

    private JPanel buildCategoryBlock() {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(BG_PANEL);
        outer.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel lbl = new JLabel("CATEGORY");
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lbl.setForeground(GRAY_MID);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(lbl);
        outer.add(Box.createVerticalStrut(3));

        String[] cats = { "-- SELECT --", "DEV TOOLS", "EMAIL", "SOCIAL", "BANKING", "MEDIA", "WORK", "OTHER" };
        categoryBox = new JComboBox<>(cats);
        categoryBox.setFont(new Font("Monospaced", Font.PLAIN, 12));
        categoryBox.setBackground(BG_INPUT);
        categoryBox.setForeground(PURPLE);
        categoryBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        outer.add(categoryBox);
        outer.add(Box.createVerticalStrut(8));

        return outer;
    }

    private JPanel buildButtonBlock() {
        JPanel panel = new JPanel(new GridLayout(7, 1, 0, 7)); 
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 14, 14, 14));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        addBtn    = cyberButton("+ ADD TO VAULT",   CYAN);
        viewPwdBtn = cyberButton("👁 VIEW PASSWORD", CYAN);
        saveBtn   = cyberButton("💾 SAVE TO DISK",   GREEN_NEON);
        loadBtn   = cyberButton("📂 LOAD FROM DISK", PURPLE);
        auditBtn  = cyberButton("⚡ RUN AUDIT",      YELLOW);
        deleteBtn = cyberButton("✕ DELETE SELECTED", RED_NEON);
        resetBtn  = cyberButton("↺ CLEAR FIELDS",   GRAY_MID);

        panel.add(addBtn);
        panel.add(viewPwdBtn);
        panel.add(saveBtn);
        panel.add(loadBtn);
        panel.add(auditBtn);
        panel.add(deleteBtn);
        panel.add(resetBtn);

        addBtn.addActionListener(this);
        viewPwdBtn.addActionListener(this);
        saveBtn.addActionListener(this);
        loadBtn.addActionListener(this);
        auditBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        resetBtn.addActionListener(this);

        return panel;
    }

    
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));


        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(BG_DARK);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        
        JLabel heading = new JLabel(" VAULT ENTRIES");
        heading.setFont(new Font("Monospaced", Font.PLAIN, 11));
        heading.setForeground(GRAY_MID);
        topBar.add(heading, BorderLayout.WEST);
        
        searchField = cyberField();
        searchField.setFont(new Font("Monospaced", Font.PLAIN, 11));
        searchField.setPreferredSize(new Dimension(200, 24));
        
        JLabel searchIcon = new JLabel("🔍 ");
        searchIcon.setForeground(CYAN);
        searchIcon.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        searchPanel.setBackground(BG_DARK);
        searchPanel.add(searchIcon);
        searchPanel.add(searchField);
        topBar.add(searchPanel, BorderLayout.EAST);
        
        
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { applySearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { applySearch(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applySearch(); }
        });
        
        panel.add(topBar, BorderLayout.NORTH);

        tableModel = new VaultTableModel();
        vaultTable = new JTable(tableModel);
        styleTable();

        JScrollPane scroll = new JScrollPane(vaultTable);
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void applySearch() {
        tableModel.filter(searchField.getText());
        updateStatusBar("  ■ FILTER ACTIVE: " + searchField.getText().toUpperCase(), CYAN);
    }

    private void styleTable() {
        vaultTable.setBackground(BG_DARK);
        vaultTable.setForeground(new Color(170, 170, 190));
        vaultTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        vaultTable.setRowHeight(26);
        vaultTable.setGridColor(BORDER_COLOR);
        vaultTable.setSelectionBackground(new Color(0, 255, 231, 22));
        vaultTable.setSelectionForeground(CYAN);
        vaultTable.setShowHorizontalLines(true);
        vaultTable.setShowVerticalLines(false);
        vaultTable.setFillsViewportHeight(true);

        JTableHeader header = vaultTable.getTableHeader();
        header.setBackground(new Color(8, 8, 18));
        header.setForeground(GRAY_MID);
        header.setFont(new Font("Monospaced", Font.PLAIN, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        vaultTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                cell.setBackground(BG_DARK);
                String s = val != null ? val.toString() : "";
                switch (s) {
                    case "STRONG": cell.setForeground(GREEN_NEON); break;
                    case "MEDIUM": cell.setForeground(YELLOW);     break;
                    case "WEAK":   cell.setForeground(RED_NEON);   break;
                    default:       cell.setForeground(GRAY_MID);   break;
                }
                cell.setOpaque(true);
                return cell;
            }
        });

        int[] widths = { 35, 160, 160, 90, 75, 100 };
        for (int i = 0; i < widths.length; i++) {
            vaultTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(8, 8, 18));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        bar.setPreferredSize(new Dimension(960, 28));

        statusLabel = new JLabel("  ■ VAULT SECURE — READY");
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statusLabel.setForeground(GREEN_NEON);
        bar.add(statusLabel, BorderLayout.WEST);

        statsLabel = new JLabel("ENTRIES: 0 | WEAK: 0 | STRONG: 0  ");
        statsLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statsLabel.setForeground(TEXT_DIM);
        bar.add(statsLabel, BorderLayout.EAST);

        return bar;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if      (src == addBtn)    handleAddEntry();
        else if (src == viewPwdBtn) handleViewPassword();
        else if (src == saveBtn)   handleSave();
        else if (src == loadBtn)   handleLoad();
        else if (src == auditBtn)  handleAudit();
        else if (src == deleteBtn) handleDelete();
        else if (src == resetBtn)  clearForm();
    }

    
    private void handleAddEntry() {
        try {
            String service  = serviceField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String category = (String) categoryBox.getSelectedItem();
            String notes    = notesField.getText().trim();

            validateNewEntry(service, username, password, category);

            String strength = validator.getStrengthLabel(password);
            VaultEntry entry = new VaultEntry(service, username, password, category, notes, strength);
            tableModel.addEntry(entry);

            updateStatusBar("  ✓ ENTRY ADDED: " + service.toUpperCase(), GREEN_NEON);
            clearForm();

        } catch (EmptyFieldException ex) {
            showError(ex.getMessage(), "MISSING FIELDS");
        } catch (WeakPasswordException ex) {
            showError(ex.getMessage(), "WEAK PASSWORD");
        } catch (DuplicateEntryException ex) {
            showError(ex.getMessage(), "DUPLICATE ENTRY");
        } catch (InvalidServiceNameException ex) {
            showError(ex.getMessage(), "INVALID SERVICE");
        } finally {
            System.out.println("[CyberVault] Add attempt processed for: " + serviceField.getText().trim());
        }
    }

    private void handleViewPassword() {
        try {
            int row = vaultTable.getSelectedRow();
            if (row < 0) {
                throw new NoSelectionException("SELECT an entry first to view its password.");
            }
            
            VaultEntry selected = tableModel.getEntry(row);
            String service = selected.getServiceName();
            String password = selected.getPassword();
            
            // Show password in a secure dialog
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(BG_DARK);
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel label = new JLabel("Password for: " + service.toUpperCase(), SwingConstants.CENTER);
            label.setForeground(CYAN);
            label.setFont(new Font("Monospaced", Font.BOLD, 14));
            
            JTextField pwdField = new JTextField(password);
            pwdField.setFont(new Font("Monospaced", Font.PLAIN, 14));
            pwdField.setBackground(BG_INPUT);
            pwdField.setForeground(GREEN_NEON);
            pwdField.setEditable(false);
            pwdField.setBorder(BorderFactory.createLineBorder(CYAN));
            
            panel.add(label, BorderLayout.NORTH);
            panel.add(pwdField, BorderLayout.CENTER);
            
            JOptionPane.showMessageDialog(this, panel, 
                "VIEW PASSWORD - " + service, 
                JOptionPane.PLAIN_MESSAGE);
                
            updateStatusBar("  👁 PASSWORD VIEWED: " + service.toUpperCase(), CYAN);
            
        } catch (NoSelectionException ex) {
            showError(ex.getMessage(), "NO SELECTION");
        }
    }

    private void handleSave() {
        try {
            VaultFileManager.saveEntries(tableModel.getAllEntries());
            updateStatusBar("  💾 VAULT SAVED SUCCESSFULLY TO DISK", GREEN_NEON);
        } catch (Exception ex) {
            showError("Failed to save vault: " + ex.getMessage(), "SAVE ERROR");
        }
    }

    private void handleLoad() {
        loadVaultFromFile();
    }

    public void loadVaultFromFile() {
        try {
            java.io.File file = new java.io.File("vault.dat");
            if (!file.exists()) {
                updateStatusBar("  ■ NO SAVED VAULT FOUND. STARTING EMPTY.", YELLOW);
                return;
            }
            java.util.List<VaultEntry> loaded = VaultFileManager.loadEntries();
            tableModel.setEntries(loaded);
            updateStatusBar("  📂 VAULT LOADED SUCCESSFULLY (" + loaded.size() + " ENTRIES)", PURPLE);
        } catch (java.io.EOFException ex) {
            showError("The vault file is empty or corrupted.", "LOAD ERROR");
        } catch (Exception ex) {
            showError("Failed to load vault: " + ex.getMessage(), "LOAD ERROR");
        }
    }

    private void handleAudit() {
        try {
            if (tableModel.getRowCount() == 0) {
                throw new EmptyVaultException("VAULT IS EMPTY. Add entries before running an audit.");
            }

            String report = validator.runAudit(tableModel.getAllEntries());

            JTextArea area = new JTextArea(report);
            area.setFont(new Font("Monospaced", Font.PLAIN, 12));
            area.setBackground(BG_DARK);
            area.setForeground(CYAN);
            area.setEditable(false);
            area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            JScrollPane sp = new JScrollPane(area);
            sp.setPreferredSize(new Dimension(440, 320));
            sp.getViewport().setBackground(BG_DARK);

            JOptionPane.showMessageDialog(this, sp, "// AUDIT REPORT", JOptionPane.PLAIN_MESSAGE);
            updateStatusBar("  ⚡ AUDIT COMPLETE — CHECK REPORT", YELLOW);

        } catch (EmptyVaultException ex) {
            showError(ex.getMessage(), "EMPTY VAULT");
        }
    }

    private void handleDelete() {
        try {
            int row = vaultTable.getSelectedRow();
            if (row < 0) {
                throw new NoSelectionException("SELECT an entry from the table first.");
            }

            VaultEntry selected = tableModel.getEntry(row);
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "DELETE entry for: " + selected.getServiceName() + "?",
                    "CONFIRM DELETE",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeEntry(row);
                updateStatusBar("  ✕ ENTRY DELETED: " + selected.getServiceName().toUpperCase(), RED_NEON);
            }

        } catch (NoSelectionException ex) {
            showError(ex.getMessage(), "NO SELECTION");
        }
    }


    private void validateNewEntry(String service, String username, String password, String category)
            throws EmptyFieldException, WeakPasswordException,
                   DuplicateEntryException, InvalidServiceNameException {

        if (service.isEmpty() || username.isEmpty() || password.isEmpty()) {
            throw new EmptyFieldException("SERVICE, USERNAME, and PASSWORD fields are required.");
        }
        if (category == null || category.startsWith("--")) {
            throw new EmptyFieldException("Please select a CATEGORY before adding.");
        }
        if (service.matches("\\d+")) {
            throw new InvalidServiceNameException("Service name must contain letters, not just numbers.");
        }
        if (validator.isTooShort(password)) {
            throw new WeakPasswordException("Password is too short. Minimum 6 characters required.");
        }
        for (VaultEntry existing : tableModel.getAllEntries()) {
            if (existing.getServiceName().equalsIgnoreCase(service)) {
                throw new DuplicateEntryException("An entry for '" + service + "' already exists in the vault.");
            }
        }
    }

    
    private void updateStrengthMeter() {
        String pwd = new String(passwordField.getPassword());
        int bars = validator.getBarCount(pwd);
        String label = validator.getStrengthLabel(pwd);

        Color barColor;
        Color labelColor;
        switch (label) {
            case "STRONG": barColor = GREEN_NEON; labelColor = GREEN_NEON; break;
            case "MEDIUM": barColor = YELLOW;     labelColor = YELLOW;     break;
            default:       barColor = RED_NEON;   labelColor = RED_NEON;   break;
        }

        for (int i = 0; i < 5; i++) {
            strengthBars[i].setBackground(i < bars ? barColor : BORDER_COLOR);
        }

        strengthLabel.setText(pwd.isEmpty() ? "STRENGTH: —" : "STRENGTH: " + label);
        strengthLabel.setForeground(pwd.isEmpty() ? GRAY_MID : labelColor);
    }

    private void updateStatusBar(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
        statsLabel.setText(String.format(
                "ENTRIES: %d | WEAK: %d | STRONG: %d  ",
                tableModel.getAllEntries().size(), 
                tableModel.getWeakCount(),
                tableModel.getStrongCount()
        ));
    }

    private void clearForm() {
        serviceField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        notesField.setText("");
        categoryBox.setSelectedIndex(0);
        for (JPanel bar : strengthBars) bar.setBackground(BORDER_COLOR);
        strengthLabel.setText("STRENGTH: —");
        strengthLabel.setForeground(GRAY_MID);
        serviceField.requestFocus();
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, "// " + title, JOptionPane.ERROR_MESSAGE);
        updateStatusBar("  ▲ ERROR: " + title, RED_NEON);
    }

    
    private JPanel sectionLabel(String text, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(8, 8, 18));
        p.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lbl.setForeground(color);
        p.add(lbl);
        return p;
    }

    private JPanel fieldBlock(String labelText, JComponent field) {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(BG_PANEL);
        outer.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lbl.setForeground(GRAY_MID);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        outer.add(lbl);
        outer.add(Box.createVerticalStrut(3));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        outer.add(field);
        outer.add(Box.createVerticalStrut(8));

        return outer;
    }

    private JTextField cyberField() {
        JTextField f = new JTextField();
        f.setBackground(BG_INPUT);
        f.setForeground(CYAN);
        f.setCaretColor(CYAN);
        f.setFont(new Font("Monospaced", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        return f;
    }

    private JPasswordField cyberPasswordField() {
        JPasswordField f = new JPasswordField();
        f.setBackground(BG_INPUT);
        f.setForeground(PURPLE);
        f.setCaretColor(PURPLE);
        f.setFont(new Font("Monospaced", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        return f;
    }

    private JButton cyberButton(String text, Color accent) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.PLAIN, 11));
        btn.setBackground(BG_INPUT);
        btn.setForeground(accent);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 25));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(BG_INPUT);
            }
        });
        return btn;
    }
}