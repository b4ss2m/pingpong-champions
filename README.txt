PingPong Champions

This document provides all the necessary instructions to setup, run, and test the PingPong Champions game.

1. Setup and Requirements
Java Environment: You must have a Java Development Kit (JDK) and Java Runtime Environment (JRE) installed (version 8 or higher is recommended) to compile and run the project.
Libraries: The project uses the **Java Swing** library for the main game window and rendering. This is part of the standard Java library, so no external dependencies are required.
Assets: All required images (`assets/images`) and audio (`assets/audio`) are included in the project structure.



2. How to Run the Game
1.  Open the project in your preferred Java IDE (e.g., Eclipse, IntelliJ, VS Code) or navigate to the source directory in a terminal.
2.  Compile the project's `.java` files.
3.  Run the **`Main.java`** file.
4.  The game window will launch, displaying the `MenuScene` (Main Menu).



3. How to Play
Navigation: Use your mouse to click buttons on the Main Menu ("Continue", "New Game") and to select a stage from the Level Select map.
Serve/Smash Ball: Press `SPACEBAR` to serve the ball (at the start of a point) or to smash the ball (hit it faster) during a rally.
Move Paddle: Use the `LEFT` and `RIGHT` arrow keys to move your paddle.



4. Features to Test
Here is a step-by-step guide to testing the game's features, based on the project backlog.

Game Window
How to Test: Run `Main.java`. A window should open, showing the main menu with "Continue" and "New Game" buttons.

Rendering (Paddles & Ball)
How to Test: From the main menu, click "Continue" (or "New Game"), then click the first stage icon on the map. The `ArenaScene` should load, showing your paddle (bottom), the opponent's paddle (top), and the ball.

Player Paddle Movement
How to Test: Start a match. Press the `LEFT` and `RIGHT` arrow keys. Your paddle should move horizontally and be constrained (stop) at the screen's edges.

Ball Movement & Collision
How to Test: Serve the ball (`SPACEBAR`) and use the arrow keys to hit it. When the ball collides with your paddle, it should reverse direction and travel back toward the opponent.

Basic Opponent AI
How to Test: Play a rally. The opponent's paddle should automatically move left and right, tracking the ball's position to return it.

Scoring System
How to Test (Win a Point): Play a rally and make the opponent miss the ball. The player's score display (your score) should increase by 1.
How to Test (Lose a Point): Serve the ball and intentionally let it go past your paddle. The opponent's score display should increase by 1.

Stage Progression & Saving
How to Test: Win a match on the first stage. You should be returned to the `LevelSelectScene` (map). The second stage should now be unlocked.

Sound Effects    
How to Test: Start a match. Listen for distinct sound effects when your paddle hits the ball and when the ball hits the table.

Camera Shake
How to Test: Hit the ball with your paddle. At the exact moment of collision, the entire screen should visibly shake for a moment to add impact.

Background Music per Stage
How to Test: Start Stage 1 and listen to the music. Win the match, return to the map, and start the (now unlocked) Stage 2. A *different* background music track should be playing.