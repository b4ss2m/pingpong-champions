package game.scenes;

import game.Assets;
import game.Save;
import game.audio.Music;
import game.audio.MusicPlayer;
import game.core.Game;
import game.core.Scene;
import game.entities.Button;
import game.entities.FadeEffectSprite;
import game.entities.LevelSelectButton;
import game.entities.ScrollingSprite;
import game.entities.Sprite;
import game.scenes.ArenaScene.Level;

/**
 * level selection screen. uses Levelprogress if provided, otherwise assumes new
 * game.
 */
public class LevelSelectScene extends Scene {
    Sprite background = add(new Sprite(Assets.MAP_BG.get()));
    ScrollingSprite birds = add(new ScrollingSprite(Assets.MAP_BG_OVERLAY.get(), 200.0, 0));
    Button backButton = add(new Button(
            Assets.BACK.get(),
            Assets.BACK_HOVER.get(),
            Assets.BACK_PRESS.get(),
            232,
            12));
    Sprite selectLevelText = add(new Sprite(Assets.MAP_SELECTLEVEL.get()));

    LevelSelectButton beachLevelButton = add(new LevelSelectButton(Assets.MAP_LEVEL1BUTTON.get(),
            Assets.MAP_LEVEL1BUTTON_PRESS.get(),
            Assets.MAP_LEVEL1BUTTON_LABEL.get(), 115, 210));

    LevelSelectButton forestLevelButton = add(new LevelSelectButton(Assets.MAP_LEVEL2BUTTON.get(),
            Assets.MAP_LEVEL2BUTTON_PRESS.get(),
            Assets.MAP_LEVEL2BUTTON_LABEL.get(), 145, 172));

    LevelSelectButton castleLevelButton = add(new LevelSelectButton(Assets.MAP_LEVEL3BUTTON.get(),
            Assets.MAP_LEVEL3BUTTON_PRESS.get(),
            Assets.MAP_LEVEL3BUTTON_LABEL.get(), 112, 110));

    Sprite lockedForest = add(new Sprite(Assets.MAP_LOCKED.get(), 145, 172));
    Sprite lockedCastle = add(new Sprite(Assets.MAP_LOCKED.get(), 112, 110));
    Sprite arrowIndicator = add(new Sprite(Assets.MAP_INDICATOR_ARROW.get(), 115, 183));
    FadeEffectSprite fade = add(new FadeEffectSprite(0.5));

    double arrowAnimTime = 0.0;
    double arrowBaseY = 183.0;
    Save progress = null;

    public LevelSelectScene() {
    }

    /** create new scene with save data (progress). */
    public LevelSelectScene(Save progress) {
        this.progress = progress;

        if (progress.isUnlocked(2)) {
            lockedForest.opacity = 0.0;
        } else {
            forestLevelButton.opacity = 0.0;
        }
        if (progress.isUnlocked(3)) {
            lockedCastle.opacity = 0.0;
        } else {
            castleLevelButton.opacity = 0.0;
        }
        if (!progress.isUnlocked(1)) {
            arrowIndicator.opacity = 1.0;
        }
        arrowIndicator.opacity = 0.0;
    }

    @Override
    public void onEnter(Game game) {
        game.resetShake();
        Music bgm = new Music(Assets.BGM_LEVEL_SELECT.get(), true);
        MusicPlayer.switchToTrack(bgm);
        super.onEnter(game);

        beachLevelButton.setOnClick(() -> {
            game.stateMachine.changeState(new ArenaScene(Level.BEACH, progress));
        });

        forestLevelButton.setOnClick(() -> {
            game.stateMachine.changeState(new ArenaScene(Level.FOREST, progress));
        });

        castleLevelButton.setOnClick(() -> {
            game.stateMachine.changeState(new ArenaScene(Level.CASTLE, progress));
        });

        backButton.setOnClick(() -> {
            game.stateMachine.changeState(new MenuScene(progress));
        });
    }

    @Override
    public void update(Game game, double delta) {
        super.update(game, delta);
        arrowAnimTime += delta * 3.0;
        arrowIndicator.y = arrowBaseY + Math.sin(arrowAnimTime) * 5.0;
    }
}