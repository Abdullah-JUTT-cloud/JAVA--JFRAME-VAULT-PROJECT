package cybervault;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class VaultFileManager {
    
    private static final String VAULT_FILE = "vault.dat";
    private static final String KEY_FILE   = "master.key";

    
    public static void saveEntries(List<VaultEntry> entries) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(VAULT_FILE))) {
            oos.writeObject(entries);
        }
    }

   
    @SuppressWarnings("unchecked")
    public static List<VaultEntry> loadEntries() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(VAULT_FILE))) {
            return (List<VaultEntry>) ois.readObject();
        }
    }

   
    public static boolean isMasterPasswordSet() {
        return new File(KEY_FILE).exists();
    }

    
    public static void saveMasterPassword(String password) throws IOException, NoSuchAlgorithmException {
        String hash = hashPassword(password);
        try (PrintWriter out = new PrintWriter(new FileWriter(KEY_FILE))) {
            out.println(hash);
        }
    }

    
    public static boolean checkMasterPassword(String password) throws IOException, NoSuchAlgorithmException {
        File file = new File(KEY_FILE);
        if (!file.exists()) return false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String savedHash = br.readLine();
            String inputHash = hashPassword(password);
            return savedHash != null && savedHash.equals(inputHash);
        }
    }

 
    private static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}