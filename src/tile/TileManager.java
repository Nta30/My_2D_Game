package tile;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;

public class TileManager {
    GamePanel gp;
    public Tile[] tile; // an array that stores different types of tiles
    public int[][][] mapTileNumber; // an 2D array that stores tile number for each grid cell

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[50];
        mapTileNumber = new int[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
        loadMap("/map/worldV3", 0);
        loadMap("/map/interior01", 1);
    }

    // loads images for different tile types
    public void getTileImage() {
        // placeholder
        setup(0, "grass00", false);
        setup(1, "grass00", false);
        setup(2, "grass00", false);
        setup(3, "grass00", false);
        setup(4, "grass00", false);
        setup(5, "grass00", false);
        setup(6, "grass00", false);
        setup(7, "grass00", false);
        setup(8, "grass00", false);
        setup(9, "grass00", false);

        // tiles
        setup(10, "grass00", false);
        setup(11, "grass01", false);
        setup(12, "water00", true);
        setup(13, "water01", true);
        setup(14, "water02", true);
        setup(15, "water03", true);
        setup(16, "water04", true);
        setup(17, "water05", true);
        setup(18, "water06", true);
        setup(19, "water07", true);
        setup(20, "water08", true);
        setup(21, "water09", true);
        setup(22, "water10", true);
        setup(23, "water11", true);
        setup(24, "water12", true);
        setup(25, "water13", true);
        setup(26, "road00", false);
        setup(27, "road01", false);
        setup(28, "road02", false);
        setup(29, "road03", false);
        setup(30, "road04", false);
        setup(31, "road05", false);
        setup(32, "road06", false);
        setup(33, "road07", false);
        setup(34, "road08", false);
        setup(35, "road09", false);
        setup(36, "road10", false);
        setup(37, "road11", false);
        setup(38, "road12", false);
        setup(39, "earth", false);
        setup(40, "wall", true);
        setup(41, "tree", true);
        setup(42, "hut", false);
        setup(43, "floor01", false);
        setup(44, "table01", true);

    }

    public void setup(int index, String imageName, boolean collision) {
        UtilityTool uTool = new UtilityTool();

        try{
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imageName + ".png"));
            tile[index].image = uTool.scaledImage(tile[index].image, gp.tileSize, gp.tileSize);
            tile[index].collision = collision;

        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath, int map) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath); // reading byte by file
            BufferedReader br = new BufferedReader(new InputStreamReader(is)); // convert data to character data

            String line;
            for(int row=0; row<gp.maxWorldRow;row++) {
                line = br.readLine();
                String[] data = line.split("\\s+"); // split data based on white space
                for(int col=0;col<gp.maxWorldCol;col++){
                    mapTileNumber[map][col][row] = Integer.parseInt(data[col]);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2d) {
        int tileNumber;
        for(int row=0;row<gp.maxWorldRow;row++) {
            for(int col=0;col<gp.maxWorldCol;col++) {
                int worldX = gp.tileSize * col; // tile x position on worldX
                int worldY = gp.tileSize * row; // tile y position on worldY
                int screenX = worldX - gp.player.worldX + gp.player.screenX; // tile x position in the screen
                int screenY = worldY - gp.player.worldY + gp.player.screenY; // tile y position in the screen
                tileNumber = mapTileNumber[gp.currentMap][col][row];

                if(worldX + gp.tileSize > gp.player.worldX -gp.player.screenX &&
                   worldX - gp.player.screenX < gp.player.worldX + gp.player.screenX  &&
                   worldY + gp.tileSize > gp.player.worldY - gp.player.screenY  &&
                   worldY - gp.player.screenX < gp.player.worldY + gp.player.screenY
                ){
                    g2d.drawImage(tile[tileNumber].image, screenX, screenY,null);
                }
            }
        }
    }
}
