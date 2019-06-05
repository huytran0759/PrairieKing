import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

public abstract class GameObj {

	PApplet window;
	PImage spriteSheet, deadSpriteSheet, background;
	float smallBorder, bigBorder;
	float x, y;
	float speed;
	float hp;
	int windowWidth = 1280;
	int windowHeight = 720;
	int spriteWidth, spriteHeight, spriteDeadWidth, spriteDeadHeight;
	int spriteX, spriteY, spriteDeadX, spriteDeadY;
	int counter, dyingCounter, deadCounter;
	boolean isDead;
	boolean isRunning;
	boolean stopDraw;

	public GameObj() {
	}

	public void draw() {
		if (!isDead && !stopDraw && isInBackground(x, y)) {
			if (!isRunning) {
				window.copy(spriteSheet, 0, spriteY, spriteWidth, spriteHeight, (int) x, (int) y, spriteWidth,
						spriteHeight);
			} else {
				window.copy(spriteSheet, spriteX, spriteY, spriteWidth, spriteHeight, (int) x, (int) y, spriteWidth,
						spriteHeight);
			}
		} else if (!stopDraw && isInWalkingBounds(x, y - spriteHeight)) {
			window.copy(deadSpriteSheet, spriteDeadX, spriteDeadY, spriteDeadWidth, spriteDeadHeight, (int) x, (int) y,
					spriteDeadWidth, spriteDeadHeight);
		}
	}

	public void move(float angle, ArrayList<Barrier> barriers) {
		if (isInWalkingBounds(x + speed * (float) Math.cos(angle), y + speed * (float) Math.sin(angle))) {
			int numCollide = 0;
			// check to see if walking location has barrier
			for (int i = 0; i < barriers.size(); i++) {
				Barrier b = barriers.get(i);
				if (isColliding(x + speed * (float) Math.cos(angle), y + speed * (float) Math.sin(angle), b)) {
					numCollide++;
				}
			}
			if (numCollide == 0) {
				x += speed * Math.cos(angle);
				y += speed * Math.sin(angle);
			} else {
				stopDraw = true;
			}

		} else {
			stopDraw = true;
		}
	}

	public void update() {
		if (!isDead) {
			if (isRunning) {
				counter++;
				if (counter == 7) {
					counter = 0;
					if (spriteX + spriteWidth >= spriteSheet.width) {
						spriteX = spriteWidth;
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
					stopDraw = true;
				} else {
					spriteDeadX += spriteDeadWidth;
				}
			}
		}
	}

	public void reset() {
		counter = 0;
		dyingCounter = 0;
		spriteDeadX = 0;
	}

	public void subtractHP(float damage) {
		if (!isDead) {
			if (hp - damage < 0) {
				hp = 0;
			} else {
				hp -= damage;
			}		
			if (hp <= 0)
				isDead = true;
		}
	}

	public boolean isIntervalOverlapping(float start1, float end1, float start2, float end2) {

		if (start1 >= start2 && start1 <= end2 || end1 >= start2 && end1 <= end2) {
			return true;
		}
		return false;
	}

	public boolean isColliding(GameObj g) {

		boolean xOverlap = isIntervalOverlapping(x, x + spriteWidth, g.getX(), g.getX() + g.getSpriteWidth());
		boolean yOverlap = isIntervalOverlapping(y, y + spriteHeight, g.getY(), g.getY() + g.getSpriteHeight());

		if (xOverlap && yOverlap) {
			return true;
		}
		return false;
	}

	public boolean isColliding(Barrier b) {

		boolean xOverlap = isIntervalOverlapping(x, x + spriteWidth, b.getX(), b.getX() + b.getSpriteWidth());
		boolean yOverlap = isIntervalOverlapping(y, y + spriteHeight, b.getY(), b.getY() + b.getSpriteHeight());

		if (xOverlap && yOverlap) {
			return true;
		}
		return false;
	}

	public boolean isColliding(float x, float y, Barrier b) {

		boolean xOverlap = isIntervalOverlapping(x, x + spriteWidth, b.getX(), b.getX() + b.getSpriteWidth());
		boolean yOverlap = isIntervalOverlapping(y, y + spriteHeight, b.getY(), b.getY() + b.getSpriteHeight());

		if (xOverlap && yOverlap) {
			return true;
		}
		return false;
	}

	public boolean isColliding(float x, float y, Orc o) {

		boolean xOverlap = isIntervalOverlapping(x, x + spriteWidth, o.getX(), o.getX() + o.getSpriteWidth());
		boolean yOverlap = isIntervalOverlapping(y, y + spriteHeight, o.getY(), o.getY() + o.getSpriteHeight());

		if (xOverlap && yOverlap) {
			return true;
		}
		return false;
	}

	public boolean isInWalkingBounds(float x, float y) {
		if (x > bigBorder && x < windowWidth - bigBorder - spriteWidth && y > smallBorder
				&& y < windowHeight - smallBorder - spriteHeight)
			return true;
		return false;
	}

	public boolean isInWalkingBounds2(float x, float y) { // walking bounderies for boss level
		if (x > bigBorder && x < windowWidth - bigBorder - spriteWidth && y > smallBorder
				&& y < windowHeight - smallBorder - 288)
			return true;
		return false;
	}

	public boolean isInBackground(float x, float y) {
		if (x >= bigBorder - spriteWidth && x <= windowWidth - bigBorder && y >= smallBorder - spriteHeight
				&& y <= windowHeight - smallBorder)
			return true;
		return false;
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

	public int getSpriteX() {
		return spriteX;
	}

	public int getSpriteY() {
		return spriteY;
	}

	public float getHP() {
		return hp;
	}

	public boolean getDead() {
		return isDead;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setSpriteX(int spriteX) {
		this.spriteX = spriteX;
	}

	public void setSpriteY(int spriteY) {
		this.spriteY = spriteY;
	}

	public void setDead(boolean status) {
		isDead = status;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
		if (!isRunning)
			counter = 0;
	}

	public void setStopDraw(boolean stopDraw) {
		this.stopDraw = stopDraw;
	}

}
