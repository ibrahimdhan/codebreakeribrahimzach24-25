/**
 * Codebreaker Game: A game where either the user or the computer guesses a secret code.
 * This is the non-GUI version of the game.
 */
import java.util.*;

public class Codebreaker {

    /** Valid colors that can be used in the code. */
    private static final String VALID_COLORS = "GRBYOP";
   
    /** Length of the secret code. */
    private static final int CODE_LENGTH = 4;
   
    /** Maximum number of guesses allowed. */
    private static final int MAX_GUESSES = 10;

    /**
     * Main method to start the game.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Codebreaker!");
        int mode = 0;

        while (mode != 1 && mode != 2) {
            System.out.println("Choose a mode:");
            System.out.println("1. Computer sets the code, user guesses.");
            System.out.println("2. User sets the code, computer guesses.");
            System.out.print("Enter your choice (1 or 2): ");

            if (scanner.hasNextInt()) {
                mode = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (mode != 1 && mode != 2) {
                    System.out.println("Invalid choice. Please select 1 or 2.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume invalid input
            }
        }

        if (mode == 1) {
            playUserGuessesMode(scanner);
        } else {
            playComputerGuessesMode(scanner);
        }
    }

    /**
     * Handles the mode where the user guesses the secret code set by the computer.
     * @param scanner Scanner for user input.
     */
    private static void playUserGuessesMode(Scanner scanner) {
        String[] secretCode = createCode(VALID_COLORS, CODE_LENGTH);
        List<String> guesses = new ArrayList<>();
        List<String> clues = new ArrayList<>();

        System.out.println("The code has been set. Try to guess it!");

        for (int guessNumber = 1; guessNumber <= MAX_GUESSES; guessNumber++) {
            System.out.print("Enter your guess of length " + CODE_LENGTH + " using " + VALID_COLORS + ": ");
            String userGuess = scanner.nextLine().toUpperCase();

            while (!isValidGuess(userGuess, CODE_LENGTH, VALID_COLORS)) {
                System.out.print("Invalid guess. Try again: ");
                userGuess = scanner.nextLine().toUpperCase();
            }

            int correctPositions = findFullyCorrect(secretCode, userGuess);
            int correctColors = findCorrectColor(secretCode, userGuess);

            String clue = "b".repeat(correctPositions) + "w".repeat(correctColors - correctPositions);
            guesses.add(userGuess);
            clues.add(clue);

            displayGameState(guesses, clues);

            if (correctPositions == CODE_LENGTH) {
                System.out.println("Congratulations! You guessed the code in " + guessNumber + " guesses.");
                return;
            }
        }

        System.out.println("I'm sorry, you lose. The correct code was: " + String.join(" ", secretCode));
    }

    /**
     * Handles the mode where the computer guesses the secret code set by the user.
     * @param scanner Scanner for user input.
     */
    private static void playComputerGuessesMode(Scanner scanner) {
        String userCode = "";

        while (!isValidGuess(userCode, CODE_LENGTH, VALID_COLORS)) {
            System.out.print("Please set a secret code of length " + CODE_LENGTH + " using " + VALID_COLORS + ": ");
            userCode = scanner.nextLine().toUpperCase();
            if (!isValidGuess(userCode, CODE_LENGTH, VALID_COLORS)) {
                System.out.println("Invalid code. Please try again.");
            }
        }

        System.out.println("The computer will now try to guess your code.");

        List<String> possibleGuesses = generateAllCombinations(VALID_COLORS, CODE_LENGTH);
        List<String> clues = new ArrayList<>();

        for (int guessNumber = 1; guessNumber <= MAX_GUESSES; guessNumber++) {
            String computerGuess = possibleGuesses.get(0);
            System.out.println("Computer's guess: " + computerGuess);

            int correctPositions = findFullyCorrect(userCode.split(""), computerGuess);
            int correctColors = findCorrectColor(userCode.split(""), computerGuess);

            if (correctPositions == CODE_LENGTH) {
                System.out.println("The computer guessed your code in " + guessNumber + " guesses!");
                return;
            }

            final String lastGuess = computerGuess;
            possibleGuesses.removeIf(guess -> !matchesFeedback(lastGuess, guess, correctPositions, correctColors));
        }

        System.out.println("The computer could not guess your code. You win!");
    }

    /**
     * Generates a random secret code.
     * @param colors The valid characters/colors for the code.
     * @param length The length of the code.
     * @return An array representing the secret code.
     */
    private static String[] createCode(String colors, int length) {
        Random random = new Random();
        String[] code = new String[length];
        for (int i = 0; i < length; i++) {
            code[i] = String.valueOf(colors.charAt(random.nextInt(colors.length())));
        }
        return code;
    }

    /**
     * Validates a user's guess.
     * @param guess The user's guess.
     * @param length The required length of the guess.
     * @param validColors The valid characters/colors for the guess.
     * @return True if the guess is valid, false otherwise.
     */
    private static boolean isValidGuess(String guess, int length, String validColors) {
        if (guess.length() != length) return false;
        for (char c : guess.toCharArray()) {
            if (validColors.indexOf(c) == -1) return false;
        }
        return true;
    }

    /**
     * Finds the number of correct positions in the user's guess.
     * @param code The secret code.
     * @param guess The user's guess.
     * @return The number of characters in the correct positions.
     */
    private static int findFullyCorrect(String[] code, String guess) {
        int count = 0;
        for (int i = 0; i < code.length; i++) {
            if (code[i].equals(String.valueOf(guess.charAt(i)))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Finds the number of correct colors in the user's guess (ignoring position).
     * @param code The secret code.
     * @param guess The user's guess.
     * @return The number of correct colors.
     */
    private static int findCorrectColor(String[] code, String guess) {
        List<String> codeList = new ArrayList<>(Arrays.asList(code));
        int count = 0;
        for (char c : guess.toCharArray()) {
            if (codeList.contains(String.valueOf(c))) {
                count++;
                codeList.remove(String.valueOf(c));
            }
        }
        return count;
    }

    /**
     * Displays the current game state with all guesses and clues.
     * @param guesses List of user guesses.
     * @param clues List of clues corresponding to each guess.
     */
    private static void displayGameState(List<String> guesses, List<String> clues) {
        System.out.println("Guess\tClues");
        for (int i = 0; i < guesses.size(); i++) {
            System.out.println(guesses.get(i) + "\t" + clues.get(i));
        }
    }

    /**
     * Generates all possible combinations of colors of a given length.
     * @param colors The valid characters/colors.
     * @param length The length of each combination.
     * @return A list of all possible combinations.
     */
    private static List<String> generateAllCombinations(String colors, int length) {
        List<String> combinations = new ArrayList<>();
        generateCombinationsRecursive("", colors, length, combinations);
        return combinations;
    }

    /**
     * Recursively generates combinations of colors.
     * @param prefix The current combination prefix.
     * @param colors The valid characters/colors.
     * @param length The length of each combination.
     * @param combinations The list to store all combinations.
     */
    private static void generateCombinationsRecursive(String prefix, String colors, int length, List<String> combinations) {
        if (prefix.length() == length) {
            combinations.add(prefix);
            return;
        }
        for (char c : colors.toCharArray()) {
            generateCombinationsRecursive(prefix + c, colors, length, combinations);
        }
    }

    /**
     * Checks if a candidate guess matches the feedback from the last guess.
     * @param guess The last guess made.
     * @param candidate A possible candidate guess.
     * @param correctPositions The number of correct positions in the feedback.
     * @param correctColors The number of correct colors in the feedback.
     * @return True if the candidate matches the feedback, false otherwise.
     */
    private static boolean matchesFeedback(String guess, String candidate, int correctPositions, int correctColors) {
        int positions = findFullyCorrect(candidate.split(""), guess);
        int colors = findCorrectColor(candidate.split(""), guess);
        return positions == correctPositions && colors == correctColors;
    }
}
