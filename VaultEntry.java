package cybervault;

import java.time.LocalDate;


public class VaultEntry implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L; 

    private String serviceName;
    private String username;
    private String password;
    private String category;
    private String notes;
    private String strength;
    private LocalDate dateAdded;

    public VaultEntry(String serviceName, String username, String password,
                      String category, String notes, String strength) {
        this.serviceName = serviceName;
        this.username    = username;
        this.password    = password;
        this.category    = category;
        this.notes       = notes;
        this.strength    = strength;
        this.dateAdded   = LocalDate.now();
    }

    
    public String getServiceName() { return serviceName; }
    public String getUsername()    { return username; }
    public String getPassword()    { return password; }
    public String getCategory()    { return category; }
    public String getNotes()       { return notes; }
    public String getStrength()    { return strength; }
    public LocalDate getDateAdded(){ return dateAdded; }

    
    public void setStrength(String strength) { this.strength = strength; }

    @Override
    public String toString() {
        return String.format("VaultEntry[service=%s, user=%s, strength=%s, added=%s]",
                serviceName, username, strength, dateAdded);
    }
}