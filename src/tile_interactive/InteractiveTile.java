package tile_interactive;

import entity.Entity;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class InteractiveTile extends Entity {

    GamePanel gp;
    public boolean destructible = false;

    public InteractiveTile(GamePanel gp, int col, int row) {
        super(gp);
        this.gp = gp;
    }

    public boolean isCorrectItem(Entity entity) {
        boolean isCorrectItem = false;
        return isCorrectItem;
    }

    public void playeSE() {

    }

    public InteractiveTile getDestroydedForm() {
        InteractiveTile tile = null;
        return tile;
    }

    @Override
    public void update() {
        if(invincible) {
            invincibleCounter++;
            if(invincibleCounter > 20){
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2d) {

        int screenX = worldX - gp.player.worldX + gp.player.screenX; // tile x position in the screen
        int screenY = worldY - gp.player.worldY + gp.player.screenY; // tile y position in the screen

        if(worldX + gp.tileSize > gp.player.worldX -gp.player.screenX &&
                worldX - gp.player.screenX < gp.player.worldX + gp.player.screenX  &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY  &&
                worldY - gp.player.screenX < gp.player.worldY + gp.player.screenY
        ){
            g2d.drawImage(down1, screenX, screenY, null);
        }
    }
}
