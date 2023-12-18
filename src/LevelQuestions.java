import java.util.Collections;
import java.util.List;

public class LevelQuestions {
    private List<Question> levelQuestions;

    public LevelQuestions(List<Question> levelQuestions) {
        this.levelQuestions = levelQuestions;
    }

    public void shuffle() {
        for (Question q : levelQuestions)
            Collections.shuffle(q.getAnswerOptionsList());
        Collections.shuffle(levelQuestions);
    }
    public Question getQuestion(int index) {
        return levelQuestions.get(index - 1);
    }
    public int getSize() {
        return levelQuestions.size();
    }
}