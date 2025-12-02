package game.arena;

import game.scenes.ArenaScene;
import game.state.State;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;

/**
 * serving state (eitehr player or opponent).
 */
public class ServingState implements State<ArenaScene> {
    private boolean playerServing = true;
    private double time = 0.0;
    private double waitTime = 1.5;

    @Override
    public void onEnter(ArenaScene arena) {
        int roundsPlayed = arena.score.oppScore + arena.score.playerScore;
        playerServing = ((roundsPlayed / 2) % 2) == 1;
        arena.ball.smashSpeedMult = 1.0;
        if (arena.hasPlayerWon() || arena.hasOpponentWon(arena)) {
            arena.stateMachine.changeState(new ResultState());
        }
    }

    @Override
    public void onExit(ArenaScene arena) {
    }

    @Override
    public void update(ArenaScene arena, double deltaTime) {
        arena.player.smashAnim = true;

        time += deltaTime;
        if (time <= waitTime) {
            arena.opponent.moveToX(128);
            return;
        }

        double period = 1;
        double t = (time % period) / period;

        // parabola
        double bounce = -4 * (t - 0.5) * (t - 0.5) + 1;
        double minHeight = 0.01;
        double maxHeight = 0.60;

        arena.ball.height = minHeight + bounce * (maxHeight - minHeight);
        if (playerServing) {
            handlePlayerServing(arena);
        } else {
            handleOpponentServing(arena, deltaTime);
        }
    }

    private void handlePlayerServing(ArenaScene arena) {
        arena.ball.tableZ = 0.99;
        arena.ball.setScreenPositionX(arena.player.x);
        arena.player.opacity = 0.6;
        arena.opponent.moveToX(128);
        if (arena.player.intersectsBall(arena.ball, true) && arena.player.smash) {
            arena.stateMachine.changeState(new RallyState());
            arena.ball.velocityZ = 1.0;
            arena.ball.serveHit(arena.speed, arena.player.horizontalVelocity);
        }
    }

    private void handleOpponentServing(ArenaScene arena, double delta) {
        arena.ball.tableZ = -0.99;
        arena.ball.setScreenPositionX(arena.opponent.x);
        arena.player.opacity = 1.0;
        if (arena.opponent.readyToServe(delta)) {
            if (arena.opponent.intersectsBall(arena.ball, true)) {
                arena.opponent.smash = true;
                arena.opponent.resetServe();
                arena.stateMachine.changeState(new RallyState());
                arena.ball.velocityZ = -1.0;
                arena.ball.serveHit(arena.speed, arena.opponent.horizontalVelocity);
            }
        }
    }

    @Override
    public void render(ArenaScene arena, Graphics2D g) {
    }

    @Override
    public void handleInput(ArenaScene arena, InputEvent e) {
    }
}