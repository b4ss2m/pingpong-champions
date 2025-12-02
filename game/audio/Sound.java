package game.audio;

import javax.sound.sampled.Clip;

/**
 * Wraps an audio clip for playback control.
 */
public class Sound {
    private final Clip clip;
    
    /** creates a sound from an audio clip. */
    public Sound(Clip clip) {
        this.clip = clip;
    }
    
    /** plays the sound from the beginning. */
    public void play() {
        if (clip != null) {
            clip.setFramePosition(0);  // Rewind to the beginning
            clip.start();
        }
    }
    
    /** stops the sound. */
    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
}