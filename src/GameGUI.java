import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameGUI extends JFrame {
    private List<JButton> answerButtons;
    private JButton questionButton = new JButton();
    private GameLogic gameLogic;
    private List<JPanel> squarePanels;
    private Question question;
    Color customColor1 = new Color(150, 255, 150);
    Color customColor2 = new Color(255, 231, 151);
    Color customColor3 = new Color(151, 207, 255);
    Color customColor4 = new Color(255, 191, 255);

    JFrame frame;

    public GameGUI() {
        setSize(1144, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.frame = this;
        answerButtons = new ArrayList<>();

        setVisible(true);
    }

    public void setGameLogic(GameLogic gameLogic){
        this.gameLogic = gameLogic;
    }
    public List<JButton> getAnswerButtons() {
        return answerButtons;
    }

    public void updateGUI(int level, int questionNumber, Question question) {
        this.question = question;
        answerButtons = new ArrayList<>();
        getContentPane().removeAll();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.setBackground(getBackgroundColor());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(getBackgroundColor());
        scoreSquares(topPanel,level, questionNumber);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        topPanel.setBackground(getBackgroundColor());

        JPanel midPanel = new JPanel(new GridLayout(2,0));
        JPanel questionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel answerPanel = new JPanel(new GridBagLayout());

        questionsPanel.setBackground(getBackgroundColor());
        answerPanel.setBackground(getBackgroundColor());

        questionsPanel.setPreferredSize(new Dimension(250,250));
        answerPanel.setPreferredSize(new Dimension(250,250));

        midPanel.setBackground(getBackgroundColor());

        questionButton.setIcon(question.getQuestion());
        questionButton.setPreferredSize(new Dimension(300, 300));
        questionsPanel.add(questionButton);

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

        mainPanel.add(midPanel, BorderLayout.CENTER);

        midPanel.add(questionsPanel, BorderLayout.NORTH);
        midPanel.add(answerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
        bottomPanel.setBackground(getBackgroundColor());
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        setupGUIListeners();

        setContentPane(mainPanel);
        revalidate();
        repaint();
        if (level == 1) {
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(450);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                gameLogic.playSound(question.getVoice());
            });
        }
    }
    public void scoreSquares(JPanel topPanel, int level, int questionNumber) {
        int squareSize = 50;
        int numberOfSquares = 3;

        squarePanels = new ArrayList<>();

        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(getBackgroundColor());

        JLabel label = new JLabel("   •   Nivå " + level + " av 3   •   Fråga " + questionNumber + " av 3   •");
        label.setBackground(getBackgroundColor());
        topPanel.add(label, BorderLayout.NORTH);

        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        scorePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
        scorePanel.setBackground(getBackgroundColor());

        for (int i = 0; i < numberOfSquares; i++) {
            JPanel squarePanel = new JPanel();
            squarePanel.setBorder(new LineBorder(Color.WHITE, 5));

            boolean[] score = GameLogic.getRoundResults();
            if (score[i]) {
                squarePanel.setBackground(Color.GREEN);
            } else {
                squarePanel.setBackground(Color.RED);
            }
            squarePanel.setPreferredSize(new Dimension(squareSize, squareSize));
            scorePanel.add(squarePanel);
            squarePanels.add(squarePanel);
        }
        topPanel.add(scorePanel, BorderLayout.CENTER);
    }

    public void displayCorrectAnswer(int correctButtonIndex) {
        if (correctButtonIndex >= 0 && correctButtonIndex < answerButtons.size()) {
            answerButtons.get(correctButtonIndex).setBackground(Color.GREEN);
            SwingUtilities.invokeLater(() -> {
                gameLogic.playSound("src/SoundFX/correctAnswer.wav");
            });
        }
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(1300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resetAnswerButtonColors();
        });
        updateSquareColors();
    }

    public void displayIncorrectAnswer(int incorrectButtonIndex, int correctButtonIndex) {

        if (incorrectButtonIndex >= 0 && incorrectButtonIndex < answerButtons.size()) {
            answerButtons.get(incorrectButtonIndex).setBackground(Color.RED);
            answerButtons.get(correctButtonIndex).setBackground(Color.GREEN);
            SwingUtilities.invokeLater(() -> {
                gameLogic.playSound("src/SoundFX/wrongAnswer.wav");
            });
        }
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(1300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resetAnswerButtonColors();
        });
        updateSquareColors();
    }

    public void resetAnswerButtonColors() {
        for (JButton button : answerButtons) {
            button.setBackground(null);
        }
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
        if (GameLogic.getCurrentLevel() == 1) {
            questionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gameLogic.playSound(question.getVoice());
                }
            });
        }
        for (JButton button : answerButtons) {
            //AtomicBoolean isMouseOver = new AtomicBoolean(false);
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

    public void updateSquareColors() {
        boolean[] temp = GameLogic.getRoundResults();

        if (temp.length != squarePanels.size()) {
            throw new IllegalArgumentException("Number of elements in the array must match the number of circles.");
        }

        for (int i = 0; i < squarePanels.size(); i++) {
            JPanel circlePanel = squarePanels.get(i);
            boolean isCorrect = temp[i];

            if (isCorrect) {

                circlePanel.setBackground(Color.GREEN);
            } else {
                circlePanel.setBackground(Color.RED);
            }
        }

        repaint();
    }

    public Color getBackgroundColor() {
        int currentLevel = GameLogic.getCurrentLevel();
        return switch (currentLevel) {
            case 1 -> customColor1;
            case 2 -> customColor2;
            case 3 -> customColor3;
            default -> Color.WHITE;
        };
    }
}