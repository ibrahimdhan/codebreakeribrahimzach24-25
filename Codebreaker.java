import org.mini2Dx.core.game.BasicGame;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.game.GameContainer;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

import java.util.ArrayList;
import java.util.Random;

class Constants {
    static final String GAME_IDENTIFIER = "com.example.codebreaker";
    static final String VALID_CHARS = "GRBYOP";
    static final int CODE_LENGTH = 4;
    static final int MAX_TRIES = 10;
}

public class Codebreaker extends BasicGame implements InputProcessor {
    private String secretCode;
    private String userGuess = "";
    private String feedback = "";
    private int currentAttempt = 0;
    private boolean gameWon = false;
    private boolean gameOver = false;

    private ArrayList attemptHistory = new ArrayList();
    private ArrayList feedbackHistory = new ArrayList();

    @Override
    public void initialise() {
        secretCode = generateRandomCode(Constants.VALID_CHARS, Constants.CODE_LENGTH);
    }

    @Override
    public void update(float delta) {
        // Game logic updates (if needed)
    }

    @Override
    public void interpolate(float alpha) {
        // Interpolation logic (not required for this version)
    }

    @Override
    public void render(Graphics g) {
        g.drawString("Welcome to Codebreaker!", 50, 50);
        g.drawString("Guess the code of length " + Constants.CODE_LENGTH + " using " + Constants.VALID_CHARS, 50, 80);
        g.drawString("Attempt: " + currentAttempt + " / " + Constants.MAX_TRIES, 50, 110);

        if (gameWon) {
            g.drawString("Congratulations! You guessed the code!", 50, 140);
        } else if (gameOver) {
            g.drawString("Game Over. The correct code was: " + secretCode, 50, 140);
        } else {
            g.drawString("Your Guess: " + userGuess, 50, 140);
            g.drawString("Press A-F to input colors (G, R, B, Y, O, P).", 50, 170);
            g.drawString("Press Enter to submit your guess.", 50, 200);

            displayHistory(g);
        }
    }

    private void displayHistory(Graphics g) {
        g.drawString("Attempts:", 50, 230);
        for (int i = 0; i < attemptHistory.size(); i++) {
            g.drawString((i + 1) + ". " + attemptHistory.get(i) + " - Feedback: " + feedbackHistory.get(i), 50, 260 + (i * 20));
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (gameWon || gameOver) return false;

        if (userGuess.length() < Constants.CODE_LENGTH && isValidKey(keycode)) {
            userGuess += (char) keycode;
        }

        if (keycode == Keys.ENTER && userGuess.length() == Constants.CODE_LENGTH) {
            processGuess();
        }
        return true;
    }

    private boolean isValidKey(int keycode) {
        return keycode == Keys.G || keycode == Keys.R || keycode == Keys.B || keycode == Keys.Y || keycode == Keys.O || keycode == Keys.P;
    }

    private void processGuess() {
        feedback = getFeedback(secretCode, userGuess);
        attemptHistory.add(userGuess);
        feedbackHistory.add(feedback);
        currentAttempt++;

        if (feedback.equals("bbbb")) {
            gameWon = true;
        } else if (currentAttempt >= Constants.MAX_TRIES) {
            gameOver = true;
        }
        userGuess = "";
    }

    private String generateRandomCode(String validChars, int length) {
        Random random = new Random();
        StringBuffer code = new StringBuffer();
        for (int i = 0; i < length; i++) {
            code.append(validChars.charAt(random.nextInt(validChars.length())));
        }
        return code.toString();
    }

    private String getFeedback(String secret, String guess) {
        boolean[] codeUsed = new boolean[Constants.CODE_LENGTH];
        boolean[] guessUsed = new boolean[Constants.CODE_LENGTH];
        int black = 0, white = 0;

        // Check for exact matches
        for (int i = 0; i < Constants.CODE_LENGTH; i++) {
            if (secret.charAt(i) == guess.charAt(i)) {
                black++;
                codeUsed[i] = true;
                guessUsed[i] = true;
            }
        }

        // Check for partial matches
        for (int i = 0; i < Constants.CODE_LENGTH; i++) {
            if (!codeUsed[i]) {
                for (int j = 0; j < Constants.CODE_LENGTH; j++) {
                    if (!guessUsed[j] && secret.charAt(i) == guess.charAt(j)) {
                        white++;
                        guessUsed[j] = true;
                        break;
                    }
                }
            }
        }

        String feedback = "";
        for (int i = 0; i < black; i++) {
            feedback += 'b';
        }
        for (int i = 0; i < white; i++) {
            feedback += 'w';
        }
        return feedback;
    }

    // Other InputProcessor methods (touch, mouse, etc.) if needed

    public static void main(String[] args) {
        GameContainer container = new GameContainer() {
            @Override
            public void start(Graphics g) {
                setGame(new Codebreaker());
                super.start(g);
            }

            @Override
            public void onResume() {
                // Implement onResume logic here
            }

            @Override
            public void onPause() {
                // Implement onPause logic here if needed
            }

            @Override
            public void render(Graphics g) {
                super.render(g); // This ensures the game's render method is called
            }

            @Override
            public void interpolate(float alpha) {
                super.interpolate(alpha); // Call super to ensure game interpolation happens
            }

            @Override
            public void update(float delta) {
                super.update(delta); // This ensures the game's update method is called
            }
        };
        container.start();
    }
}

