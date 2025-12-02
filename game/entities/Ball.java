package game.entities;

import game.Assets;
import game.audio.Sound;
import game.core.Game;
import java.awt.*;

/**
 * My implementation of projecting a 3d ball into 2d screen coordinates.
 * takes a point (x,y,z) in world space & maps to screenspace
 * camera is hardcoded to be tilted 30degrees
 * table coordinates are normalized (-1 to 1) and scaled to TABLE_WIDTH & LENGTH
 * height is used to offset the ball in world space
 * screen position (x, y) and scale (scaleX, scaleY) are updated based on depth.
 */
public class Ball extends Sprite {
    public double tableX = 999.0; // normalized cords: -1 to 1 (rlly high rn so its not visible)
    public double tableZ = 0.0; // normalized cords: -1 to 1
    public double height = 0.0; // meters
    public double baseSpeed = 0.8;
    public double smashSpeedMult = 1.0;

    public double velocityX = 0.0; // table units per second
    public double velocityZ = 0.0; // table units per second
    public double velocityY = 0.0; // vertical velocity in meters per second

    public boolean wasHit = false;

    public double minArchHeight = 0.03; // minimum peak height
    public double maxArchHeight = 0.22; // maximum peak height

    private double archHeight = 0.1; // current arch peak height
    private double bounceTableZ = 0.0; // where the ball will bounce
    private boolean isInArch = false;

    private static final double GRAVITY = 4.0; // gravity constant
    private static final double PADDLE_HEIGHT_MAX = 0.24; // max height at paddle

    private static final double CAMERA_Y = 1.5;
    private static final double CAMERA_Z = 3.0;
    private static final double FOV = 50.0; // degrees
    private static final double CAMERA_ROTATION = 30.0; // camera angle

    private static final double SIN_ALPHA = Math.sin(Math.toRadians(CAMERA_ROTATION));
    private static final double COS_ALPHA = Math.cos(Math.toRadians(CAMERA_ROTATION));

    public static final double TABLE_WIDTH = 1.5;
    public static final double TABLE_LENGTH = 3;

    private final double centerX = Game.VIRTUAL_WIDTH / 2;
    private final double centerY = Game.VIRTUAL_HEIGHT / 2;

    // scale factor to convert real distance to screen distance
    private final double fPx = (Game.VIRTUAL_HEIGHT / 2.0) / Math.tan(Math.toRadians(FOV / 2.0));
    private Sprite shadow = new Sprite(Assets.BALL_SHADOW.get(), 0.5);
    private Sprite outline = new Sprite(Assets.BALL_OUTLINE.get(), 0.5);

    /**
     * new ball.
     */
    public Ball(Image image) {
        super(image);
        updateScreenPosition();
    }

    @Override
    public void update(double delta) {
        checkCenterLineCrossing(delta * smashSpeedMult * baseSpeed);
        updateHorizontalPosition(delta * smashSpeedMult * baseSpeed);
        updateVerticalMotion(delta * smashSpeedMult * baseSpeed);
        updateScreenPosition();
    }

    private void checkCenterLineCrossing(double delta) {
        // reset wasHit when crossing the center line to prevent double hit
        if (Math.signum(tableZ) * Math.signum(tableZ + velocityZ * delta) == -1) {
            wasHit = false;
        }
    }

    private void updateHorizontalPosition(double delta) {
        tableX += velocityX * delta;
        tableZ += velocityZ * delta;
    }

    private void updateVerticalMotion(double delta) {
        if (!isInArch) {
            return;
        }

        applyGravity(delta);
        height += velocityY * delta;

        if (isBallOffTable()) {
            handleOffTablePhysics();
        } else if (height <= 0.0) {
            handleTableBounce();
        }
    }

    private void applyGravity(double delta) {
        velocityY -= GRAVITY * delta;
    }

    private boolean isBallOffTable() {
        return Math.abs(tableX) > 1.0 || Math.abs(tableZ) > 1.0;
    }

    private void handleOffTablePhysics() {
        if (height < -1.0) {
            height = -1.0;
            velocityY = 0.0;
            isInArch = false;
        }
    }

    private void handleTableBounce() {
        height = 0.0;
        new Sound(Assets.SFX_TABLE_HIT.get()).play();
        double naturalBounceVelocity = -velocityY * 0.7; // 70% energy retained
        double velocitySquared = naturalBounceVelocity * naturalBounceVelocity;
        double maxHeightAfterBounce = velocitySquared / (2.0 * GRAVITY);

        if (maxHeightAfterBounce > PADDLE_HEIGHT_MAX) {
            velocityY = Math.sqrt(2.0 * GRAVITY * PADDLE_HEIGHT_MAX);
        } else {
            velocityY = naturalBounceVelocity;
        }

        if (Math.abs(velocityY) < 0.5) {
            velocityY = 0.0;
            isInArch = false;
        }
    }

    @Override
    public void render(Graphics2D g) {
        shadow.render(g);
        outline.render(g);
        super.render(g);
    }

    /**
     * transform table coordinates to screen coordinates.
     */
    private void updateScreenPosition() {
        projectToScreen(this, height);
        projectToScreen(outline, height);
        projectToScreen(shadow, 0.0);
    }

    /**
     * project 3D point (tableX, tableZ, height) to screen coords.
     */
    private void projectToScreen(Sprite sprite, double h) {
        // normalized coords to world coords
        double worldX = tableX * TABLE_WIDTH / 2;
        double worldZ = tableZ * TABLE_LENGTH / 2;

        double dy = h - CAMERA_Y;
        double dz = worldZ - CAMERA_Z;

        // camera space coords
        double xc = worldX;
        double yc = COS_ALPHA * dy - SIN_ALPHA * dz;
        double zc = SIN_ALPHA * dy + COS_ALPHA * dz;

        double depth = -zc;

        // project to screen
        sprite.x = centerX + fPx * (xc / depth);
        sprite.y = centerY - fPx * (yc / depth);

        // scale based on depth
        sprite.scaleX = 1.0 / depth;
        sprite.scaleY = 1.0 / depth;
        if (sprite == shadow) {
            double heightFactor = 1.0 / (1.0 + height * 2.0);
            sprite.scaleX *= heightFactor;
            sprite.scaleY *= heightFactor;
            if (Math.abs(tableX) > 1 || Math.abs(tableZ) > 1) {
                sprite.opacity = 0.0;
            } else {
                sprite.opacity = 1.0;
            }
        }
    }

    /**
     * updates only tableX.
     */
    public void setScreenPositionX(double screenX) {
        double worldZ = tableZ * TABLE_LENGTH / 2;
        double dy = height - CAMERA_Y;
        double dz = worldZ - CAMERA_Z;
        double zc = SIN_ALPHA * dy + COS_ALPHA * dz;
        double depth = -zc;

        // solve for worldX
        double worldX = ((screenX - centerX) / fPx) * depth;
        this.tableX = worldX / (TABLE_WIDTH / 2);

        updateScreenPosition();
    }

    /**
     * updates world height.
     */
    public void setScreenPositionY(double screenY) {
        double dy = height - CAMERA_Y;
        double ratio = (centerY - screenY) / fPx;
        double denominator = SIN_ALPHA - COS_ALPHA * ratio;
        double dz = dy * (COS_ALPHA + SIN_ALPHA * ratio) / denominator;
        double worldZ = CAMERA_Z + dz;
        this.tableZ = worldZ / (TABLE_LENGTH / 2);
        updateScreenPosition();
    }

    /**
     * hits the ball by reflecting its velocity and intelligently adjusts direction
     * also initiates parabola arch based on speed.
     */
    public void hit(double speed, double nudge, boolean smash) {
        if (wasHit) {
            return;
        }

        handleSound(smash);

        velocityZ = -velocityZ;
        velocityX += nudge;

        normalizeAndSetVelocity(speed);

        if (smash) {
            velocityX *= 2.0;
            smashSpeedMult += 0.35;
            game.shake(smashSpeedMult * 4.0);
        } else {
            smashSpeedMult = 1.0;
        }

        adjustVelocityIfOutOfBounds(speed);
        adjustVelocityForEdgePosition(speed);

        calcArchParameters(speed, smash);
        initiateArch();
        wasHit = true;
    }

    /**
     * just a soft hit (non-smash) but with a bit of camera shake.
     */
    public void serveHit(double speed, double nudge) {
        game.shake(3.75);
        hit(speed, nudge, false);
    }

    /**
     * calc arch height based on speed.
     * smash reduces arch height
     */
    private void calcArchParameters(double speed, boolean smash) {
        double speedFactor = Math.clamp((speed - 2.5) / 4.5, 0.0, 1.0);

        double baseArch = minArchHeight + (maxArchHeight - minArchHeight) * 0.5;

        double speedArchReduction = speedFactor * 0.7;
        archHeight = baseArch * (1.0 - speedArchReduction);

        if (smash) {
            archHeight *= 0.1;
        }
        archHeight = Math.clamp(archHeight, minArchHeight * 0.5, maxArchHeight);
    }

    private void handleSound(boolean smash) {
        if (smash) {
            new Sound(Assets.SFX_SMASH.get()).play();
        }
        if (tableZ > 0) {
            new Sound(Assets.SFX_OPPONENT_HIT.get()).play();
        } else {
            new Sound(Assets.SFX_PLAYER_HIT.get()).play();
        }
    }

    /**
     * initiates a parabola arch by calculating initial vertical velocity.
     */
    private void initiateArch() {
        // low speed should bounce near center (0.2-0.4)
        // high speed bounce near edge (0.7-0.9)
        double speed = Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
        double speedFactor = Math.clamp((speed - 1.0) / 2.0, 0.0, 1.0);
        double bounceDistance = 0.25 + speedFactor * 0.6;
        bounceDistance = Math.min(bounceDistance, 0.95);

        double targetZ = -Math.signum(tableZ) * bounceDistance;
        bounceTableZ = targetZ;

        double distanceZ = Math.abs(bounceTableZ - tableZ);
        double timeToReachBounce = distanceZ / Math.abs(velocityZ);
        // using kinematic equation v = root(2*g*h) so upward velocity reaches height h
        velocityY = Math.sqrt(2.0 * GRAVITY * archHeight);
        // adjust if the calculated trajectory doesnt match our timing
        double calcdFlightTime = 2.0 * velocityY / GRAVITY;
        if (calcdFlightTime > 0) {
            double timeScale = timeToReachBounce / calcdFlightTime;
            velocityY *= timeScale;
        }

        isInArch = true;
    }

    private void normalizeAndSetVelocity(double speed) {
        double[] normalized = normalizeVelocity(velocityX, velocityZ, speed);
        velocityX = normalized[0];
        velocityZ = normalized[1];
    }

    private void adjustVelocityIfOutOfBounds(double speed) {
        double distanceToTravel = Math.abs(tableZ) + Math.abs(tableZ);
        double timeToReachEnd = Math.abs(distanceToTravel / velocityZ);
        double predictedTableX = tableX + velocityX * timeToReachEnd;

        double maxSafeX = 0.96;
        double minSafeX = -0.96;

        if (predictedTableX > maxSafeX || predictedTableX < minSafeX) {
            double targetX = (predictedTableX > maxSafeX) ? maxSafeX : minSafeX;
            double safeVelocityX = (targetX - tableX) / timeToReachEnd;

            double[] normalized = normalizeVelocity(safeVelocityX, velocityZ, speed);
            velocityX = normalized[0];
            velocityZ = normalized[1];
        }
    }

    private void adjustVelocityForEdgePosition(double speed) {
        if (isAtRightEdge()) {
            velocityX = -Math.abs(velocityX) * 0.5;
            normalizeAndSetVelocity(speed);
        } else if (isAtLeftEdge()) {
            velocityX = Math.abs(velocityX) * 0.5;
            normalizeAndSetVelocity(speed);
        }
    }

    private boolean isAtRightEdge() {
        return tableX > 1.0 && velocityX > 0;
    }

    private boolean isAtLeftEdge() {
        return tableX < -1.0 && velocityX < 0;
    }

    private double[] normalizeVelocity(double vx, double vz, double targetSpeed) {
        double currentSpeed = Math.sqrt(vx * vx + vz * vz);
        if (currentSpeed < 0.00001) {
            return new double[] { 0.0, 0.0 };
        } else {
            double normalizedVx = (vx / currentSpeed) * targetSpeed;
            double normalizedVz = (vz / currentSpeed) * targetSpeed;
            return new double[] { normalizedVx, normalizedVz };
        }
    }
}