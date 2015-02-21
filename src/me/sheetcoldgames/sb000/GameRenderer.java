package me.sheetcoldgames.sb000;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import me.sheetcoldgames.sb000.controller.GameController;
import me.sheetcoldgames.sb000.engine.ACTION;
import me.sheetcoldgames.sb000.engine.AIR_STATUS;
import me.sheetcoldgames.sb000.engine.Attack;
import me.sheetcoldgames.sb000.engine.Colors;
import me.sheetcoldgames.sb000.engine.DIRECTION;
import me.sheetcoldgames.sb000.engine.Entity;
import me.sheetcoldgames.sb000.engine.POINT_TYPE;
import me.sheetcoldgames.sb000.engine.SheetPoint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GameRenderer {

	GameController controller;
	ShapeRenderer sr;
	SpriteBatch sb;
	BitmapFont font;
	
	public static Assets assets;
	HashMap<String, Animation> heroAnimations;
	
	// BEGIN OF ANIMATION CONSTANTS =======
	// general constants
	public static final String HERO_PREFIX = "hero_";
	
	public static final String IDLE 	= "idle";
	public static final String RUN 		= "run";
	public static final String TAKE_OFF = "take_off";
	public static final String JUMP 	= "jump";
	public static final String FALL 	= "fall";
	public static final String LAND 	= "land";
	public static final String ATTACK 	= "attack";
	
	// specific constants
	public static final String HERO_IDLE 		= HERO_PREFIX + IDLE;
	public static final String HERO_RUN			= HERO_PREFIX + RUN;
	public static final String HERO_TAKE_OFF 	= HERO_PREFIX + TAKE_OFF;
	public static final String HERO_JUMP 		= HERO_PREFIX + JUMP;
	public static final String HERO_FALL 		= HERO_PREFIX + FALL;
	public static final String HERO_LAND 		= HERO_PREFIX + LAND;
	public static final String HERO_ATTACK 		= HERO_PREFIX + ATTACK;
	
	// ======= END OF ANIMATION CONSTANTS
	
	public GameRenderer(GameController controller) {
		this.controller = controller;
		assets = new Assets();
		
		sr = new ShapeRenderer();
		
		sb = new SpriteBatch();
		
		// initializing font
		font = new BitmapFont(Gdx.files.internal("inconsolatabmp.fnt"),
				Gdx.files.internal("inconsolatabmp_0.png"), false);
		font.setScale(1f);
		
		initHeroAnimations();
	}

	public void dispose() {
		sr.dispose();
		sb.dispose();
		font.dispose();
	}
	
	private void initHeroAnimations() {
		heroAnimations = new HashMap<String, Animation>();
		TextureAtlas atlas = assets.get(Directories.HERO_ATLAS, TextureAtlas.class);
		
		heroAnimations.put(HERO_IDLE, new Animation(1/12f, atlas.findRegions(HERO_IDLE), PlayMode.LOOP));
		heroAnimations.put(HERO_RUN, new Animation(1/12f, atlas.findRegions(HERO_RUN), PlayMode.LOOP));
		heroAnimations.put(HERO_TAKE_OFF, new Animation(1/12f, atlas.findRegions(HERO_TAKE_OFF), PlayMode.NORMAL));
		heroAnimations.put(HERO_JUMP, new Animation(1/12f, atlas.findRegions(HERO_JUMP), PlayMode.NORMAL));
		heroAnimations.put(HERO_FALL, new Animation(1/12f, atlas.findRegions(HERO_FALL), PlayMode.NORMAL));
		heroAnimations.put(HERO_LAND, new Animation(1/12f, atlas.findRegions(HERO_LAND), PlayMode.NORMAL));
		heroAnimations.put(HERO_ATTACK, new Animation(1/12f, atlas.findRegions(HERO_ATTACK), PlayMode.NORMAL));
	}

	public void render() {
		Gdx.gl.glClearColor(0f, .1f, .2f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		debugRender();
		renderAnimations();
	}
	
	private void renderAnimations() {
		// for (Entity ent : controller.aEntity) {
			// animate(ent);
		// }
		sb.setProjectionMatrix(controller.camera.combined);
		sb.begin();
		animate(controller.aEntity.get(0));
		sb.end();
	}
	
	
	private void animate(Entity ent) {
		if (ent.airStatus == AIR_STATUS.TAKING_OFF) {
			ent.currentFrame = heroAnimations.get(HERO_TAKE_OFF).getKeyFrame(ent.stateTime);
		} else if (ent.airStatus == AIR_STATUS.JUMPING) {
			ent.currentFrame = heroAnimations.get(HERO_JUMP).getKeyFrame(ent.stateTime);
		} else if (ent.airStatus == AIR_STATUS.FALLING) {
			ent.currentFrame = heroAnimations.get(HERO_FALL).getKeyFrame(ent.stateTime);
		} else if (ent.airStatus == AIR_STATUS.LANDING) {
			ent.currentFrame = heroAnimations.get(HERO_LAND).getKeyFrame(ent.stateTime);
		} else {
			if (ent.action == ACTION.WALKING) {
				ent.currentFrame = heroAnimations.get(HERO_RUN).getKeyFrame(ent.stateTime);
			} else if (ent.action == ACTION.ATTACKING) {
				ent.currentFrame = heroAnimations.get(HERO_ATTACK).getKeyFrame(ent.stateTime);
			} else {
				ent.currentFrame = heroAnimations.get(HERO_IDLE).getKeyFrame(ent.stateTime);
			}
		}
		
		if (ent.dir == DIRECTION.LEFT) {
			if (!ent.currentFrame.isFlipX()) {
				ent.currentFrame.flip(true, false);
			}
		} else if (ent.dir == DIRECTION.RIGHT) {
			if (ent.currentFrame.isFlipX()) {
				ent.currentFrame.flip(true, false);
			}
		}
		
		/*
		sb.draw(currentFrame, 
				ent.pos.x, ent.pos.y,
				currentFrame.getRegionWidth()/16f, currentFrame.getRegionHeight()/16f,
				// ent.width, ent.height,
				currentFrame.getRegionWidth()/16f, currentFrame.getRegionHeight()/16f,
				2f, 2f, 0f
				);
		*/
		sb.draw(ent.currentFrame,
				ent.pos.x - ent.currentFrame.getRegionWidth()/32f, 
				ent.pos.y - ent.currentFrame.getRegionHeight()/32f,
				0f, 0f,
				ent.currentFrame.getRegionWidth()/16f,
				ent.currentFrame.getRegionHeight()/16f,
				1f, 1f, 0f);
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
		font.drawMultiLine(sb,
				controller.getStatusLog(controller.aEntity.get(0)),
				controller.hudCamera.position.x + 40f,
				controller.hudCamera.position.y + 220f);
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
	
	/*
	private void renderEntities() {
		sr.setColor(Colors.PLAYER);
		for (Entity ent : controller.aEntity) {
			sr.rect(ent.pos.x-ent.width/2f,
					ent.pos.y-ent.height/2f,
					ent.width, ent.height);
		}
	}
	*/
	
	private void entityCollisionRenderer() {
		for (Entity ent : controller.aEntity) {
			renderCollisionBounds(ent);
			renderAttacks(ent);
		}
	}
	
	private void renderCollisionBounds(Entity ent) {
		sr.setColor(Colors.PLAYER);
		// horizontal top line
		sr.line(ent.pos.x - ent.collisionWidth/2f - Math.abs(ent.vel.x) - ent.offsetWidth,
				ent.pos.y + ent.collisionHeight/2f,
				ent.pos.x + ent.collisionWidth/2f + Math.abs(ent.vel.x) + ent.offsetWidth,
				ent.pos.y + ent.collisionHeight/2f);
		
		// horizontal bottom line
		sr.line(ent.pos.x - ent.collisionWidth/2f - Math.abs(ent.vel.x) - ent.offsetWidth,
				ent.pos.y - ent.collisionHeight/2f,
				ent.pos.x + ent.collisionWidth/2f + Math.abs(ent.vel.x) + ent.offsetWidth,
				ent.pos.y - ent.collisionHeight/2f);
		
		// vertical left line
		sr.line(ent.pos.x - ent.collisionWidth/2f,
				ent.pos.y - ent.collisionHeight/2f - Math.abs(ent.vel.y) - ent.offsetHeight,
				ent.pos.x - ent.collisionWidth/2f,
				ent.pos.y + ent.collisionHeight/2f + Math.abs(ent.vel.y) + ent.offsetHeight);
		
		// vertical right line
		sr.line(ent.pos.x + ent.collisionWidth/2f,
				ent.pos.y - ent.collisionHeight/2f - Math.abs(ent.vel.y) - ent.offsetHeight,
				ent.pos.x + ent.collisionWidth/2f,
				ent.pos.y + ent.collisionHeight/2f + Math.abs(ent.vel.y) + ent.offsetHeight);
		
		if (ent.currentFrame != null) {
			sr.rect(ent.pos.x - ent.currentFrame.getRegionWidth()/32f, ent.pos.y - ent.currentFrame.getRegionHeight() / 32f,
					ent.currentFrame.getRegionWidth() / 16f, ent.currentFrame.getRegionHeight() / 16f);
		} else {
			sr.rect(ent.pos.x-ent.width/2f, ent.pos.y-ent.height/2f, ent.width, ent.height);
		}
	}
	
	private void renderAttacks(Entity ent) {
		if (!ent.attacks.isEmpty()) {
			for (Attack atk : ent.attacks) {
				sr.setColor(atk.isDamaging() ? Colors.ATTACK_DAMAGE : Colors.ATTACK);
				sr.rect(atk.pos.x - atk.width/2f, atk.pos.y - atk.height/2f, atk.width, atk.height);
			}
		}
	}
}
