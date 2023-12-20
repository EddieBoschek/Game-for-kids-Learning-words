import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class GameLogic {
    private DAO dao = new DAO();
    private GameGUI gameGUI;
    private LevelManager levelManager;
    private int currentQuestion;
    private int correctAnswersInARow;
    private LevelQuestions levelQuestions;
    private Question question;
    private boolean stopped = false;
    private boolean muted = false;
    private Clip backgroundMusicClip;
    private volatile Thread backgroundThread;
    private final Object lock = new Object();
    private String music = "src/SoundFX/BackgroundMusic.wav";
    private String correctAnswerSound = "src/SoundFX/correctAnswer.wav";
    private String wrongAnswerSound = "src/SoundFX/wrongAnswer.wav";
    private String levelCompleteSound = "src/SoundFX/levelComplete.wav";

    public GameLogic(GameGUI gameGUI, LevelManager levelManager) {
        this.gameGUI = gameGUI;
        this.levelManager = levelManager;

        newGame();
        Thread musicThread = new Thread(() -> playMusic(music));
        musicThread.start();
    }

    public int getCorrectAnswersInARow() {
        return correctAnswersInARow;
    }

    private void startNewRound() {
        if (levelManager.getCurrentLevel() <= 3) {
            levelQuestions = dao.getLevelQuestions(levelManager.getCurrentLevel());
            levelQuestions.shuffle();
            if (currentQuestion <= levelQuestions.getSize()) {
                question = levelQuestions.getQuestion(currentQuestion);
                pauseAndUpdateGUI();
            } else {
                levelManager.increaseLevel();
                moveToNextLevel();
            }
        } else {
            gameGUI.endGameGUI();
        }
    }

    public void restartRound() {
        currentQuestion = 1;
        correctAnswersInARow = 0;

        SwingUtilities.invokeLater(this::startNewRound);
    }

    public void moveToNextQuestion() {
        if (currentQuestion <= levelQuestions.getSize()) {
            question = levelQuestions.getQuestion(currentQuestion);
            pauseAndUpdateGUI();
        } else {
            levelManager.increaseLevel();
            moveToNextLevel();
        }
    }

    private void moveToNextLevel() {
        currentQuestion = 1;
        correctAnswersInARow = 0;

        SwingUtilities.invokeLater(this::startNewRound);
    }

    public void handleAnswerButtonClicked(ImageIcon selectedAnswer) {
        if (!stopped) {
            Question question = levelQuestions.getQuestion(currentQuestion);

            boolean isCorrect = question.isCorrectAnswer(selectedAnswer);

            if (isCorrect) {
                correctAnswersInARow++;
                int correctButtonIndex = findAnswerButtonIndex(selectedAnswer);
                gameGUI.displayCorrectAnswer(-1, correctButtonIndex);

                if (correctAnswersInARow == 3) {
                    levelManager.increaseLevel();
                    SwingUtilities.invokeLater(() -> {
                        playSound(levelCompleteSound, 0);
                    });
                    SwingUtilities.invokeLater(this::moveToNextLevel);
                } else {
                    currentQuestion++;
                    SwingUtilities.invokeLater(this::moveToNextQuestion);
                }
            } else {
                correctAnswersInARow = 0;
                int incorrectButtonIndex = findAnswerButtonIndex(selectedAnswer);
                int correctButtonIndex = findAnswerButtonIndex(levelQuestions.getQuestion
                        (currentQuestion).getCorrectAnswer());
                gameGUI.displayCorrectAnswer(incorrectButtonIndex, correctButtonIndex);
                restartRound();
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

    public void newGame(){
        SwingUtilities.invokeLater(() -> {
            levelManager.resetLevel();
            this.currentQuestion = 1;
            this.correctAnswersInARow = 0;

            levelQuestions = dao.getLevelQuestions(levelManager.getCurrentLevel());
            levelQuestions.shuffle();
            question = levelQuestions.getQuestion(currentQuestion);

            gameGUI.updateGUI(question);

            if (levelManager.getCurrentLevel() == 1) {
                pauseAndPlaySound(450);
            }
        });
    }

    public void pauseAndPlaySound(int time) {
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            playSound(question.getVoice(), 0);
        });
    }
    public void playSound(String path, int i) {
        String filePath = path;
        if (i == 1) {
            filePath = correctAnswerSound;
        } else if (i == 2) {
            filePath = wrongAnswerSound;
        }
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(audioFile);

            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
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

    private void playMusic(String filePath) {
        try {
            while (true) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));

                backgroundMusicClip = AudioSystem.getClip();
                backgroundMusicClip.open(audioInputStream);

                FloatControl volumeControl = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(-10.0f);

                backgroundMusicClip.start();

                while (backgroundMusicClip.isRunning()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                backgroundMusicClip.close();
                audioInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void muteMusic() throws LineUnavailableException {
        if (backgroundMusicClip != null && backgroundMusicClip.
                isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volumeControl = (FloatControl) backgroundMusicClip.
                    getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(volumeControl.getMinimum());
        }
    }

    private void unmuteMusic() throws LineUnavailableException {
        if (backgroundMusicClip != null && backgroundMusicClip.
                isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volumeControl = (FloatControl) backgroundMusicClip.
                    getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-10.0f);
        }
    }

    public void musicButtonPressed() throws LineUnavailableException {
        muted = !muted;
        if (muted) {
            muteMusic();
        } else {
            unmuteMusic();
        }
    }
    public void stopButtonPressed() {
        stopped = !stopped;
        gameGUI.upDateStopButton(stopped);
        if (!stopped) {
            resumeThread();
        }
    }

    private void pauseAndUpdateGUI() {
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
                            Thread.sleep(1100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                SwingUtilities.invokeLater(() -> gameGUI.updateGUI(question));
            }
            if (levelManager.getCurrentLevel() == 1) {
                SwingUtilities.invokeLater(() -> pauseAndPlaySound(450));
            }
        });
        backgroundThread.start();
    }

    public void resumeThread() {
        synchronized (lock) {
            lock.notify();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LevelManager levelManager = LevelManager.getInstance();
            GameGUI gameGUI1 = new GameGUI(levelManager);
            GameLogic gameLogic = new GameLogic(gameGUI1, levelManager);
            gameGUI1.setGameLogic(gameLogic);

        });
    }
}