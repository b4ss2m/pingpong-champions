package game.arena;

import game.scenes.ArenaScene;
import game.state.State;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;

/**
 * after the game finished, displays win/lose screen with animation.
 */
public class ResultState implements State<ArenaScene> {
    
    private double time;
    private final double waitBeforeResult = 1.0;
    private final double winAnimationDuration = 7.0;
    private final double loseAnimationDuration = 1.0;
    private boolean playerWon;

    @Override
    public void onEnter(ArenaScene arena) {
        time = 0.0;
        playerWon = arena.hasPlayerWon();
        if (playerWon) {
            arena.unlockNextLevel();
            arena.winScreen.scaleX = arena.winScreen.scaleY = 3.0;
            arena.winScreen.rotation = 0.3;
            arena.winScreen.opacity = 0.0;
        } else {
            arena.loseScreen.scaleX = arena.loseScreen.scaleY = 3.0;
            arena.loseScreen.rotation = -0.3;
            arena.loseScreen.opacity = 0.0;
        }
    }

    @Override
    public void onExit(ArenaScene arena) {
    }

    @Override
    public void update(ArenaScene arena, double deltaTime) {
        time += deltaTime;
        
        if (time <= waitBeforeResult) {
            return;
        }
        double animTime = time - waitBeforeResult;
        double animationDuration = playerWon ? winAnimationDuration : loseAnimationDuration;
        double progress = Math.min(animTime / animationDuration, 1.0);
        double eased = 1.0 - Math.pow(1.0 - progress, 3.0);
        
        if (playerWon) {
            arena.winScreen.scaleX = arena.winScreen.scaleY = 3.0 - (2.0 * eased);
            arena.winScreen.rotation = 0.3 * (1.0 - eased);
            double opacityProgress = Math.min(animTime / (winAnimationDuration * 0.5), 1.0);
            arena.winScreen.opacity = opacityProgress;
        } else {
            arena.loseScreen.scaleX = arena.loseScreen.scaleY = 3.0 - (2.0 * eased);
            arena.loseScreen.rotation = -0.3 * (1.0 - eased);
            double opacityProgress = Math.min(animTime / (loseAnimationDuration * 0.5), 1.0);
            arena.loseScreen.opacity = opacityProgress;
        }

        if (progress >= 1.0) {
            arena.matchFinished = true;
        }
    }

    @Override
    public void render(ArenaScene arena, Graphics2D g) {
    }

    @Override
    public void handleInput(ArenaScene arena, InputEvent e) {
    }
}