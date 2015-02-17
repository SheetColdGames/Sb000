package me.sheetcoldgames.sb000.engine;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class SheetCamera extends OrthographicCamera {
	private Entity target;
	
	public SheetCamera() {
		super();
	}
	
	public SheetCamera(float w, float h) {
		super(w, h);
	}
	
	public void update() {
		update(0f, 0f);
	}
	
	public void update(float offsetX, float offsetY) {
		if (hasTarget()) {
			position.set(target.pos.x + offsetX, target.pos.y + offsetY, 0f);
		}
		super.update();
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
