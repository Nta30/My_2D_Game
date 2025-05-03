package entity;

import main.GamePanel;

import java.awt.*;

public class Projectile extends Entity{

    Entity user;

    public Projectile(GamePanel gp) {
        super(gp);
    }
    public void set(int worldX, int worldY, String direction, boolean alive, Entity user) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.direction = direction;
        this.alive = alive;
        this.user = user;
        this.life = this.maxLife;
    }
    public void update() {

        if(user == gp.player) {
            int monsterIndex = gp.collisionChecker.checkEntity(this, gp.monster);
            if(monsterIndex != 999){
                gp.player.damageMonster(monsterIndex, attack);
                generateParticle(user.projectile, gp.monster[gp.currentMap][monsterIndex]);
                alive = false;
            }
        }
        if(user != gp.player) {
            boolean contactPlayer = gp.collisionChecker.checkPlayer(this);
            if(contactPlayer){
                generateParticle(user.projectile, gp.player);
                damagePlayer(attack);
                alive = false;
            }
        }

        switch(direction) {
            case "up": worldY -= speed; break;
            case "down": worldY += speed; break;
            case "left": worldX -= speed; break;
            case "right": worldX += speed; break;
        }

        life--;
        if(life <= 0) {
            alive = false;
        }

        spriteCounter++;
        if(spriteCounter > 12){
            if(spriteNumber == 1){
                spriteNumber = 2;
            }
            else if(spriteNumber == 2){
                spriteNumber = 1;
            }
            spriteCounter = 0;
        }
    }

    public boolean haveResource(Entity user) {
        boolean haveResource = false;
        return haveResource;
    }

    public void subTractResource(Entity user) {
    }

    public Color setParticleColor() {
        Color color = null;
        return color;
    }

    @Override
    public int getParticleSize() {
        int size = 10;
        return size;
    }

    @Override
    public int getParticleSpeed() {
        int speed = 1;
        return speed;
    }

    @Override
    public int getParticleMaxLife() {
        int maxLife = 20;
        return maxLife;
    }
}
