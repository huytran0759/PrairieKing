import java.util.ArrayList;

import processing.core.PApplet;

public class Orc extends GameObj {

	float moveDirection;

	public Orc(PApplet w, float x, float y) {
		this.window = w;
		this.spriteSheet = window.loadImage("orcSprites.png");
		this.deadSpriteSheet = window.loadImage("enemyDeathSprites.png");
		this.background = w.loadImage("background.png");
		this.borderBig = (windowWidth - background.width) / 2;
		this.borderSmall = (windowHeight - background.height) / 2;
		this.spriteX = 0;
		this.spriteY = 0;
		this.spriteWidth = 32;
		this.spriteHeight = 32;
		this.spriteDeadX = 0;
		this.spriteDeadY = 0;
		this.spriteDeadWidth = 32;
		this.spriteDeadHeight = 32;
		this.x = x;
		this.y = y;
		this.speed = (float) 1.5;
		this.hp = 2;
		this.isDead = false;
		this.isRunning = true;
		this.frameToDisplay = 1;
		this.counter = 0;
		this.deadCounter = 0;
		this.dyingCounter = 0;
		this.stopDraw = false;
	}

	public void move(float playerX, float playerY, ArrayList<Barrier> barriers) {
		if (!isDead) {
			double dx = playerX - x;
			double dy = playerY - y;
			double angle = Math.atan2(dy, dx);
			if (isInWalkingBounds(x + speed * (float) Math.cos(angle), y + speed * (float) Math.sin(angle))) {
				int numCollide = 0;
				for (int i = 0; i < barriers.size(); i++) {
					Barrier b = barriers.get(i);
					if (isColliding(x + speed * (float) Math.cos(angle), y + speed * (float) Math.sin(angle), b)) {
						numCollide++;
					}
				}
				if (numCollide == 0) {
					x += speed * (float) Math.cos(angle);
					y += speed * (float) Math.sin(angle);
				}
			}
		}
	}

	public void moveInBounds(float angle) {
		if (!isDead) {
			x += speed * (float) Math.cos(angle);
			y += speed * (float) Math.sin(angle);
		}
	}

	public void update() {
		if (!isDead) {
			if (isRunning) {
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
		} else {
			dyingCounter++;
			if (dyingCounter == 7) {
				dyingCounter = 0;
				if (spriteDeadX + spriteDeadWidth >= deadSpriteSheet.width) {
					deadCounter++;
				} else {
					spriteDeadX += spriteDeadWidth;
				}
			}
			if (deadCounter == 50)
				stopDraw = true;
		}
	}

	public float getMoveDirection() {
		return moveDirection;
	}

	public void setMoveDirection(float angle) {
		moveDirection = angle;
	}

}
