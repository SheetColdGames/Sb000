package me.sheetcoldgames.sb000.controller;

import me.sheetcoldgames.sb000.engine.ACTION;
import me.sheetcoldgames.sb000.engine.Attack;
import me.sheetcoldgames.sb000.engine.DIRECTION;
import me.sheetcoldgames.sb000.engine.Entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;

public class ArcherController {
	
	public Circle[] hotspots;
	private int currentHotspot;
	private float stateTime = 0f;
	
	
	public ArcherController() {
		hotspots = new Circle[3];
		hotspots[0] = new Circle(1f, 1f, 3f);
		hotspots[1] = new Circle(10f, 1f, 3f);
		hotspots[2] = new Circle(19f, 1f, 3f);
	}
	
	public void updateAI(Entity archer, Entity enemy) {
		stateTime += Gdx.graphics.getDeltaTime();
		if (archer.action == ACTION.TAKING_DAMAGE) {
//			if (archer.dir == DIRECTION.RIGHT) {
//				archer.vel.x -= Gdx.graphics.getDeltaTime() * 10f; 
//			} else {
//				archer.vel.x += Gdx.graphics.getDeltaTime() * 10f;
//			}
			// forces the controller stateTime, so it will move to another hotspot as soon as it stops taking damage
			stateTime = 4f;
		} else if (stateTime > 4f) {
			if (currentHotspot == 0) {
				if (enemy.pos.x > 10f) {
					currentHotspot = 1;
				} else {
					currentHotspot = 2;
				}
			} else if (currentHotspot == 1) {
				if (archer.pos.x < enemy.pos.x) {
					currentHotspot = 0;
				} else {
					currentHotspot = 1;
				}
			} else {
				if (enemy.pos.x < 10f) {
					currentHotspot = 1;
				} else {
					currentHotspot = 0;
				}
			}
			// currentHotspot = (currentHotspot+1) % hotspots.length;
			stateTime = 0f;
			
		} else {
			followPoint(archer, hotspots[currentHotspot]);
			if (stateTime > 3f) { // LET'S ATTACK
				
				if (archer.attacks.isEmpty()) {
					archer.attacks.add(new Attack(archer.pos.x + (archer.dir == DIRECTION.RIGHT ? 1.0f : -1.0f), archer.pos.y, 
							1f, .5f, // width and height 
							1f, // duration 
							0f) // damage TIME!);
					);
					archer.attacks.get(0).vel.x = (archer.pos.x > enemy.pos.x) ? -.2f : .2f;
				} else {
					archer.attacks.get(0).update(Gdx.graphics.getDeltaTime());
				}
				
			}
		}
	}
	
	private void followPoint(Entity archer, Circle destiny) {
		if (destiny.contains(archer.pos.x, archer.pos.y)) {
			archer.vel.x = 0f;
		} else {
			if (archer.pos.x < destiny.x) {
				archer.vel.x += .02f;
			} else {
				archer.vel.x += -.02f;
			}
		}
		archer.vel.x = MathUtils.clamp(archer.vel.x, -.2f, .2f);
	}
	
	public int getCurrentHotspot() {
		return currentHotspot;
	}
}
