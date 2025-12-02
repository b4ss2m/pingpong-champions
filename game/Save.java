package game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/** manages all player save data using file save.txt. */
public class Save {
    public static int totalLevels = 3;
    public static String saveFileName = "save.txt";
    public boolean[] unlockedLevels;

    public Save() {
        unlockedLevels = new boolean[totalLevels];
    }

    /** update level progress with new unlocked level. */
    public void unlockLevel(int level) {
        if (level >= 1 && level <= unlockedLevels.length) { //1st level always unlocked
            unlockedLevels[level - 1] = true;
        }
    }

    /** lock all levels. */
    public void reset() {
        unlockedLevels = new boolean[totalLevels];
    }

    /** checks if a level at index has been unlocked by player. */
    public boolean isUnlocked(int level) {
        if (level >= 1 && level <= unlockedLevels.length) {
            return unlockedLevels[level - 1];
        }
        return false;
    }

    /** save level progress to save.txt file. */
    public void save() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (boolean unlocked : unlockedLevels) {
            sb.append(unlocked ? "1" : "0"); // if its unlocked put a 1, otherwise put a zero
        }
        Files.writeString(Paths.get(saveFileName), sb.toString());
    }

    /** load level progress from save.txt file. */
    public void load() throws IOException {
        String data = Files.readString(Paths.get(saveFileName));
        for (int i = 0; i < Math.min(data.length(), unlockedLevels.length); i++) {
            unlockedLevels[i] = data.charAt(i) == '1';
        }
    }
}

