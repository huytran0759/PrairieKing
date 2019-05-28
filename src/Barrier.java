import processing.core.PApplet;

public class Barrier extends BackgroundObj {

	public Barrier(PApplet w, float x, float y) {
		super();
		this.window = w;
        this.x = x;
        this.y = y;
        this.spriteSheet = w.loadImage("barrier.png");
        this.spriteX = 0;
        this.spriteY = 0;
        this.spriteWidth = 32;
        this.spriteHeight = 32;
	}
	
}
