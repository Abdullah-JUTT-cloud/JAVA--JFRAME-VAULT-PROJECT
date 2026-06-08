# CyberVault v2.0 — Credential & Identity Manager

Java: 21 | GUI: Java Swing | Testing: JUnit 5

## Project Description
CyberVault is a secure, cyberpunk-themed desktop application designed to manage digital credentials and passwords. It provides a robust, user-friendly interface for storing, generating, and auditing passwords, protected by a master password lock screen. 

This project was developed as part of the SCD (Software Construction and Design) semester requirements, focusing on clean architecture, robust error handling, and comprehensive unit testing.

## Key Features
- Master Password Protection: Secure lock screen to prevent unauthorized access
- Password Generator: Generates cryptographically secure, random passwords
- Live Strength Meter: Real-time visual feedback on password strength as you type
- Vault Auditing: Automatically detects weak passwords and duplicate credentials
- Data Persistence: Save and load your encrypted vault to/from local disk
- Live Search: Instantly filter and find credentials by service, username, or category
- View Password: Securely reveal hidden passwords when needed

## Tech Stack
- Language: Java 21
- GUI Framework: Java Swing
- Testing Framework: JUnit 5
- Version Control: Git & GitHub

## SCD Requirements Coverage

This project explicitly implements all mandatory concepts outlined in the SCD Lab requirements:

1. Event Handling
- ActionListeners: Attached to all primary buttons (Add, Save, Load, Audit, Delete, View Password)
- DocumentListeners: Used for the live password strength meter and the real-time search/filter bar
- MouseListeners: Custom hover effects on cyberpunk-styled buttons

2. Exception Handling
- Custom Exceptions: Implemented 6 specific custom exceptions (EmptyFieldException, WeakPasswordException, DuplicateEntryException, InvalidServiceNameException, NoSelectionException, EmptyVaultException) to handle specific business logic failures gracefully
- File I/O Exceptions: Handled IOException, EOFException, and ClassNotFoundException during vault save/load operations
- User Feedback: All exceptions are caught and displayed to the user via clean UI error dialogs instead of raw stack traces

3. Code Refactoring
- Separation of Concerns: The UI (CyberVaultUI) is strictly separated from business logic (PasswordValidator, PasswordGenerator) and data management (VaultFileManager, VaultTableModel)
- Custom TableModel: VaultTableModel extends AbstractTableModel to handle data state and filtering independently of the JTable UI
- Clean Code: Meaningful variable names, modular method design, and removal of code duplication

4. Unit Testing
- JUnit 5 Integration: Comprehensive automated test suites for core business logic
- Test Coverage:
  - PasswordValidatorTest.java: Tests strength calculation, common patterns, and audit logic
  - PasswordGeneratorTest.java: Tests length, character inclusion, and randomness
  - VaultTableModelTest.java: Tests data addition, removal, and filtering
  - VaultEntryTest.java: Tests data model integrity

5. Git & GitHub
- Maintained a consistent development history with meaningful, descriptive commit messages

## Setup & Installation Instructions

Prerequisites:
- Java Development Kit (JDK) 21 or higher
- An IDE like IntelliJ IDEA, Eclipse, or NetBeans
- JUnit 5 library (included in most modern IDEs by default)

How to Run:
1. Clone the repository:
   git clone https://github.com/YOUR_USERNAME/CyberVault.git](https://github.com/Abdullah-JUTT-cloud/JAVA--JFRAME-VAULT-PROJECT.git

2. Open in IDE:
   Open the project folder in your preferred Java IDE

3. Add Dependencies:
   Ensure JUnit 5 is added to your project's build path/module path

4. Run the Application:
   Execute the main method in CyberVaultUI.java

How to Use:
1. On the first launch, you will be prompted to Create a Master Password
2. On subsequent launches, you must Enter the Master Password to unlock the vault
3. Fill out the "New Entry" form on the left and click "+ ADD TO VAULT"
4. Use the "VIEW PASSWORD" button to reveal hidden passwords
5. Click "SAVE TO DISK" to persist your data to vault.dat

## Screenshots

1. Master Password Lock Screen
   Location: screenshots/lock_screen.png
   Description: The secure entry point requiring a master password

2. Main Dashboard & Password Generation
   Location: screenshots/main_dashboard.png
   Description: The cyberpunk-themed interface showing the password generator and strength meter

3. Search Feature
   Location: screenshots/search_feature.png
   Description: Filtering vault entries using the live search bar

4. Audit Report
   Location: ScreenShots-of-the-project
   Description: The automated security audit report highlighting weak and duplicate passwords

## Author
ABDULLAH JUTT
SCD Semester Project
CyberVault v2.0 - Identity Manager
