package game.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * represents a single music track.
 */
public class Music {
    private final Clip clip;
    private final boolean loop;
    private final int loopStart;

    /** new music track with loop params. */
    public Music(Clip clip, boolean loop, int loopStart) {
        this.clip = clip;
        this.loop = loop;
        this.loopStart = loopStart;
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            ((FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN)).setValue(-9f);
        }
    }

    /** new music track. */
    public Music(Clip clip, boolean loop) {
        this(clip, loop, 0);
    }

    /** play. */
    protected void play() {
        if (loop) {
            clip.setLoopPoints(loopStart, -1);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            clip.start();
        }
    }

    /** stop music. */
    void stop() {
        clip.stop();
        clip.setFramePosition(0);
    }

    Clip getClip() {
        return clip;
    }
}
