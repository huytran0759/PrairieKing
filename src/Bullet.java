import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

public class Bullet extends GameObj {

	PImage background;
	boolean exists;
	boolean shootUp, shootDown, shootLeft, shootRight;
	int damage;

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
		this.damage = 2;
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
	
	public int getDamage() {
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
