import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

public class Bullet extends GameObj {

	PImage background;
	boolean exists;
	boolean shootUp, shootDown, shootLeft, shootRight;
	float damage;

	public Bullet(PApplet w, float x, float y) {
		this.window = w;
		this.background = w.loadImage("background.png");
		this.spriteSheet = w.loadImage("bullet.png");
		this.background = w.loadImage("background.png");
		this.bigBorder = (windowWidth - background.width) / 2;
		this.smallBorder = (windowHeight - background.height) / 2;
		this.spriteHeight = spriteSheet.height;
		this.spriteWidth = spriteSheet.width;
		this.x = x;
		this.y = y;
		this.speed = 10;
		this.exists = true;
		this.damage = (float) 10;
		this.shootUp = false;
		this.shootDown = false;
		this.shootLeft = false;
		this.shootRight = false;
		this.stopDraw = false;
	}
	
	public void draw() {
		if (!stopDraw)
			window.image(spriteSheet, x, y);
	}
	
	public void move(float angle, ArrayList<Barrier> barriers) {
		if (isInWalkingBounds(x + speed * (float) Math.cos(angle), y + speed * (float) Math.sin(angle))) {
			int numCollide = 0;
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
				exists = false;
			}

		} else {
			stopDraw = true;
			exists = false;
		}
	}
	
	public boolean getShootUp() {
		return shootUp;
	}

	public boolean getShootDown() {
		return shootDown;
	}

	public boolean getShootLeft() {
		return shootLeft;
	}

	public boolean getShootRight() {
		return shootRight;
	}
	
	public float getDamage() {
		return damage;
	}
	
	public boolean getExists() {
		return exists;
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
	
	public void setExists(boolean exists) {
		this.exists = exists;
	}

}
