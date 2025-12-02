package game.entities;

import java.awt.Graphics2D;
import java.awt.Image;

/** special button (has label text) just for lvl select screen. */
public class LevelSelectButton extends Button {
    Sprite label;

    /** takes an image of button when pressed & when you press the button + x y coords. */
    public LevelSelectButton(Image regular, Image pressed, Image label, double x, double y) {
        super(regular, regular, pressed, x, y);
        this.label = new Sprite(label, x, y + 12, 0.0);
    }

    @Override
    public void update(double delta) {
        super.update(delta);
        if (this.isHovered) {
            label.opacity = 1.0;
        } else {
            label.opacity = 0.0;
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        super.render(g);
        label.render(g);
    }
}