import processing.core.PApplet;

public class OneUp extends BackgroundObj {
	
	public OneUp(PApplet w, float x, float y) {
		super();
		this.window = w;
		this.x = x;
		this.y = y;
		this.used = false;
		spriteSheet = window.loadImage("oneUp.png");
		spriteX = 0;
		spriteY = 0;
		spriteWidth = 28;
		spriteHeight = 22;
	}

}
