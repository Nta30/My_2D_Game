package monster;

import entity.Entity;
import main.GamePanel;
import object.OBJ_Coin_Bronze;
import object.OBJ_Heart;
import object.OBJ_ManaCrystal;
import object.OBJ_Rock;

import java.util.Random;

public class Mon_Orc extends Entity {
    GamePanel gp;

    public Mon_Orc(GamePanel gp) {

        super(gp);
        this.gp = gp;

        name = "Orc";
        speed = 1;
        maxLife = 10;
        life = maxLife;
        type = type_Monster;
        attack = 6;
        defense = 1;
        exp = 8;

        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    public void getImage() {
        up1 = setup("/monster/orc_up_1", gp.tileSize, gp.tileSize);
        up2 = setup("/monster/orc_up_2", gp.tileSize, gp.tileSize);
        down1 = setup("/monster/orc_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/monster/orc_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/monster/orc_left_1", gp.tileSize, gp.tileSize);
        left2 = setup("/monster/orc_left_2", gp.tileSize, gp.tileSize);
        right1 = setup("/monster/orc_right_1", gp.tileSize, gp.tileSize);
        right2 = setup("/monster/orc_right_2", gp.tileSize, gp.tileSize);
    }

    @Override
    public void setAction() {
        actionLockCounter++;
        if(actionLockCounter == 120) {
            Random random = new Random();
            int i = random.nextInt(100) + 1; // pick up a number from 1 to 100

            if(i <= 25) {
                direction = "up";
            }
            if(i>25 && i<=50) {
                direction = "down";
            }
            if(i>50 && i<=75) {
                direction = "left";
            }
            if(i>75 && i<=100) {
                direction = "right";
            }
            actionLockCounter = 0;
        }


    }

    @Override
    public void damageReaction() {
        actionLockCounter = 0;
        direction = gp.player.direction;

    }

    @Override
    public void checkDrop() {
        // cast a die
        int i = new Random().nextInt(100) + 1;

        // set the monster drop
        if(i < 50){
            dropItem(new OBJ_Coin_Bronze(gp));
            dropItem(new OBJ_Coin_Bronze(gp));
        }
        if(i >= 50 && i < 75){
            dropItem(new OBJ_Heart(gp));
            dropItem(new OBJ_Heart(gp));
        }
        if(i >= 75 && i < 100){
            dropItem(new OBJ_ManaCrystal(gp));
            dropItem(new OBJ_ManaCrystal(gp));
        }
    }
}

