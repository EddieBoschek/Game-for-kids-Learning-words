import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DAO implements DAOInterface{
    private List<LevelQuestions> levelQList = new ArrayList<>();
    private List<String> soundPaths = new ArrayList<>();
    public DAO() {
        LevelQuestions level1 = new LevelQuestions(getDataForLevelQuestions("src/GameImages", "1"));
        LevelQuestions level2 = new LevelQuestions(getDataForLevelQuestions("src/GameImages", "2"));
        LevelQuestions level3 = new LevelQuestions(getDataForLevelQuestions("src/GameImages", "3"));

        levelQList.add(level1);
        levelQList.add(level2);
        levelQList.add(level3);

        getSoundPathsData("src/SoundFX");
    }
    @Override
    public LevelQuestions getLevelQuestions(int level) {
        return levelQList.get(level - 1);
    }
    @Override
    public String getSoundPath(int pathNumber) {
        return soundPaths.get(pathNumber - 1);
    }

    public void getSoundPathsData(String filePath) {
        File[] allFiles;

        File path = new File(filePath);
        allFiles = path.listFiles();
        assert allFiles != null;
        Arrays.sort(allFiles, Comparator.comparing(File::getName));
        int count = 1;
        for (int i = 0; i < 4; i++) {
            for (File file : allFiles) {
                if (file.getName().substring(1, 2).equals(String.valueOf(count))) {
                    soundPaths.add(file.getPath());
                    count++;
                }
            }
        }
        if (soundPaths.size() == 4) {
            System.out.println("Sound paths data added correctly.");
            System.out.println("---");
        }
    }

    public List<Question> getDataForLevelQuestions(String filePath, String level) {
        List<Question> questions = new ArrayList<>();
        File[] allFiles;

        File path = new File(filePath);
        allFiles = path.listFiles();
        assert allFiles != null;
        Arrays.sort(allFiles, Comparator.comparing(File::getName));
        int count = 1;
        int skippingFileCount = 0;
        int controllerAdjusment = 1;
        if (allFiles[0].getName().substring(0, 1).equals("0")) {
            count = 0;
        }
        for (int i = 0; i < allFiles.length / 5; i++) {
            ImageIcon question = null;
            List<ImageIcon> answerOptions = new ArrayList<>();
            ImageIcon correctAnswer = null;
            String voice = null;
            for (int j = 0; j < 5; j++) {
                if (allFiles[count].getName().substring(1, 2).equals(level)) {
                    if (allFiles[count].getName().substring(7, 8).equals("1")) {
                        question = new ImageIcon(allFiles[count].getPath());
                    } else if (allFiles[count].getName().substring(7, 8).equals("2")) {
                        correctAnswer = new ImageIcon(allFiles[count].getPath());
                        answerOptions.add(new ImageIcon(allFiles[count].getPath()));
                    } else if (allFiles[count].getName().substring(7, 8).equals("3")) {
                        answerOptions.add(new ImageIcon(allFiles[count].getPath()));
                    } else if (allFiles[count].getName().substring(7, 8).equals("4")) {
                        answerOptions.add(new ImageIcon(allFiles[count].getPath()));
                    } else if (allFiles[count].getName().substring(7, 8).equals("5")) {
                        voice = allFiles[count].getPath();
                    } else {
                        System.out.println("Incorrect file name");
                    }
                }
                count++;
            }
            if (question != null && !answerOptions.contains(null) && correctAnswer != null && voice != null) {
                Question q = new Question(question, answerOptions, correctAnswer, voice);
                questions.add(q);
            } else {
                System.out.println("Skipping not relevant files.");
                skippingFileCount++;
            }
        }
        if ((questions.size() + skippingFileCount) * 5 == (count - controllerAdjusment)) {
            System.out.println("---");
            System.out.println("List of Questions created correctly.");
            System.out.println("---");
        }
        return questions;
    }
}