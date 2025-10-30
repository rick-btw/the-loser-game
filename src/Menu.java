import java.util.Scanner;
import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Menu {
    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthService authService = AuthService.getInstance();
    private static final WordBank wordBank = WordBank.getInstance();
    private static final Leaderboard leaderboard = Leaderboard.getInstance();
    private User currentUser = null;

    private void showAdminMenu() {
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. Create backup ZIP (users.csv, words.txt, leaderboard.txt)");
            System.out.println("2. Back to User Menu");
            System.out.print("Choose an option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    createBackupZip();
                    break;
                case 2:
                    return;
                default:
                    System.out.println(" Invalid option. Please try again.");
            }
        }
    }

    public void start() {
        displayTitle();
        
        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private void displayTitle() {
        System.out.println("+-------------------------------------------------------------+");
        System.out.println("|                                                             |");
        System.out.println("|                       WELCOME TO THE                        |");
        System.out.println("|                                                             |");
        System.out.println("|    _                            ____                        |");
        System.out.println("|   | |    ___  ___  ___ _ __   / ___| __ _ _ __ ___   ___    |");
        System.out.println("|   | |   / _ \\/ __|/ _ \\ '__| | |  _ / _' | '_ ' _ \\ / _ \\   |");
        System.out.println("|   | |__| (_) \\__ \\  __/ |    | |_| | (_| | | | | | |  __/   |");
        System.out.println("|   |_____\\___/|___/\\___|_|     \\____|\\__,_|_| |_| |_|\\___|   |");
        System.out.println("|                                                             |");
        System.out.println("|                         by Amirali                          |");
        System.out.println("+-------------------------------------------------------------+");
        System.out.println(" \\\\________________________________________________________\\\\");
        System.out.println("  \\\\______________________________________________________\\\\");
    }

    private void showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Login");
        System.out.println("2. Signup");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                signup();
                break;
            case 3:
                System.out.println("Goodbye! 👋");
                System.exit(0);
            default:
                System.out.println("❌ Invalid option. Please try again.");
        }
    }

    private void showUserMenu() {
        System.out.println("\n=== User Menu ===");
        System.out.println("Welcome, " + currentUser.getUsername() + "! 🎉");
        if ("admin".equalsIgnoreCase(currentUser.getUsername())) {
            System.out.println("0. Admin menu");
        }
        System.out.println("1. Start game");
        System.out.println("2. Show leaderboard");
        System.out.println("3. Add new word");
        System.out.println("4. Change password");
        System.out.println("5. Logout");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 0:
                if ("admin".equalsIgnoreCase(currentUser.getUsername())) {
                    showAdminMenu();
                } else {
                    System.out.println("Invalid option. Please try again.");
                }
                break;
            case 1:
                startGame();
                break;
            case 2:
                showLeaderboard();
                break;
            case 3:
                addNewWord();
                break;
            case 4:
                changePassword();
                break;
            case 5:
                logout();
                break;
            default:
                System.out.println("❌ Invalid option. Please try again.");
        }
    }

    private void login() {
        System.out.println("\n=== Login ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            currentUser = authService.login(username, password);
            System.out.println("✅ Login successful!");
        } catch (Exception e) {
            System.out.println("❌ Login failed: " + e.getMessage());
        }
    }

    private void createBackupZip() {

        final String WORDS_FILE = "words.txt";
        final String LEADERBOARD_FILE = "leaderboard.txt";


        String zipName = "backup_" + System.currentTimeMillis() + ".zip";
        Path zipPath = Paths.get(zipName).toAbsolutePath();


        Path tempUsersCsv = null;
        try {
            tempUsersCsv = Files.createTempFile("users_", ".csv");
            try (BufferedWriter writer = Files.newBufferedWriter(tempUsersCsv)) {
                writer.write("username,score\n");
                for (Map.Entry<String, User> e : Leaderboard.getInstance().getUserDatabase().entrySet()) {
                    String username = e.getKey();
                    int score = e.getValue().getScore();
                    writer.write(username + "," + score + "\n");
                }
            }
        } catch (IOException ioEx) {
            System.out.println("❌ Failed to export users.csv: " + ioEx.getMessage());
            return;
        }


        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {

            addFileToZip(zos, tempUsersCsv, "users.csv");


            Path wordsPath = Paths.get(WORDS_FILE);
            if (Files.exists(wordsPath)) {
                addFileToZip(zos, wordsPath, WORDS_FILE);
            } else {
                System.out.println("⚠️ " + WORDS_FILE + " not found; skipping.");
            }


            Path leaderboardPath = Paths.get(LEADERBOARD_FILE);
            if (Files.exists(leaderboardPath)) {
                addFileToZip(zos, leaderboardPath, LEADERBOARD_FILE);
            } else {
                System.out.println("⚠️ " + LEADERBOARD_FILE + " not found; skipping.");
            }
        } catch (IOException ioEx) {
            System.out.println("❌ Failed to create backup zip: " + ioEx.getMessage());
            return;
        } finally {

            try {
                if (tempUsersCsv != null) Files.deleteIfExists(tempUsersCsv);
            } catch (IOException ignore) {}
        }

        System.out.println("Backup created successfully!");
        System.out.println("ZIP path: " + zipPath.toString());
    }

    private void addFileToZip(ZipOutputStream zos, Path file, String entryName) throws IOException {
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);
        Files.copy(file, zos);
        zos.closeEntry();
    }

    private void signup() {
        System.out.println("\n=== Signup ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            currentUser = authService.signup(username, password);
            System.out.println("✅ Signup successful! You can now login.");
            currentUser = null;
        } catch (Exception e) {
            System.out.println("❌ Signup failed: " + e.getMessage());
        }
    }

    private void startGame() {
        if (wordBank.isEmpty()) {
            System.out.println("❌ No words available. Please add some words first.");
            return;
        }

        Game game = new Game();
        game.startGame(currentUser);
    }

    private void showLeaderboard() {
        System.out.println("\n=== Leaderboard ===");
        leaderboard.display();
    }

    private void addNewWord() {
        System.out.println("\n=== Add New Word ===");
        System.out.print("Enter new word: ");
        String word = scanner.nextLine().toLowerCase().trim();

        try {
            wordBank.addWord(word);
            System.out.println("✅ Word added successfully!");
        } catch (Exception e) {
            System.out.println("❌ Failed to add word: " + e.getMessage());
        }
    }

    private void changePassword() {
        System.out.println("\n=== Change Password ===");
        System.out.print("Current password: ");
        String currentPassword = scanner.nextLine();
        System.out.print("New password: ");
        String newPassword = scanner.nextLine();

        try {
            currentUser.changePassword(currentPassword, newPassword);
            System.out.println("✅ Password changed successfully!");
        } catch (Exception e) {
            System.out.println("❌ Failed to change password: " + e.getMessage());
        }
    }

    private void logout() {
        currentUser = null;
        System.out.println("👋 Logged out successfully!");
    }

    private int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("❌ Please enter a valid number: ");
            }
        }
    }
}