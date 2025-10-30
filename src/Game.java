import java.util.*;

public class Game {
    private String secretWord;
    private Set<Character> guessedLetters;
    private int remainingAttempts;
    private User currentUser;
    private boolean hintUsed;
    private List<String> attemptStatus;
    private static final String LOSER = "LOSER";

    public Game() {
        this.guessedLetters = new HashSet<>();
        this.attemptStatus = new ArrayList<>(Collections.nCopies(5, "V"));
        this.hintUsed = false;
    }

    public void startGame(User user) {
        this.currentUser = user;
        this.secretWord = WordBank.getInstance().getRandomWord();
        this.guessedLetters.clear();
        this.hintUsed = false;
        
        if (secretWord.length() <= 8) {
            this.remainingAttempts = 5;
        } else {
            this.remainingAttempts = 10;
        }
        
        System.out.println("\n🎮 Starting new game!");
        System.out.println("Word length: " + secretWord.length() + " letters");
        System.out.println("Allowed mistakes: " + remainingAttempts);
        
        playGame();
    }

    private void playGame() {
        Scanner scanner = new Scanner(System.in);
        
        while (remainingAttempts > 0 && !isWordGuessed()) {
            displayGameState();
            System.out.print("\nEnter a letter (or 'hint' for a hint): ");
            String input = scanner.nextLine().toLowerCase().trim();
            
            if (input.equals("hint")) {
                useHint();
            } else if (input.length() == 1) {
                guessLetter(input.charAt(0));
            } else {
                System.out.println("❌ Please enter a single letter or 'hint'");
            }
        }
        
        endGame();
    }

    public void guessLetter(char letter) {
        if (!Character.isLetter(letter)) {
            System.out.println("❌ Please enter a valid letter (a-z)");
            return;
        }
        
        if (guessedLetters.contains(letter)) {
            System.out.println("❌ Letter '" + letter + "' has already been guessed!");
            System.out.println("Used letters: " + getUsedLettersString());
            return;
        }
        
        guessedLetters.add(letter);
        
        if (secretWord.indexOf(letter) != -1) {
            System.out.println("✅ Correct! Letter '" + letter + "' is in the word.");
        } else {
            System.out.println("❌ Wrong! Letter '" + letter + "' is not in the word.");
            remainingAttempts--;
            updateAttemptStatus();
        }
    }

    private void useHint() {
        if (hintUsed) {
            System.out.println("❌ Hint already used in this game!");
            return;
        }
        
        if (!currentUser.useHint()) {
            System.out.println("❌ Not enough points for hint (costs 1 point)");
            return;
        }
        
        List<Character> unguessedLetters = new ArrayList<>();
        for (char c : secretWord.toCharArray()) {
            if (!guessedLetters.contains(c)) {
                unguessedLetters.add(c);
            }
        }
        
        if (!unguessedLetters.isEmpty()) {
            char hintLetter = unguessedLetters.get(new Random().nextInt(unguessedLetters.size()));
            System.out.println("💡 Hint: The letter '" + hintLetter + "' is in the word!");
            guessedLetters.add(hintLetter);
            hintUsed = true;
        } else {
            System.out.println("💡 No more letters to reveal!");
        }
    }

    private void updateAttemptStatus() {
        int mistakes = getMaxAttempts() - remainingAttempts;
        int loserIndex = (secretWord.length() <= 8) ? mistakes - 1 : (mistakes - 1) / 2;
        
        if (loserIndex >= 0 && loserIndex < 5) {
            attemptStatus.set(loserIndex, "X");
        }
    }

    private int getMaxAttempts() {
        return secretWord.length() <= 8 ? 5 : 10;
    }

    public void displayWordState() {
        StringBuilder display = new StringBuilder();
        for (char c : secretWord.toCharArray()) {
            if (guessedLetters.contains(c)) {
                display.append(c).append(" ");
            } else {
                display.append("_ ");
            }
        }
        System.out.println("Word: " + display.toString().trim());
    }

    private void displayGameState() {
        System.out.println("\n" + "=".repeat(50));
        displayWordState();
        System.out.println("Remaining attempts: " + remainingAttempts);
        System.out.println("Used letters: " + getUsedLettersString());
        displayLoserStatus();
    }

    private void displayLoserStatus() {
        System.out.println("LOSER status:");
        for (int i = 0; i < 5; i++) {
            System.out.print(LOSER.charAt(i) + ":" + attemptStatus.get(i) + " ");
        }
        System.out.println();
        
        displayHangman();
    }
    
    private void displayHangman() {
        int mistakes = getMaxAttempts() - remainingAttempts;
        int loserIndex = (secretWord.length() <= 8) ? mistakes : mistakes / 2;
        
        System.out.println();
        if (loserIndex == 0) {
            System.out.println("     __");
            System.out.println("     \\ \\");
            System.out.println(" _____\\ \\");
            System.out.println("|_____/ /");
            System.out.println("     /_/");
        } else if (loserIndex == 1) {
            System.out.println("     __    _ ");
            System.out.println("     \\ \\  | |");
            System.out.println(" _____\\ \\ | |");
            System.out.println("|_____/ / | |__");
            System.out.println("     /_/  |_____");
        } else if (loserIndex == 2) {
            System.out.println("     __    _ ");
            System.out.println("     \\ \\  | |    ___");
            System.out.println(" _____\\ \\ | |   / _ \\");
            System.out.println("|_____/ / | |__| (_) \\");
            System.out.println("     /_/  |_____\\___/");
        } else if (loserIndex == 3) {
            System.out.println("     __    _ ");
            System.out.println("     \\ \\  | |    ___  ___");
            System.out.println(" _____\\ \\ | |   / _ \\/ __|");
            System.out.println("|_____/ / | |__| (_) \\__ \\");
            System.out.println("     /_/  |_____\\___/|___/");
        } else if (loserIndex == 4) {
            System.out.println("     __    _ ");
            System.out.println("     \\ \\  | |    ___  ___  ___ ");
            System.out.println(" _____\\ \\ | |   / _ \\/ __|/ _ \\");
            System.out.println("|_____/ / | |__| (_) \\__ \\  __/");
            System.out.println("     /_/  |_____\\___/|___/\\___| ");
        } else if (loserIndex >= 5) {
            System.out.println("     __    _ ");
            System.out.println("     \\ \\  | |    ___  ___  ___ _ __ ");
            System.out.println(" _____\\ \\ | |   / _ \\/ __|/ _ \\'__|");
            System.out.println("|_____/ / | |__| (_) \\__ \\  __/ | ");
            System.out.println("     /_/  |_____\\___/|___/\\___|_| ");
        }
        System.out.println();
    }

    private String getUsedLettersString() {
        if (guessedLetters.isEmpty()) {
            return "none";
        }
        List<Character> sortedLetters = new ArrayList<>(guessedLetters);
        Collections.sort(sortedLetters);
        return sortedLetters.toString();
    }

    private boolean isWordGuessed() {
        for (char c : secretWord.toCharArray()) {
            if (!guessedLetters.contains(c)) {
                return false;
            }
        }
        return true;
    }

    private void endGame() {
        System.out.println("\n" + "=".repeat(50));
        
        if (isWordGuessed()) {
            System.out.println("🎉 Congratulations! You won!");
            System.out.println("The word was: " + secretWord);
            int points = secretWord.length();
            currentUser.addScore(points);
            System.out.println("Points earned: " + points);
            System.out.println("Total score: " + currentUser.getScore());
            
            // Update leaderboard
            Leaderboard.getInstance().updateScore(currentUser, points);
        } else {
            System.out.println("💀 Game Over! You lost!");
            System.out.println("The word was: " + secretWord);
        }
        
        System.out.println("=".repeat(50));
    }
} 