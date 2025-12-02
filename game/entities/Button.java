package game.entities;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * a button sprite with hover and pressed states.
 */
public class Button extends Sprite {
    private Image regularImage;
    private Image hoverImage;
    private Image pressedImage;

    protected boolean isHovered;
    private boolean isPressed;
    private boolean isReleased;
    private Runnable onClick;

    /**
     * init button.
     */
    public Button(Image regular, Image hover, Image pressed, double x, double y) {
        super(regular, x, y);
        this.regularImage = regular;
        this.hoverImage = hover;
        this.pressedImage = pressed;
        this.isHovered = false;
        this.isPressed = false;
    }

    /**
     * init button with opacity.
     */
    public Button(Image regular, Image hover, Image pressed, double x, double y, double opacity) {
        this(regular, hover, pressed, x, y);
        this.opacity = opacity;
    }

    /**
     * set the run function that executes when the button is clicked.
     */
    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    @Override
    public void update(double delta) {
        super.update(delta);
        if (isReleased) {
            this.image = regularImage;
            if (onClick != null) {
                onClick.run();
            }
        } else if (isPressed) {
            this.image = pressedImage;
        } else if (isHovered) {
            updateCursor(true);
            this.image = hoverImage;
        } else {
            this.image = regularImage;
        }
    }

    @Override
    public void handleInput(InputEvent e) {
        if (!(e instanceof MouseEvent me)) {
            return;
        }
        int eventID = me.getID();
        isHovered = isWithinButtonBounds(me.getX(), me.getY());
        if (eventID == MouseEvent.MOUSE_PRESSED && isHovered) {
            isPressed = true;
            isReleased = false;
        } else if (eventID == MouseEvent.MOUSE_RELEASED) {
            isReleased = isHovered;
            isPressed = false;
        }
        updateCursor(false);
    }

    private void updateCursor(boolean hover) {
        if (game == null) {
            return;
        }
        if (hover) {
            game.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            game.setCursor(Cursor.getDefaultCursor());
        }
    }

    private boolean isWithinButtonBounds(int mouseX, int mouseY) {
        return Math.abs(mouseX - x) <= width / 2
                && Math.abs(mouseY - y) <= height / 2 && opacity > 0.3;
    }
}