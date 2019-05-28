import processing.core.PApplet;
import processing.core.PImage;

public class Cactus extends BackgroundObj {

    public Cactus(PApplet w, float x, float y) {
    	super();
        this.window = w;
        this.x = x;
        this.y = y;
        this.spriteSheet = w.loadImage("cactusSprites.png");
        this.spriteX = 0;
        this.spriteY = 0;
        this.spriteWidth = 32;
        this.spriteHeight = 32;
    }

}
