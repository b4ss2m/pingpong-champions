package game.entities;

import java.awt.Image;

/**
 * serves as fundemental base type for player and opponent. can move left/right
 * and hit ball.
 */
public class Paddle extends FlipbookSprite {
    public double baseSpeed = 0.3;
    public double paddleRadius = 19.0; // in pixels
    public double maxZDistanceToIntersect = 0.05; // makes the hit detection more forgiving
    public double tableZ = -1.0;
    public boolean smash = false;
    public boolean smashAnim = false;

    protected boolean leftPressed = false;
    protected boolean rightPressed = false;
    protected int minX = 14;
    protected int maxX = 242;

    private double smashAnimTime = 0.0;
    public double horizontalVelocity = 0.0;
    private double targetLean = 0.0;
    private double acceleration = 0.0;
    private double lastMoveDirection = 0.0;

    private static final double MAX_ACCELERATION = 3.5;
    private static final double ACCELERATION_RATE = 3.0;

    public Paddle(Image[] images) {
        super(images);
    }

    /** check if the paddle is colliding or touching the ball. (radius is bigger if u "smash") */
    public boolean intersectsBall(Ball ball, boolean smash) {
        double dx = this.x - ball.x;
        double dy = this.y - ball.y;
        double screenDist = Math.sqrt(dx * dx + dy * dy);
        double zDiff = Math.abs(this.tableZ - ball.tableZ);
        double maxZDistance = maxZDistanceToIntersect;
        if (smash) {
            maxZDistance *= 5.0;
        }
        return (screenDist <= paddleRadius && zDiff <= maxZDistance);
    }

    @Override
    public void update(double delta) {
        super.update(delta);
        double moveInput = 0.0;
        double previousDirection = lastMoveDirection;

        if (leftPressed && !rightPressed) {
            moveInput = -1;
            targetLean = -25.0;
            acceleration = Math.min(acceleration + ACCELERATION_RATE * delta, MAX_ACCELERATION);
            lastMoveDirection = -1;
        } else if (rightPressed && !leftPressed) {
            moveInput = 1;
            targetLean = 25.0;
            acceleration = Math.min(acceleration + ACCELERATION_RATE * delta, MAX_ACCELERATION);
            lastMoveDirection = 1;
        } else {
            acceleration = 0.0;
            lastMoveDirection = 0.0;
            targetLean += -this.rotation * 23 * delta;
            targetLean *= Math.pow(0.995, delta * 350);
        }

        double directionChange = Math.min(0, moveInput * previousDirection);
        acceleration *= (1.0 + directionChange); // zeros acceleration when directionChange = -1

        double speedMultiplier = 1.0 + acceleration;
        horizontalVelocity += (moveInput - horizontalVelocity) * delta * 20;
        this.x += horizontalVelocity * delta * baseSpeed * speedMultiplier * (maxX - minX);
        this.x = Math.clamp(this.x, minX, maxX);
        this.rotation += (targetLean - this.rotation) * delta * 10;
        this.rotation = Math.clamp(this.rotation, -40, 40);
        setIndex(getSmashAnimIndex(delta));
    }

    private int getSmashAnimIndex(double delta) {
        if (smash && smashAnim) {
            smashAnimTime += delta * 27;
            int smashAnimIndex = images.length - (int) smashAnimTime;
            if (smashAnimIndex < 0) {
                smashAnimIndex = 0;
                smashAnimTime = 0;
                smash = false;
            }
            return smashAnimIndex;
        }
        smashAnimTime = 0;
        return 0;
    }
}