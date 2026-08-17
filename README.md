```
+-------------------------------------------------------------+
|                                                             |
|                       WELCOME TO THE                        |
|                                                             |
|    _                            ____                        |
|   | |    ___  ___  ___ _ __   / ___| __ _ _ __ ___   ___    |
|   | |   / _ \/ __|/ _ \ '__| | |  _ / _' | '_ ' _ \ / _ \   |
|   | |__| (_) \__ \  __/ |    | |_| | (_| | | | | | |  __/   |
|   |_____\___/|___/\___|_|     \____|\__,_|_| |_| |_|\___|   |
|                                                             |
|                         by Amirali                          |
+-------------------------------------------------------------+
 \\________________________________________________________\\
  \\______________________________________________________\\
```

A console-based word guessing game written in Java. Players sign in to a
persistent account, guess letters against a randomly selected word, and compete
for position on a shared leaderboard. Each wrong guess spells out one more
letter of the word "LOSER" — spell it completely and the game is over.

The project was built as an AP Computer Science final project and is written in
plain Java with no external dependencies.

---

## Contents

- [Requirements](#requirements)
- [Building and Running](#building-and-running)
- [Gameplay](#gameplay)
- [Features](#features)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Data Files](#data-files)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)

---

## Requirements

- **Java 11 or later.** The source uses `String.repeat(int)`, which was
  introduced in Java 11 and will not compile on Java 8.
- No third-party libraries, build tools, or package managers are required.

---

## Building and Running

Run both commands from the **repository root**, not from inside `src/`:

```bash
javac -d out src/*.java
```

```bash
java -cp out Main
```

The working directory matters. `words.txt` and `leaderboard.txt` are resolved
relative to wherever the program is launched from, so starting the game from a
different directory will cause it to create a fresh, empty set of data files
instead of loading the existing ones.

Compiled classes are written to `out/`, which is excluded from version control.

---

## Gameplay

### Objective

A word is drawn at random from the word bank and displayed as a row of blanks.
Guess the word one letter at a time before your mistakes spell out "LOSER".

### Turn Structure

At each prompt, enter either a single letter or the word `hint`.

- **Correct guess** — every occurrence of that letter is revealed in place.
- **Incorrect guess** — one more letter of "LOSER" is filled in, and the
  accompanying ASCII art advances to the next stage.

Repeating a letter you have already guessed costs nothing; the game rejects the
input and reprints your used letters.

### Mistake Allowance

The number of mistakes you are permitted scales with the length of the word:

| Word length     | Mistakes allowed | Mistakes per "LOSER" letter |
| --------------- | ---------------- | --------------------------- |
| 8 letters or fewer | 5             | 1                           |
| More than 8 letters | 10           | 2                           |

Longer words therefore grant a larger margin for error while still requiring the
same five-stage progression to lose.

### Hints

Typing `hint` reveals one random unguessed letter from the word.

- Costs **1 point**, deducted from your score.
- Limited to **one hint per game**.
- Unavailable if your score is zero.

### Scoring

Winning a round awards points equal to the length of the word. Losing awards
nothing. Your total score is what ranks you on the leaderboard.

---

## Features

### Accounts and Authentication

Players sign up with a username and password before playing. Accounts persist
between sessions.

- Usernames must be at least 3 characters and unique.
- Passwords are stored as **SHA-256 hashes with a unique per-user salt**
  generated from `SecureRandom`. Plaintext passwords are never written to disk.
- Passwords may be changed from the user menu after confirming the current one.

Password requirements — a password must be at least 8 characters and contain
all of the following:

- a lowercase letter
- an uppercase letter
- a digit
- a special character from `@!#$%^&*()_+-=[]{}|;:,.<>?`

### Word Bank

Words are loaded from `words.txt` at startup. If the file does not exist, it is
generated with a default set of 30 technology-related words. Players can
contribute new words from the menu; duplicates are rejected via a custom
`DuplicateWordException`.

### Leaderboard

Scores are tracked in a priority queue ordered by score descending, so rankings
stay sorted without an explicit sort step. The leaderboard is written to disk
after a score change and reloaded on the next launch.

### Administrative Tools

An additional menu is available to the account with the username `admin`,
offering a backup utility that writes a timestamped ZIP archive containing a
generated `users.csv` export alongside copies of `words.txt` and
`leaderboard.txt`.

---

## Project Structure

```
.
├── src/
│   ├── Main.java                      Entry point; delegates to Menu
│   ├── Menu.java                      Menu system, user interaction, admin tools
│   ├── Game.java                      Round logic, LOSER progression, ASCII art
│   ├── User.java                      User model, hashing, password rules
│   ├── AuthService.java               Signup and login (singleton)
│   ├── WordBank.java                  Word storage and selection (singleton)
│   ├── Leaderboard.java               Score tracking and persistence (singleton)
│   ├── InvalidPasswordException.java  Thrown on password rule violations
│   └── DuplicateWordException.java    Thrown when adding an existing word
├── words.txt                          Word bank, one word per line
├── leaderboard.txt                    Account records and scores
└── README.md
```

---

## Architecture

### Object-Oriented Design

- **Encapsulation** — all model state is private and exposed through accessors.
- **Singleton pattern** — `WordBank`, `Leaderboard`, and `AuthService` each
  expose a single shared instance via `getInstance()`, ensuring one authoritative
  copy of the word list and user database at runtime.
- **Custom exceptions** — `InvalidPasswordException` and `DuplicateWordException`
  give validation failures meaningful types rather than generic errors.
- **Separation of concerns** — `Main` holds no logic, `Menu` owns presentation
  and input, and the game and data classes own behavior and state.

### Collections

| Collection            | Used for                | Rationale                                |
| --------------------- | ----------------------- | ---------------------------------------- |
| `Set<Character>`      | Guessed letters         | Constant-time duplicate-guess checking   |
| `Map<String, User>`   | User database           | Constant-time lookup by username on login |
| `PriorityQueue<User>` | Leaderboard             | Maintains descending score order automatically |
| `List<String>`        | LOSER progress display  | Ordered, index-addressable status markers |

---

## Data Files

Both files are plain text and are created automatically if missing.

**`words.txt`** — one lowercase word per line.

**`leaderboard.txt`** — one account per line, pipe-delimited:

```
username|passwordHash|score|salt
```

Because this file stores credentials, it should not be shared. Deleting it
resets all accounts and scores.

---

## Configuration

| To change                | Edit                                                    |
| ------------------------ | ------------------------------------------------------- |
| Default word list        | `createDefaultWordsFile()` in `WordBank.java`           |
| Mistake allowance        | `getMaxAttempts()` in `Game.java`                       |
| LOSER progression rate   | `updateAttemptStatus()` in `Game.java`                  |
| Password complexity rules| `isValidPassword()` in `User.java` and `AuthService.java` |
| Hint cost                | `useHint()` in `User.java`                              |

---

## Troubleshooting

**"No words available."**
The word bank is empty. Add a word from the menu, or confirm that `words.txt`
exists in the directory you launched the program from and is not empty.

**"User not found."**
No account exists under that username. Create one with the signup option. Note
that usernames are case-sensitive.

**"Invalid password."**
The password does not satisfy every complexity requirement listed above. Note
that a single character only counts toward one category.

**Scores or accounts appear to have been lost.**
The program was most likely launched from a different working directory and
created a new set of data files there. Confirm you are running from the
repository root and check for stray `words.txt` or `leaderboard.txt` files
elsewhere in the project.

**Changes cannot be saved.**
The program requires read and write permission in its working directory in
order to update `words.txt` and `leaderboard.txt`.
