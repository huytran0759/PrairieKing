import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class Game extends PApplet {

	PImage intro, background, background2, background3, background4, bossBackground, clock, controls, arrow, tile,
			lives;
	PFont font;
	int windowWidth = 1280;
	int windowHeight = 720;
	float smallBorder, bigBorder;
	float backgroundX, backgroundY, background2X, background2Y, background3X, background3Y, background4X, background4Y,
			bossBackgroundX, bossBackgroundY;
	int mode;
	ArrayList<Cactus> cacti;
	ArrayList<Bullet> bullets;
	ArrayList<Bullet> enemyBullets;
	ArrayList<Orc> enemies;
	ArrayList<Barrier> barriers;
	ArrayList<BackgroundObj> powerUps;
	ArrayList<Explosion> explosions;
	Player player;
	EnemyPlayer enemyPlayer;
	Orc orc;
	boolean up, down, left, right;
	int previousSpriteX, previousSpriteY;
	boolean changedSpritePosUp, changedSpritePosDown, changedSpritePosLeft, changedSpritePosRight;
	boolean changedSpriteShootUp, changedSpriteShootDown, changedSpriteShootLeft, changedSpriteShootRight;
	boolean firing;
	boolean enemyFiring;
	boolean stopAnimate;
	boolean changeLevel;
	boolean scrollDown;
	boolean emptyScreen;
	boolean barriersLvl2Init, barriersLvl3Init, barriersLvl4Init, barriersLvl5Init;
	boolean stopControls;
	boolean drawEnemyPlayer;
	boolean displayEnemyHealth;
	boolean enemyPlayerAction;
	int fireCounter, enemyFireCounter, timeLeft, gameCounter, respawnCounter, spawnRate, fireCounterLimit, powerUpCounter,
			invincibleCounter, flickerCounter;
	int level;

	public void settings() {
		size(1280, 720);
	}

	public void setup() {
		size(1280, 720);
		font = createFont("Cambria Math", 20);
		textFont(font);
		intro = loadImage("intro.png");
		background = loadImage("background.png");
		background2 = loadImage("background2.png");
		background3 = loadImage("background3.png");
		background4 = loadImage("background4.png");
		bossBackground = loadImage("bossBackground.png");
		clock = loadImage("clock.png");
		controls = loadImage("controls.png");
		arrow = loadImage("arrow.png");
		tile = loadImage("tile.png");
		lives = loadImage("oneUp.png");
		bigBorder = (windowWidth - background.width) / 2;
		smallBorder = (windowHeight - background.height) / 2;
		backgroundX = bigBorder;
		backgroundY = smallBorder;
		background2X = bigBorder;
		background2Y = windowHeight - smallBorder;
		background3X = bigBorder;
		background3Y = background2Y + background.height;
		background4X = bigBorder;
		background4Y = background3Y + background.height;
		bossBackgroundX = bigBorder;
		bossBackgroundY = background4Y + background.height;
		mode = 1;
		level = 1;
		timeLeft = 5;// background.width - clock.width - 2;
		fireCounter = 0;
		enemyFireCounter = 0;
		fireCounterLimit = 10;
		cacti = new ArrayList<Cactus>();
		bullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		enemies = new ArrayList<Orc>();
		barriers = new ArrayList<Barrier>();
		powerUps = new ArrayList<BackgroundObj>();
		explosions = new ArrayList<Explosion>();
		player = new Player(this, windowWidth / 2, windowHeight / 2);
		enemyPlayer = new EnemyPlayer(this, windowWidth / 2, windowHeight - smallBorder - 96);
		stopAnimate = false;
		changeLevel = false;
		scrollDown = false;
		drawEnemyPlayer = false;
		displayEnemyHealth = false;
		firing = false;
		enemyFiring = false;
		enemyPlayerAction = false;
		barriersLvl2Init = false;
		barriersLvl3Init = false;
		barriersLvl4Init = false;
		barriersLvl5Init = false;
		stopControls = false;
		powerUpCounter = 0;
		invincibleCounter = 0;
		flickerCounter = 0;
	}

	public void draw() {
		if (mode == 0) {
			displayIntro();
		} else if (mode == 1) {
			gameCounter++;
			image(background, backgroundX, backgroundY);
			image(background2, background2X, background2Y);
			image(background3, background3X, background3Y);
			image(background4, background4X, background4Y);
			image(bossBackground, bossBackgroundX, bossBackgroundY);
			drawBorders();
			fill(255);
			text("x" + player.getLives(), bigBorder - 45, smallBorder + 17);
			image(lives, bigBorder - lives.width - 50, smallBorder);
			drawTimer();
			drawCacti();
			player.draw();
			if (drawEnemyPlayer) {
				enemyPlayer.draw();
			}	
			if (displayEnemyHealth) {
				drawEnemyHP();
			}
			animateGameObj();
			if (gameCounter < 250 && !changeLevel) {
				player.setRunning(false);
				if (level == 1) {
					image(controls, bigBorder + background.width / 2 - controls.width / 2,
							windowHeight - smallBorder + 5);
				}
			} else {
				if (level == 5) {
					enemyPlayerAction = true;
				}
				respawn();
				if (level != 5) {
					drawPowerUps();
					spawnOrcs();
					drawOrcs();
				}
				if (!stopAnimate) {
					if (timeLeft >= 0 && gameCounter % 10 == 0)
						timeLeft -= 0.001;
					playerAction();
					player.updatePowerUpDuration();
					if (enemyPlayerAction) {
						enemyPlayer.moveLeft();
						enemyFire();
					}		
					bulletAction();
					if (level != 5) {
						orcAction();
					}
					detectCollision();
					if (player.getInvincible() && invincibleCounter < 250) {
						invincibleCounter++;
						if (invincibleCounter == 250) {
							player.setInvincible(false);
							invincibleCounter = 0;
						}
					}
					if (player.getPowerUp()) {
						powerUpCounter++;
						if (powerUpCounter == 500) {
							powerUpCounter = 0;
							player.setPowerUp(false);
							fireCounterLimit = 10;
						}
					}
					if (!player.getPowerUp()) {
						player.resetPowerUp(bullets);
						fireCounterLimit = 10;
						powerUpCounter = 0;
					}
					if (level != 5) {
						checkEmptyScreen();
						changeLevel();
					}
				}
			}
			if (level == 2) {
				if (!changeLevel) {
					if (!barriersLvl2Init) {
						initLvl1Cacti();
						initLvl2Barriers();
						barriersLvl2Init = true;
					}
				}
			}
			if (level == 3) {
				if (!changeLevel) {
					if (!barriersLvl3Init) {
						barriers.clear();
						initLvl1Cacti();
						initLvl3Cacti();
						initLvl3Barriers();
						barriersLvl3Init = true;
					}
				}
			}
			if (level == 4) {
				if (!changeLevel) {
					if (!barriersLvl4Init) {
						barriers.clear();
						initLvl1Cacti();
						initLvl4Barriers();
						barriersLvl4Init = true;
					}
				}
			}
			if (level == 5) {
				if (!changeLevel) {
					if (!barriersLvl5Init) {
						barriers.clear();
						initLvl5Cacti();
						initLvl5Barriers();
						barriersLvl5Init = true;
					}
				}
			}
		}
	}

	public void drawBorders() {
		fill(0);
		rect(0, 0, windowWidth, smallBorder);
		rect(0, windowHeight - smallBorder, windowWidth, smallBorder);
		rect(0, 0, bigBorder, windowHeight);
		rect(bigBorder + background.width, 0, bigBorder, windowHeight);
	}

	public void initLvl2Barriers() {
		for (int i = 0; i < 9; i++) {
			if (i < 2 || i > 6) {
				Barrier b = new Barrier(this, bigBorder + 128 + 32 * i, smallBorder + 128);
				barriers.add(b);
			}
			if (i < 2 || i > 6) {
				Barrier b = new Barrier(this, bigBorder + 128 + 32 * i, 720 - smallBorder - 128);
				barriers.add(b);
			}
			if (i < 1 || i > 7) {
				Barrier b = new Barrier(this, bigBorder + 128 + 32 * i, smallBorder + 160);
				barriers.add(b);
			}
			if (i < 1 || i > 7) {
				Barrier b = new Barrier(this, bigBorder + 128 + 32 * i, 720 - smallBorder - 160);
				barriers.add(b);
			}
		}
	}

	public void initLvl3Barriers() {
		Barrier b1 = new Barrier(this, bigBorder + 256, smallBorder + 128);
		Barrier b2 = new Barrier(this, bigBorder + 256, windowHeight - smallBorder - 128);
		Barrier b3 = new Barrier(this, bigBorder + 128, smallBorder + 256);
		Barrier b4 = new Barrier(this, windowWidth - bigBorder - 128, smallBorder + 256);
		barriers.add(b1);
		barriers.add(b2);
		barriers.add(b3);
		barriers.add(b4);
	}

	public void initLvl4Barriers() {
		for (int i = 0; i < 7; i++) {
			if (i < 3) {
				Barrier b = new Barrier(this, bigBorder + 159 + 32 * i, smallBorder + 164);
				Barrier c = new Barrier(this, bigBorder + 159 + 32 * i, windowHeight - smallBorder - 164);
				barriers.add(b);
				barriers.add(c);
			} else if (i > 3) {
				Barrier b2 = new Barrier(this, bigBorder + 161 + 32 * i, smallBorder + 164);
				Barrier c2 = new Barrier(this, bigBorder + 161 + 32 * i, windowHeight - smallBorder - 164);
				barriers.add(b2);
				barriers.add(c2);
			}
		}
		for (int i = 0; i < 5; i++) {
			if (i < 2) {
				Barrier b = new Barrier(this, bigBorder + 158, smallBorder + 190 + 32 * i);
				Barrier c = new Barrier(this, windowWidth - bigBorder - 160, smallBorder + 190 + 32 * i);
				barriers.add(b);
				barriers.add(c);
			} else if (i > 2) {
				Barrier b = new Barrier(this, bigBorder + 158, smallBorder + 194 + 32 * i);
				Barrier c = new Barrier(this, windowWidth - bigBorder - 160, smallBorder + 194 + 32 * i);
				barriers.add(b);
				barriers.add(c);
			}
		}
	}

	public void initLvl5Barriers() {
		Barrier b1 = new Barrier(this, bigBorder + 256, smallBorder + 124);
		barriers.add(b1);
		for (int i = 0; i < 3; i++) {
			Barrier b = new Barrier(this, bigBorder + 224 + 32 * i, windowHeight - smallBorder - 128);
			barriers.add(b);
		}
	}

	public void drawBarriers() {
		for (int i = 0; i < barriers.size(); i++) {
			Barrier b = barriers.get(i);
			b.draw();
		}
	}

	public void initLvl1Cacti() {
		for (int i = 0; i < 16; i++) {
			if (i < 7 || i > 9) {
				Cactus c = new Cactus(this, bigBorder, smallBorder + 32 * i);
				cacti.add(c);
				Barrier b = new Barrier(this, bigBorder, smallBorder + 32 * i);
				barriers.add(b);
			}
		}

		for (int i = 0; i < 16; i++) {
			if (i < 7 || i > 9) {
				Cactus c = new Cactus(this, windowWidth - bigBorder - 32, smallBorder + 32 * i);
				cacti.add(c);
				Barrier b = new Barrier(this, windowWidth - bigBorder - 32, smallBorder + 32 * i);
				barriers.add(b);
			}
		}

		for (int i = 1; i < 15; i++) {
			if (i < 7 || i > 9) {
				Cactus c = new Cactus(this, bigBorder + 32 * i, smallBorder);
				cacti.add(c);
				Barrier b = new Barrier(this, bigBorder + 32 * i, smallBorder);
				barriers.add(b);
			}
		}

		for (int i = 1; i < 15; i++) {
			if (i < 7 || i > 9) {
				Cactus c = new Cactus(this, bigBorder + 32 * i, windowHeight - smallBorder - 32);
				cacti.add(c);
				Barrier b = new Barrier(this, bigBorder + 32 * i, windowHeight - smallBorder - 32);
				barriers.add(b);
			}
		}
	}

	public void initLvl3Cacti() {
		for (int i = 0; i < 14; i++) {
			if (i < 2 || i > 11) {
				Cactus c = new Cactus(this, bigBorder + 32 + 32 * i, smallBorder + 32);
				Cactus d = new Cactus(this, bigBorder + 32 + 32 * i, windowHeight - smallBorder - 64);
				Barrier a = new Barrier(this, bigBorder + 32 + 32 * i, smallBorder + 32);
				Barrier b = new Barrier(this, bigBorder + 32 + 32 * i, windowHeight - smallBorder - 64);
				cacti.add(c);
				cacti.add(d);
				barriers.add(a);
				barriers.add(b);
			}
			if (i < 1 || i > 12) {
				Cactus c = new Cactus(this, bigBorder + 32 + 32 * i, smallBorder + 64);
				Cactus d = new Cactus(this, bigBorder + 32 + 32 * i, windowHeight - smallBorder - 96);
				Barrier a = new Barrier(this, bigBorder + 32 + 32 * i, smallBorder + 64);
				Barrier b = new Barrier(this, bigBorder + 32 + 32 * i, windowHeight - smallBorder - 96);
				cacti.add(c);
				cacti.add(d);
				barriers.add(a);
				barriers.add(b);
			}
		}
	}

	public void initLvl5Cacti() {
		Cactus c1 = new Cactus(this, bigBorder + 160, smallBorder + 190);
		Barrier b1 = new Barrier(this, bigBorder + 160, smallBorder + 190);
		cacti.add(c1);
		barriers.add(b1);
		Cactus c2 = new Cactus(this, windowWidth - bigBorder - 192, smallBorder + 190);
		Barrier b2 = new Barrier(this, windowWidth - bigBorder - 192, smallBorder + 190);
		cacti.add(c2);
		barriers.add(b2);
	}

	public void drawCacti() {
		for (int i = 0; i < cacti.size(); i++) {
			Cactus c = cacti.get(i);
			c.draw();
		}
	}

	public void displayIntro() {
		background(0);
		image(intro, (windowWidth - intro.width) / 2, 0);
	}
	
	public void drawEnemyHP() {
		fill(185, 52, 74);
		rect(bigBorder, windowHeight - smallBorder + 2, enemyPlayer.getHP(), 10);
	}

	public void drawTimer() {
		image(clock, bigBorder, smallBorder - clock.width - 7);
		if (timeLeft <= 50) {
			fill(191, 51, 73);
		} else {
			fill(150, 175, 44);
		}
		rect(bigBorder + clock.width + 2, smallBorder - clock.width, timeLeft, 10);
	}

	public void playerAction() {
		if (level != 5) {
			if (up)
				player.move((float) Math.PI * 3 / 2, barriers);
			if (left)
				player.move((float) Math.PI, barriers);
			if (down)
				player.move((float) Math.PI / 2, barriers);
			if (right)
				player.move((float) 0, barriers);
		} else if (level == 5) {
			if (up)
				player.move2((float) Math.PI * 3 / 2, barriers);
			if (left)
				player.move2((float) Math.PI, barriers);
			if (down)
				player.move2((float) Math.PI / 2, barriers);
			if (right)
				player.move2((float) 0, barriers);
		}

	}

	public void bulletAction() {
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			if (b.getExists()) {
				b.draw();
				if (b.getShootUp()) {
					player.setShootUp(true);
					b.move((float) Math.PI * 3 / 2, barriers);
					if (!b.isInBackground(b.getX(), b.getY())) {
						b.setShootUp(false);
						b.setExists(false);
						player.setShootUp(false);
					}
				}
				if (b.getShootDown()) {
					player.setShootDown(true);
					b.move((float) Math.PI / 2, barriers);
					if (!b.isInBackground(b.getX(), b.getY())) {
						b.setShootDown(false);
						b.setExists(false);
						player.setShootDown(false);
					}
				}
				if (b.getShootLeft()) {
					player.setShootLeft(true);
					b.move((float) Math.PI, barriers);
					if (!b.isInBackground(b.getX(), b.getY())) {
						b.setShootLeft(false);
						b.setExists(false);
						player.setShootLeft(false);
					}
				}
				if (b.getShootRight()) {
					player.setShootRight(true);
					b.move(0, barriers);
					if (!b.isInBackground(b.getX(), b.getY())) {
						b.setShootRight(false);
						b.setExists(false);
						player.setShootRight(false);
					}
				}
			}
		}
		
		for (int i = 0; i < enemyBullets.size(); i++) {
			Bullet b = enemyBullets.get(i);
			if (b.getExists()) {
				b.draw();
				if (b.getShootUp()) {
					enemyPlayer.setShootUp(true);
					b.move((float) Math.PI * 3 / 2, barriers);
					if (!b.isInBackground(b.getX(), b.getY())) {
						b.setShootUp(false);
						b.setExists(false);
						enemyPlayer.setShootUp(false);
					}
				}
			}
		}
		if (firing) {
			fireCounter++;
		}
		
		if (enemyFiring) {
			enemyFireCounter++;
		}

		// time delay before you can shoot again
		if (fireCounter == fireCounterLimit) {
			fireCounter = 0;
			player.setSpriteX(previousSpriteX);
			player.setSpriteY(previousSpriteY);
			firing = false;
		}
		
		if (enemyFireCounter == fireCounterLimit) {
			enemyFireCounter = 0;
			enemyFiring = false;
		}
	}

	public void drawOrcs() {
		for (int i = 0; i < enemies.size(); i++) {
			Orc o = enemies.get(i);
			o.draw();
		}
	}

	public void drawPowerUps() {
		for (int i = 0; i < powerUps.size(); i++) {
			BackgroundObj b = powerUps.get(i);
			b.draw();
		}
	}

	public void orcAction() {
		for (int i = 0; i < enemies.size(); i++) {
			Orc o = enemies.get(i);
			if (o.isInWalkingBounds(o.getX(), o.getY())) {
				o.move(player.getX() + player.getSpriteWidth() / 2, player.getY() + player.getSpriteHeight() / 2,
						barriers);
			} else {
				o.moveInBounds(o.getMoveDirection());
			}
		}
	}

	public void detectCollision() {
		int numCollisions = 0;
		for (int i = 0; i < enemies.size(); i++) {
			Orc o = enemies.get(i);
			if (player.isColliding(o) && !o.getStatus() && !player.getInvincible()) {
				player.setStatus(true);
				player.setPowerUp(false);
				stopAnimate = true;
				numCollisions++;
			}

			for (int j = 0; j < bullets.size(); j++) {
				Bullet b = bullets.get(j);
				if (b.isColliding(o) && b.getExists() && !o.getStatus()) {
					o.subtractHP(b.getDamage());
					if (o.getStatus()) {
						float powerUp = (float) Math.random();
						if (powerUp <= 0.01) {
							OneUp p = new OneUp(this, o.getX(), o.getY());
							powerUps.add(p);
						} else if (powerUp <= 0.02) {
							Coffee p = new Coffee(this, o.getX(), o.getY());
							powerUps.add(p);
							player.setPowerUp(true);
						} else if (powerUp <= 0.03) {
							MachineGun p = new MachineGun(this, o.getX(), o.getY());
							powerUps.add(p);
							player.setPowerUp(true);
						} else if (powerUp <= 0.035) {
							Nuke p = new Nuke(this, o.getX(), o.getY());
							powerUps.add(p);
							player.setPowerUp(true);
						}
					}
					b.setExists(false);
				}
			}
		}
		for (int i = 0; i < enemyBullets.size(); i++) {
			Bullet b = enemyBullets.get(i);
			if (b.isColliding(player) && b.getExists() && !player.getStatus()) {
				player.setStatus(true);
				stopAnimate = true;
				numCollisions++;
				b.setExists(false);
			}
		}
		
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			if (b.isColliding(enemyPlayer) && b.getExists() && !enemyPlayer.getStatus()) {
				enemyPlayer.subtractHP(b.getDamage());
				b.setExists(true);
			}
		}
		if (numCollisions > 0) {
			player.removeLife();
		}
		for (int i = 0; i < powerUps.size(); i++) {
			BackgroundObj p = powerUps.get(i);
			if (!p.getStopDraw()) {
				if (player.isColliding(p)) {
					player.setPowerUp(false);
					p.setStopDraw(true);
					if (p instanceof OneUp) {
						if (!p.getUsed()) {
							player.addLife();
							p.setUsed();
						}
					} else if (p instanceof Coffee) {
						if (!p.getUsed()) {
							player.setPowerUp(true);
							player.activateCoffee();
							p.setUsed();
						}
					} else if (p instanceof MachineGun) {
						if (!p.getUsed()) {
							player.setPowerUp(true);
							player.activateMachineGun(bullets);
							fireCounterLimit = 1;
							p.setUsed();
						}
					} else if (p instanceof Nuke) {
						if (!p.getUsed()) {
							player.setPowerUp(true);
							for (int j = 0; j < enemies.size(); j++) {
								Orc o = enemies.get(j);
								if (!o.getStatus()) {
									o.setStatus(true);
								}
							}
							for (int j = 0; j < 15; j++) {
								Explosion o = new Explosion(this,
										bigBorder
												+ (float) Math.random() * (background.width - player.getSpriteWidth()),
										smallBorder + (float) Math.random() * (background.height - player.spriteWidth));
								explosions.add(o);
							}
							p.setUsed();
						}
					}
				}
				p.updateDespawnCounter();
				if (p.getDespawnCounter() == 500) {
					p.setStopDraw(true);
				}
			}
		}
	}

	public void respawn() {
		if (stopAnimate && player.livesLeft()) {
			for (int i = 0; i < powerUps.size(); i++) {
				BackgroundObj b = powerUps.get(i);
				b.setUsed();
				b.setStopDraw(true);
			}
			player.setInvincible(true);
			if (gameCounter % 10 == 0) {
				timeLeft += 1;
			}
			respawnCounter++;
			if (respawnCounter >= 100) {
				respawnCounter = 0;
				player.setStatus(false);
				player.setStopDraw(false);
				player.reset();
				stopAnimate = false;
				float randomX = bigBorder + player.getSpriteWidth()
						+ (float) Math.random() * (background.width - 3 * player.getSpriteWidth());
				float randomY = smallBorder + player.getSpriteWidth()
						+ (float) Math.random() * (background.height - 3 * player.getSpriteWidth());
				int collisions = 0;
				for (int i = 0; i < enemies.size(); i++) {
					Orc o = enemies.get(i);
					if (player.isColliding(randomX, randomY, o))
						collisions++;
				}
				for (int i = 0; i < barriers.size(); i++) {
					Barrier b = barriers.get(i);
					if (player.isColliding(randomX, randomY, b))
						collisions++;
				}

				if (collisions == 0) {
					player.setX(randomX);
					player.setY(randomY);
				}
			}
		}
	}

	public void spawnOrcs() {
		spawnRate++;
		if (spawnRate == 100 && timeLeft > 0 && !changeLevel) {
			spawnRate = 0;
			float rate = (float) Math.random();
			if (rate <= 0.05) {
				horizRectWave(bigBorder + 224, 0, (float) Math.PI / 2);
			} else if (rate > 0.05 && rate <= 0.1) {
				horizRectWave(bigBorder + 224, 720, 3 * (float) Math.PI / 2);
			} else if (rate > 0.1 && rate <= 0.15) {
				vertRectWave(0, smallBorder + 224, 0);
			} else if (rate > 0.15 && rate <= 0.2) {
				vertRectWave(1280, smallBorder + 224, (float) Math.PI);
			} else if (rate > 0.2 && rate <= 0.3) {
				float prob = (float) Math.random();
				if (prob < 1 / 3) {
					singleWave(bigBorder + 224, 0, (float) Math.PI / 2);
				} else if (prob < 2 / 3) {
					singleWave(bigBorder + 256, 0, (float) Math.PI / 2);
				} else {
					singleWave(bigBorder + 288, 0, (float) Math.PI / 2);
				}
			} else if (rate > 0.3 && rate <= 0.4) {
				float prob = (float) Math.random();
				if (prob < 1 / 3) {
					singleWave(bigBorder + 224, 720, (float) Math.PI * 3 / 2);
				} else if (prob < 2 / 3) {
					singleWave(bigBorder + 256, 720, (float) Math.PI * 3 / 2);
				} else {
					singleWave(bigBorder + 288, 720, (float) Math.PI * 3 / 2);
				}
			} else if (rate > 0.4 && rate <= 0.5) {
				float prob = (float) Math.random();
				if (prob < 1 / 3) {
					singleWave(0, smallBorder + 224, 0);
				} else if (prob < 2 / 3) {
					singleWave(0, smallBorder + 256, 0);
				} else {
					singleWave(0, smallBorder + 288, 0);
				}
			} else if (rate > 0.5 && rate <= 0.6) {
				float prob = (float) Math.random();
				if (prob < 1 / 3) {
					singleWave(1280, smallBorder + 224, (float) Math.PI);
				} else if (prob < 2 / 3) {
					singleWave(1280, smallBorder + 256, (float) Math.PI);
				} else {
					singleWave(1280, smallBorder + 288, (float) Math.PI);
				}
			} else if (rate > 0.6 && rate <= 0.65) {
				float prob = (float) Math.random();
				if (prob < 1 / 2) {
					doubleHorizWave(bigBorder + 224, 0, (float) Math.PI / 2);
				} else {
					doubleHorizWave(bigBorder + 256, 0, (float) Math.PI / 2);
				}
			} else if (rate > 0.65 && rate <= 0.7) {
				float prob = (float) Math.random();
				if (prob < 1 / 2) {
					doubleHorizWave(bigBorder + 224, 720, (float) Math.PI * 3 / 2);
				} else {
					doubleHorizWave(bigBorder + 256, 720, (float) Math.PI * 3 / 2);
				}
			} else if (rate > 0.7 && rate <= 0.75) {
				float prob = (float) Math.random();
				if (prob < 1 / 2) {
					doubleVertWave(0, smallBorder + 224, 0);
				} else {
					doubleVertWave(0, smallBorder + 256, 0);
				}
			} else if (rate > 0.75 && rate <= 0.8) {
				float prob = (float) Math.random();
				if (prob < 1 / 2) {
					doubleVertWave(1280, smallBorder + 224, (float) Math.PI);
				} else {
					doubleVertWave(1280, smallBorder + 256, (float) Math.PI);
				}
			} else if (rate > 0.8 && rate <= 0.85) {
				tripleHorizWave(bigBorder + 224, 0, (float) Math.PI / 2);
			} else if (rate > 0.85 && rate <= 0.9) {
				tripleHorizWave(bigBorder + 224, 720, 3 * (float) Math.PI / 2);
			} else if (rate > 0.9 && rate <= 0.95) {
				tripleVertWave(0, smallBorder + 224, 0);
			} else if (rate > 0.95 && rate <= 1) {
				tripleVertWave(1280, smallBorder + 224, (float) Math.PI);
			}
		}
	}

	public void animateGameObj() {
		player.update();
		enemyPlayer.update();
		for (int i = 0; i < cacti.size(); i++) {
			Cactus c = cacti.get(i);
			c.update();
		}
		for (int i = 0; i < enemies.size(); i++) {
			Orc o = enemies.get(i);
			o.update();
		}
		for (int i = 0; i < explosions.size(); i++) {
			Explosion e = explosions.get(i);
			e.draw();
			e.update();
		}
	}

	public void checkEmptyScreen() {
		if (timeLeft <= 0) {
			int orcsDead = 0;
			for (int i = 0; i < enemies.size(); i++) {
				Orc o = enemies.get(i);
				if (o.getStatus()) {
					orcsDead++;
				}
			}
			if (orcsDead == enemies.size()) {
				emptyScreen = true;
			}
		}

		if (timeLeft <= 0 && emptyScreen) {
			changeLevel = true;
		}
	}

	public void changeLevel() {
		if (changeLevel) {
			reset();
			if (!scrollDown) {
				image(tile, bigBorder + 224, windowHeight - smallBorder - 32);
				image(tile, bigBorder + 256, windowHeight - smallBorder - 32);
				image(tile, bigBorder + 288, windowHeight - smallBorder - 32);
				flickerCounter++;
				if (flickerCounter < 15) {
					image(arrow, bigBorder + 224, windowHeight - smallBorder - 32);
				} else if (flickerCounter < 30) {
					image(tile, bigBorder + 224, windowHeight - smallBorder - 32);
				} else if (flickerCounter == 30) {
					flickerCounter = 0;
				}
			}
			if (player.getY() >= windowHeight - smallBorder - player.getSpriteWidth() - player.getSpriteHeight() - 5
					&& player.getX() >= bigBorder + 224 && player.getX() <= bigBorder + 320) {
				up = false;
				down = false;
				left = false;
				right = false;
				scrollDown = true;
				stopControls = true;
				cacti.clear();
			}
			if (level == 1) {
				if (scrollDown == true && background2Y > smallBorder) {
					player.moveUp((float) 0.8);
					backgroundY -= 2;
					background2Y -= 2;
					background3Y -= 2;
					background4Y -= 2;
					bossBackgroundY -= 2;
				} else if (background2Y == smallBorder) {
					stopControls = false;
					player.setSpeed(2);
					scrollDown = false;
					changeLevel = false;
					level++;
				}
			}
			if (level == 2) {
				if (scrollDown == true && background3Y > smallBorder) {
					player.moveUp((float) 0.8);
					backgroundY -= 2;
					background2Y -= 2;
					background3Y -= 2;
					background4Y -= 2;
					bossBackgroundY -= 2;
				} else if (background3Y == smallBorder) {
					stopControls = false;
					player.setSpeed(2);
					scrollDown = false;
					changeLevel = false;
					level++;
				}
			}

			if (level == 3) {
				if (scrollDown == true && background4Y > smallBorder) {
					player.moveUp((float) 0.8);
					backgroundY -= 2;
					background2Y -= 2;
					background3Y -= 2;
					background4Y -= 2;
					bossBackgroundY -= 2;
				} else if (background4Y == smallBorder) {
					stopControls = false;
					player.setSpeed(2);
					scrollDown = false;
					changeLevel = false;
					level++;
				}
			}

			if (level == 4) {
				if (scrollDown == true && bossBackgroundY > smallBorder) {
					player.moveUp((float) 1);
					backgroundY -= 2;
					background2Y -= 2;
					background3Y -= 2;
					background4Y -= 2;
					bossBackgroundY -= 2;
				} else if (bossBackgroundY == smallBorder) {
					stopControls = false;
					player.setSpeed(2);
					scrollDown = false;
					changeLevel = false;
					level++;
				}
			}

			if (level == 5) {
				if (scrollDown == true && bossBackgroundY != smallBorder) {
					player.setSpeed((float) 1.5);
					backgroundY -= 2;
					background2Y -= 2;
					background3Y -= 2;
					background4Y -= 2;
					bossBackgroundY -= 2;
				} else if (bossBackgroundY == smallBorder) {
					timeLeft = 0;
					stopControls = false;
					up = false;
					player.setSpeed(2);
					scrollDown = false;
					changeLevel = false;
					drawEnemyPlayer = true;
					displayEnemyHealth = true;
				}
			}
		}
	}

	public void reset() {
		timeLeft = 5;// background.width - clock.width - 2;
		fireCounter = 0;
		firing = false;
		changedSpritePosUp = false;
		changedSpritePosDown = false;
		changedSpritePosLeft = false;
		changedSpritePosRight = false;
		stopAnimate = false;
		respawnCounter = 0;
		gameCounter = 0;
		spawnRate = 0;
	}

	public void horizRectWave(float x, float y, float angle) {
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 2; col++) {
				Orc o = new Orc(this, x + 32 * row, y + 32 * col);
				o.setMoveDirection(angle);
				enemies.add(o);
			}
		}
	}

	public void vertRectWave(float x, float y, float angle) {
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 3; col++) {
				Orc o = new Orc(this, x + 32 * row, y + 32 * col);
				o.setMoveDirection(angle);
				enemies.add(o);
			}
		}
	}

	public void singleWave(float x, float y, float angle) {
		Orc o = new Orc(this, x, y);
		o.setMoveDirection(angle);
		enemies.add(o);
	}

	public void doubleHorizWave(float x, float y, float angle) {
		for (int i = 0; i < 2; i++) {
			Orc o = new Orc(this, x + 32 * i, y);
			o.setMoveDirection(angle);
			enemies.add(o);
		}
	}

	public void doubleVertWave(float x, float y, float angle) {
		for (int i = 0; i < 2; i++) {
			Orc o = new Orc(this, x, y + 32 * i);
			o.setMoveDirection(angle);
			enemies.add(o);
		}
	}

	public void tripleHorizWave(float x, float y, float angle) {
		for (int i = 0; i < 3; i++) {
			Orc o = new Orc(this, x + 32 * i, y);
			o.setMoveDirection(angle);
			enemies.add(o);
		}
	}

	public void tripleVertWave(float x, float y, float angle) {
		for (int i = 0; i < 3; i++) {
			Orc o = new Orc(this, x, y + 32 * i);
			o.setMoveDirection(angle);
			enemies.add(o);
		}
	}
	
	public void enemyFire() {
		if (!enemyFiring && !enemyPlayer.getStatus()) {
			enemyFiring = true;
			Bullet bullet = new Bullet(this, enemyPlayer.getX() + enemyPlayer.getSpriteWidth() / 2,
					enemyPlayer.getY() + enemyPlayer.getSpriteHeight() / 2);
			enemyBullets.add(bullet);
			bullet.setShootUp(true);
		}	
	}

	public void keyPressed() {
		if (!stopControls) {
			if (key == 'w') {
				up = true;
				if (!changedSpritePosUp) { // stops sprites from being drawn constantly
					player.setSpriteX(player.getSpriteWidth());
					player.setSpriteY(player.getSpriteHeight() * 3);
					changedSpritePosUp = true;
				}
			}
			if (key == 'a') {
				left = true;
				if (!changedSpritePosLeft) {
					player.setSpriteX(player.getSpriteWidth());
					player.setSpriteY(player.getSpriteHeight());
					changedSpritePosLeft = true;
				}
			}
			if (key == 's') {
				down = true;
				if (!changedSpritePosDown) {
					player.setSpriteX(player.getSpriteWidth());
					player.setSpriteY(0);
					changedSpritePosDown = true;
				}
			}
			if (key == 'd') {
				right = true;
				if (!changedSpritePosRight) {
					player.setSpriteX(player.getSpriteWidth());
					player.setSpriteY(player.getSpriteHeight() * 2);
					changedSpritePosRight = true;
				}
			}

			if (key == CODED && firing == false && !player.getStatus()) {
				firing = true;
				Bullet bullet = new Bullet(this, player.getX() + player.getSpriteWidth() / 2,
						player.getY() + player.getSpriteHeight() / 2);
				bullets.add(bullet);
				if (keyCode == UP) {
					bullet.setShootUp(true);
					// player faces direction of shooting for a bit
					if (!changedSpriteShootUp) {
						previousSpriteX = player.getSpriteX();
						previousSpriteY = player.getSpriteY();
						player.setSpriteY(player.getSpriteHeight() * 3);
						changedSpriteShootUp = true;
					}
				}
				if (keyCode == DOWN) {
					bullet.setShootDown(true);
					if (!changedSpriteShootDown) {
						previousSpriteX = player.getSpriteX();
						previousSpriteY = player.getSpriteY();
						player.setSpriteY(0);
						changedSpriteShootDown = true;
					}
				}
				if (keyCode == LEFT) {
					bullet.setShootLeft(true);
					if (!changedSpriteShootLeft) {
						previousSpriteX = player.getSpriteX();
						previousSpriteY = player.getSpriteY();
						player.setSpriteY(player.getSpriteHeight());
						changedSpriteShootLeft = true;
					}
				}
				if (keyCode == RIGHT) {
					bullet.setShootRight(true);
					if (!changedSpriteShootUp) {
						previousSpriteX = player.getSpriteX();
						previousSpriteY = player.getSpriteY();
						player.setSpriteY(player.getSpriteHeight() * 2);
						changedSpriteShootRight = true;
					}
				}
			}
		}
	}

	public void keyReleased() {
		if (!stopControls) {
			if (key == ' ' && mode == 0) {
				mode = 1;
			}
			if (key == 'w') {
				up = false;
				player.setRunning(false);
				changedSpritePosUp = false; // allows face direction of shooting again
			}
			if (key == 'a') {
				left = false;
				player.setRunning(false);
				changedSpritePosLeft = false;
			}
			if (key == 's') {
				down = false;
				player.setRunning(false);
				changedSpritePosDown = false;
			}
			if (key == 'd') {
				right = false;
				player.setRunning(false);
				changedSpritePosRight = false;
			}
			if (key == CODED && !player.getStatus()) {
				if (keyCode == UP)
					changedSpriteShootUp = false;
				if (keyCode == DOWN)
					changedSpriteShootDown = false;
				if (keyCode == LEFT)
					changedSpriteShootLeft = false;
				if (keyCode == RIGHT)
					changedSpriteShootRight = false;
			}
		}
	}

	public static void main(String[] args) {
		PApplet.main("Game", args);
	}

}
