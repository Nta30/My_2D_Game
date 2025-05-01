package entity;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Entity {

    // image, collision area and dialogue
    GamePanel gp;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2; // image character with action
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2, attackLeft1, attackLeft2, attackRight1, attackRight2;
    public BufferedImage image, image2, image3; // image of object
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48); // collision detection area for the entity(smaller than a tile)
    public Rectangle attackArea = new Rectangle(0, 0, 0, 0);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collision = false;
    String[] dialogues = new String[20];

    // state
    public int worldX,worldY; // entity location on world map
    public String direction = "down"; // the direction the character is moving
    public int spriteNumber = 1; // chooses which sprite image to show
    int dialogueIndex = 0; // control which dialogue is currently show in an NPC conversation
    public boolean collisionOn = false; // true if this entity is currently colliding with an obstacle
    public boolean invincible = false; // true if player is temporarily invincible after taking dame
    public boolean attacking = false;
    public boolean alive = true;
    public boolean dying = false;
    boolean hpBarOn = false;

    // counter
    public int spriteCounter = 0; // count frames to change animation image
    public int actionLockCounter = 0; // count the number of frame before allowing the entity change direction
    public int invincibleCounter = 0; // counts the number of frame the player has been invincible
    public int shootAvailableCounter = 0;
    public int dyingCounter = 0;
    int hpBarCounter = 0;

    // inventory
    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 20;

    // character attributes
    public String name;
    public int speed;
    public int maxLife; // max heart of entity
    public int life; // current heart of entity
    public int maxMana;
    public int mana;
    public int level;
    public int strength;
    public int dexterity;
    public int attack;
    public int defense;
    public int exp;
    public int nextLevelExp;
    public int coin; // how much money player has
    public Entity currentWeapon;
    public Entity currentShield;
    public Projectile projectile;

    // item attributes
    public int value;
    public int attackValue;
    public int defenseValue;
    public String description = "";
    public int useCost;
    public int price;

    // type
    public int type; // 0: player, 1: npc, 2: monster
    public final int type_Player = 0;
    public final int type_NPC = 1;
    public final int type_Monster = 2;
    public final int type_Sword = 3;
    public final int type_Axe = 4;
    public final int type_Shield = 5;
    public final int type_Consumable = 6;
    public final int type_PickupOnly = 7;

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public BufferedImage setup(String imagePath, int width, int height) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;

        try{
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
            image = uTool.scaledImage(image, width, height);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void setAction() {

    }

    public void damageReaction() {

    }

    public void speak() {
        if(dialogues[dialogueIndex] == null) {
            dialogueIndex = 0;
        }
        gp.ui.currentDialogue = dialogues[dialogueIndex];
        dialogueIndex++;

        switch(gp.player.direction){
            case "up":
                direction = "down";
                break;
            case "down":
                direction = "up";
                break;
            case "left":
                direction = "right";
                break;
            case "right":
                direction = "left";
                break;
        }
    }

    public void use(Entity entity) {

    }

    public void checkDrop() {

    }

    public void dropItem(Entity droppedItem) {
        for(int i=0;i<gp.obj[gp.currentMap].length;i++){
            if(gp.obj[gp.currentMap][i] == null){
                gp.obj[gp.currentMap][i] = droppedItem;
                gp.obj[gp.currentMap][i].worldX = worldX; // position monster dead
                gp.obj[gp.currentMap][i].worldY = worldY;
                break;
            }
        }
    }

    public Color getParticleColor() {
        Color color = null;
        return color;
    }

    public int getParticleSize() {
        int size = 0; // 6 pixel
        return size;
    }

    public int getParticleSpeed() {
        int speed = 0;
        return speed;
    }

    public int getParticleMaxLife() {
        int maxLife = 0;
        return maxLife;
    }

    // generates particle effects at the target entity's
    public void generateParticle(Entity generator, Entity target) {
        Color color = generator.getParticleColor();
        int size = generator.getParticleSize();
        int speed = generator.getParticleSpeed();
        int maxLife = generator.getParticleMaxLife();

        Particle p1 = new Particle(gp, target, color, size, speed, maxLife, -2, -1);
        Particle p2 = new Particle(gp, target, color, size, speed, maxLife, 2, -1);
        Particle p3 = new Particle(gp, target, color, size, speed, maxLife, -2, 1);
        Particle p4 = new Particle(gp, target, color, size, speed, maxLife, 2, 1);
        gp.particleList.add(p1);
        gp.particleList.add(p2);
        gp.particleList.add(p3);
        gp.particleList.add(p4);
    }

    public void update() {
        setAction();

        collisionOn = false;
        gp.collisionChecker.checkTile(this);
        gp.collisionChecker.checkObject(this,false);
        gp.collisionChecker.checkEntity(this, gp.npc);
        gp.collisionChecker.checkEntity(this, gp.monster);
        gp.collisionChecker.checkEntity(this, gp.iTile);
        boolean contactPlayer = gp.collisionChecker.checkPlayer(this);

        if(this.type == type_Monster && contactPlayer) {
            damagePlayer(attack);
        }

        if(!collisionOn) {
            switch(direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
            }
            spriteCounter++;
            if(spriteCounter > 12){
                if(spriteNumber == 1){
                    spriteNumber = 2;
                }else if(spriteNumber == 2){
                    spriteNumber = 1;
                }
                spriteCounter = 0;
            }
        }
        // count how long entity has been invincible
        if(invincible) {
            invincibleCounter++;
            if(invincibleCounter > 40){
                invincible = false;
                invincibleCounter = 0;
            }
        }
        if(shootAvailableCounter < 30){
            shootAvailableCounter++;
        }
    }

    public void damagePlayer(int attack) {
        if(!gp.player.invincible) {
            gp.playSE(6);

            int damage = attack - gp.player.defense;
            if(damage < 0){
                damage = 0;
            }

            gp.player.life -= damage;
            gp.player.invincible = true;
        }
    }

    public void draw(Graphics2D g2d) {

        BufferedImage image = null;

        switch (direction) {
            case "up":
                if(spriteNumber == 1){ image = up1; }
                if(spriteNumber == 2){ image = up2; }
                break;
            case "left":
                if(spriteNumber == 1){ image = left1; }
                if(spriteNumber == 2){ image = left2; }
                break;
            case "down":
                if(spriteNumber == 1){ image = down1; }
                if(spriteNumber == 2){ image = down2; }
                break;
            case "right":
                if(spriteNumber == 1){ image = right1; }
                if(spriteNumber == 2){ image = right2; }
                break;
        }

        int screenX = worldX - gp.player.worldX + gp.player.screenX; // tile x position in the screen
        int screenY = worldY - gp.player.worldY + gp.player.screenY; // tile y position in the screen

        if(worldX + gp.tileSize > gp.player.worldX -gp.player.screenX &&
                worldX - gp.player.screenX < gp.player.worldX + gp.player.screenX  &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY  &&
                worldY - gp.player.screenX < gp.player.worldY + gp.player.screenY
        ){
            // entity hp bar
            if(type == 2 && hpBarOn){
                double oneScale = (double) gp.tileSize/maxLife; // length of 1 hp
                double hpBarValue = oneScale*life; // current length of hp bar

                g2d.setColor(new Color(35, 35, 35));
                g2d.fillRect(screenX-1, screenY-11, gp.tileSize+2, 12);

                g2d.setColor(new Color(255, 0, 30));
                g2d.fillRect(screenX , screenY - 10, (int) hpBarValue, 10);

                hpBarCounter++;
                if(hpBarCounter > 600){
                    hpBarOn = false;
                    hpBarCounter = 0;
                }
            }


            if(invincible){
                hpBarOn = true;
                hpBarCounter = 0;
                // apply 30% opacity to entity image
                changeAlpha(g2d,0.4f);
            }
            if(dying){
                dyingAnimation(g2d);
            }
            g2d.drawImage(image, screenX, screenY, null);
            // reset opacity
            changeAlpha(g2d,1f);
        }
    }
    public void dyingAnimation(Graphics2D g2d) {
        dyingCounter++;
        int i = 5;

        if(dyingCounter <= i){ changeAlpha(g2d,0f); }
        if(dyingCounter > i && dyingCounter <= i*2){ changeAlpha(g2d,1f); }
        if(dyingCounter > i*2 && dyingCounter <= i*3){ changeAlpha(g2d,0f); }
        if(dyingCounter > i*3 && dyingCounter <= i*4){ changeAlpha(g2d,1f); }
        if(dyingCounter > i*4 && dyingCounter <= i*5){ changeAlpha(g2d,0f); }
        if(dyingCounter > i*5 && dyingCounter <= i*6){ changeAlpha(g2d,1f); }
        if(dyingCounter > i*6 && dyingCounter <= i*7){ changeAlpha(g2d,0f); }
        if(dyingCounter > i*7 && dyingCounter <= i*8){ changeAlpha(g2d,1f); }
        if(dyingCounter > i*8){
            alive = false;
        }
    }

    public void changeAlpha(Graphics2D g2d, float alphaValue) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
    }
}
