package game.state;

import java.awt.Graphics2D;
import java.awt.event.InputEvent;

/**
 * generic state interface that can be used for anything that needs to track state.
 */
public interface State<T> {
    void onEnter(T entity);

    void onExit(T entity);

    void update(T entity, double deltaTime);

    void render(T entity, Graphics2D g);

    void handleInput(T entity, InputEvent e);
}