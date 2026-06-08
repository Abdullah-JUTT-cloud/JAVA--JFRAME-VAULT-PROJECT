package cybervault;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;


class VaultTableModelTest {

    private VaultTableModel model;

    @BeforeEach
    void setUp() {
        model = new VaultTableModel();
    }

    @Test
    void testInitialState_Empty() {
        assertEquals(0, model.getRowCount());
        assertEquals(6, model.getColumnCount());
    }

    @Test
    void testAddEntry_IncreasesRowCount() {
        VaultEntry entry = new VaultEntry("Gmail", "user@gmail.com", "Pass123!", "EMAIL", "", "STRONG");
        model.addEntry(entry);
        assertEquals(1, model.getRowCount());
    }

    @Test
    void testAddMultipleEntries_CorrectCount() {
        model.addEntry(new VaultEntry("Site1", "u1", "P1!", "WORK", "", "STRONG"));
        model.addEntry(new VaultEntry("Site2", "u2", "P2!", "WORK", "", "MEDIUM"));
        model.addEntry(new VaultEntry("Site3", "u3", "P3!", "WORK", "", "WEAK"));
        assertEquals(3, model.getRowCount());
    }

    @Test
    void testRemoveEntry_DecreasesRowCount() {
        model.addEntry(new VaultEntry("Gmail", "user@gmail.com", "Pass123!", "EMAIL", "", "STRONG"));
        model.addEntry(new VaultEntry("GitHub", "dev", "Pass456!", "DEV TOOLS", "", "STRONG"));
        assertEquals(2, model.getRowCount());

        model.removeEntry(0);
        assertEquals(1, model.getRowCount());
    }

    @Test
    void testRemoveEntry_InvalidIndex_NoChange() {
        model.addEntry(new VaultEntry("Gmail", "user@gmail.com", "Pass123!", "EMAIL", "", "STRONG"));
        model.removeEntry(-1); // Invalid
        model.removeEntry(99); // Out of bounds
        assertEquals(1, model.getRowCount()); // Should not change
    }

    @Test
    void testGetEntry_ReturnsCorrectEntry() {
        VaultEntry entry = new VaultEntry("Gmail", "user@gmail.com", "Pass123!", "EMAIL", "", "STRONG");
        model.addEntry(entry);
        VaultEntry retrieved = model.getEntry(0);
        assertEquals("Gmail", retrieved.getServiceName());
        assertEquals("user@gmail.com", retrieved.getUsername());
    }

    @Test
    void testFilter_EmptyFilter_ShowsAll() {
        model.addEntry(new VaultEntry("Gmail", "u1", "P1!", "EMAIL", "", "STRONG"));
        model.addEntry(new VaultEntry("GitHub", "u2", "P2!", "DEV TOOLS", "", "STRONG"));
        model.filter("");
        assertEquals(2, model.getRowCount());
    }

    @Test
    void testFilter_MatchesServiceName() {
        model.addEntry(new VaultEntry("Gmail", "u1", "P1!", "EMAIL", "", "STRONG"));
        model.addEntry(new VaultEntry("GitHub", "u2", "P2!", "DEV TOOLS", "", "STRONG"));
        model.addEntry(new VaultEntry("Facebook", "u3", "P3!", "SOCIAL", "", "STRONG"));

        model.filter("git");
        assertEquals(1, model.getRowCount());
        assertEquals("GitHub", model.getEntry(0).getServiceName());
    }

    @Test
    void testFilter_MatchesUsername() {
        model.addEntry(new VaultEntry("Gmail", "john@gmail.com", "P1!", "EMAIL", "", "STRONG"));
        model.addEntry(new VaultEntry("GitHub", "jane@dev.com", "P2!", "DEV TOOLS", "", "STRONG"));

        model.filter("john");
        assertEquals(1, model.getRowCount());
        assertEquals("Gmail", model.getEntry(0).getServiceName());
    }

    @Test
    void testFilter_CaseInsensitive() {
        model.addEntry(new VaultEntry("Gmail", "user@gmail.com", "P1!", "EMAIL", "", "STRONG"));
        model.filter("GMAIL");
        assertEquals(1, model.getRowCount());
    }

    @Test
    void testFilter_NoMatches_EmptyTable() {
        model.addEntry(new VaultEntry("Gmail", "u1", "P1!", "EMAIL", "", "STRONG"));
        model.filter("nonexistent");
        assertEquals(0, model.getRowCount());
    }

    @Test
    void testGetWeakCount_Correct() {
        model.addEntry(new VaultEntry("Site1", "u1", "weak", "WORK", "", "WEAK"));
        model.addEntry(new VaultEntry("Site2", "u2", "weak2", "WORK", "", "WEAK"));
        model.addEntry(new VaultEntry("Site3", "u3", "Strong!1", "WORK", "", "STRONG"));

        assertEquals(2, model.getWeakCount());
    }

    @Test
    void testGetStrongCount_Correct() {
        model.addEntry(new VaultEntry("Site1", "u1", "Strong!1", "WORK", "", "STRONG"));
        model.addEntry(new VaultEntry("Site2", "u2", "Strong!2", "WORK", "", "STRONG"));
        model.addEntry(new VaultEntry("Site3", "u3", "weak", "WORK", "", "WEAK"));

        assertEquals(2, model.getStrongCount());
    }

    @Test
    void testSetEntries_ReplacesAllData() {
        model.addEntry(new VaultEntry("Old1", "u1", "P1!", "WORK", "", "STRONG"));
        model.addEntry(new VaultEntry("Old2", "u2", "P2!", "WORK", "", "STRONG"));

        List<VaultEntry> newEntries = new ArrayList<>();
        newEntries.add(new VaultEntry("New1", "u3", "P3!", "WORK", "", "STRONG"));
        model.setEntries(newEntries);

        assertEquals(1, model.getRowCount());
        assertEquals("New1", model.getEntry(0).getServiceName());
    }

    @Test
    void testGetValueAt_ReturnsCorrectColumnData() {
        VaultEntry entry = new VaultEntry("Gmail", "user@gmail.com", "Pass123!", "EMAIL", "", "STRONG");
        model.addEntry(entry);

        assertEquals("01", model.getValueAt(0, 0));              
        assertEquals("Gmail", model.getValueAt(0, 1));           
        assertEquals("user@gmail.com", model.getValueAt(0, 2));  
        assertEquals("EMAIL", model.getValueAt(0, 3));           
        assertEquals("STRONG", model.getValueAt(0, 4));         
        assertNotNull(model.getValueAt(0, 5));                  
    }

    @Test
    void testColumnNames_AreCorrect() {
        assertEquals("#", model.getColumnName(0));
        assertEquals("SERVICE", model.getColumnName(1));
        assertEquals("USERNAME", model.getColumnName(2));
        assertEquals("CATEGORY", model.getColumnName(3));
        assertEquals("STRENGTH", model.getColumnName(4));
        assertEquals("DATE ADDED", model.getColumnName(5));
    }
}