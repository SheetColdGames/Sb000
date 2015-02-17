package me.sheetcoldgames.sb000;

import java.util.ArrayList;
import java.util.LinkedList;

import me.sheetcoldgames.sb000.controller.GameController;
import me.sheetcoldgames.sb000.engine.Colors;
import me.sheetcoldgames.sb000.engine.Entity;
import me.sheetcoldgames.sb000.engine.POINT_TYPE;
import me.sheetcoldgames.sb000.engine.SheetPoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GameRenderer {

	GameController controller;
	ShapeRenderer sr;
	SpriteBatch sb;
	BitmapFont font;
	
	public GameRenderer(GameController controller) {
		this.controller = controller;
		sr = new ShapeRenderer();
		
		sb = new SpriteBatch();
		
		// initializing font
		font = new BitmapFont(Gdx.files.internal("inconsolatabmp.fnt"),
				Gdx.files.internal("inconsolatabmp_0.png"), false);
		font.setScale(1f);
	}

	public void dispose() {
		sr.dispose();
		sb.dispose();
		font.dispose();
	}

	public void render() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		debugRender();		
	}
	
	private void debugRender() {
		sr.setProjectionMatrix(controller.camera.combined);
		sr.begin(ShapeType.Line);
		renderCollisionPoints(controller.groupPoints);
		entityCollisionRenderer();
		sr.end();
		
		sr.begin(ShapeType.Filled);
		// renderEntities();
		sr.end();
		
		sb.setProjectionMatrix(controller.hudCamera.combined);
		sb.begin();
		font.draw(sb, String.valueOf(Gdx.graphics.getFramesPerSecond()), 
				controller.hudCamera.position.x-controller.hudCamera.viewportWidth/2f+24,
				controller.hudCamera.position.y+controller.hudCamera.viewportHeight/2f-24);
		sb.end();
	}
	
	public void renderCollisionPoints(ArrayList<LinkedList<SheetPoint>> aGroup) {
		for (int groupIndex = 0; groupIndex < aGroup.size(); groupIndex++) {
			for (int currentIndex = 0; currentIndex < aGroup.get(groupIndex).size()-1; currentIndex++) {
				sr.setColor(Colors.SOLID_POINT);
				if (aGroup.get(groupIndex).get(currentIndex).type == POINT_TYPE.DOOR) {
					sr.setColor(Colors.DOOR_POINT);
				}				
				// Let's render a circle
				sr.circle(aGroup.get(groupIndex).get(currentIndex).X(),
						aGroup.get(groupIndex).get(currentIndex).Y(),
						.25f, 16);
				// Let's render a line
				sr.line(aGroup.get(groupIndex).get(currentIndex).pos,
						aGroup.get(groupIndex).get(currentIndex+1).pos);
			}
			// if this is the last point, then render a circle for it
			sr.circle(aGroup.get(groupIndex).getLast().pos.x,
					aGroup.get(groupIndex).getLast().pos.y,
					.25f, 16);
		}
	}
	
	private void renderEntities() {
		sr.setColor(Colors.PLAYER);
		for (Entity ent : controller.aEntity) {
			sr.rect(ent.pos.x-ent.width/2f,
					ent.pos.y-ent.height/2f,
					ent.width, ent.height);
		}
	}
	
	private void entityCollisionRenderer() {
		for (Entity ent : controller.aEntity) {
			renderCollisionBounds(ent);
		}
	}
	
	private void renderCollisionBounds(Entity ent) {
		sr.setColor(Color.RED);
		// horizontal top line
		sr.line(ent.pos.x - ent.width/2f - Math.abs(ent.vel.x) - ent.offset,
				ent.pos.y + ent.height/2f,
				ent.pos.x + ent.width/2f + Math.abs(ent.vel.x) + ent.offset,
				ent.pos.y + ent.height/2f);
		
		// horizontal bottom line
		sr.line(ent.pos.x - ent.width/2f - Math.abs(ent.vel.x) - ent.offset,
				ent.pos.y - ent.height/2f,
				ent.pos.x + ent.width/2f + Math.abs(ent.vel.x) + ent.offset,
				ent.pos.y - ent.height/2f);
		
		// vertical left line
		sr.line(ent.pos.x - ent.width/2f,
				ent.pos.y - ent.height/2f - Math.abs(ent.vel.y) - ent.offset,
				ent.pos.x - ent.width/2f,
				ent.pos.y + ent.height/2f + Math.abs(ent.vel.y) + ent.offset);
		
		// vertical right line
		sr.line(ent.pos.x + ent.width/2f,
				ent.pos.y - ent.height/2f - Math.abs(ent.vel.y) - ent.offset,
				ent.pos.x + ent.width/2f,
				ent.pos.y + ent.height/2f + Math.abs(ent.vel.y) + ent.offset);
	}
}
