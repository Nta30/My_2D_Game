package object;

import entity.Entity;
import main.GamePanel;

public class OBJ_Potion_Red extends Entity {

    public GamePanel gp;

    public OBJ_Potion_Red(GamePanel gp) {
        super(gp);
        this.gp = gp;
        type = type_Consumable;
        name = "Red Potion";
        value = 5;
        down1 = setup("/objects/potion_red", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nHeal your life by " + value + "!";
        price = 25;
    }

    @Override
    public void use(Entity entity){
        gp.gameState = gp.dialogueState;
        gp.ui.currentDialogue = "You drink the" + name + "!\nYour life is recovered by " + value + "!";
        entity.life += value;
        gp.playSE(2);
    }
}
