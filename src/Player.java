import java.util.ArrayList;

import processing.core.PApplet;

public class Player extends GameObj {

	boolean shootUp, shootDown, shootLeft, shootRight;
	int lives;
	int powerUpDuration;
	boolean hasPowerUp;
	boolean invincible;

	public Player(PApplet w, float x, float y) {
		super();
		this.window = w;
		this.spriteSheet = w.loadImage("playerSprites.png");
		this.deadSpriteSheet = w.loadImage("playerDeathSprites.png");
		this.background = w.loadImage("background.png");
		this.bigBorder = (windowWidth - background.width) / 2;
		this.smallBorder = (windowHeight - background.height) / 2;
		this.spriteX = 0;
		this.spriteY = 0;
		this.spriteWidth = 30;
		this.spriteHeight = 32;
		this.spriteDeadX = 0;
		this.spriteDeadY = 0;
		this.spriteDeadWidth = 32;
		this.spriteDeadHeight = 32;
		this.x = x;
		this.y = y;
		this.speed = 2;
		this.isDead = false;
		this.counter = 0;
		this.dyingCounter = 0;
		this.deadCounter = 0;
		this.isRunning = false;
		this.stopDraw = false;
		this.lives = 3;
		this.powerUpDuration = 0;
		this.hasPowerUp = false;
		this.invincible = false;
	}

	public void move(float angle, ArrayList<Barrier> barriers) {
		if (!isDead && isInWalkingBounds(x + speed * (float) Math.cos(angle), y + speed * (float) Math.sin(angle))) {
			int numCollide = 0;
			// check if location walking to has barrier
			for (int i = 0; i < barriers.size(); i++) {
				Barrier b = barriers.get(i);
				if (isColliding(x + speed * (float) Math.cos(angle), y + speed * (float) Math.sin(angle), b)) {
					numCollide++;
				}
			}
			if (numCollide == 0) {
				isRunning = true;
				x += speed * Math.cos(angle);
				y += speed * Math.sin(angle);
			}	
		}
	}
	
	public void move2(float angle, ArrayList<Barrier> barriers) { // movement for boss level
		if (!isDead && isInWalkingBounds2(x + speed * (float) Math.cos(angle), y + speed * (float) Math.sin(angle))) {
			int numCollide = 0;
			for (int i = 0; i < barriers.size(); i++) {
				Barrier b = barriers.get(i);
				if (isColliding(x + speed * (float) Math.cos(angle), y + speed * (float) Math.sin(angle), b)) {
					numCollide++;
				}
			}
			if (numCollide == 0) {
				isRunning = true;
				x += speed * Math.cos(angle);
				y += speed * Math.sin(angle);
			}	
		}
	}
	
	public void moveUp(float y) {
		this.y -= y;
	}
	
	public boolean isColliding(BackgroundObj b) {

		boolean xOverlap = isIntervalOverlapping(x, x + spriteWidth, b.getX(), b.getX() + b.getSpriteWidth());
		boolean yOverlap = isIntervalOverlapping(y, y + spriteHeight, b.getY(), b.getY() + b.getSpriteHeight());

		if (xOverlap && yOverlap) {
			return true;
		}
		return false;
	}
	
	public int getLives() {
		return lives;
	}

	public void setShootUp(boolean shootUp) {
		if (!isDead)
			this.shootUp = shootUp;
	}

	public void setShootDown(boolean shootDown) {
		if (!isDead)
			this.shootDown = shootDown;
	}

	public void setShootLeft(boolean shootLeft) {
		if (!isDead)
			this.shootLeft = shootLeft;
	}

	public void setShootRight(boolean shootRight) {
		if (!isDead)
			this.shootRight = shootRight;
	}

	public boolean livesLeft() {
		if (lives >= 0)
			return true;
		return false;
	}
	
	public void addLife() {
		lives++;
	}

	public void removeLife() {
		lives--;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public void setInvincible(boolean invincible) {
		this.invincible = invincible;
	}
	
	public boolean getInvincible() {
		return invincible;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public boolean getPowerUp() {
		return hasPowerUp;
	}
	
	public void setPowerUp(boolean powerUp) {
		this.hasPowerUp = powerUp;
	}
	
	public void updatePowerUpDuration() {
		if (hasPowerUp) {
			powerUpDuration++;
		}
	}
	
	public void activateCoffee() {
		if (hasPowerUp && powerUpDuration < 500) {
			setSpeed(5);
		} else {
			setSpeed(2);
			powerUpDuration = 0;
			hasPowerUp = false;
		}
	}
	
	public void activateMachineGun(ArrayList<Bullet> bullets) {
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			if (hasPowerUp && powerUpDuration < 500) {
				b.setSpeed(200);
			} else {
				b.setSpeed(10);
				powerUpDuration = 0;
				hasPowerUp = false;
			}
		}
	}
	
	public void resetPowerUp(ArrayList<Bullet> bullets) {
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			b.setSpeed(10);
		}
		setSpeed(2);
		powerUpDuration = 0;
		
	}
}
