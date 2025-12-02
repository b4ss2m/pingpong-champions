package game.entities;

import game.core.Game;
import java.awt.Graphics2D;
import java.awt.Image;

/** infinite scrolling sprite used for clouds, birds, rain etc. */
public class ScrollingSprite extends Sprite {

    private double vx;
    private double vy;

    /** create with img and scroll speed on x and y. */
    public ScrollingSprite(Image image, double vx, double vy) {
        super(image);
        this.vx = vx;
        this.vy = vy;
    }

    @Override
    public void update(double delta) {

        x += vx * delta;
        y += vy * delta;

        // keep wrapping it around for infite scrolling
        x = (x + width) % width;
        y = (y + height) % height;
    }

    @Override
    public void render(Graphics2D g) {
        // add surrounding tiles for the illusion of infinite scroll
        int tilesX = ((Game.VIRTUAL_WIDTH + width - 1) / width + 2);
        int tilesY = ((Game.VIRTUAL_HEIGHT + height - 1) / height + 2);

        // starting position for drawing tiles
        double startX = x % width - width;
        double startY = y % height - height;

        // draw tiles over the entire screen
        for (int tileY = 0; tileY < tilesY; tileY++) {
            for (int tileX = 0; tileX < tilesX; tileX++) {
                x = startX + tileX * width;
                y = startY + tileY * height;
                super.render(g);
            }
        }
    }
}