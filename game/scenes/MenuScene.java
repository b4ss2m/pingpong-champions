package game.scenes;

import game.Assets;
import game.Save;
import game.audio.Music;
import game.audio.MusicPlayer;
import game.core.Game;
import game.core.Scene;
import game.entities.Button;
import game.entities.FadeEffectSprite;
import game.entities.ScrollingSprite;
import game.entities.Sprite;
import java.io.IOException;

/**
 * main menu screen (greeter).
 */
public class MenuScene extends Scene {
    Sprite background = add(new Sprite(Assets.MENU_BG.get()));
    ScrollingSprite clouds = add(new ScrollingSprite(Assets.MENU_CLOUDS.get(), 7.5, 0));
    Sprite overlay = add(new Sprite(Assets.MENU_BG_OVERLAY.get()));
    Sprite logo = add(new Sprite(Assets.MENU_LOGO.get(), Game.VIRTUAL_WIDTH / 2, -500));
    Sprite continueInactive = add(new Sprite(Assets.MENU_CONTINUE_INACTIVE.get(), 128, 155, 0.0));
    Button newGameButton = add(new Button(Assets.MENU_NEWGAME_BUTTON.get(),
            Assets.MENU_NEWGAME_HOVER.get(),
            Assets.MENU_NEWGAME_PRESS.get(),
            128, 128, 0.0));

    Button continueButton = add(new Button(Assets.MENU_CONTINUE_BUTTON.get(),
            Assets.MENU_CONTINUE_HOVER.get(),
            Assets.MENU_CONTINUE_PRESS.get(),
            128, 155, 0.0));

    FadeEffectSprite fade = add(new FadeEffectSprite(0.5));

    private final double logoStartY = -60; // logo starting height
    private final double logoTargetY = 45; // logo landing pos

    private double logoAnimTime = 0;
    private boolean logoHasLanded = false;
    private double logoShakeTime = 0;
    private float bgmWaitTime = 0;

    private boolean hasProgress; // determines if the continue button should be active
    Save progress = null;

    /** create new scene & read save data (progress). */
    public MenuScene(Save progress) {
        Music bgm = new Music(Assets.BGM_TITLE.get(), true, 57352);
        MusicPlayer.switchToTrack(bgm);
        this.progress = progress;
        if (!progress.isUnlocked(1)) {
            hasProgress = false;
        } else {
            hasProgress = true;
        }
    }

    @Override
    public void onEnter(Game game) {
        game.resetShake();
        super.onEnter(game);
        continueButton.setOnClick(() -> {
            game.stateMachine.changeState(new LevelSelectScene(progress));
        }); // keep in mind the button wont work unless its visible (opacity > 0.3)

        newGameButton.setOnClick(() -> {
            progress.reset();
            try {
                progress.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            game.stateMachine.changeState(new LevelSelectScene(progress));
        });
    }

    @Override
    public void update(Game game, double delta) {
        super.update(game, delta);

        bgmWaitTime += delta;
        if (bgmWaitTime <= 0.175) {
            return;
        }

        if (!logoHasLanded) {
            logoAnimTime += delta;
            double t = Math.min(logoAnimTime, 1.0);
            double eased = t * t * t; // cubic easing function
            logo.y = logoStartY + (logoTargetY - logoStartY) * eased;

            if (t >= 1.0) {
                logoHasLanded = true;
                logo.y = logoTargetY;
                game.shake(5.0);
            }
        } else { // animate shake
            logoShakeTime += delta;
            double shakeIntensity = Math.max(0, 1 - (logoShakeTime / 0.25));
            logo.rotation = Math.sin(logoShakeTime * 45) * 3 * shakeIntensity;
            logo.y = logoTargetY - Math.sin(logoShakeTime * 35) * 1.7 * shakeIntensity;
            // fade in the buttons
            newGameButton.opacity += (1.0 - newGameButton.opacity) * delta;
            continueInactive.opacity += (1.0 - continueInactive.opacity) * delta;
            if (hasProgress) {
                continueButton.opacity += (1.0 - newGameButton.opacity) * delta;
            }
        }
    }
}
