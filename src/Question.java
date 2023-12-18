import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Question {
    private ImageIcon question;
    private List<ImageIcon> answerOptions;
    private ImageIcon correctAnswer;
    private String voice;

    public Question(ImageIcon question, List<ImageIcon> answerOptions, ImageIcon correctAnswer, String voice) {
        this.question = question;
        this.answerOptions = answerOptions;
        this.correctAnswer = correctAnswer;
        this.voice = voice;
    }

    public ImageIcon getQuestion() {
        return question;
    }
    public ImageIcon getAnswerOption(int index) {
        return answerOptions.get(index);
    }
    public List<ImageIcon> getAnswerOptionsList() {
        return answerOptions;
    }
    public ImageIcon getCorrectAnswer() {
        return correctAnswer;
    }
    public String getVoice() {
        return this.voice;
    }

    public int getNumberOfAnswerOptions (){
        int temp = answerOptions.size();
        return temp;
    }
    public boolean isCorrectAnswer(ImageIcon answer) {
        Image image1 = answer.getImage();
        Image image2 = this.correctAnswer.getImage();
        System.out.println(image1.equals(image2));
        return image1.equals(image2);
    }
}