package me.sheetcoldgames.sb000.engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class SheetCamera extends OrthographicCamera {
	private Entity target;
	float minX, minY;
	float maxX, maxY;
	
	public SheetCamera() {
		super();
	}
	
	public SheetCamera(float w, float h) {
		super(w, h);
		minX = maxX = minY = maxY = 0f;
	}
	
	public void update() {
		update(0f, 0f);
	}
	
	public void update(float offsetX, float offsetY) {
		if (hasTarget()) {
			position.set(target.pos.x + offsetX, target.pos.y + offsetY, 0f);
		}
		if (minX != maxX && minY != maxY) {
			position.x = MathUtils.clamp(position.x, minX, maxX);
			position.y = MathUtils.clamp(position.y, minY, maxY);
		}
		super.update();
	}
	
	public void setBounds(float xa, float ya, float xb, float yb) {
		minX = xa;
		minY = ya;
		maxX = xb;
		maxY = yb;
	}
	
	public Entity getTarget() {
		return target;
	}
	
	public void setTarget(Entity ent) {
		target = ent;
	}
	
	public boolean hasTarget() {
		return target != null;
	}
}
