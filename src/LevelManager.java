public class LevelManager {
    private static LevelManager instance;
    private int currentLevel;

    private LevelManager() {
        currentLevel = 1;
    }

    public static synchronized LevelManager getInstance() {
        if (instance == null) {
            instance = new LevelManager();
        }
        return instance;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
    public void increaseLevel() {
        this.currentLevel++;
    }
    public void resetLevel() {
        currentLevel = 1;
    }
}