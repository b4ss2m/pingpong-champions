package game.entities;

import java.awt.Image;
import java.util.Random;

/**
 * Opponent paddle - uses the same "interface" as paddle but auto controlled.
 */
public class OpponentPaddle extends Paddle {
    Random random = new Random();

    private double serveWaitTime = 0.0;
    private double serveWaitDuration = 0.75;
    private boolean serveHasPickedSpot = false;

    private double serveMoveTimer = 0.0;
    private boolean serveMovingRight = true;

    // play style parameters
    public double responseTime = 0.15; // seconds delay before reacting to ball
    public double smashRate = 0.1; // 0.0 to 1.0 chance of smashing

    private double reactionTimer = 0.0;
    private boolean hasReacted = false;
    private double targetX = 0.0;
    private boolean shouldSmash = false;
    private boolean hasDecidedSmash = false;

    /** create flipbook opponent paddle. */
    public OpponentPaddle(Image[] images) {
        super(images);
        this.minX = 80;
        this.maxX = 175;
        this.y = 60;
    }

    /** indicates that the paddle has moved to its desired. */
    public boolean readyToServe(double delta) {
        if (!serveHasPickedSpot) {
            serveMovingRight = random.nextBoolean();
            serveHasPickedSpot = true;
        }

        // random movement to build velocity
        serveMoveTimer += delta;
        if (serveMoveTimer >= random.nextDouble() * 50.0) {
            serveMoveTimer = 0.0;
            serveMovingRight = !serveMovingRight;
        }

        if (serveMovingRight) {
            leftPressed = false;
            rightPressed = true;
        } else {
            leftPressed = true;
            rightPressed = false;
        }

        serveWaitTime += delta;
        if (serveWaitTime >= serveWaitDuration) {
            return true;
        }
        return false;
    }

    /** reset all serve related things. */
    public void resetServe() {
        serveHasPickedSpot = false;
        serveWaitTime = 0.0;
        serveMoveTimer = 0.0;
        resetReaction();
    }

    /** reset play state. */
    public void resetReaction() {
        reactionTimer = 0.0;
        hasReacted = false;
        shouldSmash = false;
        hasDecidedSmash = false;
    }

    /**
     * play logic with response time and smash decision.
     * called during rally state.
     */
    public void opponentPlay(Ball ball, double delta) {
        boolean ballComingToward = ball.velocityZ < 0;

        if (!ballComingToward) {
            if (hasReacted) {
                resetReaction();
            }
            leftPressed = false;
            rightPressed = false;
            shouldSmash = false;
            return;
        }

        if (!hasReacted) {
            reactionTimer += delta;

            if (reactionTimer >= responseTime) {
                hasReacted = true;
            } else {
                leftPressed = false;
                rightPressed = false;
                shouldSmash = false;
                return;
            }
        }

        shouldSmash |= decideSmash(ball);
        
        targetX = ball.x;
        moveToX(targetX);

        smash |= shouldSmash;
    }

    private boolean decideSmash(Ball ball) {
        if (!hasDecidedSmash) {
            double distanceToPaddle = Math.abs(ball.tableZ - (-1.0));
            if (distanceToPaddle < 0.1) {
                hasDecidedSmash = true;
                return random.nextDouble() < smashRate;
            }
        }
        return false;
    }

    /** goes to x position and returns true when reached. */
    public boolean moveToX(double targetX) {
        double threshold = 1.0;

        if (Math.abs(targetX - this.x) < threshold) {
            leftPressed = false;
            rightPressed = false;
            return true;
        } else if (this.x > targetX) {
            leftPressed = true;
            rightPressed = false;
            return false;
        } else {
            leftPressed = false;
            rightPressed = true;
            return false;
        }
    }
}