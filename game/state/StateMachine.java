package game.state;

import java.awt.Graphics2D;
import java.awt.event.InputEvent;

/**
 * Generic state machine to manage state changing.
 */
public class StateMachine<T> {
    private State<T> currentState;
    private T owner;
    
    public StateMachine(T owner) {
        this.owner = owner;
    }
    
    /**
     * switch to a new state, calls exit/enter stuff.
     */
    public void changeState(State<T> newState) {
        if (currentState != null) {
            currentState.onExit(owner);
        }
        currentState = newState;
        currentState.onEnter(owner);
    }

    /**
     * update current state with time step.
     */
    public void update(double deltaTime) {
        if (currentState != null) {
            currentState.update(owner, deltaTime);
        }
    }
    
    /**
     * render for current state.
     */
    public void render(Graphics2D g) {
        if (currentState != null) {
            currentState.render(owner, g);
        }
    }

    /**
     * send input to current state.
     */
    public void handleInput(InputEvent e) {
        if (currentState != null) {
            currentState.handleInput(owner, e);
        }
    }
}