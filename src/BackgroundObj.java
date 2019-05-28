import processing.core.PApplet;
import processing.core.PImage;

public abstract class BackgroundObj {

	float x, y;
	int spriteX, spriteY, spriteWidth, spriteHeight;
	PApplet window;
	PImage spriteSheet;
	int counter = 0;
	int despawnCounter = 0;
	boolean stopDraw;
	boolean used;

	public BackgroundObj() {
	}

	public void draw() {
		if (!stopDraw) {
			window.copy(spriteSheet, spriteX, spriteY, spriteWidth, spriteHeight, (int) x, (int) y, spriteWidth,
					spriteHeight);
		}
	}

	public void update() {
		counter++;
		if (counter == 50) {
			counter = 0;
			if (spriteX + spriteWidth >= spriteSheet.width) {
				spriteX = 0;
			} else {
				spriteX += spriteWidth;
			}
		}
	}
	
	public void updateDespawnCounter() {
		despawnCounter++;
	}
	
	public int getDespawnCounter() {
		return despawnCounter;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public int getSpriteWidth() {
		return spriteWidth;
	}

	public int getSpriteHeight() {
		return spriteHeight;
	}
	
	public boolean getStopDraw() {
		return stopDraw;
	}
	
	public boolean getUsed() {
		return used;
	}

	public void setStopDraw(boolean stopDraw) {
		this.stopDraw = stopDraw;
	}
	
	public void setUsed() {
		this.used = true;
	}

}
