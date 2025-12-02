package game.core;

import java.awt.Graphics2D;
import java.awt.event.InputEvent;

/**
 * represents a single object within a Scene.
 * does its own update, render, and enter/exit.
 */
public abstract class Entity {
    public int renderOrder;
    protected Game game;

    void setGame(Game game) {
        this.game = game;
    }

    /**
     * called once when the object is added to a scene.
     */
    public void onEnter() {
    }

    /**
     * called once when the object is removed or when a Scene exits.
     */
    public void onExit() {
    }

    /**
     * called every frame to update object logic.
     */
    public abstract void update(double delta);

    /**
     * called every frame to render the object.
     */
    public abstract void render(Graphics2D g);

    /**
     * called every time new input is recorded.
     */
    public abstract void handleInput(InputEvent e);
}
