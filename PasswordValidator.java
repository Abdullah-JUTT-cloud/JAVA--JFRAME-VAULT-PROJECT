package cybervault;


public class PasswordValidator {

    
    private static final int WEAK_MAX   = 40;
    private static final int MEDIUM_MAX = 70;

   
    public int calculateScore(String password) {
        if (password == null || password.isEmpty()) return 0;

        int score = 0;

        if (password.length() >= 8)  score += 20;
        if (password.length() >= 12) score += 10;
        if (password.length() >= 16) score += 10;

        if (password.matches(".*[A-Z].*"))              score += 20;
        if (password.matches(".*[a-z].*"))              score += 20;
        if (password.matches(".*\\d.*"))                score += 15;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score += 15;

        if (isCommonPattern(password)) score -= 10;

        return Math.max(0, Math.min(100, score));
    }

   
    public String getStrengthLabel(String password) {
        int score = calculateScore(password);
        if (score <= WEAK_MAX)   return "WEAK";
        if (score <= MEDIUM_MAX) return "MEDIUM";
        return "STRONG";
    }

    
    public int getBarCount(String password) {
        int score = calculateScore(password);
        if (score <= 20)  return 1;
        if (score <= 40)  return 2;
        if (score <= 60)  return 3;
        if (score <= 80)  return 4;
        return 5;
    }

    
    public boolean isTooShort(String password) {
        return password == null || password.length() < 6;
    }

  
    public boolean isCommonPattern(String password) {
        if (password == null) return false;
        String lower = password.toLowerCase();
        String[] patterns = { "password", "123456", "qwerty", "abc123",
                              "letmein", "admin", "welcome", "monkey", "dragon" };
        for (String p : patterns) {
            if (lower.contains(p)) return true;
        }
        return false;
    }

    public String runAudit(java.util.List<VaultEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return "VAULT IS EMPTY — NOTHING TO AUDIT.";
        }

        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════╗\n");
        report.append("║     CYBERVAULT AUDIT REPORT      ║\n");
        report.append("╚══════════════════════════════════╝\n\n");

        int weakCount = 0;
        int mediumCount = 0;
        int strongCount = 0;

        
        report.append("[ STRENGTH ANALYSIS ]\n");
        for (VaultEntry e : entries) {
            String label = getStrengthLabel(e.getPassword());
            report.append(String.format("  %-20s → %s\n", e.getServiceName(), label));
            if ("WEAK".equals(label))        weakCount++;
            else if ("MEDIUM".equals(label)) mediumCount++;
            else                             strongCount++;
        }

        
        report.append("\n[ DUPLICATE PASSWORD CHECK ]\n");
        boolean foundDup = false;
        for (int i = 0; i < entries.size(); i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                if (entries.get(i).getPassword().equals(entries.get(j).getPassword())) {
                    report.append(String.format("  ⚠ DUPLICATE: %s and %s share the same password!\n",
                            entries.get(i).getServiceName(), entries.get(j).getServiceName()));
                    foundDup = true;
                }
            }
        }
        if (!foundDup) report.append("  ✓ No duplicate passwords detected.\n");

        
        report.append("\n[ SUMMARY ]\n");
        report.append(String.format("  Total Entries : %d\n", entries.size()));
        report.append(String.format("  Strong        : %d\n", strongCount));
        report.append(String.format("  Medium        : %d\n", mediumCount));
        report.append(String.format("  Weak          : %d  %s\n", weakCount,
                weakCount > 0 ? "← UPDATE THESE!" : ""));

        if (weakCount == 0 && !foundDup) {
            report.append("\n  ■ VAULT STATUS: SECURE\n");
        } else {
            report.append("\n  ▲ VAULT STATUS: ACTION REQUIRED\n");
        }

        return report.toString();
    }
}