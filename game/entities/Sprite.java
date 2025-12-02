package game.entities;

import game.core.Entity;
import game.core.Game;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.geom.AffineTransform;

/**
 * a sprite is an image rendered to screen that can transform
 * (rotate,move,scale).
 */
public class Sprite extends Entity {
    protected Image image;

    public double x; // x position
    public double y; // y position

    public double vx; // x velocity
    public double vy; // y velocity

    public double rotation; // Rotation in degrees
    public double scaleX = 1.0;
    public double scaleY = 1.0;

    protected int width; // Base size (from image)
    public int height;

    public double opacity = 1.0; // 0.0 (transparent) to 1.0 (opaque)

    /**
     * init sprite with img (puts in center).
     */
    public Sprite(Image image) {
        this(image, Game.VIRTUAL_WIDTH / 2, Game.VIRTUAL_HEIGHT / 2);
    }

    /**
     * Init sprite with img and position.
     */
    public Sprite(Image image, double x, double y) {
        this(image, x, y, 1.0);
    }

    /**
     * Init sprite with img and opacity (centered).
     */
    public Sprite(Image image, double opacity) {
        this(image, Game.VIRTUAL_WIDTH / 2, Game.VIRTUAL_HEIGHT / 2, opacity);
    }

    /**
     * Init sprite with img, opacity, and position.
     */
    public Sprite(Image image, double x, double y, double opacity) {
        this.image = image;
        this.opacity = opacity;
        this.x = x;
        this.y = y;
        this.rotation = 0;
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
    }

    @Override
    public void update(double delta) {
    }

    @Override
    public void render(Graphics2D g) {
        opacity = Math.clamp(opacity, 0, 1);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(Math.toRadians(rotation));
        transform.scale(scaleX, scaleY);
        g.setTransform(transform);
        g.drawImage(image, -width / 2, -height / 2, width, height, null);
    }

    @Override
    public void handleInput(InputEvent e) {
    }
}