package entity;

import main.GamePanel;

import java.awt.*;

public class Particle extends Entity{

    Entity generator; // the entity that produces this particle
    Color color;
    int size;
    int xd;
    int yd;

    public Particle(GamePanel gp, Entity generator, Color color, int size, int speed, int maxLife, int xd, int yd) {
        super(gp);

        this.generator = generator;
        this.color = color;
        this.size = size;
        this.speed = speed;
        this.maxLife = maxLife;
        this.xd = xd;
        this.yd = yd;

        life = maxLife;
        int offset = (gp.tileSize/2) - (size/2);
        worldX = generator.worldX + offset;
        worldY = generator.worldY + offset;
    }

    @Override
    public void update() {
        life--;

        if(life < maxLife/3){
            yd++;
        }

        worldX += xd*speed;
        worldY += yd*speed;
        if(life == 0){
            alive = false;
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        g2d.setColor(color);
        g2d.fillRect(screenX, screenY, size, size);
    }
}
