import processing.core.PApplet;

public class Coffee extends BackgroundObj {
	
	public Coffee(PApplet w, float x, float y) {
		super();
		this.window = w;
		this.x = x;
		this.y = y;
		this.used = false;
		spriteSheet = window.loadImage("coffee.png");
		spriteX = 0;
		spriteY = 0;
		spriteWidth = 24;
		spriteHeight = 24;
	}

}
