package game.entities;

import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

/**
 * a fullscreen black image that fades in/out when added or removed
 * keep in mind it blocks the main loop while fading.
 */
public class FadeEffectSprite extends Sprite {
    private double duration = 0.5; // fade duration in seconds

    /**
     * creates a massive black rectangle that fades opacity.
     */
    public FadeEffectSprite(double duration) {
        super(new BufferedImage(999, 999, BufferedImage.TYPE_INT_RGB));
        this.duration = duration;
    }

    private void fade(double target) {
        long start = System.nanoTime();
        double startOpacity = opacity;
        double delta = target - startOpacity;

        while (true) {
            long elapsed = System.nanoTime() - start;
            double progress = elapsed / (duration * 1000000000.0);
            opacity = startOpacity + delta * progress;

            game.repaint();
            
            if (delta * (target - opacity) <= 0) {
                opacity = target;
                break;
            }
        }
        opacity = target; 
    }

    /**
     * fades in.
     */
    @Override
    public void onEnter() {
        fade(0);
    }

    /**
     * fades out.
     */
    @Override
    public void onExit() {
        fade(1);
    }

    @Override
    public void update(double delta) {
    }

    @Override
    public void handleInput(InputEvent e) {
    }
}