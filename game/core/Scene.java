package game.core;

import game.state.State;
import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * a scene is a game state holding entities. all it does is literally forward
 * events like update, render, or input to each entity.
 */
public abstract class Scene implements State<Game> {
    protected List<Entity> gameObjects = new ArrayList<>();
    private int nextRenderOrder = 0;

    /**
     * add an object to the scene.
     */
    protected <T extends Entity> T add(T obj) {
        obj.renderOrder = nextRenderOrder++;
        gameObjects.add(obj);
        return obj;
    }

    /**
     * remove an object from the scene.
     */
    protected void remove(Entity obj) {
        obj.onExit();
        gameObjects.remove(obj);
    }

    @Override
    public void onEnter(Game game) {
        game.requestFocusInWindow();
        for (Entity obj : new ArrayList<>(gameObjects)) {
            obj.setGame(game);
            obj.onEnter();
        }
    }

    @Override
    public void onExit(Game game) {
        for (Entity obj : new ArrayList<>(gameObjects)) {
            obj.onExit();
        }
        gameObjects.clear();
    }

    @Override
    public void update(Game game, double delta) {
        for (Entity obj : new ArrayList<>(gameObjects)) {
            obj.update(delta);
        }
    }

    @Override
    public void render(Game game, Graphics2D g) {
        List<Entity> snapshot = new ArrayList<>(gameObjects);
        snapshot.removeIf(e -> e == null);
        snapshot.sort(Comparator.comparingInt(e -> e.renderOrder));
        for (Entity obj : snapshot) {
            obj.render(g);
        }
    }

    @Override
    public void handleInput(Game game, InputEvent e) {
        for (Entity obj : new ArrayList<>(gameObjects)) {
            obj.handleInput(e);
        }
    }
}