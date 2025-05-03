package main;


import entity.Entity;

import java.awt.*;

public class EventHandler {
    GamePanel gp;
    EventRect[][][] eventRect;
    int previousEventX, previousEventY; // stores the last event position to prevent repeated activation
    boolean canTouchEvent = true; // flag to determine if the player can trigger an event
    int tempMap, tempCol, tempRow;

    public EventHandler(GamePanel gp) {
        this.gp = gp;
        eventRect = new EventRect[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];

        for(int map=0;map<gp.maxMap;map++){
            for(int row=0;row<gp.maxWorldRow;row++){
                for(int col=0;col<gp.maxWorldCol;col++){
                    eventRect[map][col][row] = new EventRect();
                    eventRect[map][col][row].x = 23;
                    eventRect[map][col][row].y = 23;
                    eventRect[map][col][row].width = 2;
                    eventRect[map][col][row].height = 2;
                    eventRect[map][col][row].eventRectDefaultX = eventRect[map][col][row].x;
                    eventRect[map][col][row].eventRectDefaultY = eventRect[map][col][row].y;
                }
            }
        }
    }

    public void checkEvent() {
        // check if the player charater is more than 1 tile away from the last event
        int xDistance = Math.abs(gp.player.worldX - previousEventX);
        int yDistance = Math.abs(gp.player.worldY - previousEventY);
        int distance = Math.max(xDistance, yDistance);
        if(distance>gp.tileSize){
            canTouchEvent = true;
        }
        if(canTouchEvent) {
            if(hit(0,27,16,"any")){
                damagePit(gp.dialogueState);
            }
            if(hit(0,23,12,"any")){
                healingPool(gp.dialogueState);
            }
            else if(hit(0,10,39, "any")){
                teleport(1, 12, 13);
            }
            else if(hit(1, 12, 13, "any")){
                teleport(0,10,39);
            }
            else if(hit(1, 12, 9, "up")){
                speak(gp.npc[1][0]);
            }
        }
    }

    public boolean hit(int map, int col, int row, String requiredDirection) {
        boolean hit = false;

        if(map == gp.currentMap){
            gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
            gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;
            eventRect[map][col][row].x = col*gp.tileSize + eventRect[map][col][row].x;
            eventRect[map][col][row].y = row*gp.tileSize + eventRect[map][col][row].y;

            if(gp.player.solidArea.intersects(eventRect[map][col][row]) && !eventRect[map][col][row].eventDone){
                if(gp.player.direction.contentEquals(requiredDirection) || requiredDirection.contentEquals("any")){
                    hit = true;
                    previousEventX = gp.player.worldX;
                    previousEventY = gp.player.worldY;
                }
            }
            gp.player.solidArea.x = gp.player.solidAreaDefaultX;
            gp.player.solidArea.y = gp.player.solidAreaDefaultY;
            eventRect[map][col][row].x = eventRect[map][col][row].eventRectDefaultX;
            eventRect[map][col][row].y = eventRect[map][col][row].eventRectDefaultY;
        }
        return hit;
    }

    public void damagePit(int gameState) {
        gp.gameState = gameState;
        gp.playSE(2);
        gp.ui.currentDialogue = "You fall into a pit!";
        gp.player.life -= 1;
        canTouchEvent = false;
    }

    public void healingPool(int gameState) {

        if(gp.keyHandler.enterPressed){
            gp.gameState = gameState;
            gp.player.attackCanceled = true;
            gp.playSE(2);
            gp.ui.currentDialogue = "You drink a water!\nYour life has been recovered!";
            gp.player.life = gp.player.maxLife;
            gp.player.mana = gp.player.maxMana;
            gp.assetSetter.setMonster();
        }
    }

    public void teleport(int map, int col, int row) {
        gp.gameState = gp.transitionState;
        tempMap = map;
        tempCol = col;
        tempRow = row;
        canTouchEvent = false;
        gp.playSE(13);
    }

    public void speak(Entity entity) {
        if(gp.keyHandler.enterPressed){
            gp.gameState = gp.dialogueState;
            gp.player.attackCanceled = true;
            entity.speak();
            gp.keyHandler.enterPressed = false;
        }
    }

}
