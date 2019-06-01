import java.util.ArrayList;

import processing.core.PApplet;

public class EnemyPlayer extends GameObj {

	boolean shootUp, shootDown, shootLeft, shootRight;

	public EnemyPlayer(PApplet w, float x, float y) {
		super();
		this.window = w;
		this.spriteSheet = w.loadImage("enemyPlayerSprites.png");
		this.background = w.loadImage("background.png");
		this.bigBorder = (windowWidth - background.width) / 2;
		this.smallBorder = (windowHeight - background.height) / 2;
		this.spriteX = 0;
		this.spriteY = 0;
		this.spriteWidth = 32;
		this.spriteHeight = 32;
		this.x = x;
		this.y = y;
		this.speed = 2;
		this.isDead = false;
		this.frameToDisplay = 1;
		this.counter = 0;
		this.isRunning = false;
		this.stopDraw = false;
		this.hp = background.width;
	}

	public void draw() {
		if (!isDead && !stopDraw && isInBackground(x, y)) {
			if (!isRunning) {
				window.copy(spriteSheet, spriteX, 0, spriteWidth, spriteHeight, (int) x, (int) y, spriteWidth,
						spriteHeight);
			} else {
				window.copy(spriteSheet, spriteX, spriteY, spriteWidth, spriteHeight, (int) x, (int) y, spriteWidth,
						spriteHeight);
			}
		}
	}

	public void update() {
		if (!isDead) {
			counter++;
			if (counter == 7) {
				counter = 0;
				if (spriteX + spriteWidth >= spriteSheet.width) {
					spriteX = 0;
				} else {
					spriteX += spriteWidth;
				}
			}

		}
	}

	public void moveLeft() {
		isRunning = true;
		spriteY = 32;
		if (isInWalkingBounds(x - speed, y)) {
			x -= speed;
		}
	}

	public void moveRight() {
		isRunning = true;
		spriteX = 32;
		spriteY = 32;
		if (isInWalkingBounds(x + speed, y)) {
			x += speed;
		}
	}

	public void setShootUp(boolean shootUp) {
		if (!isDead)
			this.shootUp = shootUp;
	}

}
