/**
 * Codebreaker Game
 * @author Zachary Vandenburg
 * @version 1.1, 01/13/25
 * This program implements a Codebreaker game with two modes:
 * 1. The computer sets a secret code, and the user guesses.
 * 2. The user sets a secret code, and the computer guesses based on feedback.
 * The game includes a maximum of 10 guesses and uses the colours 'G', 'R', 'B', 'Y', 'O', 'P'.
 */

import java.io.*; // import io library
import java.util.*; // import util library

public class Codebreaker {

    // constants
    static final String VALID_CHARS = "GRBYOP"; // valid characters for code
    static final int CODE_LENGTH = 4; // length of secret code
    static final int MAX_TRIES = 10; // max number of guesses/attempts
    
    // main method
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // instantiate bufferedreader object

        // main game loop
        while (true) {
            System.out.println("Welcome to Codebreaker!"); // welcome message
            System.out.println("Choose a mode:"); // prompt user to select mode
            System.out.println("1. Computer sets the code, and you guess.");
            System.out.println("2. You set the code, and the computer guesses.");
            System.out.println("3. View instructions.");
            System.out.println("4. Exit the game.");

            int mode = getValidMode(reader); // call getValidMode method to get valid input

            // execute the chosen mode
            if (mode == 1) {
                playUserGuessesMode(reader); // user guesses the code
            } 
            else if (mode == 2) {
                playComputerGuessesMode(reader); // computer guesses the code
            }
            else if(mode == 3) {
                System.out.println("Codebreaker instructions/rules:");
                System.out.println("1. In the mode where you guess, the computer will set a random secret code that is 4 characters in length.");
                System.out.println("This secret code can be made up of any of the following colours/characters: GRBYOP");
                System.out.println("You will input your guess, and the computer will give you feedback.");
                System.out.println("'b' means that one of your characters is in the correct spot.");
                System.out.println("'w' means that you have the correct character in the wrong spot.");
                System.out.println("You have 10 guesses total to crack the code. Good luck!");
                System.out.println("2. In the mode where the computer guesses, you will set a secret code that is 4 characters in length.");
                System.out.println("This secret code can be made up of any of the following colours/characters: GRBYOP");
                System.out.println("You will give the computer feedback for its guesses. The 'b' and 'w' characters mean the same thing as the previous mode.");
                System.out.println("If the feedback you provide is inconsistent, the game will end.");
                System.out.println("The computer has 3 modes of guessing: easy, medium, and difficult.");
                System.out.println("In easy mode, the computer guesses randomly. In medium mode, the computer uses feedback to guess. In hard mode, the computer chooses the best guess after using the feedback.");
                System.out.println("Have fun!");
            }
            else if (mode == 4) {
                System.out.println("Thank you for playing Codebreaker! Goodbye."); // exit message
                break; // exit the loop to end the program
            }
        }
    }

    /**
     * getValidInput
     * Ensures the user selects a valid mode (1, 2, or 3).
     * uses a loop to repeatedly prompt until valid input is provided
     * @param reader
     * @return mode
     */
    public static int getValidMode(BufferedReader reader) throws IOException {
        while (true) { // infinite loop until valid input
            System.out.print("Enter 1, 2, or 3: ");
            try {
                int mode = Integer.parseInt(reader.readLine()); // parse input as integer
                if (mode >= 1 && mode <= 3) { // check if input is within range
                    return mode; // return valid mode
                }
            } catch (NumberFormatException e) {
            }
            System.out.println("Invalid input. Please enter 1, 2, or 3."); // error message
        }
    }

    /**
     * playUserGuessesMode
     * game mode where the computer sets the code, and the user guesses
     * @param VALID_CHARS
     * @param CODE_LENGTH
     */
    public static void playUserGuessesMode(BufferedReader reader) throws IOException {
        char[] secretCode = generateRandomCode(VALID_CHARS, CODE_LENGTH); // call generateRandomCode method to generate random code
        System.out.println("The computer has set a secret code."); // output

        // loop for user attempts
        for (int attempt = 1; attempt <= MAX_TRIES; attempt++) {
            System.out.println("Attempt " + attempt + "/" + MAX_TRIES + ": Please enter your guess of length " + CODE_LENGTH + " using the letters " + VALID_CHARS + ":");
            String guess = getValidGuess(reader); // call getValidGuess method to get valid user guess

            char[] guessArray = guess.toCharArray(); // convert guess (String) to char array
            String feedback = getFeedback(secretCode, guessArray); // call getFeedback method to return feedback on the user's guess
            System.out.println("Guess: " + guess + "\tClues: " + feedback); // display guess and feedback

            if (feedback.equals("bbbb")) { // check if guess is correct
                System.out.println("Congratulations! You guessed the code in " + attempt + " attempts!");
                return; // end the game if guessed correctly
            }
        }

        System.out.println("Sorry, you lost. The correct code was: " + new String(secretCode)); // game over message
    }

    /**
     * playComputerGuessesMode
     * game mode where the user sets the code, and the computer guesses
     * @param reader
     */
    public static void playComputerGuessesMode(BufferedReader reader) throws IOException {
        // prompt user to choose a difficulty
        System.out.println("Choose AI difficulty level:");
        System.out.println("1. Easy (Random guessing)");
        System.out.println("2. Medium (Feedback-based elimination)");
        System.out.println("3. Hard (Optimized strategy)");
        int difficulty = getValidMode(reader); // call getValidMode to ensure correct mode is chosen
    
        // prompt user to set a secret code
        System.out.println("Please set a secret code of length " + CODE_LENGTH + " using the letters " + VALID_CHARS + ": ");
        String userCode = getValidGuess(reader); // reuse getValidGuess method to get a valid code
    
        char[] secretCode = userCode.toCharArray(); // convert user code (String) to char array
        ArrayList<String> allPossibleCodes = generateAllPossibleCodes(VALID_CHARS, CODE_LENGTH); // generate all possible codes
    
        // loop for computer attempts
        for (int attempt = 1; attempt <= MAX_TRIES; attempt++) {
            if (allPossibleCodes.isEmpty()) { // check if all possibilities are guessed
                System.out.println("The feedback provided is inconsistent, code could not be guessed."); // output to user
                return;
            }
    
            String computerGuess;
    
            // select AI strategy based on difficulty
            if (difficulty == 1) {
                computerGuess = easyAIGuess(VALID_CHARS, CODE_LENGTH); // easy mode: random guessing
            } else if (difficulty == 2) {
                computerGuess = mediumAIGuess(allPossibleCodes); // medium: feedback based guesses
            } else {
                computerGuess = hardAIGuess(allPossibleCodes); // hard: best strategy
            }
    
            System.out.println("Attempt " + attempt + "/" + MAX_TRIES + ": Computer guesses: " + computerGuess); // output computer guess
    
            System.out.println("Provide feedback (e.g., 'bbww' for 2 black and 2 white pegs): "); // prompt user for feedback
            String feedback = getValidFeedback(reader, CODE_LENGTH); // call method getValidFeedback to save feedback given
    
            if (feedback.equals("bbbb")) { // check if guess is correct
                System.out.println("The computer guessed your code in " + attempt + " attempts!");
                return; // exit game
            }
    
            // update possible codes based on feedback (medium and hard levels only)
            if (difficulty > 1) {
                allPossibleCodes.removeIf(code -> !getFeedback(code.toCharArray(), computerGuess.toCharArray()).equals(feedback));
            }
        }
    
        System.out.println("The computer couldn't guess your code. Congratulations, you win!"); // output game result
    }
    
    /**
     * easyAIGuess
     * method for computer guessing on easy mode
     * guesses randomly from all valid combinations, ignoring feedback
     * @param validChars
     * @param length
     * @return guess
     */
    public static String easyAIGuess(String validChars, int length) {
        Random random = new Random();
        StringBuilder guess = new StringBuilder();
        for (int i = 0; i < length; i++) {
            guess.append(validChars.charAt(random.nextInt(validChars.length())));
        }
        return guess.toString();
    }
    
    /**
     * mediumAIGuess
     * method for computer guessing on medium mode
     * guesses from the remaining possible codes, using feedback to eliminate invalid options
     * @param possibleCodes
     */
    public static String mediumAIGuess(ArrayList<String> possibleCodes) {
        // picks a random guess from the remaining valid options
        Random random = new Random();
        return possibleCodes.get(random.nextInt(possibleCodes.size()));
    }
    
    /**
     * hardAIGuess
     * method for computer guessing on hard mode
     * uses a more complex strategy to minimize guesses (knuth's algorithm)
     * @param possibleCodes
     */
    public static String hardAIGuess(ArrayList<String> possibleCodes) {
        // chooses the first guess from the sorted list of possible codes
        return possibleCodes.get(0);
    }
    
    /**
     * generateRandomCode
     * generates a random code of the specified length using the given valid characters
     * @param validChars
     * @param length
     * @return code
     */
    public static char[] generateRandomCode(String validChars, int length) {
        Random random = new Random(); // random object
        char[] code = new char[length]; // array to hold the generated code

        for (int i = 0; i < length; i++) {
            code[i] = validChars.charAt(random.nextInt(validChars.length())); // randomly select a character
        }

        return code; // return the generated code
    }

    /**
     * getValidGuess
     * ensures the guess is valid and prompts the user until it is
     * checks for correct length and valid characters
     * @param reader
     * @return guess
     */
    public static String getValidGuess(BufferedReader reader) throws IOException {
        while (true) { // infinite loop until valid guess is entered
            String guess = reader.readLine().toUpperCase(); // read user input and convert to uppercase
            if (isValidGuess(guess)) { // validate the guess by calling isValidGuess method
                return guess; // return valid guess
            }
            System.out.println("Invalid guess. Please enter a guess of length " + CODE_LENGTH + " using the letters " + VALID_CHARS + ":"); // error message
        }
    }

    /**
     * getValidFeedback
     * ensures the feedback is valid and prompts the user until it is
     * checks for correct length and valid characters ('b' and 'w')
     * @param reader
     * @return feedback
     */
    public static String getValidFeedback(BufferedReader reader, int length) throws IOException {
        while (true) { // infinite loop until valid feedback is entered
            String feedback = reader.readLine().toLowerCase(); // read user input and convert to lowercase
            if (isValidFeedback(feedback, length)) { // validate the feedback by calling isValidFeedback method
                return feedback; // return valid feedback
            }
            System.out.println("Invalid feedback. Please provide feedback using 'b' and 'w' of length up to " + length + ":"); // error message
        }
    }

    /**
     * isValidGuess
     * checks if the guess is of the correct length and contains only valid characters
     * @param guess
     * @return false
     * @return true
     */
    public static boolean isValidGuess(String guess) {
        if (guess.length() != CODE_LENGTH) { // Check if guess has correct length
            return false;
        }

        for (char c : guess.toCharArray()) { // Iterate through each character in the guess
            if (VALID_CHARS.indexOf(c) == -1) { // Check if character is valid
                return false;
            }
        }

        return true; // Return true if all checks pass
    }

    /**
     * isValidFeedback
     * checks if the feedback is valid (contains only 'b' and 'w' and is not too long)
     * @param feedback
     * @param maxLength
     * @return false
     * @return true
     */
    public static boolean isValidFeedback(String feedback, int maxLength) {
        if (feedback.length() > maxLength) { // check if feedback length is within limit
            return false;
        }

        for (char c : feedback.toCharArray()) { // iterate through each character in the feedback
            if (c != 'b' && c != 'w') { // check if character is valid
                return false;
            }
        }

        return true; // return true if all checks pass
    }

    /**
     * getFeedback
     * provides feedback for a guess compared to the secret code
     * returns a string with 'b' for each correct position and 'w' for correct colour but wrong position
     * @param secretCode
     * @param guess
     * 
     */
    public static String getFeedback(char[] secretCode, char[] guess) {
        int black = 0, white = 0; // initialize counters for black and white pegs
        boolean[] codeUsed = new boolean[secretCode.length]; // tracks used positions in secret code
        boolean[] guessUsed = new boolean[guess.length]; // tracks used positions in guess

        // count black pegs (correct position and colour)
        for (int i = 0; i < secretCode.length; i++) {
            if (secretCode[i] == guess[i]) { // check if character matches in position and value
                black++;
                codeUsed[i] = true; // mark position as used in secret code
                guessUsed[i] = true; // mark position as used in guess
            }
        }

        // count white pegs (correct colour, wrong position)
        for (int i = 0; i < secretCode.length; i++) {
            if (codeUsed[i]) continue; // skip already used positions in secret code
            for (int j = 0; j < guess.length; j++) {
                if (!guessUsed[j] && secretCode[i] == guess[j]) { // check for correct colour
                    white++;
                    guessUsed[j] = true; // mark position as used in guess
                    break; // move to the next character in secret code
                }
            }
        }
        // Construct the feedback string using iteration
        String feedback = "";
        for (int i = 0; i < black; i++) {
            feedback += "b";
        }
        for (int i = 0; i < white; i++) {
            feedback += "w";
        }

        return feedback; // return feedback as a string of 'b' and 'w'
    }

    /**
     * generateAllPossibleCodes
     * generates all possible codes of the given length using the valid characters
     * uses recursion to generate combinations based on the length and valid characters
     * @param validChars
     * @param length
     * @return codes
     */
    public static ArrayList<String> generateAllPossibleCodes(String validChars, int length) {
    ArrayList<String> codes = new ArrayList<>(); // List to store all possible codes

    // generate codes calling the helper method for recursion
    generateCodesHelper(validChars, "", length, codes);

    return codes;
    }
    
    /**
     * generateCodesHelper
     * recursively generates all possible codes
     * @param validChars
     * @param currentCode
     * @param length
     * @param codes
    **/
    public static void generateCodesHelper(String validChars, String currentCode, int length, ArrayList<String> codes) {
        if (currentCode.length() == length) {
            // base case - add the completed code to the list
            codes.add(currentCode);
            return;
        }
    
        // recursive case - add each valid character to the current code
        for (int i = 0; i < validChars.length(); i++) {
            generateCodesHelper(validChars, currentCode + validChars.charAt(i), length, codes);
        }
    }
}
