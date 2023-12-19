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
    boolean stopped = false;
    Question question;
    private volatile Thread backgroundThread;
    private final Object lock = new Object();

    public GameLogic(GameGUI gameGUI) {
        this.gameGUI = gameGUI;
        currentLevel = 1;
        this.currentQuestion = 1;
        this.correctAnswersInARow = 0;
        roundResults = new boolean[3];

        newGame();
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
                question = levelQuestions.getQuestion(currentQuestion);
                System.out.println("Innan updateGUI 1");
                pauseThread();
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
            question = levelQuestions.getQuestion(currentQuestion);
            System.out.println("Innan updateGUI 2");
            pauseThread();
        } else {
            currentLevel++;
            moveToNextLevel();
        }
    }

    private void moveToNextLevel() {
        currentQuestion = 1;
        correctAnswersInARow = 0;
        roundResults = new boolean[3];

        SwingUtilities.invokeLater(this::startNewRound);
    }

    public void handleAnswerButtonClicked(ImageIcon selectedAnswer) {
        if (!stopped) {
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

            levelQuestions = dao.getLevelQuestions(currentLevel);
            levelQuestions.shuffle();
            question = levelQuestions.getQuestion(currentQuestion);

            gameGUI.updateGUI(currentLevel, currentQuestion, question);
        });
    }

    public void stopButtonPressed() {
        stopped = !stopped;
        gameGUI.upDateStopButton(stopped);
        if (!stopped) {
            resumeThread();
        }
    }

    private void pauseThread() {
        backgroundThread = new Thread(() -> {
            try {
                Thread.sleep(1300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock) {
                while (stopped) {
                    try {
                        lock.wait();
                        try {
                            Thread.sleep(1250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("innan updateGUI 3");
                SwingUtilities.invokeLater(() -> gameGUI.updateGUI(currentLevel, currentQuestion, question));
            }
        });
        System.out.println("Thread start");
        backgroundThread.start();
    }

    // När du vill fortsätta tråden
    public void resumeThread() {
        synchronized (lock) {
            // Notify för att väcka tråden
            lock.notify();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameGUI gameGUI1 = new GameGUI();
            GameLogic gameLogic = new GameLogic(gameGUI1);
            gameGUI1.setGameLogic(gameLogic);

        });
    }
}