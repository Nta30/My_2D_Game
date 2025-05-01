package main;

import entity.Entity;
import entity.Player;
import tile.TileManager;
import tile_interactive.InteractiveTile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GamePanel extends JPanel implements Runnable{
    // Screen setting
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 12;
    public final int screenWidth = maxScreenCol * tileSize; // 960 pixels
    public final int screenHeight = maxScreenRow * tileSize; // 576 pixels

    // world setting
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int maxMap = 10;
    public int currentMap = 0;

    // FPS
    int fps = 60; // 60 frame per second

    // system
    TileManager tileManager = new TileManager(this);
    public KeyHandler keyHandler = new KeyHandler(this); // using to add action on keyboard
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public UI ui = new UI(this);
    public EventHandler eventHandler = new EventHandler(this);
    Config config = new Config(this);
    Thread gameThread;

    // player
    public Player player = new Player(this, keyHandler);
    // npc
    public Entity[][] npc = new Entity[maxMap][10];
    // object
    public Entity[][] obj = new Entity[maxMap][20]; // array that can store SuperObject objects
    // monster
    public Entity[][] monster = new Entity[maxMap][20];
    // interactive tile
    public InteractiveTile[][] iTile = new InteractiveTile[maxMap][50];
    //entity list(player, monster, npc, object, ...)
    ArrayList<Entity> entityList = new ArrayList<Entity>();
    public ArrayList<Entity> projectileList = new ArrayList<>();
    public ArrayList<Entity> particleList = new ArrayList<>();

    AssetSetter assetSetter = new AssetSetter(this);

    // game state
    public int gameState; // variable to store the current state of the game
    public final int titleState = 0; // constant for the "title state"
    public final int playState = 1; // constant for the "playing" state
    public final int pauseState = 2; // constant for the "pause" state
    public final int dialogueState = 3; // constant for the "dialogue"
    public final int characterState = 4; // constant for the "character status"
    public final int optionState = 5;
    public final int gameOverState = 6;
    public final int transitionState = 7;
    public final int tradeState = 8;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // reduce flickering by enable double buffering
        addKeyListener(keyHandler);
        this.setFocusable(true); // allow to receive keyboard focus
    }

    public void setupGame() {
        assetSetter.setNPC();
        assetSetter.setObject();
        assetSetter.setMonster();
        assetSetter.setInteractiveTile();
        gameState = titleState;
    }

    public void retry() {
        player.setDefaultPosition();
        player.restoreLifeAndMana();
        assetSetter.setNPC();
        assetSetter.setMonster();
    }

    public void restart() {
        player.setDefaultValues();
        player.setItems();
        assetSetter.setNPC();
        assetSetter.setObject();
        assetSetter.setMonster();
        assetSetter.setInteractiveTile();
    }

    public void startGameThread() {
        gameThread = new Thread(this); // gameThread will execute the run() method of GamePanel class
        gameThread.start();
    }

    @Override
    public void run() {

        double drawInterval = (double) 1000000000 / fps; // time per frame
        double delta = 0; // represents how many frame's worth of time have passed
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0; // check time to print FPS
        int drawCount = 0; // counts how many frame were draw

        // Game loop
        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += currentTime - lastTime;
            lastTime = currentTime;

            if(delta >= 1) {
                update(); // Update information
                repaint(); // Draw the screen with the update information
                drawCount++;
                delta--;
            }
            // println FPS every 1 second
            if(timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                timer = 0;
                drawCount = 0;
            }
        }
    }

    public void update() {
        if(gameState == playState) {
            player.update();
            for(int i=0;i<npc[currentMap].length;i++) {
                if(npc[currentMap][i] != null) {
                    npc[currentMap][i].update();
                }
            }
            for(int i=0;i<monster[currentMap].length;i++) {
                if(monster[currentMap][i] != null) {
                    if(monster[currentMap][i].alive && !monster[currentMap][i].dying){
                        monster[currentMap][i].update();
                    }
                    if(!monster[currentMap][i].alive){
                        monster[currentMap][i].checkDrop();
                        monster[currentMap][i] = null;
                    }
                }
            }
            for(int i=0;i<projectileList.size();i++){
                if(projectileList.get(i) != null){
                    if(projectileList.get(i).alive){
                        projectileList.get(i).update();
                    }
                    if(!projectileList.get(i).alive){
                        projectileList.remove(i);
                    }
                }
            }
            // paricle list
            for(int i=0;i<particleList.size();i++){
                if(particleList.get(i) != null){
                    if(particleList.get(i).alive){
                        particleList.get(i).update();
                    }
                    if(!particleList.get(i).alive){
                        particleList.remove(i);
                    }
                }
            }
            for(int i=0;i<iTile[currentMap].length;i++){
                if(iTile[currentMap][i] != null){
                    iTile[currentMap][i].update();
                }
            }
        }
        if(gameState == pauseState) {

        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // title screen
        if(gameState == titleState) {
            // UI
            ui.draw(g2d);
        }
        // others
        else {
            long drawStart = System.nanoTime();
            // tile
            tileManager.draw(g2d);

            // interactive tile
            for(int i=0;i<iTile[currentMap].length;i++){
                if(iTile[currentMap][i] != null){
                    iTile[currentMap][i].draw(g2d);
                }
            }

            // add entity to the list
            entityList.add(player);
            for(int i=0;i<npc[currentMap].length;i++){
                if(npc[currentMap][i] != null) {
                    entityList.add(npc[currentMap][i]);
                }
            }
            for(int i=0;i<obj[currentMap].length;i++){
                if(obj[currentMap][i] != null){
                    entityList.add(obj[currentMap][i]);
                }
            }
            for(int i=0;i<monster[currentMap].length;i++){
                if(monster[currentMap][i] != null){
                    entityList.add(monster[currentMap][i]);
                }
            }
            for(int i=0;i<projectileList.size();i++){
                if(projectileList.get(i) != null){
                    entityList.add(projectileList.get(i));
                }
            }
            for(int i=0;i<particleList.size();i++){
                if(particleList.get(i) != null){
                    entityList.add(particleList.get(i));
                }
            }
            // sort base on worldY
            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity e1, Entity e2) {
                    int result = Integer.compare(e1.worldY, e2.worldY);
                    return result;
                }
            });

            // draw entity
            for(int i=0;i<entityList.size();i++){
                entityList.get(i).draw(g2d);
            }
            // reset entity list
            entityList.clear();

            // UI
            ui.draw(g2d);

            // debug
            if(keyHandler.showDebugText) {
                long drawEnd = System.nanoTime();
                long passed = drawEnd - drawStart;
                g2d.setFont(new Font("Arial", Font.PLAIN, 20));
                g2d.setColor(Color.white);
                int x = 10;
                int y = 400;
                int lineHeight = 20;
                g2d.drawString("WorldX: " + player.worldX, x, y); y+=lineHeight;
                g2d.drawString("WorldY: " + player.worldY, x, y); y+=lineHeight;
                g2d.drawString("Col: " + (player.worldX + player.solidArea.x)/tileSize, x, y); y+=lineHeight;
                g2d.drawString("Row: " + (player.worldY + player.solidArea.y)/tileSize, x, y); y+=lineHeight;
                g2d.drawString("Draw time: " + passed, x, y);
            }
        }
        g2d.dispose(); // remove this graphics context and release any resource that it is using
    }

    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }
}
