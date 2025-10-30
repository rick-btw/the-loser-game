import java.io.*;
import java.util.*;

public class Leaderboard {
    private static Leaderboard instance;
    private Map<String, User> userDatabase;
    private PriorityQueue<User> leaderboard;
    private static final String LEADERBOARD_FILE = "leaderboard.txt";

    private Leaderboard() {
        this.userDatabase = new HashMap<>();
        this.leaderboard = new PriorityQueue<>((u1, u2) -> u2.getScore() - u1.getScore());
        loadLeaderboard();
    }

    public static Leaderboard getInstance() {
        if (instance == null) {
            instance = new Leaderboard();
        }
        return instance;
    }

    public void updateScore(User user, int points) {
        userDatabase.put(user.getUsername(), user);
        
        leaderboard.removeIf(u -> u.getUsername().equals(user.getUsername()));
        
        leaderboard.offer(user);
        
        saveLeaderboard();
    }

    public void display() {
        if (leaderboard.isEmpty()) {
            System.out.println("No scores yet. Play some games to see the leaderboard!");
            return;
        }

        System.out.println("\n🏆 LEADERBOARD 🏆");
        System.out.println("-".repeat(30));
        
        PriorityQueue<User> tempQueue = new PriorityQueue<>(leaderboard);
        int rank = 1;
        
        while (!tempQueue.isEmpty()) {
            User user = tempQueue.poll();
            System.out.printf("%d. %s --- %d%n", rank++, user.getUsername(), user.getScore());
        }
        System.out.println("-".repeat(30));
    }

    public User getUser(String username) {
        return userDatabase.get(username);
    }

    public Map<String, User> getUserDatabase() {
        return userDatabase;
    }

    public void addUser(User user) {
        userDatabase.put(user.getUsername(), user);
        leaderboard.offer(user);
        saveLeaderboard();
    }

    private void loadLeaderboard() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LEADERBOARD_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    String username = parts[0];
                    String encryptedPassword = parts[1];
                    int score = Integer.parseInt(parts[2]);
                    String salt = parts[3];
                    
                    User user = new User(username, encryptedPassword, score, salt);
                    userDatabase.put(username, user);
                    leaderboard.offer(user);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.err.println("Error loading leaderboard: " + e.getMessage());
        }
    }

    private void saveLeaderboard() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LEADERBOARD_FILE))) {
            for (User user : userDatabase.values()) {
                writer.write(String.format("%s|%s|%d|%s%n", 
                    user.getUsername(), 
                    user.getEncryptedPassword(), 
                    user.getScore(), 
                    user.getSalt()));
            }
        } catch (IOException e) {
            System.err.println("Error saving leaderboard: " + e.getMessage());
        }
    }
} 