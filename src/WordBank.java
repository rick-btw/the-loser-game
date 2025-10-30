import java.io.*;
import java.util.*;

public class WordBank {
    private static WordBank instance;
    private Set<String> words;
    private static final String WORDS_FILE = "words.txt";
    private Random random;

    private WordBank() {
        this.words = new HashSet<>();
        this.random = new Random();
        loadWords();
    }

    public static WordBank getInstance() {
        if (instance == null) {
            instance = new WordBank();
        }
        return instance;
    }

    public void addWord(String word) throws DuplicateWordException {
        if (words.contains(word.toLowerCase())) {
            throw new DuplicateWordException("Word '" + word + "' already exists");
        }
        
        words.add(word.toLowerCase());
        saveWords();
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("No words available in the word bank");
        }
        
        List<String> wordList = new ArrayList<>(words);
        return wordList.get(random.nextInt(wordList.size()));
    }

    public boolean isEmpty() {
        return words.isEmpty();
    }

    public int size() {
        return words.size();
    }

    private void loadWords() {
        try (BufferedReader reader = new BufferedReader(new FileReader(WORDS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (!word.isEmpty()) {
                    words.add(word);
                }
            }
        } catch (FileNotFoundException e) {
            createDefaultWordsFile();
        } catch (IOException e) {
            System.err.println("Error loading words: " + e.getMessage());
        }
    }

    private void saveWords() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(WORDS_FILE))) {
            for (String word : words) {
                writer.write(word);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving words: " + e.getMessage());
        }
    }

    private void createDefaultWordsFile() {
        List<String> defaultWords = Arrays.asList(
            "pizza", "computer", "programming", "java", "algorithm",
            "database", "network", "security", "encryption", "authentication",
            "framework", "library", "interface", "implementation", "architecture",
            "development", "testing", "deployment", "maintenance", "optimization",
            "documentation", "version", "control", "repository", "collaboration",
            "innovation", "technology", "software", "hardware", "system"
        );
        
        words.addAll(defaultWords);
        saveWords();
    }
} 