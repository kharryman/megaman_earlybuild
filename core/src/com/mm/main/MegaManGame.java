package com.mm.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mm.screens.PlayScreen;

public class MegaManGame extends Game {
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT  = 208;
	public final static float PPM = 100; //PIXELS PER METER
	public SpriteBatch batch;
	
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
}
