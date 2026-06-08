package cybervault;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    @Test
    void testGenerate_DefaultLength_AtLeast8Chars() {
        String password = PasswordGenerator.generate(8);
        assertTrue(password.length() >= 8);
    }

    @Test
    void testGenerate_CustomLength_CorrectLength() {
        String password = PasswordGenerator.generate(16);
        assertEquals(16, password.length());
    }

    @Test
    void testGenerate_ContainsUpperCase() {
        String password = PasswordGenerator.generate(20);
        assertTrue(password.matches(".*[A-Z].*"));
    }

    @Test
    void testGenerate_ContainsLowerCase() {
        String password = PasswordGenerator.generate(20);
        assertTrue(password.matches(".*[a-z].*"));
    }

    @Test
    void testGenerate_ContainsDigit() {
        String password = PasswordGenerator.generate(20);
        assertTrue(password.matches(".*\\d.*"));
    }

    @Test
    void testGenerate_ContainsSpecialChar() {
        String password = PasswordGenerator.generate(20);
        assertTrue(password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{}|;:,.<>?].*"));
    }

    @Test
    void testGenerate_DifferentEachTime() {
        String pwd1 = PasswordGenerator.generate(16);
        String pwd2 = PasswordGenerator.generate(16);
        assertNotEquals(pwd1, pwd2);
    }
}