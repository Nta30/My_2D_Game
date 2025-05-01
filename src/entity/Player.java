package entity;

import main.GamePanel;
import main.KeyHandler;
import object.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Entity{
    KeyHandler keyHandler;

    public final int screenX; // player x position on screen
    public final int screenY; // player y position on screen

    public boolean attackCanceled = false;

    public Player(GamePanel gp, KeyHandler keyHandler) {
        super(gp);
        this.keyHandler = keyHandler;
        screenX = gp.screenWidth/2 - gp.tileSize/2; // center player x position on screen
        screenY = gp.screenHeight/2 - gp.tileSize/2; // center player y position on screen

        // define the collision area(hit box) of player
        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 32;
        solidArea.height = 32;

        setDefaultValues();
        getPlayerImage();
        getPlayerAttackImage();
        setItems();
    }

    // default values of player
    public void setDefaultValues() {
        worldX = gp.tileSize * 23; // player start at column 23
        worldY = gp.tileSize * 21; // player start at row 21
//        worldX = gp.tileSize * 12; // player start at column 23
//        worldY = gp.tileSize * 12; // player start at row 21
        speed = 4; // every move, player change 4px
        direction = "down";

        // player status
        level = 1;
        maxLife = 6;
        life = maxLife;
        maxMana = 4;
        mana = maxMana;
        strength = 1; // the more strength player has, the more damage he gives
        dexterity = 1; // the more dexterity player has, the less damage he receives
        exp = 0;
        nextLevelExp = 5;
        coin = 0;
        currentWeapon = new OBJ_Sword_Normal(gp);
        currentShield = new OBJ_Shield_Wood(gp);
        projectile = new OBJ_Fireball(gp);
        attack = getAttack(); // the total attack value is decided by strength and weapon
        defense = getDefense(); // the total defense value is decided by dexterity and shield
    }

    public void setDefaultPosition() {
        worldX = gp.tileSize * 23; // player start at column 23
        worldY = gp.tileSize * 21; // player start at row 21
        direction = "down";
    }

    public void restoreLifeAndMana() {
        life = maxLife;
        mana = maxMana;
        invincible = false;
    }

    public void setItems() {
        inventory.clear();
        inventory.add(currentWeapon);
        inventory.add(currentShield);
        inventory.add(new OBJ_Key(gp));
    }

    public int getAttack() {
        attackArea = currentWeapon.attackArea;
        return attack = strength * currentWeapon.attackValue;
    }

    public int getDefense() {
        return defense = dexterity * currentShield.defenseValue;
    }

    // load character image based on direction
    public void getPlayerImage() {
        up1 = setup("/player/boy_up_1", gp.tileSize, gp.tileSize);
        up2 = setup("/player/boy_up_2", gp.tileSize, gp.tileSize);
        down1 = setup("/player/boy_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/player/boy_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/player/boy_left_1", gp.tileSize, gp.tileSize);
        left2 = setup("/player/boy_left_2", gp.tileSize, gp.tileSize);
        right1 = setup("/player/boy_right_1", gp.tileSize, gp.tileSize);
        right2 = setup("/player/boy_right_2", gp.tileSize, gp.tileSize);
    }

    public void getPlayerAttackImage() {

        if(currentWeapon.type == type_Sword){
            attackUp1 = setup("/player/boy_attack_up_1", gp.tileSize, gp.tileSize*2);
            attackUp2 = setup("/player/boy_attack_up_2", gp.tileSize, gp.tileSize*2);
            attackDown1 = setup("/player/boy_attack_down_1", gp.tileSize, gp.tileSize*2);
            attackDown2 = setup("/player/boy_attack_down_2", gp.tileSize, gp.tileSize*2);
            attackLeft1 = setup("/player/boy_attack_left_1", gp.tileSize*2, gp.tileSize);
            attackLeft2 = setup("/player/boy_attack_left_2", gp.tileSize*2, gp.tileSize);
            attackRight1 = setup("/player/boy_attack_right_1", gp.tileSize*2, gp.tileSize);
            attackRight2 = setup("/player/boy_attack_right_2", gp.tileSize*2, gp.tileSize);
        }

        if(currentWeapon.type == type_Axe){
            attackUp1 = setup("/player/boy_axe_up_1", gp.tileSize, gp.tileSize*2);
            attackUp2 = setup("/player/boy_axe_up_2", gp.tileSize, gp.tileSize*2);
            attackDown1 = setup("/player/boy_axe_down_1", gp.tileSize, gp.tileSize*2);
            attackDown2 = setup("/player/boy_axe_down_2", gp.tileSize, gp.tileSize*2);
            attackLeft1 = setup("/player/boy_axe_left_1", gp.tileSize*2, gp.tileSize);
            attackLeft2 = setup("/player/boy_axe_left_2", gp.tileSize*2, gp.tileSize);
            attackRight1 = setup("/player/boy_axe_right_1", gp.tileSize*2, gp.tileSize);
            attackRight2 = setup("/player/boy_axe_right_2", gp.tileSize*2, gp.tileSize);
        }

    }

    public void update() {

        if(attacking){
            attacking();
        }
        else if(keyHandler.upPressed || keyHandler.downPressed ||
                keyHandler.leftPressed || keyHandler.rightPressed) {

            if(keyHandler.upPressed){
                direction = "up";
                collision();
                if(!collisionOn){
                    worldY -= speed;
                }
            }
            if(keyHandler.leftPressed){
                direction = "left";
                collision();
                if(!collisionOn){
                    worldX -= speed;
                }
            }
            if(keyHandler.downPressed){
                direction = "down";
                collision();
                if(!collisionOn){
                    worldY += speed;
                }

            }
            if(keyHandler.rightPressed){
                direction = "right";
                collision();
                if(!collisionOn){
                    worldX += speed;
                }
            }
            // check event
            gp.eventHandler.checkEvent();

            //check player attack
            if(keyHandler.enterPressed && !attackCanceled){
                gp.playSE(7);
                attacking = true;
                spriteCounter = 0;
            }
            attackCanceled = false;

            spriteCounter++;
            if(spriteCounter > 12){
                if(spriteNumber == 1){
                    spriteNumber = 2;
                }else if(spriteNumber == 2){
                    spriteNumber = 1;
                }
                spriteCounter = 0;
            }
        }else {
            direction = "down";
        }

        if(gp.keyHandler.shootKeyPressed && !projectile.alive && shootAvailableCounter == 30 &&
                projectile.haveResource(this)){

            // set default coordinates, direction and user
            projectile.set(worldX, worldY, direction, true, this);
            // add it to array list
            gp.projectileList.add(projectile);
            gp.playSE(10);
            shootAvailableCounter = 0;

            // subtract the cost (mana)
            projectile.subTractResource(this);
        }
        // count time player can shoot fireball
        if(shootAvailableCounter < 30){
            shootAvailableCounter++;
        }

        // counts how long the player has been invincible
        if(invincible) {
            invincibleCounter++;
            if(invincibleCounter > 60){
                invincible = false;
                invincibleCounter = 0;
            }
        }
        if(life > maxLife){
            life = maxLife;
        }
        if(mana > maxMana){
            mana = maxMana;
        }
        // check if player is still life??
        if(life<=0){
            gp.gameState = gp.gameOverState;
            gp.ui.commandNumber = -1;
            gp.playSE(12);
            gp.stopMusic();
        }
    }

    public void attacking() {
        spriteCounter++;

        if(spriteCounter <= 5){
            spriteNumber = 1;
        }
        if(spriteCounter > 5 && spriteCounter <= 25){
            spriteNumber = 2;

            // save the current worldX, worldY, solidArea
            int curentWorldX = worldX;
            int currentWorldY = worldY;
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;

            // adjust player's worldX/Y for the attackArea
            switch(direction){
                case "up": worldY -= attackArea.height; break;
                case "down": worldY += attackArea.height; break;
                case "left": worldX -= attackArea.width; break;
                case "right": worldX += attackArea.width; break;
            }
            // attack area becomes solidArea
            solidArea.width = attackArea.width;
            solidArea.height = attackArea.height;
            // check monster collision with the updated worldX, worldY, solidArea
            int monterIndex = gp.collisionChecker.checkEntity(this,gp.monster);
            damageMonster(monterIndex, attack);

            int iTileIndex = gp.collisionChecker.checkEntity(this, gp.iTile);
            damageInteractiveTile(iTileIndex);

            // after checking collision, restore the original data
            worldX = curentWorldX;
            worldY = currentWorldY;
            solidArea.width = solidAreaWidth;
            solidArea.height = solidAreaHeight;

        }
        if(spriteCounter > 25){
            spriteNumber = 1;
            spriteCounter = 0;
            attacking = false;
        }
    }

    public void pickUpObject(int i) {
        String text;

        if (i != 999) {

            // pick up only items
            if(gp.obj[gp.currentMap][i].type == type_PickupOnly){ // fixed
                gp.obj[gp.currentMap][i].use(this); // fixed
                gp.obj[gp.currentMap][i] = null; // fixed
            }
            else {
                // inventory items
                if(inventory.size() != maxInventorySize){
                    inventory.add(gp.obj[gp.currentMap][i]); //fixed
                    gp.playSE(1);
                    text = "Got a " + gp.obj[gp.currentMap][i].name + "!"; //fixed
                }else{
                    text = "You cannot carry more!";
                }
                gp.ui.addMessage(text);
                gp.obj[gp.currentMap][i] = null; //fixed
            }
        }
    }

    public void interactNPC(int i) {
        if(gp.keyHandler.enterPressed){
            if(i != 999){
                attackCanceled = true;
                gp.gameState = gp.dialogueState;
                gp.npc[gp.currentMap][i].speak(); //fixed
            }
        }
    }

    public void contactMonster(int i) {
        if(i != 999){
            if(!invincible && !gp.monster[gp.currentMap][i].dying){ // fixed
                gp.playSE(6);

                int damage = gp.monster[gp.currentMap][i].attack - gp.player.defense; // fixed
                if(damage < 0){
                    damage = 0;
                }

                life -= damage;
                invincible = true;
            }
        }
    }

    public void damageMonster(int i, int attack) {
        if(i != 999){
            if(!gp.monster[gp.currentMap][i].invincible){ // fixed
                gp.playSE(5);

                int damage = attack - gp.monster[gp.currentMap][i].defense; // fixed
                if(damage < 0){
                    damage = 0;
                }

                gp.monster[gp.currentMap][i].life -= damage; // fixed
                gp.ui.addMessage(damage + " damage!");
                gp.monster[gp.currentMap][i].invincible = true; // fixed
                gp.monster[gp.currentMap][i].damageReaction(); // when taking dame, the monster reacts // fixed

                if(gp.monster[gp.currentMap][i].life <= 0){ // fixed
                    gp.monster[gp.currentMap][i].dying = true; // fixed
                    gp.ui.addMessage("Killed the " + gp.monster[gp.currentMap][i].name + "!"); // fixed
                    gp.ui.addMessage("Exp + " + gp.monster[gp.currentMap][i].exp); // fixed
                    exp += gp.monster[gp.currentMap][i].exp; // fixed
                    checkLevelUp();
                }
            }
        }
    }

    public void damageInteractiveTile(int i) {
        if(i != 999 && gp.iTile[gp.currentMap][i].destructible && gp.iTile[gp.currentMap][i].isCorrectItem(this) &&
            !gp.iTile[gp.currentMap][i].invincible) { // fixed

            gp.iTile[gp.currentMap][i].playeSE(); // fixed
            gp.iTile[gp.currentMap][i].life--; // fixed
            gp.iTile[gp.currentMap][i].invincible = true; // fixed
            // generate particle
            generateParticle(gp.iTile[gp.currentMap][i],gp.iTile[gp.currentMap][i]); // fixed

            if(gp.iTile[gp.currentMap][i].life == 0){ // fixed
                gp.iTile[gp.currentMap][i] = gp.iTile[gp.currentMap][i].getDestroydedForm(); // fixed
            }
        }
    }

    public void checkLevelUp() {
        if(exp >= nextLevelExp){
            level++;
            nextLevelExp *= 2;
            maxLife += 2;
            strength++;
            dexterity++;
            attack = getAttack();
            defense = getDefense();
            gp.playSE(8);
            gp.gameState = gp.dialogueState;
            gp.ui.currentDialogue = "You are level " + level + " now\nYou feel stronger!";
        }
    }

    public void selectItem() {
        int itemIndex = gp.ui.getItemIndexOnSlot(gp.ui.playerSlotCol, gp.ui.playerSlotRow);

        if(itemIndex < inventory.size()) {
            Entity selectedItem = inventory.get(itemIndex);
            if(selectedItem.type == type_Sword || selectedItem.type == type_Axe){
                currentWeapon = selectedItem;
                attack = getAttack();
                getPlayerAttackImage();
            }
            if(selectedItem.type == type_Shield){
                currentShield = selectedItem;
                defense = getDefense();
            }
            if(selectedItem.type == type_Consumable){
                selectedItem.use(this);
                inventory.remove(itemIndex);
            }
        }

    }

    public void draw(Graphics2D g2d) {
        BufferedImage image = null;
        int tempScreenX = screenX;
        int tempScreenY = screenY;

        switch (direction) {
            case "up":
                if(!attacking){
                    if(spriteNumber == 1) { image = up1; }
                    if(spriteNumber == 2) { image = up2; }
                }
                if(attacking){
                    tempScreenY = screenY - gp.tileSize;
                    if(spriteNumber == 1) { image = attackUp1; }
                    if(spriteNumber == 2) { image = attackUp2; }
                }
                break;
            case "left":
                if(!attacking){
                    if(spriteNumber == 1) { image = left1; }
                    if(spriteNumber == 2) { image = left2; }
                }
                if(attacking){
                    tempScreenX = screenX - gp.tileSize;
                    if(spriteNumber == 1) { image = attackLeft1; }
                    if(spriteNumber == 2) { image = attackLeft2; }
                }
                break;
            case "down":
                if(!attacking){
                    if(spriteNumber == 1) { image = down1; }
                    if(spriteNumber == 2) { image = down2; }
                }
                if(attacking){
                    if(spriteNumber == 1) { image = attackDown1; }
                    if(spriteNumber == 2) { image = attackDown2; }
                }
                break;
            case "right":
                if(!attacking){
                    if(spriteNumber == 1) { image = right1; }
                    if(spriteNumber == 2) { image = right2; }
                }
                if(attacking){
                    if(spriteNumber == 1) { image = attackRight1; }
                    if(spriteNumber == 2) { image = attackRight2; }
                }
                break;
        }
        if(invincible){
            // apply 30% opacity to player image
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        }
        g2d.drawImage(image, tempScreenX, tempScreenY, null);
        // reset opacity
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
    public void collision() {
        collisionOn = false;
        gp.collisionChecker.checkTile(this);
        pickUpObject(gp.collisionChecker.checkObject(this,true));
        interactNPC(gp.collisionChecker.checkEntity(this, gp.npc));
        contactMonster(gp.collisionChecker.checkEntity(this,gp.monster));
        gp.collisionChecker.checkEntity(this, gp.iTile);
    }
}
