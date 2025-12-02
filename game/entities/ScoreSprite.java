package game.entities;

import java.awt.*;

/** score indicator & handler. */
public class ScoreSprite extends Sprite {
    public int playerScore = 0;
    public int oppScore = 0;

    public double playerScoreX = 108;
    public double playerScoreY = 26;
    public double oppScoreX = 147;
    public double oppScoreY = 26;
    
    private double baseScoreY = 26;

    public Color playerColor = Color.RED;
    public Color oppColor = Color.BLACK;

    private Font scoreFont = new Font("Monospaced", Font.BOLD, 24);
    private double playerAnimationTime = 0.0; // little bounce when you increment score
    private double oppAnimationTime = 0.0;

    public ScoreSprite(Image image) {
        super(image);
    }

    /** add 1 to player score and animate a bit. */
    public void incrementPlayerScore() {
        playerScore++;
        playerAnimationTime = 0.0;
    }

    /** add 1 to opp score and animate a bit. */
    public void incrementOpponentScore() {
        oppScore++;
        oppAnimationTime = 0.0;
    }

    @Override
    public void update(double delta) {
        playerAnimationTime += delta;
        if (playerAnimationTime < 2 * Math.PI / 15) {
            playerScoreY = baseScoreY - Math.sin(playerAnimationTime * 15) * 5.0;
        } else {
            playerScoreY = baseScoreY;
        }

        oppAnimationTime += delta;
        if (oppAnimationTime < 2 * Math.PI / 15) {
            oppScoreY = baseScoreY - Math.sin(oppAnimationTime * 15) * 5.0;
        } else {
            oppScoreY = baseScoreY;
        }
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);
        g.setTransform(new java.awt.geom.AffineTransform());
        g.setFont(scoreFont);
        FontMetrics m = g.getFontMetrics();

        renderCentered(g, m, String.valueOf(playerScore), playerScoreX, playerScoreY, playerColor);
        renderCentered(g, m, String.valueOf(oppScore), oppScoreX, oppScoreY, oppColor);
    }

    private void renderCentered(Graphics2D g,
            FontMetrics m,
            String text,
            double x,
            double y,
            Color c) {
        g.setColor(c);
        g.drawString(text, (float) (x - m.stringWidth(text) / 2.0),
                (float) (y + (m.getAscent() - m.getDescent()) / 2.0));
    }
}