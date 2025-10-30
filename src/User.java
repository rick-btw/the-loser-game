import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class User {
    private String username;
    private String encryptedPassword;
    private int score;
    private String salt;

    public User(String username, String password) {
        this.username = username;
        this.salt = generateSalt();
        this.encryptedPassword = encryptPassword(password, this.salt);
        this.score = 0;
    }

    public User(String username, String encryptedPassword, int score, String salt) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.score = score;
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void changePassword(String oldPassword, String newPassword) throws InvalidPasswordException {
        if (!verifyPassword(oldPassword)) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
        
        if (!isValidPassword(newPassword)) {
            throw new InvalidPasswordException("New password does not meet requirements");
        }
        
        this.salt = generateSalt();
        this.encryptedPassword = encryptPassword(newPassword, this.salt);
    }

    public void addScore(int wordLength) {
        this.score += wordLength;
    }

    public boolean useHint() {
        if (this.score > 0) {
            this.score--;
            return true;
        }
        return false;
    }

    public boolean verifyPassword(String password) {
        String hashedPassword = encryptPassword(password, this.salt);
        return hashedPassword.equals(this.encryptedPassword);
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String encryptPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + salt;
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
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

    @Override
    public String toString() {
        return username + " --- " + score;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
} 