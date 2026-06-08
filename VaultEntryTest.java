package cybervault;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;


class VaultEntryTest {

    @Test
    void testConstructor_SetsAllFieldsCorrectly() {
        VaultEntry entry = new VaultEntry("Gmail", "user@gmail.com", "Pass123!", "EMAIL", "My email", "STRONG");

        assertEquals("Gmail", entry.getServiceName());
        assertEquals("user@gmail.com", entry.getUsername());
        assertEquals("Pass123!", entry.getPassword());
        assertEquals("EMAIL", entry.getCategory());
        assertEquals("My email", entry.getNotes());
        assertEquals("STRONG", entry.getStrength());
    }

    @Test
    void testDateAdded_IsSetToToday() {
        VaultEntry entry = new VaultEntry("Test", "user", "pass", "WORK", "", "STRONG");
        assertEquals(LocalDate.now(), entry.getDateAdded());
    }

    @Test
    void testSetStrength_UpdatesValue() {
        VaultEntry entry = new VaultEntry("Test", "user", "pass", "WORK", "", "WEAK");
        assertEquals("WEAK", entry.getStrength());

        entry.setStrength("STRONG");
        assertEquals("STRONG", entry.getStrength());
    }

    @Test
    void testToString_ContainsServiceName() {
        VaultEntry entry = new VaultEntry("Gmail", "user@gmail.com", "Pass123!", "EMAIL", "", "STRONG");
        String str = entry.toString();
        
        assertTrue(str.contains("Gmail"));
        assertTrue(str.contains("user@gmail.com"));
        assertTrue(str.contains("STRONG"));
    }

    @Test
    void testGetters_ReturnCorrectValues() {
        VaultEntry entry = new VaultEntry("GitHub", "dev@github.com", "Secure!99", "DEV TOOLS", "Work account", "MEDIUM");
        
        assertEquals("GitHub", entry.getServiceName());
        assertEquals("dev@github.com", entry.getUsername());
        assertEquals("Secure!99", entry.getPassword());
        assertEquals("DEV TOOLS", entry.getCategory());
        assertEquals("Work account", entry.getNotes());
        assertEquals("MEDIUM", entry.getStrength());
    }
}