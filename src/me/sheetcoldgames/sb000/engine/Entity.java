package me.sheetcoldgames.sb000.engine;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Entity {
	/** the position vector */
	public Vector2 pos;
	/** the velocity vector */
	public Vector2 vel;
	
	/** actual width of the entity */
	public float width;
	/** actual height of the entity */
	public float height;
	
	public float offsetWidth;
	public float offsetHeight;
	
	public float collisionWidth;
	public float collisionHeight;
	
	public boolean grounded = false;
	
	public ArrayList<Attack> attacks;
	public int currentAttackIndex = 0;
	
	public DIRECTION dir;
	public AIR_STATUS airStatus;
	public ACTION action;
	
	public float stateTime = 0f;
	
	/** Probably shouldn't be here, needs review */
	public TextureRegion currentFrame;
	
	public Entity(float w, float h, float x, float y) {
		offsetWidth = .4f;
		offsetHeight = .4f;
		width = w-offsetWidth;
		height = h-offsetHeight;
		pos = new Vector2(x, y);
		vel = new Vector2();
		collisionWidth = width - offsetWidth;
		collisionHeight = height - offsetHeight;
		
		attacks = new ArrayList<Attack>();
		
		dir = DIRECTION.RIGHT;
		airStatus = AIR_STATUS.GROUNDED;
		action = ACTION.IDLE;
	}
	
	public Entity() {
		this(1f, 1f, 0f, 0f);
	}
	
	public Entity(float w, float h) {
		this(w, h, 0f, 0f);
	}
}
