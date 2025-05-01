package main;

import entity.Entity;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    // check if the given entity is colliding
    public void checkTile(Entity entity) {
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX/gp.tileSize; // column index of the left edge of the entity hitbox
        int entityRightCol = entityRightWorldX/gp.tileSize; // column index of the right edge of the entity hitbox
        int entityTopRow = entityTopWorldY/gp.tileSize; // row index of the top edge of the entity hitbox
        int entityBottomRow = entityBottomWorldY/gp.tileSize; // row index of the bottom edge of the entity hitbox

        int tileNum1 = 0, tileNum2 = 0;
        switch(entity.direction){
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed)/gp.tileSize;
                // predict the tile number at the top-left the entity after moving
                tileNum1 = gp.tileManager.mapTileNumber[gp.currentMap][entityLeftCol][entityTopRow];
                // predict the tile number at the top-right the entity after moving
                tileNum2 = gp.tileManager.mapTileNumber[gp.currentMap][entityRightCol][entityTopRow];
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed)/gp.tileSize;
                // predict the tile number at the bottom-left the entity after moving
                tileNum1 = gp.tileManager.mapTileNumber[gp.currentMap][entityLeftCol][entityBottomRow];
                // predict the tile number at the bottom-right the entity after moving
                tileNum2 = gp.tileManager.mapTileNumber[gp.currentMap][entityRightCol][entityBottomRow];
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.speed)/gp.tileSize;
                // predict the tile number at the top-left the entity after moving
                tileNum1 = gp.tileManager.mapTileNumber[gp.currentMap][entityLeftCol][entityTopRow];
                // predict the tile number at the bottom-left the entity after moving
                tileNum2 = gp.tileManager.mapTileNumber[gp.currentMap][entityLeftCol][entityBottomRow];
                break;
            case "right":
                entityRightCol = (entityRightWorldX + entity.speed)/gp.tileSize;
                // predict the tile number at the top-left the entity after moving
                tileNum1 = gp.tileManager.mapTileNumber[gp.currentMap][entityRightCol][entityTopRow];
                // predict the tile number at the bottom-left the entity after moving
                tileNum2 = gp.tileManager.mapTileNumber[gp.currentMap][entityRightCol][entityBottomRow];
                break;
        }
        if(gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision){
            entity.collisionOn = true;
        }
    }

    public int checkObject(Entity entity, boolean player) {
        int index = 999;

        for(int i=0;i<gp.obj[gp.currentMap].length;i++) { //fixed
            if(gp.obj[gp.currentMap][i] != null) { // fixed
                // get entity's solid area position
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;
                // get the object's solid area position
                gp.obj[gp.currentMap][i].solidArea.x = gp.obj[gp.currentMap][i].worldX + gp.obj[gp.currentMap][i].solidArea.x; // fixed
                gp.obj[gp.currentMap][i].solidArea.y = gp.obj[gp.currentMap][i].worldY + gp.obj[gp.currentMap][i].solidArea.y; // fixed

                switch(entity.direction) {
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        break;
                }
                if(entity.solidArea.intersects(gp.obj[gp.currentMap][i].solidArea)) { // fixed
                    if(gp.obj[gp.currentMap][i].collision) { // fixed
                        entity.collisionOn = true;
                    }
                    if(player) {
                        index = i;
                    }
                }
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[gp.currentMap][i].solidArea.x = gp.obj[gp.currentMap][i].solidAreaDefaultX; // fixed
                gp.obj[gp.currentMap][i].solidArea.y = gp.obj[gp.currentMap][i].solidAreaDefaultY; // fixed
            }
        }
        return index;
    }

    public int checkEntity(Entity entity, Entity[][] target) {
        int index = 999;

        for(int i=0;i<target[gp.currentMap].length;i++) {
            if(target[gp.currentMap][i] != null) {
                // get entity's solid area position
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;
                // get the npc's solid area position
                target[gp.currentMap][i].solidArea.x = target[gp.currentMap][i].worldX + target[gp.currentMap][i].solidArea.x;
                target[gp.currentMap][i].solidArea.y = target[gp.currentMap][i].worldY + target[gp.currentMap][i].solidArea.y;

                switch(entity.direction) {
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        break;
                }
                if(entity.solidArea.intersects(target[gp.currentMap][i].solidArea)) {
                    if(target[gp.currentMap][i] != entity){
                        entity.collisionOn = true;
                        index = i;
                    }
                }
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                target[gp.currentMap][i].solidArea.x = target[gp.currentMap][i].solidAreaDefaultX;
                target[gp.currentMap][i].solidArea.y = target[gp.currentMap][i].solidAreaDefaultY;
            }
        }

        return index;
    }

    public boolean checkPlayer(Entity entity) {

        boolean contactPlayer = false;

        // get entity's solid area position
        entity.solidArea.x = entity.worldX + entity.solidArea.x;
        entity.solidArea.y = entity.worldY + entity.solidArea.y;
        // get the player's solid area position
        gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
        gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;

        switch(entity.direction) {
            case "up":
                entity.solidArea.y -= entity.speed;
                break;
            case "down":
                entity.solidArea.y += entity.speed;
                break;
            case "left":
                entity.solidArea.x -= entity.speed;
                break;
            case "right":
                entity.solidArea.x += entity.speed;
                break;
        }
        if(entity.solidArea.intersects(gp.player.solidArea)) {
            entity.collisionOn = true;
            contactPlayer = true;
        }
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;

        return contactPlayer;
    }
}
