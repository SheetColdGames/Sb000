package me.sheetcoldgames.sb000.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import me.sheetcoldgames.sb000.Constants;
import me.sheetcoldgames.sb000.engine.Attack;
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
			player.vel.x = MathUtils.lerp(player.vel.x, 0f, .1f);
		}
		
		if (input.buttons[Input.ACTION]) {
			attack(player);
		} else {
			attacking = false;
		}
		
		// RELACIONADO AO PULO
		if (initialImpulse > 0f) initialImpulse -= .004f;
		if (initialImpulse < 0f) {
			initialImpulse =  0f;
		}
		
		if (input.buttons[Input.JUMP]) {
			jump(Gdx.graphics.getDeltaTime(), player);
		} else {
			jump = false;
		}
		
		player.vel.x = MathUtils.clamp(player.vel.x, -maxHorizontalSpeed, maxHorizontalSpeed); 
	}
	
	private void attack(Entity ent) {
		if (!attacking) {
			attacking = true;
			ent.attacks.add(new Attack(ent.pos.x, ent.pos.y, 2f));
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
			updateEntityPosition(ent);
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
						ent.pos.x - ent.width/2f, 
						ent.pos.y + ent.height/2f + Math.abs(ent.vel.y) + ent.offset, 
						ent.pos.x - ent.width/2f,
						ent.pos.y - ent.height/2f - Math.abs(ent.vel.y) - ent.offset, 
						intersection)) {
					ent.vel.y = 0;
					ent.grounded = true;
					if (intersection.y > ent.pos.y) {
						ent.pos.y = intersection.y - ent.height/2f - ent.offset;
					} else {
						ent.pos.y = intersection.y + ent.height/2f + ent.offset;
					}
				}
				
				// right ray
				if (Intersector.intersectSegments(px1, py1, px2, py2, 
						ent.pos.x + ent.width/2f, 
						ent.pos.y + ent.height/2f + Math.abs(ent.vel.y) + ent.offset,
						ent.pos.x + ent.width/2f,
						ent.pos.y - ent.height/2f - Math.abs(ent.vel.y) - ent.offset, 
						intersection)) {
					ent.vel.y = 0;
					ent.grounded = true;
					if (intersection.y > ent.pos.y) {
						ent.pos.y = intersection.y - ent.height/2f - ent.offset;
					} else {
						ent.pos.y = intersection.y + ent.height/2f + ent.offset;
					}
				}
				
				// top ray
				if (Intersector.intersectSegments(px1, py1, px2, py2, 
						ent.pos.x - ent.width/2f - Math.abs(ent.vel.x) - ent.offset,
						ent.pos.y + ent.height/2f,
						ent.pos.x + ent.width/2f + Math.abs(ent.vel.x) + ent.offset,
						ent.pos.y + ent.height/2f, 
						intersection)) {
					
					if (intersection.x > ent.pos.x) {
						ent.pos.x = intersection.x - ent.width/2f - ent.offset + ent.vel.x - minSpeed;
					} else {
						ent.pos.x = intersection.x + ent.width/2f + ent.offset - ent.vel.x + minSpeed;
					}
					ent.vel.x = 0;
				}
				
				// bottom ray
				if (Intersector.intersectSegments(px1, py1, px2, py2, 
						ent.pos.x - ent.width/2f - Math.abs(ent.vel.x) - ent.offset,
						ent.pos.y - ent.height/2f,
						ent.pos.x + ent.width/2f + Math.abs(ent.vel.x) + ent.offset,
						ent.pos.y - ent.height/2f, 
						intersection)) {
					
					if (intersection.x > ent.pos.x) {
						ent.pos.x = intersection.x - ent.width/2f - ent.offset + ent.vel.x - minSpeed;
					} else {
						ent.pos.x = intersection.x + ent.width/2f + ent.offset - ent.vel.x + minSpeed;
					}
					ent.vel.x = 0;
				}
				
				/*
				// bottom ray
				if (Intersector.intersectSegments(px1, py1, px2, py2, 
						ent.pos.x - ent.width/2f - Math.abs(ent.vel.x), 
						ent.pos.y - ent.height/2f, 
						ent.pos.x + ent.width/2f + Math.abs(ent.vel.x),
						ent.pos.y - ent.height/2f, 
						intersection)) {
					if (intersection.x > ent.pos.x) {
						ent.pos.x = intersection.x - ent.width/2f + ent.vel.x - minSpeed;
					} else {
						ent.pos.x = intersection.x + ent.width/2f + ent.vel.x + minSpeed;
					}
					ent.vel.x = 0;
				}
				
				// top ray
				if (Intersector.intersectSegments(px1, py1, px2, py2, 
						ent.pos.x - ent.width/2f - Math.abs(ent.vel.x), 
						ent.pos.y + ent.height/2f, 
						ent.pos.x + ent.width/2f + Math.abs(ent.vel.x),
						ent.pos.y + ent.height/2f, 
						intersection)) {
					if (intersection.x > ent.pos.x) {
						ent.pos.x = intersection.x - ent.width/2f + ent.vel.x - minSpeed;
					} else {
						ent.pos.x = intersection.x + ent.width/2f + ent.vel.x + minSpeed;
					}
					ent.vel.x = 0;
				}
				*/
			}
		}
		ent.pos.x += ent.vel.x;
		ent.pos.y += ent.vel.y;
	}
}
