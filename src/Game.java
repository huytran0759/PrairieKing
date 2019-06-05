import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class Game extends PApplet {

	PImage intro, background, background2, background3, background4, bossBackground, clock, controls, arrow, tile,
			lives, enemySpeechBubble, end;
	PFont font;
	int windowWidth = 1280;
	int windowHeight = 720;
	float smallBorder, bigBorder;
	float backgroundX, backgroundY, background2Y, background3Y, background4Y, bossBackgroundY;
	int mode;
	ArrayList<Cactus> cacti;
	ArrayList<Bullet> bullets;
	ArrayList<Bullet> enemyBullets;
	ArrayList<Orc> orcs;
	ArrayList<Barrier> barriers;
	ArrayList<BackgroundObj> powerUps;
	ArrayList<Explosion> explosions;
	Player player;
	int maxTimeLeft;
	EnemyPlayer enemyPlayer;
	Orc orc;
	boolean up, down, left, right;
	int previousSpriteX, previousSpriteY;
	boolean changedSpritePosUp, changedSpritePosDown, changedSpritePosLeft, changedSpritePosRight;
	boolean changedEnemySpritePosLeft, changedEnemySpritePosRight;
	boolean changedSpriteShootUp, changedSpriteShootDown, changedSpriteShootLeft, changedSpriteShootRight;
	boolean firing;
	boolean enemyFiring;
	boolean playerDead;
	boolean changeLevel;
	boolean scrollDown;
	boolean emptyScreen;
	boolean barriersLvl2Init, barriersLvl3Init, barriersLvl4Init, barriersLvl5Init, barriersLvl1Init;
	boolean stopControls;
	boolean drawEnemyPlayer;
	boolean displayEnemyHealth;
	boolean enemyPlayerAction;
	boolean displaySpeechBubble;
	boolean displayEnd;
	int fireCounter, enemyFireCounter, timeLeft, gameCounter, respawnCounter, spawnRate, fireCounterLimit,
			powerUpCounter, invincibleCounter, flickerCounter, gameOverCounter, winCounter;
	int orcSpawn;
	int level;
	int enemyNewX;

	public void settings() {
		size(windowWidth, windowHeight);
	}

	public void setup() {
		size(windowWidth, windowHeight);
		font = createFont("Cambria Math", 20);
		textFont(font);
		intro = loadImage("intro.png");
		end = loadImage("end.png");
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
		enemySpeechBubble = loadImage("enemySpeechBubble.png");
		bigBorder = (windowWidth - background.width) / 2;
		smallBorder = (windowHeight - background.height) / 2;
		backgroundX = bigBorder;
		backgroundY = smallBorder;
		background2Y = windowHeight - smallBorder;
		background3Y = background2Y + background.height;
		background4Y = background3Y + background.height;
		bossBackgroundY = background4Y + background.height;
		mode = 0;
		level = 1;
		maxTimeLeft = background.width - clock.width - 2;
		timeLeft = maxTimeLeft;
		fireCounter = 0;
		enemyFireCounter = 0;
		fireCounterLimit = 10;
		cacti = new ArrayList<Cactus>();
		bullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<Bullet>();
		orcs = new ArrayList<Orc>();
		barriers = new ArrayList<Barrier>();
		powerUps = new ArrayList<BackgroundObj>();
		explosions = new ArrayList<Explosion>();
		player = new Player(this, windowWidth / 2, windowHeight / 2);
		enemyPlayer = new EnemyPlayer(this, -1000, -1000);
		playerDead = false;
		changeLevel = false;
		scrollDown = false;
		drawEnemyPlayer = false;
		displayEnemyHealth = false;
		displaySpeechBubble = false;
		firing = false;
		enemyFiring = false;
		enemyPlayerAction = false;
		barriersLvl2Init = false;
		barriersLvl3Init = false;
		barriersLvl4Init = false;
		barriersLvl5Init = false;
		barriersLvl1Init = false;
		stopControls = false;
		powerUpCounter = 0;
		invincibleCounter = 0;
		flickerCounter = 0;
		gameOverCounter = 0;
		winCounter = 0;
		orcSpawn = 100;
		changedSpritePosUp = false;
		changedSpritePosDown = false;
		changedSpritePosLeft = false;
		changedSpritePosRight = false;
		changedEnemySpritePosLeft = false;
		changedEnemySpritePosRight = false;
		enemyNewX = (int) windowWidth / 2;
		displayEnd = false;
	}

	public void draw() {
		if (mode == 0) {
			displayIntro();
		} else if (mode == 1) {
			gameCounter++;
			// display every object
			drawBackgrounds();
			drawBorders();
			drawLives();
			drawTimer();
			drawCacti();
			player.draw();
			drawEnemy();
			animateGameObj();
			// stops movement at the beginning of each level
			if (gameCounter < 250 && !changeLevel) {
				player.setRunning(false);
				if (level == 1)
					image(controls, bigBorder + background.width / 2 - controls.width / 2,
							windowHeight - smallBorder + 5);
				if (displaySpeechBubble)
					image(enemySpeechBubble, enemyPlayer.getX() - 16, enemyPlayer.getY() - 64);
			} else {
				respawn();
				// if level is 5, don't spawn orcs or power-ups
				if (level != 5) {
					drawPowerUps();
					spawnOrcs();
					drawOrcs();
				} else {
					enemyPlayerAction = true;
					displaySpeechBubble = false;
				}
				if (!playerDead) {
					decreaseTime();
					enemyAction();
					playerAction();
					player.updatePowerUpDuration();
					bulletAction();
					orcAction();
					detectCollision();
					checkInvincibility();
					powerUpAction();
					checkForWin();
				}
			}
			levelInit();
		} else if (mode == 2) {
			displayEnd();
		}
	}

	public void drawBackgrounds() {
		image(background, backgroundX, backgroundY);
		image(background2, backgroundX, background2Y);
		image(background3, backgroundX, background3Y);
		image(background4, backgroundX, background4Y);
		image(bossBackground, backgroundX, bossBackgroundY);
	}
	
	public void drawBorders() {
		fill(0);
		rect(0, 0, windowWidth, smallBorder);
		rect(0, windowHeight - smallBorder, windowWidth, smallBorder);
		rect(0, 0, bigBorder, windowHeight);
		rect(bigBorder + background.width, 0, bigBorder, windowHeight);
	}

	public void drawLives() {
		fill(255);
		text("x" + player.getLives(), bigBorder - 45, smallBorder + 17);
		image(lives, bigBorder - lives.width - 50, smallBorder);
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

	public void initDefaultCacti() {
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

	public void displayEnd() {
		background(0);
		imageMode(CENTER);
		image(end, windowWidth / 2, windowHeight / 2);
	}

	public void drawEnemy() {
		if (drawEnemyPlayer && displayEnemyHealth) {
			enemyPlayer.draw();
			drawEnemyHP();
		}
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

	public void decreaseTime() {
		if (timeLeft >= 0 && gameCounter % 10 == 0) {
			timeLeft -= 0.001;
		}
	}

	public void checkInvincibility() {
		if (player.getInvincible() && invincibleCounter < 250) {
			invincibleCounter++;
			if (invincibleCounter == 250) {
				player.setInvincible(false);
				invincibleCounter = 0;
			}
		}
	}

	public void powerUpAction() {
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
		} else {
			// movement for boss level
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
			b.draw();
			if (b.getShootUp()) {
				player.setShootUp(true);
				b.move((float) Math.PI * 3 / 2, barriers);
				if (!b.getExists()) {
					bullets.remove(b);
					player.setShootUp(false);
				}
			}
			if (b.getShootDown()) {
				player.setShootDown(true);
				b.move((float) Math.PI / 2, barriers);
				if (!b.getExists()) {
					bullets.remove(b);
					player.setShootDown(false);
				}
			}
			if (b.getShootLeft()) {
				player.setShootLeft(true);
				b.move((float) Math.PI, barriers);
				if (!b.getExists()) {
					bullets.remove(b);
					player.setShootLeft(false);
				}
			}
			if (b.getShootRight()) {
				player.setShootRight(true);
				b.move(0, barriers);
				if (!b.getExists()) {
					bullets.remove(b);
					player.setShootRight(false);
				}
			}
		}

		for (int i = 0; i < enemyBullets.size(); i++) {
			Bullet b = enemyBullets.get(i);
			b.draw();
			if (b.getShootUp()) {
				enemyPlayer.setShootUp(true);
				b.move((float) Math.PI * 3 / 2, barriers);
				if (!b.getExists()) {
					bullets.remove(b);
					enemyPlayer.setShootUp(false);
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
		if (fireCounter >= fireCounterLimit) {
			fireCounter = 0;
			player.setSpriteX(previousSpriteX);
			player.setSpriteY(previousSpriteY);
			firing = false;
		}

		if (enemyFireCounter >= fireCounterLimit) {
			enemyFireCounter = 0;
			enemyFiring = false;
		}
	}

	public void drawOrcs() {
		for (int i = 0; i < orcs.size(); i++) {
			Orc o = orcs.get(i);
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
		for (int i = 0; i < orcs.size(); i++) {
			Orc o = orcs.get(i);
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
		// orc collision with player
		for (int i = 0; i < orcs.size(); i++) {
			Orc o = orcs.get(i);
			if (player.isColliding(o) && !o.getDead() && !player.getInvincible()) {
				player.setDead(true);
				player.setPowerUp(false);
				playerDead = true;
				numCollisions++;
			}
			// bullet collision with orc
			for (int j = 0; j < bullets.size(); j++) {
				Bullet b = bullets.get(j);
				if (b.isColliding(o) && !o.getDead()) {
					o.subtractHP(b.getDamage());
					// power-ups drop if orc dies
					if (o.getDead()) {
						float powerUp = (float) Math.random();
						if (powerUp <= 0.01) {
							OneUp p = new OneUp(this, o.getX(), o.getY());
							powerUps.add(p);
						} else if (powerUp <= 0.06) {
							Coffee p = new Coffee(this, o.getX(), o.getY());
							powerUps.add(p);
							player.setPowerUp(true);
						} else if (powerUp <= 0.11) {
							MachineGun p = new MachineGun(this, o.getX(), o.getY());
							powerUps.add(p);
							player.setPowerUp(true);
						} else if (powerUp <= 0.12) {
							Nuke p = new Nuke(this, o.getX(), o.getY());
							powerUps.add(p);
							player.setPowerUp(true);
						}
					}
					bullets.remove(b);
				}
			}
		}
		// boss's bullets collision with player
		for (int i = 0; i < enemyBullets.size(); i++) {
			Bullet b = enemyBullets.get(i);
			if (b.isColliding(player) && !player.getDead()) {
				player.setDead(true);
				playerDead = true;
				numCollisions++;
				enemyBullets.remove(b);
			}
		}
		// if many things colliding with player, only 1 life taken away
		if (numCollisions > 0) {
			player.removeLife();
		}	
		// player's bullets collision with boss
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			if (b.isColliding(enemyPlayer) && !enemyPlayer.getDead()) {
				enemyPlayer.subtractHP(b.getDamage());
				bullets.remove(b);
			}
		}
		// bug fix for bullets staying after colliding with barrier
		for (int i = 0; i < barriers.size(); i++) {
			Barrier b = barriers.get(i);
			for (int j = 0; j < bullets.size(); j++) {
				Bullet pb = bullets.get(j);
				if (pb.isColliding(b)) {
					bullets.remove(pb);
				}
			}
			for (int j = 0; j < enemyBullets.size(); j++) {
				Bullet eb = enemyBullets.get(j);
				if (eb.isColliding(b)) {
					enemyBullets.remove(eb);
				}
			}
		}
		// check what kind of power-up and activate power-up
		for (int i = 0; i < powerUps.size(); i++) {
			BackgroundObj p = powerUps.get(i);
			if (!p.getStopDraw()) {
				if (player.isColliding(p)) {
					player.setPowerUp(false);
					p.setStopDraw(true);
					if (p instanceof OneUp) {
						player.addLife();
						powerUps.remove(p);
					} else if (p instanceof Coffee) {
						player.setPowerUp(true);
						player.activateCoffee();
						powerUps.remove(p);
					} else if (p instanceof MachineGun) {
						player.setPowerUp(true);
						player.activateMachineGun(bullets);
						fireCounterLimit = 1;
						powerUps.remove(p);
					} else if (p instanceof Nuke) {
						player.setPowerUp(true);
						for (int j = 0; j < orcs.size(); j++) {
							Orc o = orcs.get(j);
							if (!o.getDead()) {
								o.setDead(true);
							}
						}
						// when nuke used, explosions in random places
						for (int j = 0; j < 15; j++) {
							Explosion o = new Explosion(this,
									bigBorder + (float) Math.random() * (background.width - player.getSpriteWidth()),
									smallBorder + (float) Math.random() * (background.height - player.spriteWidth));
							explosions.add(o);
						}
						powerUps.remove(p);

					}
				}
				// power-ups despawn after period of time
				p.updateDespawnCounter();
				if (p.getDespawnCounter() == 500) {
					p.setStopDraw(true);
					powerUps.remove(p);
				}
			}
		}
	}

	public void respawn() {
		// if the player has lives left, set invincibility temporarily
		if (playerDead && player.livesLeft()) {
			player.setInvincible(true);
			// remove all power-ups if dead
			for (int i = 0; i < powerUps.size(); i++) {
				BackgroundObj b = powerUps.get(i);
				b.setUsed();
				b.setStopDraw(true);
			}
			// add some time to clock as penalty for death
			if (gameCounter % 10 == 0 && level != 5) {
				timeLeft += 1;
			}
			respawnCounter++;
			if (respawnCounter >= 100) {
				respawnCounter = 0;
				player.setDead(false);
				player.setStopDraw(false);
				player.reset();
				playerDead = false;
				float randomX = 0;
				float randomY = 0;
				// if regular level, spawn anywhere on screen, if boss level, spawn only on top half
				if (level != 5) {
					randomX = bigBorder + player.getSpriteWidth()
							+ (float) Math.random() * (background.width - 3 * player.getSpriteWidth());
					randomY = smallBorder + player.getSpriteWidth()
							+ (float) Math.random() * (background.height - 3 * player.getSpriteWidth());
				} else {
					randomX = windowWidth / 2;
					randomY = windowHeight / 2 - 64;
				}

				int collisions = 0;
				// check if new random spawn is on top of orc or barrier
				for (int i = 0; i < orcs.size(); i++) {
					Orc o = orcs.get(i);
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
		} else if (!player.livesLeft()) { // if player has no lives left, reset everything and show title
			gameOverCounter++;
			if (gameOverCounter == 200) {
				setup();
				mode = 0;
			}
		}
	}

	public void spawnOrcs() {
		spawnRate++;
		if (spawnRate == orcSpawn && timeLeft > 0 && !changeLevel) {
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
		enemyPlayer.update();
		player.update();
		for (int i = 0; i < cacti.size(); i++) {
			Cactus c = cacti.get(i);
			c.update();
		}
		for (int i = 0; i < orcs.size(); i++) {
			Orc o = orcs.get(i);
			o.update();
		}
		for (int i = 0; i < explosions.size(); i++) {
			Explosion e = explosions.get(i);
			e.draw();
			e.update();
		}
	}

	public void checkForWin() {
		// if not boss level, then continue game
		if (level != 5) {
			checkEmptyScreen();
			changeLevel();
		} else {
			checkEmptyScreen();
			if (displayEnd) {
				winCounter++;
				if (winCounter >= 200) {
					mode = 2;
				}
			}
		}
	}
	
	public void checkEmptyScreen() {
		// empty screen for regular level is that all orcs are dead, empty screen for boss level is boss is dead
		if (level != 5) {
			if (timeLeft <= 0) {
				int orcsDead = 0;
				for (int i = 0; i < orcs.size(); i++) {
					Orc o = orcs.get(i);
					if (o.getDead()) {
						orcsDead++;
					}
				}
				if (orcsDead == orcs.size()) {
					emptyScreen = true;
					changeLevel = true;
				}
			}
		} else {
			if (enemyPlayer.getDead()) {
				emptyScreen = true;
				displayEnd = true;
			}
		}
	}

	public void changeLevel() {
		if (changeLevel) {
			reset();
			// if the player doesn't move down, display
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
			// when player hits spot, stop all movement
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
			// move all backgrounds up until background level is main
			if (level == 1) {
				if (scrollDown == true && background2Y > smallBorder) {
					player.moveUp((float) 0.8); // moves player back to center of screen while backgrounds move down
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
			// if boss level, spawn boss
			if (level == 5) {
				if (scrollDown == true && bossBackgroundY != smallBorder) {
					player.setSpeed((float) 1.5);
					backgroundY -= 2;
					background2Y -= 2;
					background3Y -= 2;
					background4Y -= 2;
					bossBackgroundY -= 2;
				} else if (bossBackgroundY == smallBorder) {
					enemyPlayer = new EnemyPlayer(this, windowWidth / 2, windowHeight - smallBorder - 96);
					timeLeft = 0;
					stopControls = false;
					up = false;
					player.setSpeed(2);
					scrollDown = false;
					changeLevel = false;
					drawEnemyPlayer = true;
					displayEnemyHealth = true;
					displaySpeechBubble = true;
				}
			}
		}
	}

	public void levelInit() {
		if (!changeLevel) {
			switch (level) {
			case 1:
				if (!barriersLvl1Init) {
					initDefaultCacti();
					barriersLvl1Init = true;
				}
				break;
			case 2:
				if (!barriersLvl2Init) {
					initDefaultCacti();
					initLvl2Barriers();
					barriersLvl2Init = true;
				}
				break;
			case 3:
				if (!barriersLvl3Init) {
					barriers.clear();
					initDefaultCacti();
					initLvl3Cacti();
					initLvl3Barriers();
					barriersLvl3Init = true;
				}
				break;
			case 4:
				if (!barriersLvl4Init) {
					barriers.clear();
					initDefaultCacti();
					initLvl4Barriers();
					barriersLvl4Init = true;
				}
				break;
			case 5:
				if (!barriersLvl5Init) {
					barriers.clear();
					initLvl5Cacti();
					initLvl5Barriers();
					barriersLvl5Init = true;
				}
				break;
			}
		}
	}

	public void reset() {
		timeLeft = maxTimeLeft;
		firing = false;
		changedSpritePosUp = false;
		changedSpritePosDown = false;
		changedSpritePosLeft = false;
		changedSpritePosRight = false;
		playerDead = false;
		respawnCounter = 0;
		fireCounter = 0;
		gameCounter = 0;
		spawnRate = 0;
	}

	public void horizRectWave(float x, float y, float angle) {
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 2; col++) {
				Orc o = new Orc(this, x + 32 * row, y + 32 * col);
				o.setMoveDirection(angle);
				orcs.add(o);
			}
		}
	}

	public void vertRectWave(float x, float y, float angle) {
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < 3; col++) {
				Orc o = new Orc(this, x + 32 * row, y + 32 * col);
				o.setMoveDirection(angle);
				orcs.add(o);
			}
		}
	}

	public void singleWave(float x, float y, float angle) {
		Orc o = new Orc(this, x, y);
		o.setMoveDirection(angle);
		orcs.add(o);
	}

	public void doubleHorizWave(float x, float y, float angle) {
		for (int i = 0; i < 2; i++) {
			Orc o = new Orc(this, x + 32 * i, y);
			o.setMoveDirection(angle);
			orcs.add(o);
		}
	}

	public void doubleVertWave(float x, float y, float angle) {
		for (int i = 0; i < 2; i++) {
			Orc o = new Orc(this, x, y + 32 * i);
			o.setMoveDirection(angle);
			orcs.add(o);
		}
	}

	public void tripleHorizWave(float x, float y, float angle) {
		for (int i = 0; i < 3; i++) {
			Orc o = new Orc(this, x + 32 * i, y);
			o.setMoveDirection(angle);
			orcs.add(o);
		}
	}

	public void tripleVertWave(float x, float y, float angle) {
		for (int i = 0; i < 3; i++) {
			Orc o = new Orc(this, x, y + 32 * i);
			o.setMoveDirection(angle);
			orcs.add(o);
		}
	}

	public void enemyAction() {
		// give enemy new movement pattern
		if (enemyNewX == enemyPlayer.getX() || enemyNewX == enemyPlayer.getX() - 1
				|| enemyNewX == enemyPlayer.getX() + 1) {
			enemyNewX = (int) (bigBorder + Math.random() * (background.width - enemyPlayer.getSpriteWidth()));
		}
		
		float dx = enemyNewX - enemyPlayer.getX();
		// if new position is left of enemy
		if (dx < 0) {
			if (enemyPlayerAction) {
				if (!changedEnemySpritePosLeft) {
					enemyPlayer.setRunning(true);
					enemyPlayer.setSpriteX(0);
					enemyPlayer.setSpriteY(enemyPlayer.getSpriteHeight());
					changedEnemySpritePosLeft = true;
				}
				enemyPlayer.move((float) Math.PI);
				if (enemyPlayer.getX() < bigBorder + 224 || enemyPlayer.getX() > bigBorder + 320) {
					enemyFire();
				}
			}
		} else if (dx > 0) { // if new position is right of enemy
			if (enemyPlayerAction) {
				if (!changedEnemySpritePosRight) {
					enemyPlayer.setRunning(true);
					enemyPlayer.setSpriteX(32);
					enemyPlayer.setSpriteY(enemyPlayer.getSpriteHeight());
					changedEnemySpritePosRight = true;
				}
				enemyPlayer.move(0);
				if (enemyPlayer.getX() < bigBorder + 224 || enemyPlayer.getX() > bigBorder + 320) {
					enemyFire();
				}
			}
		}
	}

	public void enemyFire() {
		if (!enemyFiring && !enemyPlayer.getDead()) {
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

			if (key == CODED && firing == false && !player.getDead()) {
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
			if (key == CODED && !player.getDead()) {
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
