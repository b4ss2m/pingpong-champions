import game.core.Game;

/**
 * creates the game with a name, and runs it.
 */
public class Main {
    public static void main(String[] args) {
        Game game = new Game("Ping-Pong Champions");
        game.start();
    }
}
