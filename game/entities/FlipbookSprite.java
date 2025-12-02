package game.entities;

import game.core.Game;
import java.awt.*;

/** sprite that can display >1 images like a flipbook, using index. */
public class FlipbookSprite extends Sprite {
    protected Image[] images;
    protected int currentIndex;

    /** init flipbook sprite with array of images (centered). */
    public FlipbookSprite(Image[] images) {
        this(images, Game.VIRTUAL_WIDTH / 2, Game.VIRTUAL_HEIGHT / 2);
    }

    /** init flipbook sprite with array of images and position. */
    public FlipbookSprite(Image[] images, double x, double y) {
        this(images, x, y, 1.0);
    }

    /** init flipbook sprite with array of images and opacity (centered). */
    public FlipbookSprite(Image[] images, double opacity) {
        this(images, Game.VIRTUAL_WIDTH / 2, Game.VIRTUAL_HEIGHT / 2, opacity);
    }

    /** init flipbook sprite with array of images, position, and opacity. */
    public FlipbookSprite(Image[] images, double x, double y, double opacity) {
        super(images[0], x, y, opacity);
        this.images = images;
        this.currentIndex = 0;
    }

    /** sets the current image index where index is 0 to images.length - 1. */
    public void setIndex(int index) {
        if (index >= 0 && index < images.length) {
            currentIndex = index;
            image = images[currentIndex];
            width = image.getWidth(null);
            height = image.getHeight(null);
        }
    }
}