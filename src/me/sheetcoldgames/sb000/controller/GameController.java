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
	
	// hero constants - this should be organized
	public final float HERO_IDLE_DURATION 		= 5/12f;
	public final float HERO_RUNNING_DURATION 	= 8/12f;
	public final float HERO_TAKE_OFF_DURATION 	= 3/12f;
	public final float HERO_JUMPING_DURATION 	= 4/12f;
	public final float HERO_FALLING_DURATION 	= 4/12f;
	public final float HERO_LANDING_DURATION 	= 3/12f;
	
	public GameController() {
		initCameras();
		
		initializeTestMap("map.fis");
		
		initializeEntities();
		camera.setTarget(aEntity.get(0));
		
		input = new Input();
		Gdx.input.setInputProcessor(input);
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
		aEntity.get(0).pos.set(5f, 8f);
		
		// another entity, the BOSS
		aEntity.add(new Entity(1f, 2f));
		aEntity.get(1).pos.set(9f, 9f);
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
		camera.update();
	}
	
	public void update() {
		aEntity.get(0).stateTime += Gdx.graphics.getDeltaTime();
		handleInput(aEntity.get(0));
		
		updateEntities();
		camera.update(0f, 4f);
	}
	
	float walkSpeed = 1f;
	float maxHorizontalSpeed = .12f;
	float minSpeed = .12f;
	
	boolean attacking = false;;
	
	// initial impulse
	float initialImpulse = .1f;
	boolean jump = false;
	
	private void handleInput(Entity player) {
		if (input.buttons[Input.RIGHT]) {
			if (player.vel.x == 0f) player.vel.x = minSpeed;
			player.vel.x += Gdx.graphics.getDeltaTime() * walkSpeed;
		} else if (input.buttons[Input.LEFT]) {
			if (player.vel.x == 0f) player.vel.x = -minSpeed;
			player.vel.x -= Gdx.graphics.getDeltaTime() * walkSpeed;
		} else {
			player.vel.x = 0f; // MathUtils.lerp(player.vel.x, 0f, .1f);
		}
		
		if (input.buttons[Input.ACTION] && player.airStatus == AIR_STATUS.GROUNDED) {
			attack(player);
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
		} else */ if (input.buttons[Input.JUMP]) {
			jump(Gdx.graphics.getDeltaTime(), player);
		} else {
			jump = false;
		}
		
		player.vel.x = MathUtils.clamp(player.vel.x, -maxHorizontalSpeed, maxHorizontalSpeed); 
	}
	
	private void attack(Entity ent) {
		if (!attacking) {
			attacking = true;
			ent.attacks.add(
					new Attack(ent.pos.x + (ent.dir == DIRECTION.RIGHT ? 1.0f : -1.0f), ent.pos.y, 
							1f, 1f, // width and height 
							10f/12f, // duration 
							1/2f) // damage TIME!
					);
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
		for (Entity ent : aEntity) {
			ent.vel.y += Constants.GRAVITY * Gdx.graphics.getDeltaTime();
			ent.vel.y = MathUtils.clamp(ent.vel.y, -1f, 1f);
			ent.grounded = false;
			updateAttackStatus(ent);
			updateEntityPosition(ent);
			updateEntityStatuses(ent);
		}
		printStatusLog(aEntity.get(0));
	}
	
	private void updateAttackStatus(Entity ent) {
		if (!ent.attacks.isEmpty()) {
			ent.vel.x = 0f;
			for (int currAtk = 0; currAtk < ent.attacks.size(); currAtk++) {
				ent.attacks.get(currAtk).update(Gdx.graphics.getDeltaTime());
				if (ent.attacks.get(currAtk).isFinished()) {
					ent.attacks.remove(currAtk);
				}
			}
		}
	}
	
	private void updateEntityPosition(Entity ent) {
		Vector2 intersection = new Vector2(0f, 0f);
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
					ent.vel.y = 0;
					
					if (intersection.y > ent.pos.y) {
						// remover essa constante
						ent.pos.y = intersection.y - ent.collisionHeight/2f - ent.offsetHeight -.1f;
					} else {
						ent.pos.y = intersection.y + ent.collisionHeight/2f + ent.offsetHeight;
						ent.grounded = true;
					}
				}
				
				// right ray
				if (Intersector.intersectSegments(px1, py1, px2, py2, 
						ent.pos.x + ent.collisionWidth/2f, 
						ent.pos.y + ent.collisionHeight/2f + Math.abs(ent.vel.y) + ent.offsetHeight,
						ent.pos.x + ent.collisionWidth/2f,
						ent.pos.y - ent.collisionHeight/2f - Math.abs(ent.vel.y) - ent.offsetHeight, 
						intersection)) {
					ent.vel.y = 0;
					
					if (intersection.y > ent.pos.y) {
						// remover essa constante
						ent.pos.y = intersection.y - ent.collisionHeight/2f - ent.offsetHeight - .1f;
					} else {
						ent.pos.y = intersection.y + ent.collisionHeight/2f + ent.offsetHeight;
						ent.grounded = true;
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
				}
			}
		}
		ent.pos.x += ent.vel.x;
		ent.pos.y += ent.vel.y;
	}
	
	private void updateEntityStatuses(Entity ent) {
		if (ent.vel.x > 0f) {
			ent.dir = DIRECTION.RIGHT;
		} else if (ent.vel.x < 0f) {
			ent.dir = DIRECTION.LEFT;
		}
		
		if (ent.vel.y != 0f) {
			if (ent.vel.y > 0) { // we are probably jumping
				if (ent.airStatus == AIR_STATUS.GROUNDED) {
					ent.stateTime = 0f;
					ent.airStatus = AIR_STATUS.TAKING_OFF;
				} else if (ent.airStatus != AIR_STATUS.JUMPING && ent.stateTime > HERO_TAKE_OFF_DURATION) {
					ent.stateTime = 0f;
					ent.airStatus = AIR_STATUS.JUMPING;
				}
			} else { // then we are falling
				if (ent.airStatus != AIR_STATUS.FALLING) {
					ent.airStatus = AIR_STATUS.FALLING;
					ent.stateTime = 0f;
				}			
			}
			
			// if the character is jumping, he's idle
			ent.action = ACTION.IDLE;
			
		} else {
			if (ent.airStatus == AIR_STATUS.FALLING) {
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
						if (ent.action != ACTION.IDLE) {
							ent.stateTime = 0f;
						}
						ent.action = ACTION.IDLE;
					} else {
						if (ent.action != ACTION.ATTACKING) {
							ent.stateTime = 0f;
						}
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
	
	private void printStatusLog(Entity ent) {
		System.out.println("current entity status:");
		System.out.println("DIRECTION: " + ent.dir);
		System.out.println("AIR_STATUS: " + ent.airStatus);
		System.out.println("ACTION: " + ent.action);
		System.out.println();
	}
}
