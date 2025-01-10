import com.github.schuettec.cobra2d.entity.ColoredRectangle;
import com.github.schuettec.cobra2d.entity.SimpleText;
import com.github.schuettec.cobra2d.math.Point;
import com.github.schuettec.cobra2d.math.Dimension;
import com.github.schuettec.cobra2d.world.Cobra2DWorld;
import com.github.schuettec.cobra2d.controller.Controller;
import com.github.schuettec.cobra2d.screen.SimpleScreen;
import com.github.schuettec.cobra2d.engine.Cobra2DEngine;

import java.util.ArrayList;
import java.util.Random;

public class CodebreakerGUI extends SimpleScreen {
    private Cobra2DWorld world;
    private SimpleText feedbackText, guessText, attemptText, modeText;
    private ColoredRectangle[] modeButtons;
    private ColoredRectangle[] colorButtons;
    private ColoredRectangle submitButton;
    private char[] guess = new char[CODE_LENGTH];
    private char[] secretCode;
    private ArrayList<String> allPossibleCodes;
    private int mode = 0;
    private int attempts = 0;

    static final String VALID_CHARS = "GRBYOP";
    static final int CODE_LENGTH = 4;
    static final int MAX_TRIES = 10;

    public static void main(String[] args) {
        Cobra2DEngine engine = new Cobra2DEngine(new CodebreakerGUI(), "Codebreaker", 800, 600);
        engine.run();
    }

    public CodebreakerGUI() {
        super();
        world = new Cobra2DWorld();
        setupUI();
        resetGame();
    }

    private void setupUI() {
        // Welcome and Feedback
        modeText = new SimpleText(new Point(10, 10), "Welcome to Codebreaker!");
        world.addEntity(modeText);
        feedbackText = new SimpleText(new Point(10, 40), "Choose a mode:");
        world.addEntity(feedbackText);

        // Mode Selection Buttons
        modeButtons = new ColoredRectangle[3];
        String[] modes = {"User Guesses", "Computer Guesses", "Exit"};
        for (int i = 0; i < 3; i++) {
            modeButtons[i] = new ColoredRectangle(new Point(50, 100 + i * 70), new Dimension(200, 60));
            modeButtons[i].setColor(i == 0 ? Color.BLUE : i == 1 ? Color.GREEN : Color.RED);
            SimpleText modeText = new SimpleText(new Point(60, 120 + i * 70), modes[i]);
            world.addEntity(modeButtons[i]);
            world.addEntity(modeText);
        }

        // Color selection buttons for guessing
        colorButtons = new ColoredRectangle[VALID_CHARS.length()];
        for (int i = 0; i < VALID_CHARS.length(); i++) {
            colorButtons[i] = new ColoredRectangle(new Point(50 + i * 60, 300), new Dimension(50, 50));
            colorButtons[i].setColor(getColorFromChar(VALID_CHARS.charAt(i)));
            world.addEntity(colorButtons[i]);
        }

        // Guess Display
        guessText = new SimpleText(new Point(10, 400), "Guess: ");
        world.addEntity(guessText);

        // Submit Button
        submitButton = new ColoredRectangle(new Point(50, 450), new Dimension(100, 50));
        submitButton.setColor(Color.GRAY);
        SimpleText submitText = new SimpleText(new Point(60, 470), "Submit");
        world.addEntity(submitButton);
        world.addEntity(submitText);

        // Attempt Counter
        attemptText = new SimpleText(new Point(10, 500), "Attempts: 0/" + MAX_TRIES);
        world.addEntity(attemptText);
    }

    private Color getColorFromChar(char c) {
        switch (c) {
            case 'G': return Color.GREEN;
            case 'R': return Color.RED;
            case 'B': return Color.BLUE;
            case 'Y': return Color.YELLOW;
            case 'O': return Color.ORANGE;
            case 'P': return Color.PINK;
            default: return Color.BLACK;
        }
    }

    @Override
    public void update() {
        super.update();
        guessText.setText("Guess: " + new String(guess));
    }

    @Override
    public void processController(Controller controller) {
        if (controller.isButtonPressed(Controller.BUTTON1)) { // Mouse click
            Point mousePosition = controller.getMousePosition();

            // Check mode selection
            if (mode == 0) {
                for (int i = 0; i < modeButtons.length; i++) {
                    if (modeButtons[i].getRectangle().contains(mousePosition)) {
                        handleModeSelection(i + 1);
                    }
                }
            } else {
                // Handle guess input for both modes
                for (int i = 0; i < colorButtons.length; i++) {
                    if (colorButtons[i].getRectangle().contains(mousePosition) && attempts < MAX_TRIES) {
                        for (int j = 0; j < CODE_LENGTH; j++) {
                            if (guess[j] == '\0') {
                                guess[j] = VALID_CHARS.charAt(i);
                                break;
                            }
                        }
                    }
                }
                // Submit guess
                if (submitButton.getRectangle().contains(mousePosition)) {
                    if (mode == 1) {
                        handleUserGuess();
                    } else if (mode == 2) {
                        handleComputerGuess();
                    }
                }
            }
        }
    }

    private void handleModeSelection(int selectedMode) {
        if (selectedMode == 3) {
            System.exit(0);
        } else {
            mode = selectedMode;
            if (mode == 1) {
                secretCode = generateRandomCode(VALID_CHARS, CODE_LENGTH);
                feedbackText.setText("The computer has set a secret code.");
                modeText.setText("You're guessing!");
            } else if (mode == 2) {
                feedbackText.setText("Set your code by clicking:");
                modeText.setText("Computer will guess!");
            }
        }
    }

    private void handleUserGuess() {
        if (isValidGuess(new String(guess))) {
            attempts++;
            attemptText.setText("Attempts: " + attempts + "/" + MAX_TRIES);
            String feedback = getFeedback(secretCode, guess);
            feedbackText.setText("Guess: " + new String(guess) + "\tClues: " + feedback);
            if (feedback.equals("bbbb")) {
                feedbackText.setText("Congratulations! You guessed in " + attempts + " attempts!");
                resetGame();
            } else if (attempts == MAX_TRIES) {
                feedbackText.setText("Sorry, you lost. Code was: " + new String(secretCode));
                resetGame();
            }
            guess = new char[CODE_LENGTH]; // Clear guess for next attempt
        }
    }

    private void handleComputerGuess() {
        if (allPossibleCodes == null) {
            allPossibleCodes = generateAllPossibleCodes(VALID_CHARS, CODE_LENGTH);
            secretCode = guess; // User's secret code
            guess = new char[CODE_LENGTH];
        }
        if (allPossibleCodes.isEmpty()) {
            feedbackText.setText("Inconsistent feedback, couldn't guess.");
            resetGame();
            return;
        }
        String computerGuess = allPossibleCodes.get(0);
        attempts++;
        attemptText.setText("Attempts: " + attempts + "/" + MAX_TRIES);
        feedbackText.setText("Computer guesses: " + computerGuess);

        // Here, you would normally ask for feedback from the user, but since this is a GUI, we'll simulate it:
        String feedback = getFeedback(secretCode, computerGuess.toCharArray());
        if (feedback.equals("bbbb")) {
            feedbackText.setText("Computer guessed in " + attempts + " attempts!");
            resetGame();
        } else if (attempts == MAX_TRIES) {
            feedbackText.setText("Computer couldn't guess. You win!");
            resetGame();
        }
        allPossibleCodes.removeIf(code -> !getFeedback(code.toCharArray(), computerGuess.toCharArray()).equals(feedback));
    }

    private void resetGame() {
        mode = 0;
        attempts = 0;
        guess = new char[CODE_LENGTH];
        allPossibleCodes = null;
        modeText.setText("Welcome to Codebreaker!");
        feedbackText.setText("Choose a mode:");
        attemptText.setText("Attempts: 0/" + MAX_TRIES);
    }

    // Your existing methods here with minor adjustments for GUI interaction
    private char[] generateRandomCode(String validChars, int length) {
        Random random = new Random();
        char[] code = new char[length];
        for (int i = 0; i < length; i++) {
            code[i] = validChars.charAt(random.nextInt(validChars.length()));
        }
        return code;
    }

    private boolean isValidGuess(String guess) {
        return guess.length() == CODE_LENGTH && guess.chars().allMatch(c -> VALID_CHARS.indexOf(c) != -1);
    }

    private String getFeedback(char[] secretCode, char[] guess) {
        int black = 0, white = 0;
        boolean[] codeUsed = new boolean[secretCode.length];
        boolean[] guessUsed = new boolean[guess.length];

        for (int i = 0; i < secretCode.length; i++) {
            if (secretCode[i] == guess[i]) {
                black++;
                codeUsed[i] = guessUsed[i] = true;
            }
        }

        for (int i = 0; i < secretCode.length; i++) {
            if (!codeUsed[i]) {
                for (int j = 0; j < guess.length; j++) {
                    if (!guessUsed[j] && secretCode[i] == guess[j]) {
                        white++;
                        guessUsed[j] = true;
                        break;
                    }
                }
            }
        }

        return "b".repeat(black) + "w".repeat(white);
    }

    private ArrayList<String> generateAllPossibleCodes(String validChars, int length) {
        ArrayList<String> codes = new ArrayList<>();
        generateCodesHelper(validChars, "", length, codes);
        return codes;
    }

    private void generateCodesHelper(String validChars, String currentCode, int length, ArrayList<String> codes) {
        if (currentCode.length() == length) {
            codes.add(currentCode);
            return;
        }
        for (int i = 0; i < validChars.length(); i++) {
            generateCodesHelper(validChars, currentCode + validChars.charAt(i), length, codes);
        }
    }
}