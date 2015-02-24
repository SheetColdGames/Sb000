package me.sheetcoldgames.sb000.engine;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

public class Input implements InputProcessor {
	
	public boolean buttons[];
	
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	public static final int ATTACK = 4;
	public static final int SPECIAL_ATTACK = 5;
	public static final int BLOCK = 6;
	public static final int JUMP = 7;
	
	public static final int SELECT = 8;
	
	public static final int ENTER = 9;
	
	public static final int CAM_UP = 10;
	public static final int CAM_DOWN = 11;
	public static final int CAM_LEFT = 12;
	public static final int CAM_RIGHT = 13;
	
	// ======= related to the editor =======
	public static final int PREVIOUS_GROUP = 14;
	public static final int NEXT_GROUP = 15;
	public static final int TOGGLE_GRID = 16;
	public static final int TOGGLE_MAP = 17;
	public static final int SAVE_POINTS = 18;
	
	// ======= miscelaneous =======
	public static final int DEBUG_DAMAGE = 27;
	public static final int DEBUG_RENDER = 28;
	public static final int ESCAPE = 29;
	public static final int CTRL = 30;
	public static final int SHIFT = 31;
	
	// ======= The binded keys =======
	int KEY_UP;
	int KEY_DOWN;
	int KEY_LEFT;
	int KEY_RIGHT;
	
	int KEY_ATTACK;
	int KEY_SPECIAL_ATTACK;
	int KEY_BLOCK;
	int KEY_JUMP;
	
	int KEY_SELECT;
	
	int KEY_ENTER;
	
	int KEY_CAM_UP;
	int KEY_CAM_DOWN;
	int KEY_CAM_LEFT;
	int KEY_CAM_RIGHT;
	
	// ======= Related to the editor ======
	int KEY_PREVIOUS_GROUP;
	int KEY_NEXT_GROUP;
	
	int KEY_TOGGLE_GRID;
	int KEY_TOGGLE_MAP;
	
	int KEY_SAVE_POINTS;
	
	// ======= Miscelaneous/helper keys =======
	int KEY_DEBUG_DAMAGE;
	int KEY_DEBUG_RENDER;
	int KEY_ESCAPE;
	int KEY_CTRL;
	int KEY_SHIFT;
	
	// ======= Related to mouse click =======
	/** contains the position of the last action done with the mosue */
	public Vector3 currentRawPoint; 
	/** contains the mouse position everytime */
	public Vector3 movingMousePosition;
	/** this variable can have the value of Buttons.LEFT, Buttons.RIGHT or Buttons.Middle */
	public int lastMouseButton;
	public boolean mouseDown = false;
	public boolean mouseDragged = false;
	public boolean mouseReleased = false;
	
	public Input() {
		buttons = new boolean[32];
		
		defaultControls();
		currentRawPoint = new Vector3();
		movingMousePosition = new Vector3();
	}
	
	public void releaseAllKeys() {
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = false;
		}
	}
	
	private void defaultControls() {
		KEY_UP 		= Keys.W;
		KEY_DOWN 	= Keys.S;
		KEY_LEFT 	= Keys.A;
		KEY_RIGHT 	= Keys.D;
		
		KEY_ATTACK 			= Keys.J;
		KEY_SPECIAL_ATTACK 	= Keys.K;
		KEY_BLOCK 			= Keys.L;
		KEY_JUMP 			= Keys.SPACE;
		
		KEY_SELECT 	= Keys.C;
		
		KEY_ENTER 	= Keys.ENTER;
		
		KEY_CAM_UP 		= Keys.UP;
		KEY_CAM_DOWN 	= Keys.DOWN;
		KEY_CAM_LEFT 	= Keys.LEFT;
		KEY_CAM_RIGHT 	= Keys.RIGHT;
		
		KEY_PREVIOUS_GROUP = Keys.Q;
		KEY_NEXT_GROUP = Keys.E;
		
		KEY_TOGGLE_GRID = Keys.T;
		KEY_TOGGLE_MAP = Keys.M;
		
		KEY_SAVE_POINTS = Keys.B;
		
		KEY_DEBUG_RENDER = Keys.HOME;
		KEY_DEBUG_DAMAGE = Keys.O;
		
		KEY_ESCAPE = Keys.ESCAPE;
		KEY_CTRL = Keys.CONTROL_LEFT;
		KEY_SHIFT = Keys.SHIFT_LEFT;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == KEY_UP) {
			buttons[UP] = true;
		} else if (keycode == KEY_LEFT) {
			buttons[LEFT] = true;
		} else if (keycode == KEY_RIGHT) {
			buttons[RIGHT] = true;
		} else if (keycode == KEY_DOWN) {
			buttons[DOWN] = true;
		} else if (keycode == KEY_ATTACK) {
			buttons[ATTACK] = true;
		} else if (keycode == KEY_SPECIAL_ATTACK) {
			buttons[SPECIAL_ATTACK] = true;
		} else if (keycode == KEY_BLOCK) {
			buttons[BLOCK] = true;
		} else if (keycode == KEY_JUMP) {
			buttons[JUMP] = true;
		} else if (keycode == KEY_SELECT) {
			buttons[SELECT] = true;
		} else if (keycode == KEY_ENTER) {
			buttons[ENTER] = true;
		} else if (keycode == KEY_CAM_UP) {
			buttons[CAM_UP] = true;
		} else if (keycode == KEY_CAM_DOWN) {
			buttons[CAM_DOWN] = true;
		} else if (keycode == KEY_CAM_LEFT) {
			buttons[CAM_LEFT] = true;
		} else if (keycode == KEY_CAM_RIGHT) {
			buttons[CAM_RIGHT] = true;
		} else if (keycode == KEY_NEXT_GROUP) {
			buttons[NEXT_GROUP] = true;
		} else if (keycode == KEY_PREVIOUS_GROUP) {
			buttons[PREVIOUS_GROUP] = true;
		} else if (keycode == KEY_TOGGLE_GRID) {
			buttons[TOGGLE_GRID] = true;
		} else if (keycode == KEY_TOGGLE_MAP) {
			buttons[TOGGLE_MAP] = true;
		} else if (keycode == KEY_SAVE_POINTS) {
			buttons[SAVE_POINTS] = true;
		} else if (keycode == KEY_DEBUG_RENDER) {
			buttons[DEBUG_RENDER] = true;
		} else if (keycode == KEY_ESCAPE)  {
			buttons[ESCAPE] = true;
		} else if (keycode == KEY_CTRL) {
			buttons[CTRL] = true;
		} else if (keycode == KEY_SHIFT) {
			buttons[SHIFT] = true;
		} else if (keycode == KEY_DEBUG_DAMAGE) {
			buttons[DEBUG_DAMAGE] = true;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == KEY_UP) {
			buttons[UP] = false;
		} else if (keycode == KEY_LEFT) {
			buttons[LEFT] = false;
		} else if (keycode == KEY_RIGHT) {
			buttons[RIGHT] = false;
		} else if (keycode == KEY_DOWN) {
			buttons[DOWN] = false;
		} else if (keycode == KEY_ATTACK) {
			buttons[ATTACK] = false;
		} else if (keycode == KEY_SPECIAL_ATTACK) {
			buttons[SPECIAL_ATTACK] = true;
		} else if (keycode == KEY_BLOCK) {
			buttons[BLOCK] = false;
		} else if (keycode == KEY_JUMP) {
			buttons[JUMP] = false;
		} else if (keycode == KEY_SELECT) {
			buttons[SELECT] = false;
		} else if (keycode == KEY_ENTER) {
			buttons[ENTER] = false;
		} else if (keycode == KEY_CAM_UP) {
			buttons[CAM_UP] = false;
		} else if (keycode == KEY_CAM_DOWN) {
			buttons[CAM_DOWN] = false;
		} else if (keycode == KEY_CAM_LEFT) {
			buttons[CAM_LEFT] = false;
		} else if (keycode == KEY_CAM_RIGHT) {
			buttons[CAM_RIGHT] = false;
		} else if (keycode == KEY_NEXT_GROUP) {
			buttons[NEXT_GROUP] = false;
		} else if (keycode == KEY_PREVIOUS_GROUP) {
			buttons[PREVIOUS_GROUP] = false;
		} else if (keycode == KEY_TOGGLE_GRID) {
			buttons[TOGGLE_GRID] = false;
		} else if (keycode == KEY_TOGGLE_MAP) {
			buttons[TOGGLE_MAP] = false;
		} else if (keycode == KEY_SAVE_POINTS) {
			buttons[SAVE_POINTS] = false;
		} else if (keycode == KEY_DEBUG_RENDER) {
			buttons[DEBUG_RENDER] = false; 
		} else if (keycode == KEY_ESCAPE)  {
			buttons[ESCAPE] = false;
		} else if (keycode == KEY_CTRL) {
			buttons[CTRL] = false;
		} else if (keycode == KEY_SHIFT) {
			buttons[SHIFT] = false;
		} else if (keycode == KEY_DEBUG_DAMAGE) {
			buttons[DEBUG_DAMAGE] = false;
		}
		return true;
	}

	public char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '\b'};
	public char lastCharTyped = '\0';
	public boolean hasTyped = false;
	
	@Override
	public boolean keyTyped(char character) {
		for (int k = 0; k < chars.length; k++) {
			if (character == chars[k]) {
				hasTyped = true;
				lastCharTyped = character;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		currentRawPoint.set(screenX, screenY, 0);
		this.lastMouseButton = button;
		mouseReleased = false;
		mouseDown = true;
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		currentRawPoint.set(screenX, screenY, 0);
		this.lastMouseButton = button;
		mouseDragged = false;
		mouseDown = false;
		mouseReleased = true;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		currentRawPoint.set(screenX, screenY, 0);
		mouseDown = false;
		mouseDragged = true;
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		movingMousePosition.set(screenX, screenY, 0f);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}	
}

