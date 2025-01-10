/**
 * Codebreaker Game
 * This program implements a Codebreaker game with two modes:
 * 1. The computer sets a secret code, and the user guesses.
 * 2. The user sets a secret code, and the computer guesses based on feedback.
 * The game includes a maximum of 10 guesses and uses the colours 'G', 'R', 'B', 'Y', 'O', 'P'.
 */

import java.util.*;

public class Codebreaker {

    // Constants
    private static final String VALID_CHARS = "GRBYOP";
    private static final int CODE_LENGTH = 4;
    private static final int MAX_TRIES = 10;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Codebreaker!");
        System.out.println("Choose a mode:");
        System.out.println("1. Computer sets the code, and you guess.");
        System.out.println("2. You set the code, and the computer guesses.");

        int mode = getValidMode(scanner);

        if (mode == 1) {
            playUserGuessesMode(scanner);
        } else if (mode == 2) {
            playComputerGuessesMode(scanner);
        }

        scanner.close();
    }

    /**
     * Ensures the user selects a valid mode (1 or 2).
     */
    private static int getValidMode(Scanner scanner) {
        int mode = -1;
        while (mode != 1 && mode != 2) {
            System.out.print("Enter 1 or 2: ");
            if (scanner.hasNextInt()) {
                mode = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } else {
                scanner.nextLine(); // Clear invalid input
                System.out.println("Invalid input. Please enter 1 or 2.");
            }
        }
        return mode;
    }

    /**
     * Mode where the computer sets the code, and the user guesses.
     */
    private static void playUserGuessesMode(Scanner scanner) {
        char[] secretCode = generateRandomCode(VALID_CHARS, CODE_LENGTH);
        System.out.println("The computer has set a secret code.");

        for (int attempt = 1; attempt <= MAX_TRIES; attempt++) {
            System.out.printf("Attempt %d/%d: Please enter your guess of length %d using the letters %s: ",
                    attempt, MAX_TRIES, CODE_LENGTH, VALID_CHARS);
            String guess = getValidGuess(scanner);

            char[] guessArray = guess.toCharArray();
            String feedback = getFeedback(secretCode, guessArray);

            System.out.printf("Guess: %s\tClues: %s\n", guess, feedback);

            if (feedback.equals("bbbb")) {
                System.out.printf("Congratulations! You guessed the code in %d attempts!\n", attempt);
                return;
            }
        }

        System.out.println("I'm sorry you lost. The correct code was: " + Arrays.toString(secretCode));
    }

    /**
     * Mode where the user sets the code, and the computer guesses.
     */
    private static void playComputerGuessesMode(Scanner scanner) {
        System.out.printf("Please set a secret code of length %d using the letters %s: ", CODE_LENGTH, VALID_CHARS);
        String userCode = getValidGuess(scanner);

        char[] secretCode = userCode.toCharArray();
        Set<String> allPossibleCodes = generateAllPossibleCodes(VALID_CHARS, CODE_LENGTH);

        for (int attempt = 1; attempt <= MAX_TRIES; attempt++) {
            String computerGuess = allPossibleCodes.iterator().next();
            System.out.printf("Attempt %d/%d: Computer guesses: %s\n", attempt, MAX_TRIES, computerGuess);

            System.out.print("Provide feedback (e.g., 'bbww' for 2 black and 2 white pegs): ");
            String feedback = getValidFeedback(scanner, CODE_LENGTH);

            if (feedback.equals("bbbb")) {
                System.out.printf("The computer guessed your code in %d attempts!\n", attempt);
                return;
            }

            allPossibleCodes.removeIf(code -> !getFeedback(code.toCharArray(), computerGuess.toCharArray()).equals(feedback));
        }

        System.out.println("The computer couldn't guess your code. Please reveal the code.");
    }

    /**
     * Generates a random code of the specified length using the given valid characters.
     */
    private static char[] generateRandomCode(String validChars, int length) {
        Random random = new Random();
        char[] code = new char[length];

        for (int i = 0; i < length; i++) {
            code[i] = validChars.charAt(random.nextInt(validChars.length()));
        }

        return code;
    }

    /**
     * Ensures the guess is valid and prompts the user until it is.
     */
    private static String getValidGuess(Scanner scanner) {
        String guess;
        while (true) {
            guess = scanner.nextLine().toUpperCase();
            if (isValidGuess(guess)) {
                return guess;
            }
            System.out.printf("Invalid guess. Please enter a guess of length %d using the letters %s: ",
                    CODE_LENGTH, VALID_CHARS);
        }
    }

    /**
     * Ensures the feedback is valid and prompts the user until it is.
     */
    private static String getValidFeedback(Scanner scanner, int length) {
        String feedback;
        while (true) {
            feedback = scanner.nextLine().toLowerCase();
            if (isValidFeedback(feedback, length)) {
                return feedback;
            }
            System.out.printf("Invalid feedback. Please provide feedback using 'b' and 'w' of length up to %d: ", length);
        }
    }

    /**
     * Validates whether the guess is of the correct length and contains only valid characters.
     */
    private static boolean isValidGuess(String guess) {
        if (guess.length() != CODE_LENGTH) {
            return false;
        }

        for (char c : guess.toCharArray()) {
            if (VALID_CHARS.indexOf(c) == -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates whether the feedback is valid (contains only 'b' and 'w' and is not too long).
     */
    private static boolean isValidFeedback(String feedback, int maxLength) {
        if (feedback.length() > maxLength) {
            return false;
        }

        for (char c : feedback.toCharArray()) {
            if (c != 'b' && c != 'w') {
                return false;
            }
        }

        return true;
    }

    /**
     * Provides feedback for a guess compared to the secret code.
     * Returns a string with 'b' for each correct position and 'w' for correct colour but wrong position.
     */
    private static String getFeedback(char[] secretCode, char[] guess) {
        int black = 0, white = 0;
        boolean[] codeUsed = new boolean[secretCode.length];
        boolean[] guessUsed = new boolean[guess.length];

        // Count black pegs
        for (int i = 0; i < secretCode.length; i++) {
            if (secretCode[i] == guess[i]) {
                black++;
                codeUsed[i] = true;
                guessUsed[i] = true;
            }
        }

        // Count white pegs
        for (int i = 0; i < secretCode.length; i++) {
            if (codeUsed[i]) continue;
            for (int j = 0; j < guess.length; j++) {
                if (!guessUsed[j] && secretCode[i] == guess[j]) {
                    white++;
                    guessUsed[j] = true;
                    break;
                }
            }
        }

        return "b".repeat(black) + "w".repeat(white);
    }

    /**
     * Generates all possible codes of the given length using the valid characters.
     */
    private static Set<String> generateAllPossibleCodes(String validChars, int length) {
        Set<String> codes = new HashSet<>();
        generateAllCodesRecursive("", validChars, length, codes);
        return codes;
    }

    /**
     * Recursive helper method to generate all possible codes.
     */
    private static void generateAllCodesRecursive(String prefix, String validChars, int length, Set<String> codes) {
        if (prefix.length() == length) {
            codes.add(prefix);
            return;
        }

        for (char c : validChars.toCharArray()) {
            generateAllCodesRecursive(prefix + c, validChars, length, codes);
        }
    }
}
