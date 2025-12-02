package game.entities;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/** player paddle - controlled by user input. */
public class PlayerPaddle extends Paddle {
    private FlipbookSprite lightingEffect;

    /** player paddle - controlled by user input. */
    public PlayerPaddle(Image[] images) {
        super(images);
        this.y = 181;
        this.lightingEffect = new FlipbookSprite(images, opacity);
    }

    @Override
    public void update(double delta) {
        super.update(delta);
    }

    @Override
    public void render(Graphics2D g) {
        double targetTotalOpacity = this.opacity;
        double lightingFraction = Math.clamp(0.5 + (rotation / 24.0), 0.0, 1.0);
        double lightingOpacity = targetTotalOpacity * lightingFraction;
        double savedOpacity = this.opacity;

        this.opacity = targetTotalOpacity;
        super.render(g);
        this.opacity = savedOpacity;

        // lighting effect is just a flipped version of the sprite lmao
        lightingEffect.setIndex(this.currentIndex);
        lightingEffect.width = -(this.width);
        lightingEffect.opacity = lightingOpacity;
        lightingEffect.x = this.x;
        lightingEffect.y = this.y;
        lightingEffect.rotation = this.rotation;
        lightingEffect.render(g);
    }

    @Override
    public void handleInput(InputEvent e) {
        if (!(e instanceof KeyEvent ke)) {
            return;
        }
        int keyCode = ke.getKeyCode();
        if (ke.getID() == KeyEvent.KEY_PRESSED) {
            handleKeyPressed(keyCode);
        }
        if (ke.getID() == KeyEvent.KEY_RELEASED) {
            handleKeyReleased(keyCode);
        }
    }

    private void handleKeyPressed(int keyCode) {
        leftPressed |= keyCode == KeyEvent.VK_LEFT;
        rightPressed |= keyCode == KeyEvent.VK_RIGHT;
        leftPressed |= keyCode == KeyEvent.VK_A;
        rightPressed |= keyCode == KeyEvent.VK_D;
        smash |= keyCode == KeyEvent.VK_SPACE;
    }

    private void handleKeyReleased(int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT) {
            leftPressed = false;
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        if (keyCode == KeyEvent.VK_A) {
            leftPressed = false;
        } else if (keyCode == KeyEvent.VK_D) {
            rightPressed = false;
        }
    }
}