package game.audio;

/**
 * global music player where only one track plays at a time.
 */
public class MusicPlayer {
    private static Music currentTrack = null;

    /** change the currently playing music. */
    public static void switchToTrack(Music music) {
        stop();
        currentTrack = music;
        music.play();
    }

    /** stop the currently playing music. */
    public static void stop() {
        if (currentTrack != null) {
            currentTrack.stop();
            currentTrack = null;
        }
    }
}