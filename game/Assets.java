package game;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * registry / catalogue of all the assets in the game (sorry i know it is ugly)
 * call loadAll at startup, because assets must be loaded once at the start.
 */
public enum Assets {
    MENU_BG("assets/images/menu/bg.png"),
    MENU_BG_OVERLAY("assets/images/menu/bg_overlay.png"),
    MENU_CLOUDS("assets/images/menu/bg_clouds.png"),
    MENU_LOGO("assets/images/menu/logo.png"),
    MENU_NEWGAME_BUTTON("assets/images/menu/newgame_button.png"),
    MENU_NEWGAME_HOVER("assets/images/menu/newgame_button_hover.png"),
    MENU_NEWGAME_PRESS("assets/images/menu/newgame_button_press.png"),
    MENU_CONTINUE_BUTTON("assets/images/menu/contine_button.png"),
    MENU_CONTINUE_HOVER("assets/images/menu/contine_button_hover.png"),
    MENU_CONTINUE_PRESS("assets/images/menu/contine_button_press.png"),
    MENU_CONTINUE_INACTIVE("assets/images/menu/contine_button_inactive.png"),

    BACK("assets/images/back.png"),
    BACK_HOVER("assets/images/back_hover.png"),
    BACK_PRESS("assets/images/back_press.png"),

    MAP_BG("assets/images/map/map_bg.png"),
    MAP_BG_OVERLAY("assets/images/map/map_bg_overlay.png"),
    MAP_SELECTLEVEL("assets/images/map/select_a_level.png"),
    MAP_INDICATOR_ARROW("assets/images/map/indicator_arrow.png"),
    MAP_LOCKED("assets/images/map/locked.png"),
    MAP_LEVEL1BUTTON("assets/images/map/button1.png"),
    MAP_LEVEL1BUTTON_PRESS("assets/images/map/button1_press.png"),
    MAP_LEVEL1BUTTON_LABEL("assets/images/map/level1_text.png"),
    MAP_LEVEL2BUTTON("assets/images/map/button2.png"),
    MAP_LEVEL2BUTTON_PRESS("assets/images/map/button2_press.png"),
    MAP_LEVEL2BUTTON_LABEL("assets/images/map/level2_text.png"),
    MAP_LEVEL3BUTTON("assets/images/map/button3.png"),
    MAP_LEVEL3BUTTON_PRESS("assets/images/map/button3_press.png"),
    MAP_LEVEL3BUTTON_LABEL("assets/images/map/level3_text.png"),

    WIN("assets/images/win.png"),
    LOSE("assets/images/lose.png"),

    BEACH_BG("assets/images/beach/bg.png"),
    BEACH_TABLE("assets/images/beach/table.png"),
    BEACH_TUT1("assets/images/beach/tutorial1.png"),
    BEACH_TUT2("assets/images/beach/tutorial2.png"),
    BEACH_TUT3("assets/images/beach/tutorial3.png"),
    BEACH_TUT4("assets/images/beach/tutorial4.png"),
    BEACH_OPP_PADDLE("assets/images/beach/opp_paddle.png"),
    BEACH_OPP_PADDLE_ANIM_1("assets/images/beach/opp_paddle_anim1.png"),
    BEACH_OPP_PADDLE_ANIM_2("assets/images/beach/opp_paddle_anim2.png"),

    FOREST_BG("assets/images/forest/bg.png"),
    FOREST_TABLE("assets/images/forest/table.png"),
    FOREST_OVERLAY("assets/images/forest/rain_overlay.png"),
    FOREST_OPP_PADDLE("assets/images/forest/opp_paddle.png"),
    FOREST_OPP_PADDLE_ANIM_1("assets/images/forest/opp_paddle_anim1.png"),
    FOREST_OPP_PADDLE_ANIM_2("assets/images/forest/opp_paddle_anim2.png"),

    CASTLE_BG("assets/images/castle/bg.png"),
    CASTLE_TABLE("assets/images/castle/table.png"),
    CASTLE_OPP_PADDLE("assets/images/castle/opp_paddle.png"),
    CASTLE_OPP_PADDLE_ANIM_1("assets/images/castle/opp_paddle_anim1.png"),
    CASTLE_OPP_PADDLE_ANIM_2("assets/images/castle/opp_paddle_anim2.png"),

    SCORE_CNTR("assets/images/score_counter.png"),
    PLAYER_PADDLE("assets/images/player_paddle.png"),
    PLAYER_PADDLE_ANIM_1("assets/images/player_paddle_anim1.png"),
    PLAYER_PADDLE_ANIM_2("assets/images/player_paddle_anim2.png"),
    PLAYER_PADDLE_ANIM_3("assets/images/player_paddle_anim3.png"),
    BALL("assets/images/ball.png"),
    BALL_SHADOW("assets/images/ball_shadow.png"),
    BALL_OUTLINE("assets/images/ball_outline.png"),
    ICON("assets/images/icon.png"),

    // MUSIC

    BGM_BEACH("assets/audio/music/beach.wav"),
    BGM_FOREST("assets/audio/music/forest.wav"),
    BGM_CASTLE("assets/audio/music/castle.wav"),
    BGM_LEVEL_SELECT("assets/audio/music/level_select.wav"),
    BGM_TITLE("assets/audio/music/title_screen.wav"),
    BGM_WIN("assets/audio/music/win.wav"),
    BGM_LOSE("assets/audio/music/lose.wav"),
    BGM_PROLOGUE("assets/audio/music/prologue.wav"),

    // SOUND FX

    SFX_PLAYER_HIT("assets/audio/player_hit.wav"),
    SFX_OPPONENT_HIT("assets/audio/opponent_hit.wav"),
    SFX_SMASH("assets/audio/smash.wav"),
    SFX_TABLE_HIT("assets/audio/table_hit.wav"),
    SFX_WHISTLE("assets/audio/whistle.wav");

    private final String path;
    private static final Map<Assets, Object> CACHE = new EnumMap<>(Assets.class);

    Assets(String path) {
        this.path = path;
    }

    /**
     * initialize and cache all assets. we call this once atthe game startup.
     */
    public static void loadAll() {
        for (Assets asset : Assets.values()) {
            Object loaded = asset.load();
            CACHE.put(asset, loaded);
        }
    }

    /**
     * get cached asset.
     */
    public <T> T get() {
        return (T) CACHE.get(this);
    }

    /**
     * load asset from path and infer the type from its extension.
     */
    private <T> T load() {
        String lower = path.toLowerCase();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (lower.endsWith(".png")) {
                return (T) ImageIO.read(is);
            } else if (lower.endsWith(".wav")) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(is);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                return (T) clip;
            }
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
        return null;
    }
}