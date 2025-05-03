package main;

import entity.Entity;
import object.OBJ_Coin_Bronze;
import object.OBJ_Heart;
import object.OBJ_ManaCrystal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UI {

    GamePanel gp;
    Graphics2D g2d;
    Font maruMonica, purisaB; // font of game
    BufferedImage heartFull, heartHalf, heartBlank, crystalFull, crystalBlank, coin;
    ArrayList<String> message = new ArrayList<>();
    ArrayList<Integer> messageCounter = new ArrayList<Integer>();
    public String  currentDialogue = "";
    public int commandNumber = 0; // use to check the command in tile screen
    public int titleScreenState = 0; // the current state of the title screen (0: the first screen, 1: second, ...)
    public int playerSlotCol = 0;
    public int playerSlotRow = 0;
    public int npcSlotCol = 0;
    public int npcSlotRow = 0;
    int subState;
    int counter = 0;
    public Entity npc;

    public UI(GamePanel gp) {
        this.gp = gp;
        try {
            InputStream is = getClass().getResourceAsStream("/font/Purisa Bold.ttf");
            purisaB = Font.createFont(Font.TRUETYPE_FONT, is);
            is = getClass().getResourceAsStream("/font/x12y16pxMaruMonica.ttf");
            maruMonica = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create heart object
        Entity heart = new OBJ_Heart(gp);
        heartFull = heart.image;
        heartHalf = heart.image2;
        heartBlank = heart.image3;

        // create mana crystal object
        Entity manaCrystal = new OBJ_ManaCrystal(gp);
        crystalFull = manaCrystal.image;
        crystalBlank = manaCrystal.image2;
        Entity bronzeCoin = new OBJ_Coin_Bronze(gp);
        coin = bronzeCoin.down1;
    }

    public void addMessage(String text) {
        message.add(text);
        messageCounter.add(0);
    }

    public void draw(Graphics2D g2d) {
        this.g2d = g2d;
        g2d.setFont(maruMonica);
        g2d.setColor(Color.white);

        // title state
        if(gp.gameState == gp.titleState) {
            drawTitleScreen();
        }
        // play state
        if(gp.gameState == gp.playState) {
            drawPlayerLifeAndMana();
            drawMessage();
        }
        // pause state
        if(gp.gameState == gp.pauseState) {
            drawPlayerLifeAndMana();
            drawPauseScreen();
        }
        // dialogue state
        if(gp.gameState == gp.dialogueState) {
            drawDialogueScreen();
        }
        // character status state
        if(gp.gameState == gp.characterState) {
            drawCharacterScreen();
            drawInventory(gp.player, true);
        }
        // option state
        if(gp.gameState == gp.optionState) {
            drawOptionScreen();
        }
        // game over state
        if(gp.gameState == gp.gameOverState) {
            drawGameOverState();
        }
        // transition
        if(gp.gameState == gp.transitionState) {
            drawTransition();
        }
        // trade state
        if(gp.gameState == gp.tradeState) {
            drawTradeState();
        }
    }

    public void drawPlayerLifeAndMana() {
        int x = gp.tileSize/2;
        int y = gp.tileSize/2;
        int i = 0;
        // draw blank heart
        while(i < gp.player.maxLife/2) {
            g2d.drawImage(heartBlank, x, y, null);
            i++;
            x += gp.tileSize;
        }
        // reset
        x = gp.tileSize/2;
        y = gp.tileSize/2;
        i = 0;
        while(i < gp.player.life){
            g2d.drawImage(heartHalf, x, y, null);
            i++;
            if(i<gp.player.life) {
                g2d.drawImage(heartFull, x, y, null);
            }
            i++;
            x += gp.tileSize;
        }

        // draw max mana
        x = gp.tileSize/2;
        y = gp.tileSize/2 + gp.tileSize;
        i = 0;
        while(i < gp.player.maxMana) {
            g2d.drawImage(crystalBlank, x, y, null);
            x += 35;
            i++;
        }
        // draw mana
        x= gp.tileSize/2;
        y = gp.tileSize/2 + gp.tileSize;
        i = 0;
        while(i < gp.player.mana) {
            g2d.drawImage(crystalFull, x, y, null);
            x += 35;
            i++;
        }

    }

    public void drawMessage() {
        int messageX = gp.tileSize;
        int messageY = gp.tileSize*4;
        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 32));

        for(int i=0;i<message.size();i++){
            if(message.get(i) != null){
                g2d.setColor(Color.black);
                g2d.drawString(message.get(i), messageX+2, messageY+2);
                g2d.setColor(Color.white);
                g2d.drawString(message.get(i), messageX, messageY);

                int counter = messageCounter.get(i)+1;
                messageCounter.set(i,counter); // messageCounter[i]++
                messageY += 50;

                if(messageCounter.get(i) > 180){
                    message.remove(i);
                    messageCounter.remove(i);
                }
            }
        }
    }

    public void drawTitleScreen() {

        g2d.setColor(new Color(0,0,0));
        g2d.fillRect(0,0 ,gp.screenWidth,gp.screenHeight);

        // title name
        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 96)); // create new font based on current font
        String text = "Blue Boy Adventure";
        int x = getXForCenteredText(text);
        int y = gp.tileSize*3;

        // shadow
        g2d.setColor(Color.gray);
        g2d.drawString(text, x+5, y+5);

        // main text
        g2d.setColor(Color.white);
        g2d.drawString(text, x, y);

        // character image
        x = gp.screenWidth/2 - (gp.tileSize*2)/2;
        y += gp.tileSize*2;
        g2d.drawImage(gp.player.down1, x, y, gp.tileSize*2, gp.tileSize*2, null);

        // menu
        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 48));

        text = "NEW GAME";
        x = getXForCenteredText(text);
        y += gp.tileSize*3.5;
        g2d.drawString(text, x, y);
        if(commandNumber == 0) {
            g2d.drawString(">", x-gp.tileSize, y);
        }

        text = "LOAD GAME";
        x = getXForCenteredText(text);
        y += gp.tileSize;
        g2d.drawString(text, x, y);
        if(commandNumber == 1) {
            g2d.drawString(">", x-gp.tileSize, y);
        }

        text = "QUIT";
        x = getXForCenteredText(text);
        y += gp.tileSize;
        g2d.drawString(text, x, y);
        if(commandNumber == 2) {
            g2d.drawString(">", x-gp.tileSize, y);
        }
    }

    public void drawPauseScreen() {
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 80));
        String text = "PAUSED";
        int x = getXForCenteredText(text);
        int y = gp.screenHeight / 2;

        g2d.drawString(text, x, y);
    }

    public void drawDialogueScreen() {
        // window
        int x = gp.tileSize*3;
        int y = gp.tileSize/2;
        int width = gp.screenWidth - (gp.tileSize*6);
        int height = gp.tileSize*4;
        drawSubWindow(x, y, width, height);

        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 32));
        x += gp.tileSize/2;
        y += gp.tileSize;
        for(String line : currentDialogue.split("\n")){
            g2d.drawString(line, x, y);
            y += 40;
        }
    }

    public void drawCharacterScreen() {

        // create a frame
        final int frameX = gp.tileSize;
        final int frameY = gp.tileSize;
        final int frameWidth = gp.tileSize*5;
        final int frameHeight = gp.tileSize*10;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // text
        g2d.setColor(Color.white);
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 32));

        // name status
        int textX = frameX + 20;
        int textY = frameY + gp.tileSize;
        final int lineHeight = 35; // the height of each line
        g2d.drawString("Level", textX, textY); textY += lineHeight;
        g2d.drawString("Life", textX, textY); textY += lineHeight;
        g2d.drawString("Mana", textX, textY); textY += lineHeight;
        g2d.drawString("Strength", textX, textY); textY += lineHeight;
        g2d.drawString("Dexterity", textX, textY); textY += lineHeight;
        g2d.drawString("Attack", textX, textY); textY += lineHeight;
        g2d.drawString("Defense", textX, textY); textY += lineHeight;
        g2d.drawString("Exp", textX, textY); textY += lineHeight;
        g2d.drawString("Next Level", textX, textY); textY += lineHeight;
        g2d.drawString("Coin", textX, textY); textY += lineHeight + 10;
        g2d.drawString("Weapon", textX, textY); textY += lineHeight + 15;
        g2d.drawString("Shield", textX, textY); textY += lineHeight;
        // value
        int tailX = (frameX + frameWidth) - 30;
        // reset textY
        textY = frameY + gp.tileSize;
        String value;

        value = String.valueOf(gp.player.level);
        textX = getXForAlignToRightText(value, tailX);
        g2d.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.life + "/" + gp.player.maxLife);
        textX = getXForAlignToRightText(value, tailX);
        g2d.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.mana + "/" + gp.player.maxMana);
        textX = getXForAlignToRightText(value, tailX);
        g2d.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.strength);
        textX = getXForAlignToRightText(value, tailX);
        g2d.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.dexterity);
        textX = getXForAlignToRightText(value, tailX);
        g2d.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.attack);
        textX = getXForAlignToRightText(value, tailX);
        g2d.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.defense);
        textX = getXForAlignToRightText(value, tailX);
        g2d.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.exp);
        textX = getXForAlignToRightText(value, tailX);
        g2d.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.nextLevelExp);
        textX = getXForAlignToRightText(value, tailX);
        g2d.drawString(value, textX, textY);
        textY += lineHeight;

        value = String.valueOf(gp.player.coin);
        textX = getXForAlignToRightText(value, tailX);
        g2d.drawString(value, textX, textY);
        textY += lineHeight;

        g2d.drawImage(gp.player.currentWeapon.down1, tailX - gp.tileSize, textY-25, null);
        textY += gp.tileSize;
        g2d.drawImage(gp.player.currentShield.down1, tailX - gp.tileSize, textY-25, null);
    }

    public void drawInventory(Entity entity, boolean cursor) {

        int frameX = 0;
        int frameY = 0;
        int frameWidth = 0;
        int frameHeight = 0;
        int slotCol = 0;
        int slotRow = 0;

        if(entity == gp.player){
            frameX = gp.tileSize*12;
            frameY = gp.tileSize;
            frameWidth = gp.tileSize*6;
            frameHeight = gp.tileSize*5;
            slotCol = playerSlotCol;
            slotRow = playerSlotRow;
        }
        else{
            frameX = gp.tileSize*2;
            frameY = gp.tileSize;
            frameWidth = gp.tileSize*6;
            frameHeight = gp.tileSize*5;
            slotCol = npcSlotCol;
            slotRow = npcSlotRow;
        }

        // frame

        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // slot
        final int slotXStart = frameX + 20;
        final int slotYStart = frameY + 20;
        int slotX = slotXStart;
        int slotY = slotYStart;
        int slotSize = gp.tileSize + 3;

        // draw player's items
        for(int i=0;i<entity.inventory.size();i++){
            // equip cursor
            if(entity.inventory.get(i) == entity.currentWeapon ||
                    entity.inventory.get(i) == entity.currentShield){
                g2d.setColor(new Color(240, 190, 90));
                g2d.fillRoundRect(slotX, slotY, slotSize, slotSize, 10, 10);
            }

            // items
            g2d.drawImage(entity.inventory.get(i).down1, slotX, slotY, null);
            slotX += slotSize;
            if(i == 4 || i == 9 || i == 14){
                slotX = slotXStart;
                slotY += slotSize;
            }
        }

        // cursor
        if(cursor){
            int cursorX = slotXStart + (slotSize*slotCol);
            int cursorY = slotYStart + (slotSize*slotRow);
            int cursorWidth = slotSize;
            int cursorHeight = slotSize;

            //draw cursor
            g2d.setColor(Color.white);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);

            // description frame
            int dFrameX = frameX;
            int dFrameY = frameY + frameHeight;
            int dFrameWidth = frameWidth;
            int dFrameHeight = gp.tileSize*3;
            g2d.setStroke(new BasicStroke(5));

            // draw description text
            int textX = dFrameX + 20;
            int textY = dFrameY + gp.tileSize;
            g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 28));

            int itemIndex = getItemIndexOnSlot(slotCol, slotRow);
            if(itemIndex < entity.inventory.size()){
                // draw description window
                drawSubWindow(dFrameX, dFrameY, dFrameWidth, dFrameHeight);
                // split \n in description
                for(String line : entity.inventory.get(itemIndex).description.split("\n")){
                    g2d.drawString(line, textX, textY);
                    textY += 32;
                }
            }
        }

    }

    public void drawGameOverState() {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int x;
        int y;
        String text;
        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 110));

        text = "Game Over";
        // shadow
        g2d.setColor(Color.black);
        x = getXForCenteredText(text);
        y = gp.tileSize*4;
        g2d.drawString(text, x, y);
        // text
        g2d.setColor(Color.white);
        g2d.drawString(text, x-4, y-4);

        // retry
        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 50));
        text = "Retry";
        x = getXForCenteredText(text);
        y += gp.tileSize*4;
        g2d.drawString(text, x, y);
        if(commandNumber == 0){
            g2d.drawString(">", x-gp.tileSize/2, y);
        }

        // back to the title screen
        text = "Quit";
        x = getXForCenteredText(text);
        y += gp.tileSize + 10;
        g2d.drawString(text, x, y);
        if(commandNumber == 1){
            g2d.drawString(">", x-gp.tileSize/2, y);
        }
    }

    public void drawOptionScreen() {
        g2d.setColor(Color.white);
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 32));

        // sub window
        int frameX = gp.tileSize*6;
        int frameY = gp.tileSize + gp.tileSize/2;
        int frameWidth = gp.tileSize*8;
        int frameHeight = gp.tileSize*9;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        switch(subState){
            case 0:
                optionTop(frameX, frameY);
                break;
            case 1:
                optionsControl(frameX, frameY);
                break;
            case 2:
                optionEndGameConfirmation(frameX, frameY);
                break;
        }
        gp.keyHandler.enterPressed = false;
    }

    public void optionTop(int frameX, int frameY) {
        int textX;
        int textY;

        // title
        String text = "Options";
        textX = getXForCenteredText(text);
        textY = frameY + gp.tileSize;
        g2d.drawString(text, textX, textY);

        // music
        text = "Music";
        textX = frameX + gp.tileSize;
        textY += gp.tileSize*2;
        g2d.drawString(text, textX, textY);
        if(commandNumber == 0){
            g2d.drawString(">", textX - gp.tileSize/2, textY);
        }

        // sound effect
        text = "Sound Effect";
        textY += gp.tileSize;
        g2d.drawString(text, textX, textY);
        if(commandNumber == 1){
            g2d.drawString(">", textX - gp.tileSize/2, textY);
        }

        // control
        text = "Control";
        textY += gp.tileSize;
        g2d.drawString(text, textX, textY);
        if(commandNumber == 2){
            g2d.drawString(">", textX - gp.tileSize/2, textY);
            if(gp.keyHandler.enterPressed){
                subState = 1;
                commandNumber = 0;
            }
        }

        // end game
        text = "End Game";
        textY += gp.tileSize;
        g2d.drawString(text, textX, textY);
        if(commandNumber == 3){
            g2d.drawString(">", textX - gp.tileSize/2, textY);
            if(gp.keyHandler.enterPressed){
                subState = 2;
                commandNumber = 0;
            }
        }

        // back
        text = "Back";
        textY += gp.tileSize*2;
        g2d.drawString(text, textX, textY);
        if(commandNumber == 4){
            g2d.drawString(">", textX - gp.tileSize/2, textY);
            if(gp.keyHandler.enterPressed){
                gp.gameState = gp.playState;
                commandNumber = 0;
            }
        }

        // music volume
        textX = frameX +  gp.tileSize*5;
        textY = frameY + gp.tileSize*2 + gp.tileSize/2;
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(textX, textY, 120, 24); // 120/5 = 24
        int volumeWidth = 24 * gp.music.volumeScale;
        g2d.fillRect(textX, textY, volumeWidth, 24);

        // sound effect
        textX = frameX +  gp.tileSize*5;
        textY += gp.tileSize;
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(textX, textY, 120, 24); // 120/5 = 24
        volumeWidth = 24 * gp.se.volumeScale;
        g2d.fillRect(textX, textY, volumeWidth, 24);

        gp.config.saveConfig();
    }

    public void optionsControl(int frameX, int frameY) {
        int textX;
        int textY;

        // title
        String text = "Control";
        textX = getXForCenteredText(text);
        textY = frameY + gp.tileSize;
        g2d.drawString(text, textX, textY);

        textX = frameX + gp.tileSize;
        textY += gp.tileSize;
        g2d.drawString("Move", textX, textY); textY += gp.tileSize;
        g2d.drawString("Confirm/Attack", textX, textY); textY += gp.tileSize;
        g2d.drawString("Shoot/Cast", textX, textY); textY += gp.tileSize;
        g2d.drawString("Character Screen", textX, textY); textY += gp.tileSize;
        g2d.drawString("Pause", textX, textY); textY += gp.tileSize;
        g2d.drawString("Options", textX, textY); textY += gp.tileSize;

        textX = frameX + gp.tileSize*6;
        textY = frameY + gp.tileSize*2;
        g2d.drawString("WASD", textX, textY); textY += gp.tileSize;
        g2d.drawString("ENTER", textX, textY); textY += gp.tileSize;
        g2d.drawString("F", textX, textY); textY += gp.tileSize;
        g2d.drawString("C", textX, textY); textY += gp.tileSize;
        g2d.drawString("P", textX, textY); textY += gp.tileSize;
        g2d.drawString("ESC", textX, textY); textY += gp.tileSize;

        // back
        textX = frameX + gp.tileSize;
        textY = frameY + gp.tileSize*8;
        g2d.drawString("Back", textX, textY);
        if(commandNumber==0){
            g2d.drawString(">", textX - gp.tileSize/2, textY);
            if(gp.keyHandler.enterPressed){
                subState = 0;
                commandNumber = 2;
            }
        }
    }

    public void optionEndGameConfirmation(int frameX, int frameY) {
        int textX = frameX + gp.tileSize;
        int textY = frameY + gp.tileSize*2;

        currentDialogue = "Quit the game and\nreturn to the title screen?";
        for(String line: currentDialogue.split("\n")){
            g2d.drawString(line, textX, textY);
            textY += 40;
        }

        // yes
        String text = "Yes";
        textX = getXForCenteredText(text);
        textY += gp.tileSize*3;
        g2d.drawString(text, textX, textY);
        if(commandNumber == 0){
            g2d.drawString(">", textX - gp.tileSize/2, textY);
            if(gp.keyHandler.enterPressed){
                subState = 0;
                gp.gameState = gp.titleState;
                commandNumber = 0;
                gp.music.stop();
            }
        }

        // no
        text = "No";
        textX = getXForCenteredText(text);
        textY += gp.tileSize;
        g2d.drawString(text, textX, textY);
        if(commandNumber == 1){
            g2d.drawString(">", textX - gp.tileSize/2, textY);
            if(gp.keyHandler.enterPressed){
                subState = 0;
                commandNumber = 3;
            }
        }

    }

    public void drawTransition() {
        counter++;
        g2d.setColor(new Color(0, 0, 0, counter*5));
        g2d.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        if(counter == 50) {
            counter = 0;
            gp.gameState = gp.playState;
            gp.currentMap = gp.eventHandler.tempMap;
            gp.player.worldX = gp.eventHandler.tempCol * gp.tileSize;
            gp.player.worldY = gp.eventHandler.tempRow * gp.tileSize;
            gp.eventHandler.previousEventX = gp.player.worldX;
            gp.eventHandler.previousEventY = gp.player.worldY;
        }
    }

    public void drawTradeState() {
        switch(subState) {
            case 0: tradeSelect(); break;
            case 1: tradeBuy(); break;
            case 2: tradeSell(); break;
        }
        gp.keyHandler.enterPressed = false;
    }

    public void tradeSelect() {
        drawDialogueScreen();

        // draw window
        int x = gp.tileSize * 15;
        int y = gp.tileSize * 4;
        int width = gp.tileSize*3;
        int height = gp.tileSize*4 - gp.tileSize/2;
        drawSubWindow(x, y, width, height);

        // draw texts
        x += gp.tileSize;
        y += gp.tileSize;
        g2d.drawString("Buy", x, y);
        if(commandNumber == 0){
            g2d.drawString(">", x - gp.tileSize/2, y);
            if(gp.keyHandler.enterPressed){
                subState = 1;
            }
        }
        y += gp.tileSize;
        g2d.drawString("Sell", x, y);
        if(commandNumber == 1){
            g2d.drawString(">", x - gp.tileSize/2, y);
            if(gp.keyHandler.enterPressed){
                subState = 2;
            }
        }
        y += gp.tileSize;
        g2d.drawString("Leave", x, y);
        if(commandNumber == 2){
            g2d.drawString(">", x - gp.tileSize/2, y);
            if(gp.keyHandler.enterPressed){
                commandNumber = 0;
                gp.gameState = gp.dialogueState;
                currentDialogue = "Come again, here!";
            }
        }
    }

    public void tradeBuy() {
        // draw player inventory
        drawInventory(gp.player, false);
        // draw npc inventory
        drawInventory(npc, true);

        // draw hint window
        int x = gp.tileSize*2;
        int y = gp.tileSize*9;
        int width = gp.tileSize*6;
        int height = gp.tileSize*2;
        drawSubWindow(x, y, width, height);
        g2d.drawString("[ESC] Back", x+24, y+60);

        // draw player coin window
        x = gp.tileSize*12;
        y = gp.tileSize*9;
        width = gp.tileSize*6;
        height = gp.tileSize*2;
        drawSubWindow(x, y, width, height);
        g2d.drawString("Your coin: " + gp.player.coin, x+24, y+60);

        // draw price window
        int itemIndex = getItemIndexOnSlot(npcSlotCol, npcSlotRow);
        if(itemIndex < npc.inventory.size()){
            x = gp.tileSize*6 - gp.tileSize/2;
            y = gp.tileSize*6 - gp.tileSize/2;
            width = gp.tileSize*3 - gp.tileSize/2;
            height = gp.tileSize;
            drawSubWindow(x, y, width, height);
            g2d.drawImage(coin, x+10, y+8, 32, 32, null);

            int price = npc.inventory.get(itemIndex).price;
            String text = "" + price;
            x = getXForAlignToRightText(text, gp.tileSize*8-20);
            g2d.drawString(text, x, y+34);

            // buy an item
            if(gp.keyHandler.enterPressed){
                if(npc.inventory.get(itemIndex).price > gp.player.coin){
                    subState = 0;
                    gp.gameState = gp.dialogueState;
                    currentDialogue = "You need more coin to buy that!";
                    drawDialogueScreen();
                }
                else if(gp.player.inventory.size() == gp.player.maxInventorySize){
                    subState = 0;
                    gp.gameState = gp.dialogueState;
                    currentDialogue = "You cannot carry any more!";
                }
                else{
                    gp.player.coin -= npc.inventory.get(itemIndex).price;
                    gp.player.inventory.add(npc.inventory.get(itemIndex));
                }
            }
        }

    }

    public void tradeSell() {
        // draw player inventory
        drawInventory(gp.player, true);

        int x;
        int y;
        int width;
        int height;

        // draw hint window
        x = gp.tileSize*2;
        y = gp.tileSize*9;
        width = gp.tileSize*6;
        height = gp.tileSize*2;
        drawSubWindow(x, y, width, height);
        g2d.drawString("[ESC] Back", x+24, y+60);

        // draw player coin window
        x = gp.tileSize*12;
        y = gp.tileSize*9;
        width = gp.tileSize*6;
        height = gp.tileSize*2;
        drawSubWindow(x, y, width, height);
        g2d.drawString("Your coin: " + gp.player.coin, x+24, y+60);

        // draw price window
        int itemIndex = getItemIndexOnSlot(playerSlotCol, playerSlotRow);
        if(itemIndex < gp.player.inventory.size()){
            x = gp.tileSize*16 - gp.tileSize/2;
            y = gp.tileSize*6 - gp.tileSize/2;
            width = gp.tileSize*3 - gp.tileSize/2;
            height = gp.tileSize;
            drawSubWindow(x, y, width, height);
            g2d.drawImage(coin, x+10, y+8, 32, 32, null);

            int price = gp.player.inventory.get(itemIndex).price/2;
            String text = "" + price;
            x = getXForAlignToRightText(text, gp.tileSize*18-20);
            g2d.drawString(text, x, y+34);

            // sell an item
            if(gp.keyHandler.enterPressed){
                if(gp.player.inventory.get(itemIndex) == gp.player.currentWeapon ||
                    gp.player.inventory.get(itemIndex) == gp.player.currentShield){
                    commandNumber = 0;
                    subState = 0;
                    gp.gameState = gp.dialogueState;
                    currentDialogue = "You cannot sell an equipped item!";
                }
                else{
                    gp.player.inventory.remove(itemIndex);
                    gp.player.coin += price;
                }
            }
        }
    }

    public int getItemIndexOnSlot(int slotCol, int slotRow) {
        int itemIndex = slotCol + (slotRow*5);
        return itemIndex;
    }

    public void drawSubWindow(int x, int y, int width, int height) {

        Color c = new Color(0, 0, 0, 210);
        g2d.setColor(c);
        g2d.fillRoundRect(x, y, width, height, 35, 35); // fill rect with border

        c = new Color(255, 255, 255);
        g2d.setColor(c);
        g2d.setStroke(new BasicStroke(5));
        g2d.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
    }

    public int getXForCenteredText(String text) {
        // get the width of the text in pixels
        int length = (int) g2d.getFontMetrics().getStringBounds(text,g2d).getWidth();
        int x = gp.screenWidth/2 - length/2;
        return x;
    }

    public int getXForAlignToRightText(String text, int tailX) {
        // get the width of the text in pixels
        int length = (int) g2d.getFontMetrics().getStringBounds(text,g2d).getWidth();
        int x = tailX - length;
        return x;
    }
}
