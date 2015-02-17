package me.sheetcoldgames.sb000.engine;

import com.badlogic.gdx.math.Vector2;

public class SheetPoint {
	public Vector2 pos;
	public POINT_TYPE type;
	
	boolean active = true;
	
	public SheetPoint(float x, float y, POINT_TYPE t) {
		pos = new Vector2(x, y);
		type = t;
	}
	
	public SheetPoint() {
		this(0, 0, POINT_TYPE.SOLID);
	}
	
	public void set(float x, float y) {
		pos.set(x, y);
	}
	
	public float X() {
		return pos.x;
	}
	
	public float Y() {
		return pos.y;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 * @return
	 */
	public boolean contains(float x, float y, float radius) {
		if (x < pos.x + radius && x > pos.x - radius) {
			if (y < pos.y + radius && y > pos.y - radius) {
				return true;
			}
		}
		return false;
	}	
}
