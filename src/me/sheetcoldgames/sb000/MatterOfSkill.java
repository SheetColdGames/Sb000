package me.sheetcoldgames.sb000;

import me.sheetcoldgames.sb000.controller.GameController;

import com.badlogic.gdx.ApplicationAdapter;

public class MatterOfSkill extends ApplicationAdapter {
	
	GameController controller;
	GameRenderer renderer;
	
	public void create() {
		controller = new GameController();
		renderer = new GameRenderer(controller);
	}
	
	public void dispose() {
		controller.dispose();
		renderer.dispose();
	}
	
	public void render() {
		controller.update();
		renderer.render();
	}

}
