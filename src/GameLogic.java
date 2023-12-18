import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class GameLogic {
    private DAO dao = new DAO();
    private GameGUI gameGUI;
    private static int currentLevel;
    private int currentQuestion;
    private int correctAnswersInARow;
    private static boolean[] roundResults;
    private LevelQuestions levelQuestions;

    public GameLogic(GameGUI gameGUI) {
        this.gameGUI = gameGUI;
        currentLevel = 1;
        this.currentQuestion = 1;
        this.correctAnswersInARow = 0;
        roundResults = new boolean[3];

        startNewRound();
    }

    public static boolean[] getRoundResults() {
        return roundResults;
    }

    public void restartGame() {
        currentQuestion = 1;
        correctAnswersInARow = 0;
        roundResults = new boolean[3];

        SwingUtilities.invokeLater(this::startNewRound);
    }

    private void startNewRound() {
        if (currentLevel <= 3) {
            levelQuestions = dao.getLevelQuestions(currentLevel);
            levelQuestions.shuffle();
            if (currentQuestion <= levelQuestions.getSize()) {
                Question question = levelQuestions.getQuestion(currentQuestion);
                gameGUI.updateGUI(currentLevel, currentQuestion, question);
            } else {
                currentLevel++;
                moveToNextLevel();
            }
        } else {
            gameGUI.endGameGUI();
        }
    }

    public void moveToNextQuestion() {
        if (currentQuestion <= levelQuestions.getSize()) {
            Question question = levelQuestions.getQuestion(currentQuestion);
            gameGUI.updateGUI(currentLevel, currentQuestion, question);
        } else {
            currentLevel++;
            moveToNextLevel();
        }
    }

    public void handleAnswerButtonClicked(ImageIcon selectedAnswer) {
        Question question = levelQuestions.getQuestion(currentQuestion);

        boolean isCorrect = question.isCorrectAnswer(selectedAnswer);
        roundResults[currentQuestion - 1] = isCorrect;

        if (isCorrect) {
            int correctButtonIndex = findAnswerButtonIndex(selectedAnswer);
            gameGUI.displayCorrectAnswer(correctButtonIndex);
            correctAnswersInARow++;
            System.out.println("Correct answers in a row: " + correctAnswersInARow);

            if (correctAnswersInARow == 3) {

                currentLevel++;
                SwingUtilities.invokeLater(() -> {
                    playSound("src/SoundFX/levelComplete.wav");
                });
                SwingUtilities.invokeLater(this::moveToNextLevel);
            } else {
                currentQuestion++;
                SwingUtilities.invokeLater(this::moveToNextQuestion);
            }
        } else {
            correctAnswersInARow = 0;
            System.out.println("Incorrect answer. Resetting correctAnswersInARow.");
            int incorrectButtonIndex = findAnswerButtonIndex(selectedAnswer);
            int correctButtonIndex = findAnswerButtonIndex(levelQuestions.getQuestion
                    (currentQuestion).getCorrectAnswer());
            gameGUI.displayIncorrectAnswer(incorrectButtonIndex, correctButtonIndex);
            restartGame();
        }
    }

    private int findAnswerButtonIndex(ImageIcon selectedAnswer) {
        Image answer = selectedAnswer.getImage();
        for (int i = 0; i < gameGUI.getAnswerButtons().size(); i++) {
            ImageIcon temp = (ImageIcon) gameGUI.getAnswerButtons().get(i).getIcon();
            Image answerOption = temp.getImage();
            if (answerOption.equals(answer)) {
                return i;
            }
        }
        return -1;
    }

    private void moveToNextLevel() {
        currentQuestion = 1;
        correctAnswersInARow = 0;
        roundResults = new boolean[3];

        SwingUtilities.invokeLater(this::startNewRound);
    }

    public static int getCurrentLevel() {
        return currentLevel;
    }

    public void playSound(String path) {
        try {
            File ljudFil = new File(path);
            AudioInputStream ljudInput = AudioSystem.getAudioInputStream(ljudFil);

            Clip clip = AudioSystem.getClip();
            clip.open(ljudInput);
            clip.start();

            while (!clip.isRunning()) {
                Thread.sleep(10);
            }
            while (clip.isRunning()) {
                Thread.sleep(10);
            }
            clip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void newGame(){
        SwingUtilities.invokeLater(() -> {
            currentLevel = 1;
            this.currentQuestion = 1;
            this.correctAnswersInARow = 0;
            roundResults = new boolean[3];

            startNewRound();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameGUI gameGUI1 = new GameGUI();
            GameLogic gameLogic = new GameLogic(gameGUI1);
            gameGUI1.setGameLogic(gameLogic);

        });
    }
}