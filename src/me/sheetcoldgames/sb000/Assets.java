package me.sheetcoldgames.sb000;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets extends AssetManager {
	
	public Assets() {
		load(Directories.HERO_ATLAS, TextureAtlas.class);
		finishLoading();
	}
	
	public synchronized void dispose() {
		super.dispose();
	}

}
