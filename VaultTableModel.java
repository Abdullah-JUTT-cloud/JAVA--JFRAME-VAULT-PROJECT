package cybervault;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;


public class VaultTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLUMNS = {
        "#", "SERVICE", "USERNAME", "CATEGORY", "STRENGTH", "DATE ADDED"
    };

    private final List<VaultEntry> allEntries = new ArrayList<>();
    private List<VaultEntry> displayedEntries = new ArrayList<>();
    private String currentFilter = "";

 
    public void setEntries(List<VaultEntry> entries) {
        this.allEntries.clear();
        if (entries != null) {
            this.allEntries.addAll(entries);
        }
        filter(currentFilter); 
    }

    public void addEntry(VaultEntry entry) {
        allEntries.add(entry);
        filter(currentFilter); 
    }

    public void removeEntry(int displayedRowIndex) {
        if (displayedRowIndex >= 0 && displayedRowIndex < displayedEntries.size()) {
            VaultEntry toRemove = displayedEntries.get(displayedRowIndex);
            allEntries.remove(toRemove); 
            filter(currentFilter);       
        }
    }

   
    public void filter(String text) {
        this.currentFilter = text;
        displayedEntries.clear();
        
        if (text == null || text.trim().isEmpty()) {
            displayedEntries.addAll(allEntries);
        } else {
            String lowerText = text.toLowerCase();
            for (VaultEntry e : allEntries) {
                if (e.getServiceName().toLowerCase().contains(lowerText) ||
                    e.getUsername().toLowerCase().contains(lowerText) ||
                    e.getCategory().toLowerCase().contains(lowerText)) {
                    displayedEntries.add(e);
                }
            }
        }
        fireTableDataChanged(); 
    }
    
    public String getCurrentFilter() { return currentFilter; }
    public VaultEntry getEntry(int displayedRowIndex) { return displayedEntries.get(displayedRowIndex); }
    public List<VaultEntry> getAllEntries() { return new ArrayList<>(allEntries); }

    public int getWeakCount() {
        return (int) allEntries.stream().filter(e -> "WEAK".equals(e.getStrength())).count();
    }

    public int getStrongCount() {
        return (int) allEntries.stream().filter(e -> "STRONG".equals(e.getStrength())).count();
    }

    @Override public int getRowCount()    { return displayedEntries.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int col) { return COLUMNS[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        VaultEntry e = displayedEntries.get(row);
        switch (col) {
            case 0: return String.format("%02d", row + 1);
            case 1: return e.getServiceName();
            case 2: return e.getUsername();
            case 3: return e.getCategory();
            case 4: return e.getStrength();
            case 5: return e.getDateAdded().toString();
            default: return "";
        }
    }
}