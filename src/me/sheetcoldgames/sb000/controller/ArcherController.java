package me.sheetcoldgames.sb000.controller;

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
		if (stateTime > 4f) {
			currentHotspot = (currentHotspot+1) % hotspots.length;
			stateTime = 0f;
		}
		
		followPoint(archer, hotspots[currentHotspot]);
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
		archer.vel.x = MathUtils.clamp(archer.vel.x, -.16f, .16f);
	}
	
	public int getCurrentHotspot() {
		return currentHotspot;
	}
}
