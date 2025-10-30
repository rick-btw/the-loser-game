# 🎮 The Loser Game

A Java implementation of a word guessing game with user authentication, scoring system, and leaderboard functionality.

## 🎯 Game Rules

- A random word is selected from the word bank
- Player guesses letters one by one
- ✅ **Correct guess**: Letter positions are revealed
- ❌ **Wrong guess**: One letter of "LOSER" is added
- **Winning**: Guess all letters before "LOSER" is fully formed
- **Losing**: "LOSER" is fully formed before guessing the word

### Attempt Limits
- **Words ≤ 8 letters**: 5 mistakes allowed
- **Words > 8 letters**: 10 mistakes allowed (every 2 mistakes = one letter of LOSER)

## 🚀 How to Run

### Prerequisites
- Java 8 or higher
- No additional dependencies required

### Compilation and Execution
```bash
# Navigate to the src directory
cd src

# Compile all Java files
javac *.java

# Run the game
java Main
```

## 🎮 Game Features

### User System
- **Signup**: Create account with username and password
- **Login**: Authenticate with existing credentials
- **Password Requirements**: 
  - At least 8 characters
  - Contains lowercase & uppercase letters
  - Contains digits and special characters
- **Password Encryption**: SHA-256 with salt

### Game Features
- **ASCII Art Title**: Beautiful game title display
- **Random Word Selection**: From predefined word bank
- **Hint System**: Use points to reveal a random letter (once per game)
- **Score System**: Points = word length
- **LOSER Progress**: Visual display of mistake accumulation with hangman art
- **Visual Hangman**: ASCII art representation of the game state with enhanced final "LOSER" display

### Leaderboard
- **Automatic Sorting**: Descending order by score
- **Persistent Storage**: Saved between sessions
- **Real-time Updates**: After each game

### Word Management
- **Add New Words**: Users can contribute to the word bank
- **Duplicate Prevention**: Automatic checking for existing words
- **Default Words**: 30 programming-related words included

## 🏗 Architecture

### OOP Principles
- **Encapsulation**: Private fields with getters/setters
- **Singleton Pattern**: WordBank, Leaderboard, AuthService
- **Custom Exceptions**: InvalidPasswordException, DuplicateWordException

### Required Collections
1. **Set<Character>**: Manage guessed letters (O(1) duplicate checking)
2. **Map<String, User>**: User database (O(1) login operations)
3. **PriorityQueue<User>**: Leaderboard (automatic descending order)
4. **List<String>**: LOSER status display (V/X indicators)

### Classes
- **Main**: Entry point that delegates to Menu
- **Menu**: Handles all menu functionality and user interactions
- **User**: User data and authentication
- **Game**: Core game logic and state management
- **WordBank**: Word collection and random selection
- **Leaderboard**: Score tracking and display
- **AuthService**: User authentication and registration

## 📁 File Structure
```
src/
├── Main.java              # Entry point (delegates to Menu)
├── Menu.java              # Menu system and user interactions
├── User.java              # User class with encryption
├── Game.java              # Game logic and mechanics
├── WordBank.java          # Word management (Singleton)
├── Leaderboard.java       # Score tracking (Singleton)
├── AuthService.java       # Authentication (Singleton)
├── InvalidPasswordException.java
└── DuplicateWordException.java

Generated Files:
├── words.txt              # Word bank (auto-created)
├── leaderboard.txt        # User data and scores
└── *.class               # Compiled Java bytecode
```

## 🎯 Sample Game Flow

1. **Start**: Run `java Main`
2. **Signup**: Create account with valid credentials
3. **Login**: Authenticate with your account
4. **Start Game**: Choose option 1
5. **Play**: Guess letters or use hints
6. **Score**: Win to earn points based on word length
7. **Leaderboard**: View rankings
8. **Add Words**: Contribute to the word bank

## 🔧 Customization

### Adding Default Words
Edit the `createDefaultWordsFile()` method in `WordBank.java` to add more default words.

### Changing Game Rules
Modify the attempt limits in `Game.java`:
- `getMaxAttempts()` method for different word length thresholds
- `updateAttemptStatus()` for LOSER progression logic

### Password Requirements
Update the `isValidPassword()` method in `User.java` to change password complexity rules.

## 🐛 Troubleshooting

### Common Issues
- **"No words available"**: Add words via the menu or check `words.txt` file
- **"User not found"**: Create account first via signup
- **"Invalid password"**: Ensure password meets complexity requirements

### File Permissions
Ensure the application has read/write permissions in the directory for:
- `words.txt`
- `leaderboard.txt`

## 🎉 Enjoy the Game!

The Loser Game combines word guessing with strategic thinking and user management. Challenge yourself to guess words efficiently while managing your limited attempts! 
