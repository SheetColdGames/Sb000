package me.sheetcoldgames.sb000.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import me.sheetcoldgames.sb000.Constants;
import me.sheetcoldgames.sb000.engine.ACTION;
import me.sheetcoldgames.sb000.engine.AIR_STATUS;
import me.sheetcoldgames.sb000.engine.Attack;
import me.sheetcoldgames.sb000.engine.DIRECTION;
import me.sheetcoldgames.sb000.engine.Entity;
import me.sheetcoldgames.sb000.engine.Input;
import me.sheetcoldgames.sb000.engine.POINT_TYPE;
import me.sheetcoldgames.sb000.engine.SheetCamera;
import me.sheetcoldgames.sb000.engine.SheetPoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class GameController {
	
	public SheetCamera camera;
	public OrthographicCamera hudCamera;
	
	public ArrayList<LinkedList<SheetPoint>> groupPoints;
	
	// Our player
	public ArrayList<Entity> aEntity;
	
	Input input;
	
	public ArcherController archerController;
	
	public int heroIndex = 0;
	public int bossIndex = 1; 
	
	// hero constants - this should be organized
	public final float HERO_IDLE_DURATION 		= 5/12f;
	public final float HERO_RUNNING_DURATION 	= 8/12f;
	public final float HERO_TAKE_OFF_DURATION 	= 2/12f;
	public final float HERO_JUMPING_DURATION 	= 4/12f;
	public final float HERO_FALLING_DURATION 	= 6/12f;
	public final float HERO_LANDING_DURATION 	= 3/12f;
	public final float[] HERO_BASIC_ATTACK_DURATION = {10/12f, 10/12f};
	public final float HERO_BLOCK_DURATION 		= 14f/12f;
	public final float HERO_DAMAGE_DURATION 	= 3f/12f;
	public final float HERO_DYING_DURATION 		= 8f/12f;
	
	public GameController() {
		initCameras();
		
		initializeTestMap("map.fis");
		
		initializeEntities();
		camera.setTarget(aEntity.get(0));
		
		input = new Input();
		Gdx.input.setInputProcessor(input);
		
		archerController = new ArcherController();
	}
	
	private void initializeTestMap(String filename) {
		groupPoints = new ArrayList<LinkedList<SheetPoint>>();
		// here we'll load the map inside the ArrayList<LinkedList<>>
		FileHandle handle = Gdx.files.internal(filename);
		Scanner scan = new Scanner(handle.readString());
		int i = -1;
		
		while (scan.hasNext()) {
			StringBuffer line = new StringBuffer(scan.nextLine());
			String[] sa = line.toString().split(" ");
			if (sa.length == 1) {
				// we're dealing with a new group
				groupPoints.add(new LinkedList<SheetPoint>());
				i++;
				System.out.println(sa[0]);
			} else {
				// we're dealing with new points
				groupPoints.get(i).add(new SheetPoint(
								Float.parseFloat(sa[0]),
								Float.parseFloat(sa[1]), getPointType(sa[2])));
				System.out.println(sa[0] + " " + sa[1] + " " + sa[2]);
			}
		}
		scan.close();
		if (i == -1) {
			// We don't have any points, we must initialize with a group with no points
			groupPoints.add(new LinkedList<SheetPoint>());
		}
	}
	
	private void initializeEntities() {
		aEntity = new ArrayList<Entity>();
		
		aEntity.add(new Entity(1.5f, 1.5f));
		aEntity.get(heroIndex).pos.set(5f, 8f);
		
		// another entity, the BOSS
		aEntity.add(new Entity(20/16f, 22/16f));
		aEntity.get(bossIndex).pos.set(9f, 9f);
	}
	
	public void dispose() {
		deletingMapPoints();
	}
	
	private void deletingMapPoints() {
		if (!groupPoints.isEmpty()) {
			for (int k = 0; k < groupPoints.size(); k++) {
				for (int j = 0; j < groupPoints.get(k).size(); j++) {
					groupPoints.get(k).clear();
				}
			}
			groupPoints.clear();
		}
	}
	
	public POINT_TYPE getPointType(String type) {
		if (type.equals(POINT_TYPE.SOLID.toString())) {
			return POINT_TYPE.SOLID;
		} else if (type.equals(POINT_TYPE.DOOR.toString())) {
			return POINT_TYPE.DOOR;
		}
		return null;
	}
	
	private void initCameras() {
		// initializing the hud camera
		hudCamera = new OrthographicCamera(Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
		hudCamera.position.set(hudCamera.viewportWidth/2f, hudCamera.viewportHeight/2f, 0f);
		hudCamera.update();
		
		// initializing the main game camera
		camera = new SheetCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
		camera.position.set(camera.viewportWidth/2f, camera.viewportHeight/2f, 0f);
		camera.setBounds(camera.viewportWidth/2f - 1, camera.viewportHeight/2f - 1, // this one is almost mandatory
				1 + camera.viewportWidth/2f, 1 + camera.viewportHeight/2f); // add constants to these variables
		camera.update();
	}
	
	/**
	 * Here's where all the magic happens
	 */
	public void update() {
		// updating the state time
		for (Entity ent : aEntity) {
			ent.stateTime += Gdx.graphics.getDeltaTime();
		}

		// getting input response
		handleInput(aEntity.get(heroIndex));
		archerController.updateAI(aEntity.get(bossIndex), aEntity.get(heroIndex));
		
		// updating
		updateEntities();
		camera.update(0f, 0f);
	}
	
	float walkSpeed = 1f;
	float maxHorizontalSpeed = .12f;
	float minSpeed = .12f;
	
	boolean attacking = false;
	boolean blocking = false;
	
	// initial impulse
	float initialImpulse = .04f;
	boolean jump = false;
	
	private void handleInput(Entity player) {
		if (player.action != ACTION.TAKING_DAMAGE) {
			if (input.buttons[Input.RIGHT]) {
				if (player.vel.x == 0f) player.vel.x = minSpeed;
				player.vel.x += Gdx.graphics.getDeltaTime() * walkSpeed;
			} else if (input.buttons[Input.LEFT]) {
				if (player.vel.x == 0f) player.vel.x = -minSpeed;
				player.vel.x -= Gdx.graphics.getDeltaTime() * walkSpeed;
			} else {
				player.vel.x = 0f; // MathUtils.lerp(player.vel.x, 0f, .1f);
			}
		} else {
			if (player.dir == DIRECTION.RIGHT) {
				player.vel.x -= Gdx.graphics.getDeltaTime() * walkSpeed/4f; 
			} else {
				player.vel.x += Gdx.graphics.getDeltaTime() * walkSpeed/4f;
			}
		}
		
		// if you're walking or idle, you can attack or block
		if (player.airStatus == AIR_STATUS.GROUNDED && (player.action == ACTION.IDLE || 
				player.action == ACTION.WALKING || player.action == ACTION.ATTACKING)) {
			if (input.buttons[Input.ATTACK]) {
				attack(player);
			} else {
				attacking = false;
			}
		} else {
			attacking = false;
		}
		
		// RELACIONADO AO PULO
		if (initialImpulse > 0f) initialImpulse -= .004f;
		if (initialImpulse < 0f) {
			initialImpulse =  0f;
		}
		
		/*
		if (input.buttons[Input.JUMP] 
				&& player.airStatus == AIR_STATUS.GROUNDED 
				&& player.action != ACTION.ATTACKING) {
			player.airStatus = AIR_STATUS.TAKING_OFF;
		} else */ 
		if ((input.buttons[Input.JUMP] || input.buttons[Input.UP]) && player.attacks.isEmpty()) {
			jump(Gdx.graphics.getDeltaTime(), player);
		} else {
			jump = false;
		}
		
		if (input.buttons[Input.DEBUG_DAMAGE] && !player.takingDamage) {
			resetEntityProperties(player);
			player.takingDamage = true;
		}
		
		player.vel.x = MathUtils.clamp(player.vel.x, -maxHorizontalSpeed, maxHorizontalSpeed); 
	}
	
	private void attack(Entity ent) {
		if (!attacking) {
			attacking = true;
			if (ent.attacks.size() < 2) {
				ent.attacks.add(
						new Attack(ent.pos.x + (ent.dir == DIRECTION.RIGHT ? 1.0f : -1.0f), ent.pos.y, 
								1f, 1f, // width and height 
								10f/12f, // duration 
								1/2f) // damage TIME!
						);
			}
		}
	}
	
	private void jump(float deltaTime, Entity ent) {
		if (ent.grounded && !jump) {
			initialImpulse = .04f;
			ent.grounded = false;
			ent.pos.y += .1f;
			ent.vel.y = initialImpulse;
			jump = true;
		} else {
			ent.vel.y += initialImpulse;
		}
	}
	
	private void updateEntities() {
		int index = 0;
		for (Entity ent : aEntity) {
			ent.vel.y += Constants.GRAVITY * Gdx.graphics.getDeltaTime();
			ent.vel.y = MathUtils.clamp(ent.vel.y, -1f, 1f);
			ent.grounded = false;
			updateAttackStatus(ent, index);
			updateEntityPosition(ent);
			updateEntityStatuses(ent);
			index++;
		}
		System.out.println(getStatusLog(aEntity.get(bossIndex)));
	}
	
	private void updateAttackStatus(Entity ent, int entIndex) {
		if (!ent.attacks.isEmpty() && ent.currentAttackIndex < ent.attacks.size()) {
			ent.vel.x = 0f;
			ent.attacks.get(ent.currentAttackIndex).update(Gdx.graphics.getDeltaTime());
			if (ent.attacks.get(ent.currentAttackIndex).isDamaging()) {
				for (int k = 0; k < aEntity.size(); k++) {
					// we don't want to attack ourselves
					if (k == entIndex) {
						continue;
					}
					// This is ugly as fuck, fix the looks, see the method signature to understand
					if (collidesWithEnemy(
							ent.attacks.get(ent.currentAttackIndex).pos.x - ent.attacks.get(ent.currentAttackIndex).width/2f,
							ent.attacks.get(ent.currentAttackIndex).pos.y - ent.attacks.get(ent.currentAttackIndex).height/2f,
							ent.attacks.get(ent.currentAttackIndex).width, ent.attacks.get(ent.currentAttackIndex).height,
							aEntity.get(k).pos.x - aEntity.get(k).width/2f, aEntity.get(k).pos.y - aEntity.get(k).height/2f,
							aEntity.get(k).width, aEntity.get(k).height)) {
						System.out.println("SAHUAISDHUAIFHUIEOWHFEUIFHUIEVWHUEWYTGUHGRUIGBHRUEIGHRUWVH");
						if (aEntity.get(k).action != ACTION.TAKING_DAMAGE) {
							// we'll leave a hit on him
							aEntity.get(k).hitPoints -= 10f;
						}
						if (aEntity.get(k).action != ACTION.ATTACKING) {
							// aEntity.get(k).hitPoints -= 1f;
							resetEntityProperties(aEntity.get(k));
							aEntity.get(k).takingDamage = true;
						}
					}
				}
			}
			if (ent.attacks.get(ent.currentAttackIndex).isFinished()) {
				ent.currentAttackIndex++;
				// check and clear if it's empty
				if (ent.currentAttackIndex >= ent.attacks.size()) {
					ent.attacks.clear();
					ent.currentAttackIndex = 0;
				}
			}
		} else { // just to guarantee if it will erase
			ent.attacks.clear();
			ent.currentAttackIndex = 0;
		}
	}
	
	private boolean collidesWithEnemy(
			float attX, float attY, float attWidth, float attHeight,
			float enemyX, float enemyY, float enemyWidth, float enemyHeight) {
		return (attX < enemyX + enemyWidth &&
	        attX + attWidth > enemyX &&
	        attY < enemyY + enemyHeight &&
	        attHeight + attY > enemyY);
	}
	
	private void updateEntityPosition(Entity ent) {
		Vector2 intersection = new Vector2(0f, 0f);
		// we need to determine whose methods are able to change statuses
		if (ent.action == ACTION.PUSHING_WALL) { ent.action = ACTION.IDLE; }
		for (int currentGroup = 0; currentGroup < groupPoints.size(); currentGroup++) {
			for (int currentPoint = 0; currentPoint < groupPoints.get(currentGroup).size() - 1; currentPoint++) {
				float px1 = groupPoints.get(currentGroup).get(currentPoint).pos.x;
				float py1 = groupPoints.get(currentGroup).get(currentPoint).pos.y;
				float px2 = groupPoints.get(currentGroup).get(currentPoint+1).pos.x;
				float py2 = groupPoints.get(currentGroup).get(currentPoint+1).pos.y;
				
				// left ray
				if (Intersector.intersectSegments(px1, py1, px2, py2, 
						ent.pos.x - ent.collisionWidth/2f, 
						ent.pos.y + ent.collisionHeight/2f + Math.abs(ent.vel.y) + ent.offsetHeight, 
						ent.pos.x - ent.collisionWidth/2f,
						ent.pos.y - ent.collisionHeight/2f - Math.abs(ent.vel.y) - ent.offsetHeight, 
						intersection)) {
					
					
					if (intersection.y > ent.pos.y) {
						// remover essa constante
						ent.pos.y = intersection.y - ent.collisionHeight/2f - ent.offsetHeight -.1f;
						if (ent.vel.y > 0f) {
							ent.vel.y = 0f;
						}
					} else {
						ent.pos.y = intersection.y + ent.collisionHeight/2f + ent.offsetHeight;
						ent.grounded = true;
						ent.vel.y = 0;
					}
				}
				
				// right ray
				if (Intersector.intersectSegments(px1, py1, px2, py2, 
						ent.pos.x + ent.collisionWidth/2f, 
						ent.pos.y + ent.collisionHeight/2f + Math.abs(ent.vel.y) + ent.offsetHeight,
						ent.pos.x + ent.collisionWidth/2f,
						ent.pos.y - ent.collisionHeight/2f - Math.abs(ent.vel.y) - ent.offsetHeight, 
						intersection)) {
					
					if (intersection.y > ent.pos.y) {
						// remover essa constante
						ent.pos.y = intersection.y - ent.collisionHeight/2f - ent.offsetHeight - .1f;
						if (ent.vel.y > 0f) {
							ent.vel.y = 0f;
						}
					} else {
						ent.pos.y = intersection.y + ent.collisionHeight/2f + ent.offsetHeight;
						ent.grounded = true;
						ent.vel.y = 0;
					}
				}
				
				// top ray
				if (Intersector.intersectSegments(px1, py1, px2, py2, 
						ent.pos.x - ent.collisionWidth/2f - Math.abs(ent.vel.x) - ent.offsetWidth,
						ent.pos.y + ent.collisionHeight/2f,
						ent.pos.x + ent.collisionWidth/2f + Math.abs(ent.vel.x) + ent.offsetWidth,
						ent.pos.y + ent.collisionHeight/2f, 
						intersection)) {
					
					if (intersection.x > ent.pos.x) {
						ent.pos.x = intersection.x - ent.collisionWidth/2f - ent.offsetWidth + ent.vel.x - minSpeed;
					} else if (intersection.x < ent.pos.x) {
						ent.pos.x = intersection.x + ent.collisionWidth/2f + ent.offsetWidth + ent.vel.x + minSpeed;
					}
					ent.vel.x = 0;
					if (ent.airStatus == AIR_STATUS.GROUNDED && ent.action != ACTION.ATTACKING && ent.action != ACTION.TAKING_DAMAGE) {
						ent.action = ACTION.PUSHING_WALL;
					}
				}
				
				// bottom ray
				if (Intersector.intersectSegments(px1, py1, px2, py2, 
						ent.pos.x - ent.collisionWidth/2f - Math.abs(ent.vel.x) - ent.offsetWidth,
						ent.pos.y - ent.collisionHeight/2f,
						ent.pos.x + ent.collisionWidth/2f + Math.abs(ent.vel.x) + ent.offsetWidth,
						ent.pos.y - ent.collisionHeight/2f, 
						intersection)) {
					
					if (intersection.x > ent.pos.x) {
						ent.pos.x = intersection.x - ent.collisionWidth/2f - ent.offsetWidth + ent.vel.x - minSpeed;
					} else if (intersection.x < ent.pos.x) {
						ent.pos.x = intersection.x + ent.collisionWidth/2f + ent.offsetWidth + ent.vel.x + minSpeed;
					}
					ent.vel.x = 0;
					// if he's grounded, then he's pushing a wall
					if (ent.airStatus == AIR_STATUS.GROUNDED && ent.action != ACTION.ATTACKING && ent.action != ACTION.TAKING_DAMAGE) {
						ent.action = ACTION.PUSHING_WALL;
					}
				}
			}
		}
		ent.pos.x += ent.vel.x;
		ent.pos.y += ent.vel.y;
	}
	
	/** This method should be used in case we need to kill any action done by the
	 *  Entity. It is the case with cutscenes or even when the entity is taking
	 *  damage. Other factors may also occur */
	private void resetEntityProperties(Entity ent) {
		ent.attacks.clear();
		ent.currentAttackIndex = 0;
		ent.vel.x = 0;
	}
	
	private void updateEntityStatuses(Entity ent) {
		if (ent.action != ACTION.TAKING_DAMAGE) {
			if (ent.vel.x > 0f) {
				ent.dir = DIRECTION.RIGHT;
			} else if (ent.vel.x < 0f) {
				ent.dir = DIRECTION.LEFT;
			}
		} else {
			if (ent.vel.x > 0f) {
				ent.dir = DIRECTION.LEFT;
			} else if (ent.vel.x < 0f) {
				ent.dir = DIRECTION.RIGHT;
			}
		}
		
		if (ent.takingDamage) { // takingDamage is like god
			if (ent.action != ACTION.TAKING_DAMAGE) {
				ent.stateTime = 0f;
				ent.action = ACTION.TAKING_DAMAGE;
			} else if (ent.stateTime > HERO_DAMAGE_DURATION) {
				ent.takingDamage = false;
			}
		} else if (ent.vel.y != 0f) {
			if (ent.vel.y > 0) { // we are probably jumping
				if (ent.airStatus == AIR_STATUS.GROUNDED) {
					ent.stateTime = 0f;
					ent.airStatus = AIR_STATUS.TAKING_OFF;
				} else if (ent.airStatus != AIR_STATUS.JUMPING && ent.stateTime > HERO_TAKE_OFF_DURATION) {
					ent.stateTime = 0f;
					ent.airStatus = AIR_STATUS.JUMPING;
				} else if (ent.airStatus == AIR_STATUS.JUMPING && ent.stateTime > HERO_JUMPING_DURATION) {
					// Converter  números mágicos
					ent.stateTime = HERO_JUMPING_DURATION - 2/12f;
				}
			} else { // then we are falling
				if (ent.airStatus != AIR_STATUS.FALLING) {
					ent.airStatus = AIR_STATUS.FALLING;
					ent.stateTime = 0f;
				} else if (ent.airStatus == AIR_STATUS.FALLING && ent.stateTime > HERO_FALLING_DURATION) {
					// Converter  números mágicos
					ent.stateTime = HERO_FALLING_DURATION - 3f/24f;
				}
			}
			
			// if the character is jumping, he's idle
			ent.action = ACTION.IDLE;
		} else {
			if (ent.airStatus == AIR_STATUS.TAKING_OFF || ent.airStatus == AIR_STATUS.JUMPING) {
				ent.airStatus = AIR_STATUS.LANDING;
				ent.action = ACTION.IDLE;
				ent.stateTime = 0f;
			} else if (ent.airStatus == AIR_STATUS.FALLING) {
				ent.airStatus = AIR_STATUS.LANDING;
				ent.action = ACTION.IDLE; // he can't attack while landing
				ent.stateTime = 0f;
			} else if (ent.airStatus == AIR_STATUS.LANDING && ent.stateTime > HERO_LANDING_DURATION) {
				ent.airStatus = AIR_STATUS.GROUNDED;
				ent.action = ACTION.IDLE;
				ent.stateTime = 0f;
			} else if (ent.airStatus == AIR_STATUS.GROUNDED) {
				if (ent.vel.x == 0f) {
					if (ent.attacks.isEmpty()) {
						if (ent.action == ACTION.PUSHING_WALL) {
							// ent.action = ACTION.IDLE;
						} else if (ent.action != ACTION.IDLE) {
							ent.stateTime = 0f;
							ent.action = ACTION.IDLE;
						}
					} else { 
						ent.stateTime = ent.attacks.get(ent.currentAttackIndex).getStateTime();
						ent.action = ACTION.ATTACKING;
					}
				} else {
					if (ent.action != ACTION.WALKING) {
						ent.stateTime = 0f;
					}
					ent.action = ACTION.WALKING;
				}
			} else {
				// we reset it to the default grounded status
				// ent.airStatus = AIR_STATUS.GROUNDED;
			}
		}
	}
	
	public String getStatusLog(Entity ent) {
		return String.format("stateTime: %f\nhitPoints: %f\nDIRECTION: %s\nAIR_STATUS: %s\nACTION: %s\n", 
				ent.stateTime, ent.hitPoints, ent.dir.toString(), ent.airStatus.toString(), ent.action.toString());
	}
}
