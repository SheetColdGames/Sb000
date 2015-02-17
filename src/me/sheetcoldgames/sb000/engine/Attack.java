package me.sheetcoldgames.sb000.engine;

import com.badlogic.gdx.math.Vector2;

public class Attack {
	public float duration;
	float stateTime = 0f;
	
	float damage;
	
	Vector2 pos;
	Vector2 vel;
	
	public Attack(float x, float y, float duration) {
		this.duration = duration;
		pos = new Vector2(x, y);
		vel = new Vector2();
	}
	
	public void update(float dt) {
		stateTime += dt;
	}
	
	public float getStateTime() {
		return stateTime;
	}
}
