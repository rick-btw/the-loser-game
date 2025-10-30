import java.util.Map;

public class AuthService {
    private static AuthService instance;
    private Map<String, User> userDatabase;

    private AuthService() {
        this.userDatabase = Leaderboard.getInstance().getUserDatabase();
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public User signup(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
        
        if (userDatabase.containsKey(username)) {
            throw new IllegalArgumentException("Username '" + username + "' already exists");
        }
        
        if (!isValidPassword(password)) {
            throw new InvalidPasswordException("Password must be at least 8 characters and contain lowercase, uppercase, digit, and special character");
        }
        
        User newUser = new User(username, password);
        
        Leaderboard.getInstance().addUser(newUser);
        
        return newUser;
    }

    public User login(String username, String password) throws Exception {
        User user = userDatabase.get(username);
        
        if (user == null) {
            throw new IllegalArgumentException("User '" + username + "' not found");
        }
        
        if (!user.verifyPassword(password)) {
            throw new InvalidPasswordException("Incorrect password");
        }
        
        return user;
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;
        String specialChars = "@!#$%^&*()_+-=[]{}|;:,.<>?";
        
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (specialChars.indexOf(c) != -1) hasSpecial = true;
        }
        
        return hasLower && hasUpper && hasDigit && hasSpecial;
    }
} 