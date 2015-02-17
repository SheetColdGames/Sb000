package me.sheetcoldgames.sb000;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopMain {
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 480;
		config.resizable = false;
		config.title = "Matter of Skill";
		new LwjglApplication(new MatterOfSkill(), config);
	}
}
