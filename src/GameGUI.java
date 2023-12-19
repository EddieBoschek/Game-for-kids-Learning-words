import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameGUI extends JFrame {
    private JButton questionButton = new JButton();
    private List<JButton> answerButtons;
    private JButton stopButton = new JButton("Stop");
    private List<JPanel> squarePanels;
    private GameLogic gameLogic;
    private LevelManager levelManager;
    private Question question;
    Color customColor1 = new Color(150, 255, 150);
    Color customColor2 = new Color(255, 231, 151);
    Color customColor3 = new Color(151, 207, 255);
    Color customColor4 = new Color(255, 191, 255);

    public GameGUI(LevelManager levelManager) {
        this.levelManager = levelManager;
        setSize(1144, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setGameLogic(GameLogic gameLogic){
        this.gameLogic = gameLogic;
    }
    public List<JButton> getAnswerButtons() {
        return answerButtons;
    }

    public void updateGUI(int questionNumber, Question question) {
        this.question = question;
        answerButtons = new ArrayList<>();
        getContentPane().removeAll();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(getBackgroundColor());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(getBackgroundColor());

        JLabel infoLabel = new JLabel("   •   Nivå " + levelManager.getCurrentLevel() + " av 3" +
                                        "   •   Fråga " + questionNumber + " av 3   •");
        infoLabel.setBackground(getBackgroundColor());
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        topPanel.add(infoLabel, BorderLayout.NORTH);

        JPanel midPanel = new JPanel(new GridLayout(2,0));
        JPanel questionsPanel = new JPanel(new GridLayout(1, 3));
        JPanel emptyPanel = new JPanel();
        JPanel answerPanel = new JPanel(new GridBagLayout());

        questionsPanel.setBackground(getBackgroundColor());
        emptyPanel.setBackground(getBackgroundColor());
        answerPanel.setBackground(getBackgroundColor());

        midPanel.setBackground(getBackgroundColor());

        questionButton.setIcon(question.getQuestion());
        questionButton.setPreferredSize(new Dimension(300, 300));
        JPanel qButtonPanel = new JPanel();
        qButtonPanel.setBackground(getBackgroundColor());
        qButtonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        qButtonPanel.add(questionButton);

        questionsPanel.add(emptyPanel);
        questionsPanel.add(qButtonPanel);
        scoreSquares(questionsPanel);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        for (int i = 0; i < question.getNumberOfAnswerOptions(); i++) {
            JButton button = new JButton(question.getAnswerOption(i));
            button.setPreferredSize(new Dimension(300, 300));
            button.setOpaque(true);
            button.setBorder(new LineBorder(Color.WHITE, 5));
            constraints.gridx = i;
            answerPanel.add(button,constraints);
            answerButtons.add(button);
        }

        midPanel.add(questionsPanel, BorderLayout.NORTH);
        midPanel.add(answerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(getBackgroundColor());
        stopButton.setPreferredSize(new Dimension(60, 60));
        bottomPanel.add(stopButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(midPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setupGUIListeners();

        setContentPane(mainPanel);
        revalidate();
        repaint();
    }
    public void scoreSquares(JPanel questionPanel) {
        int squareSize = 50;
        int numberOfSquares = 3;

        squarePanels = new ArrayList<>();

        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        scorePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
        scorePanel.setBackground(getBackgroundColor());

        for (int i = 0; i < numberOfSquares; i++) {
            JPanel squarePanel = new JPanel();
            squarePanel.setBorder(new LineBorder(Color.WHITE, 5));
            squarePanel.setBackground(Color.RED);
            squarePanel.setPreferredSize(new Dimension(squareSize, squareSize));
            scorePanel.add(squarePanel);
            squarePanels.add(squarePanel);
        }
        updateScore();
        questionPanel.add(scorePanel, BorderLayout.EAST);
    }

    public void displayCorrectAnswer(int incorrectButtonIndex, int correctButtonIndex) {
        if (incorrectButtonIndex == -1) {
            if (correctButtonIndex >= 0 && correctButtonIndex < answerButtons.size()) {
                answerButtons.get(correctButtonIndex).setBackground(Color.GREEN);
                SwingUtilities.invokeLater(() -> {
                    gameLogic.playSound("src/SoundFX/correctAnswer.wav");
                });
            }
        } else {
            if (incorrectButtonIndex >= 0 && incorrectButtonIndex < answerButtons.size()) {
                answerButtons.get(incorrectButtonIndex).setBackground(Color.RED);
                answerButtons.get(correctButtonIndex).setBackground(Color.GREEN);
                SwingUtilities.invokeLater(() -> {
                    gameLogic.playSound("src/SoundFX/wrongAnswer.wav");
                });
            }
        }
        updateScore();
    }

    public void endGameGUI(){
        getContentPane().removeAll();

        JPanel endGamePanel = new JPanel(new GridLayout(3,1));

        JPanel imagePanel = new JPanel();
        imagePanel.setBorder(BorderFactory.createEmptyBorder(49, 0, 0, 24));
        JPanel emptyPanel = new JPanel(new FlowLayout());
        JPanel choicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        choicePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        imagePanel.setBackground(customColor4);
        choicePanel.setBackground(customColor4);
        emptyPanel.setBackground(customColor4);

        endGamePanel.add(imagePanel);
        endGamePanel.add(choicePanel);
        endGamePanel.add(emptyPanel);

        Icon playAgainIcon = new ImageIcon("src/EndGameImages/playAgain.png");

        Icon yesIcon = new ImageIcon("src/EndGameImages/YesIcon.png");
        Icon noIcon = new ImageIcon("src/EndGameImages/NoIcon.png");

        JLabel playAgainLabel = new JLabel(playAgainIcon);
        JButton newGame = new JButton(yesIcon);
        JButton closeGame = new JButton(noIcon);

        newGame.setFocusable(false);
        newGame.setBorderPainted(false);
        newGame.setContentAreaFilled(false);

        closeGame.setFocusable(false);
        closeGame.setBorderPainted(false);
        closeGame.setContentAreaFilled(false);

        Dimension buttonSize = new Dimension(100, 100);
        newGame.setSize(buttonSize);
        closeGame.setSize(buttonSize);
        newGame.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));

        imagePanel.add(playAgainLabel);
        choicePanel.add(newGame);
        choicePanel.add(closeGame);

        setContentPane(endGamePanel);
        revalidate();
        repaint();

        newGame.addActionListener(e -> {
            gameLogic.newGame();
        });
        closeGame.addActionListener(e -> System.exit(0));
    }

    public void setupGUIListeners() {
        for (ActionListener listener : questionButton.getActionListeners()) {
            questionButton.removeActionListener(listener);
        }
        if (levelManager.getCurrentLevel() == 1) {
            questionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gameLogic.playSound(question.getVoice());
                }
            });
        }
        for (ActionListener listener : stopButton.getActionListeners()) {
            stopButton.removeActionListener(listener);
        }
            stopButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gameLogic.stopButtonPressed();
                }
            });
        for (JButton button : answerButtons) {
            for (ActionListener listener : button.getActionListeners()) {
                button.removeActionListener(listener);
            }
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                        gameLogic.handleAnswerButtonClicked((ImageIcon) button.getIcon());
                }
            });
        }
    }

    public void updateScore() {
        for (int i = 0; i < gameLogic.getCorrectAnswersInARow(); i++) {
            squarePanels.get(i).setBackground(Color.GREEN);
        }
        repaint();
    }

    public void upDateStopButton(boolean stopped) {
        if (stopped) {
            stopButton.setText("Start");
        } else {
            stopButton.setText("Stop");
        }
    }

    public Color getBackgroundColor() {
        int currentLevel = levelManager.getCurrentLevel();
        return switch (currentLevel) {
            case 1 -> customColor1;
            case 2 -> customColor2;
            case 3 -> customColor3;
            default -> Color.WHITE;
        };
    }
}