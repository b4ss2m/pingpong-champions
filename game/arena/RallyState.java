package game.arena;

import game.Assets;
import game.audio.Sound;
import game.scenes.ArenaScene;
import game.state.State;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;

/**
 * rally (ball back n forth) state.
 */
public class RallyState implements State<ArenaScene> {

    @Override
    public void onEnter(ArenaScene arena) {
        arena.player.opacity = 1.0;
        arena.opponent.resetReaction();
    }

    @Override
    public void onExit(ArenaScene arena) {
        new Sound(Assets.SFX_WHISTLE.get()).play();
    }

    @Override
    public void update(ArenaScene arena, double deltaTime) {
        arena.player.smashAnim = true;
        arena.opponent.smashAnim = true;

        if (arena.player.intersectsBall(arena.ball, arena.player.smash)) {
            arena.player.smashAnim = arena.player.smash;
            arena.ball.hit(arena.speed, arena.player.horizontalVelocity, arena.player.smash);
        }

        if (arena.opponent.intersectsBall(arena.ball, arena.opponent.smash)) {
            arena.opponent.smashAnim = arena.opponent.smash;
            arena.ball.hit(arena.speed, arena.opponent.horizontalVelocity, arena.opponent.smash);
        }

        arena.opponent.opponentPlay(arena.ball, deltaTime);

        if (arena.ball.tableZ > 1.3) {
            arena.score.incrementOpponentScore();
            arena.stateMachine.changeState(new ServingState());
        } else if (arena.ball.tableZ < -1.3) {
            arena.score.incrementPlayerScore();
            arena.stateMachine.changeState(new ServingState());
        }
    }

    @Override
    public void render(ArenaScene arena, Graphics2D g) {
    }

    @Override
    public void handleInput(ArenaScene arena, InputEvent e) {
    }
}