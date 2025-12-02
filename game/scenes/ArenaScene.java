package game.scenes;

import game.Assets;
import game.Save;
import game.arena.ServingState;
import game.audio.Music;
import game.audio.MusicPlayer;
import game.core.Game;
import game.core.Scene;
import game.entities.Ball;
import game.entities.Button;
import game.entities.FadeEffectSprite;
import game.entities.OpponentPaddle;
import game.entities.PlayerPaddle;
import game.entities.ScoreSprite;
import game.entities.ScrollingSprite;
import game.entities.Sprite;
import game.state.StateMachine;
import java.awt.Color;
import java.awt.Image;
import java.io.IOException;

/** main game loop, has 2 states serve and rally. */
public class ArenaScene extends Scene {
    public OpponentPaddle opponent;
    private Button backButton;
    public Ball ball;
    public PlayerPaddle player;
    public ScoreSprite score;
    public StateMachine<ArenaScene> stateMachine;
    private Sprite table;
    private Sprite tutorial1;
    private Sprite tutorial2;
    private Sprite tutorial3;
    private Sprite tutorial4;
    public Sprite winScreen;
    public Sprite loseScreen;
    public boolean matchFinished = false;

    private boolean showTutorial1;
    private boolean tutorial1Completed;
    private boolean tutorial2Completed;
    private boolean tutorial3Completed;
    private boolean tutorial4Completed;
    private double tutorial1DisplayTime;
    private double tutorial2DisplayTime;
    private double tutorial3DisplayTime;
    private double tutorial4DisplayTime;
    public final double speed = 1.5;
    private double introWaitTime = 0.0;
    private double currentSpeed = 1.0;
    private Save prog;

    /** level types. */
    public enum Level {
        BEACH,
        FOREST,
        CASTLE,
    }

    private Level lvl;

    /** make new arena scene. */
    public ArenaScene(Level level, Save progress) {
        prog = progress;
        showTutorial1 = (level == Level.BEACH) && !prog.isUnlocked(1);
        tutorial1Completed = false;
        tutorial2Completed = false;
        tutorial3Completed = false;
        tutorial4Completed = false;
        tutorial1DisplayTime = 0.0;
        tutorial2DisplayTime = 0.0;
        tutorial3DisplayTime = 0.0;
        tutorial4DisplayTime = 0.0;
        lvl = level;

        Music bgm = null;
        switch (lvl) {
            case BEACH -> {
                bgm = new Music(Assets.BGM_BEACH.get(), true, 124983);

                add(new Sprite(Assets.BEACH_BG.get()));
                opponent = add(new OpponentPaddle(new Image[] {
                        Assets.BEACH_OPP_PADDLE.get(),
                        Assets.BEACH_OPP_PADDLE_ANIM_1.get(),
                        Assets.BEACH_OPP_PADDLE_ANIM_2.get(),
                }));
                score = add(new ScoreSprite(Assets.SCORE_CNTR.get()));
                score.oppColor = Color.BLUE;
                table = add(new Sprite(Assets.BEACH_TABLE.get()));
                opponent.baseSpeed = 0.12;
                opponent.responseTime = 0.25;
                opponent.smashRate = 0.0;
                currentSpeed = 0.83;
            }
            case FOREST -> {
                bgm = new Music(Assets.BGM_FOREST.get(), true, 325905);
                add(new Sprite(Assets.FOREST_BG.get()));
                opponent = add(new OpponentPaddle(new Image[] {
                        Assets.FOREST_OPP_PADDLE.get(),
                        Assets.FOREST_OPP_PADDLE_ANIM_1.get(),
                        Assets.FOREST_OPP_PADDLE_ANIM_2.get(),
                }));
                score = add(new ScoreSprite(Assets.SCORE_CNTR.get()));
                score.oppColor = new Color(24, 160, 15);
                table = add(new Sprite(Assets.FOREST_TABLE.get()));
                opponent.baseSpeed = 0.21;
                opponent.responseTime = 0.07;
                opponent.smashRate = 0.15;
                ScrollingSprite overlay = new ScrollingSprite(Assets.FOREST_OVERLAY.get(), -60, 60);
                overlay.renderOrder = 100;
                add(overlay);
                currentSpeed = 0.9;
            }
            case CASTLE -> {
                bgm = new Music(Assets.BGM_CASTLE.get(), true, 419166);
                add(new Sprite(Assets.CASTLE_BG.get()));
                opponent = add(new OpponentPaddle(new Image[] {
                        Assets.CASTLE_OPP_PADDLE.get(),
                        Assets.CASTLE_OPP_PADDLE_ANIM_1.get(),
                        Assets.CASTLE_OPP_PADDLE_ANIM_2.get(),
                }));
                score = add(new ScoreSprite(Assets.SCORE_CNTR.get()));
                score.oppColor = new Color(240, 160, 15);
                table = add(new Sprite(Assets.CASTLE_TABLE.get()));
                opponent.baseSpeed = 0.32;
                opponent.responseTime = 0.03;
                opponent.smashRate = 0.6;
                currentSpeed = 1.05;
            }
            default -> {
            }
        }

        ball = add(new Ball(Assets.BALL.get()));
        ball.baseSpeed = currentSpeed;
        MusicPlayer.switchToTrack(bgm);

        backButton = add(new Button(
                Assets.BACK.get(),
                Assets.BACK_HOVER.get(),
                Assets.BACK_PRESS.get(),
                232,
                12));

        opponent.paddleRadius = 10;

        player = add(new PlayerPaddle(new Image[] {
                Assets.PLAYER_PADDLE.get(),
                Assets.PLAYER_PADDLE_ANIM_1.get(),
                Assets.PLAYER_PADDLE_ANIM_2.get(),
                Assets.PLAYER_PADDLE_ANIM_3.get()
        }));
        player.tableZ = 1.0;

        winScreen = add(new Sprite(Assets.WIN.get(), 0.0));
        loseScreen = add(new Sprite(Assets.LOSE.get(), 0.0));

        add(new FadeEffectSprite(0.5));

        tutorial1 = add(new Sprite(Assets.BEACH_TUT1.get(), 0.0));
        tutorial2 = add(new Sprite(Assets.BEACH_TUT2.get(), 0.0));
        tutorial3 = add(new Sprite(Assets.BEACH_TUT3.get(), 0.0));
        tutorial4 = add(new Sprite(Assets.BEACH_TUT4.get(), 0.0));

        stateMachine = new StateMachine<>(this);
        stateMachine.changeState(new ServingState());
    }

    /** based on what level were on we unlock the next one. */
    public void unlockNextLevel() {
        if (!hasPlayerWon()) {
            return;
        }
        switch (lvl) {
            case BEACH -> {
                prog.unlockLevel(1); // just to mark that the game was played (continue button)
                prog.unlockLevel(2);
            }
            case FOREST -> {
                prog.unlockLevel(3);
            }
            default -> {
            }

        }
        try {
            prog.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** checks if the player won. */
    public boolean hasPlayerWon() {
        int playerScore = score.playerScore;
        int opponentScore = score.oppScore;

        if (playerScore >= 5 && playerScore - opponentScore >= 2) {
            MusicPlayer.switchToTrack(new Music(Assets.BGM_WIN.get(), false));
            return true;
        }

        return false;
    }

    /** checks if the popponent won. */
    public boolean hasOpponentWon(ArenaScene arena) {
        int playerScore = arena.score.playerScore;
        int opponentScore = arena.score.oppScore;

        if (opponentScore >= 5 && opponentScore - playerScore >= 2) {
            MusicPlayer.switchToTrack(new Music(Assets.BGM_LOSE.get(), false));
            return true;
        }

        return false;
    }

    @Override
    public void onEnter(Game game) {
        game.resetShake();
        super.onEnter(game);
        backButton.setOnClick(() -> {
            game.stateMachine.changeState(new LevelSelectScene(prog));
        });
    }

    @Override
    public void update(Game game, double delta) {
        super.update(game, delta);
        introWaitTime += delta;
        if (introWaitTime <= 1.5) { // 1.5 second at start of match
            return;
        }
        stateMachine.update(delta);

        if (showTutorial1) {
            handleTutorial1(delta);
            handleTutorial2(delta);
            handleTutorial3(delta);
            handleTutorial4(delta);
        }

        if (matchFinished) {
            game.stateMachine.changeState(new LevelSelectScene(prog));
        }

        updateRenderOrder();
    }

    private void updateRenderOrder() { // make sure objects Z are always in correct order
        ball.renderOrder = table.renderOrder + 1;
        if (ball.tableZ < -1.0) {
            ball.renderOrder = opponent.renderOrder - 1;
        } else if (ball.tableZ > 1.0) {
            ball.renderOrder = player.renderOrder + 1;
        }
    }

    private void handleTutorial1(double delta) {
        if (!tutorial1Completed) {
            tutorial1.opacity += (1.0 - tutorial1.opacity) * delta;
            tutorial1DisplayTime += delta;

            if (Math.abs(player.horizontalVelocity) > 0.9 && tutorial1DisplayTime >= 1.0) {
                tutorial1Completed = true;
            }
        } else {
            tutorial1.opacity -= tutorial1.opacity * delta * 3.0;
        }
    }

    private void handleTutorial2(double delta) {
        if (!tutorial2Completed && score.playerScore + score.oppScore >= 2) {
            tutorial2.opacity += (1.0 - tutorial2.opacity) * delta;
            tutorial2DisplayTime += delta;

            if (player.smash && tutorial2DisplayTime >= 1.0) {
                tutorial2Completed = true;
            }
        } else {
            tutorial2.opacity -= tutorial2.opacity * delta * 3.0;
        }
    }

    private void handleTutorial3(double delta) {
        if (!tutorial3Completed && score.playerScore + score.oppScore >= 3) {
            tutorial3.opacity += (1.0 - tutorial3.opacity) * delta;
            tutorial3DisplayTime += delta;
            if (tutorial3DisplayTime >= 4.0) {
                tutorial3Completed = true;
            }
        } else {
            tutorial3.opacity -= tutorial3.opacity * delta * 3.0;
        }
    }

    private void handleTutorial4(double delta) {
        if (!tutorial4Completed && score.playerScore + score.oppScore >= 4) {
            tutorial4.opacity += (1.0 - tutorial4.opacity) * delta;
            tutorial4DisplayTime += delta;
            if (tutorial4DisplayTime >= 4.0) {
                tutorial4Completed = true;
            }
        } else {
            tutorial4.opacity -= tutorial4.opacity * delta * 3.0;
        }
    }
}