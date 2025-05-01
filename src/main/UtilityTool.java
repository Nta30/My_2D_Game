package main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UtilityTool {

    public BufferedImage scaledImage(BufferedImage original, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(original, 0, 0, width, height, null);
        return scaledImage;
    }
}
