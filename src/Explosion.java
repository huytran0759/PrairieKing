import processing.core.PApplet;

public class Explosion extends BackgroundObj {
	
	int counter;
	
	public Explosion(PApplet w, float x, float y) {
		super();
		this.window = w;
		this.x = x;
		this.y = y;
		this.used = false;
		spriteSheet = window.loadImage("explosion.png");
		spriteX = 0;
		spriteY = 0;
		spriteWidth = 32;
		spriteHeight = 32;
		counter = 0;
	}
	
	public void update() {
		if (!stopDraw) {
			counter++;
			if (counter == 7) {
				counter = 0;
				if (spriteX + spriteWidth >= spriteSheet.width) {
					stopDraw = true;
				} else {
					spriteX += spriteWidth;
				}	
			}
		}
	}

}
