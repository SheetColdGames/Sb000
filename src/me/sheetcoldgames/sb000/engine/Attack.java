package me.sheetcoldgames.sb000.engine;

import com.badlogic.gdx.math.Vector2;

public class Attack {
	public float duration;
	float stateTime = 0f;
	
	float damageInstant;
	
	public Vector2 pos;
	public Vector2 vel;
	
	public float width;
	public float height;
	
	boolean finished = false;
	
	/**
	 * 
	 * @param x the x position in the world
	 * @param y the y position in the world
	 * @param duration the duration of the attack
	 * @param w the width of the rectangle box of the attack
	 * @param h the height of the rectangle box of the attack
	 */
	public Attack(float x, float y, float w, float h, float duration, float damageInstant) {
		pos = new Vector2(x, y);
		vel = new Vector2();
		width = w;
		height = h;
		this.duration = duration;
		this.damageInstant = damageInstant;
	}
	
	public void update(float dt) {
		stateTime += dt;
		finished = stateTime > duration;
		pos.x += vel.x;
		pos.y += vel.y;
	}
	
	public float getStateTime() {
		return stateTime;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public boolean isDamaging() {
		return stateTime > damageInstant;
	}
}
