package cybervault;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class PasswordValidatorTest {

    private PasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
    }

    @Test
    void testCalculateScore_EmptyPassword_ReturnsZero() {
        assertEquals(0, validator.calculateScore(""));
    }

    @Test
    void testCalculateScore_NullPassword_ReturnsZero() {
        assertEquals(0, validator.calculateScore(null));
    }

    @Test
    void testCalculateScore_WeakPassword_LowScore() {
        int score = validator.calculateScore("abc");
        assertTrue(score <= 40, "Score should be 40 or less for weak password");
    }

    @Test
    void testCalculateScore_StrongPassword_HighScore() {
        int score = validator.calculateScore("MyStr0ng!Pass#2024");
        assertTrue(score > 70, "Score should be above 70 for strong password");
    }

    @Test
    void testGetStrengthLabel_Weak() {
        assertEquals("WEAK", validator.getStrengthLabel("123"));
    }

    @Test
    void testGetStrengthLabel_Strong() {
        assertEquals("STRONG", validator.getStrengthLabel("MyStr0ng!Pass#2024"));
    }

    @Test
    void testGetBarCount_Strong_Returns5() {
        assertEquals(5, validator.getBarCount("V3ry$trong!Pass#99"));
    }

    @Test
    void testIsTooShort_ShortPassword_ReturnsTrue() {
        assertTrue(validator.isTooShort("12345"));
    }

    @Test
    void testIsTooShort_LongPassword_ReturnsFalse() {
        assertFalse(validator.isTooShort("ThisIsAVeryLongPassword"));
    }

    @Test
    void testIsCommonPattern_ContainsPassword_ReturnsTrue() {
        assertTrue(validator.isCommonPattern("mypassword123"));
    }

    @Test
    void testIsCommonPattern_UniquePassword_ReturnsFalse() {
        assertFalse(validator.isCommonPattern("xK9#mP2!qL"));
    }

    @Test
    void testRunAudit_EmptyList_ReturnsEmptyMessage() {
        String report = validator.runAudit(new ArrayList<>());
        assertTrue(report.contains("EMPTY"));
    }

    @Test
    void testRunAudit_WithDuplicatePasswords_DetectsDuplicates() {
        List<VaultEntry> entries = new ArrayList<>();
        entries.add(new VaultEntry("Site1", "user1", "SamePass123!", "WORK", "", "STRONG"));
        entries.add(new VaultEntry("Site2", "user2", "SamePass123!", "WORK", "", "STRONG"));

        String report = validator.runAudit(entries);
        assertTrue(report.contains("DUPLICATE"));
    }
}