package me.sheetcoldgames.sb000.engine;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public class Entity {
	/** the position vector */
	public Vector2 pos;
	/** the velocity vector */
	public Vector2 vel;
	
	public float width;
	public float height;
	
	public float offset;
	
	public boolean grounded = false;
	
	public ArrayList<Attack> attacks;
	
	public Entity(float w, float h, float x, float y) {
		width = w;
		height = h;
		pos = new Vector2(x, y);
		vel = new Vector2();
		offset = .1f;
		
		attacks = new ArrayList<Attack>();
	}
	
	public Entity() {
		this(1f, 1f, 0f, 0f);
	}
	
	public Entity(float w, float h) {
		this(w, h, 0f, 0f);
	}
}
