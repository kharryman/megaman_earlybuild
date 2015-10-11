package com.mm.screens;

import java.text.DecimalFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mm.main.MegaManGame;
import com.mm.scenes.Hud;
import com.mm.sprites.MegaMan;

public class PlayScreen implements Screen {
	private MegaManGame game;
	private OrthographicCamera gamecam;
	private Viewport gamePort;
	private Hud hud;
	private float ppm = MegaManGame.PPM;
	private MegaMan player;
	
	//tiled map variables
	private TmxMapLoader mapLoader;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	
	//Box2d variables
	private World world;
	private Box2DDebugRenderer b2dr; //draws outlines of boxes
	
	
	public PlayScreen(MegaManGame game){
		this.game = game;
		gamecam = new OrthographicCamera();
		gamePort = new FitViewport(MegaManGame.V_WIDTH / ppm, MegaManGame.V_HEIGHT / ppm, gamecam);
		hud = new Hud(game.batch);
		
		
		//create a class to load maps from a menu here and use the below code to load the maps/////////
		
		mapLoader = new TmxMapLoader();
		//prevent graphic tearing on maps?
		TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
		params.textureMagFilter = TextureFilter.Nearest;
		params.textureMinFilter = TextureFilter.Nearest;

		map = mapLoader.load("custom_B.tmx", params); //choose map to load============================================>>>
		renderer = new OrthogonalTiledMapRenderer(map, 1/ppm); //render map to screen
		gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0); //set starting position for camera
		
		world = new World(new Vector2(0, -10), true);
		b2dr = new Box2DDebugRenderer();
		player = new MegaMan(world, 200,650); // world and starting xy-axis
		
		
		// the following will be created in their own classes later on
		BodyDef bdef = new BodyDef();
		PolygonShape shape = new PolygonShape();
		FixtureDef fdef = new FixtureDef();
		Body body;
		
		//create and add ground objects
		for(MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)){
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set((rect.getX() + rect.getWidth() / 2)/ppm, (rect.getY() + rect.getHeight() / 2)/ppm);
			body = world.createBody(bdef);
			
			shape.setAsBox((rect.getWidth() / 2)/ppm, (rect.getHeight() / 2)/ppm);
			fdef.shape = shape;
			body.createFixture(fdef);
		}
		// end temporary block
		
	}
	
	//update game world here
	public void update(float dt){
		handleInput(dt);
		
		world.step(1/60f, 6, 2);
		
		
		//this attaches the camera to the player and moves it when he moves. adjust it later
		
		
		
		gamecam.position.y = player.b2body.getPosition().y;
		//gamecam.position.x = player.b2body.getPosition().x;
		

			
			
			
		
			
		//System.out.println("Cam position: X = " + gamecam.position.x + "Y = " + gamecam.position.y);
		System.out.println(String.format("Player: x=%f     y=%f", player.b2body.getPosition().x, player.b2body.getPosition().y));
		
		
		
		//update camera when it moves
		gamecam.update();
		renderer.setView(gamecam);
	}
	
	
	
	public void handleInput(float dt){
		if(Gdx.input.isKeyJustPressed(Keys.SPACE)){
			//gamecam.position.y += 200 * dt; //temp
			if(player.b2body.getLinearVelocity().y == 0){ //prevent multi jumping
				player.b2body.setLinearVelocity(new Vector2(player.b2body.getLinearVelocity().x, 0)); //keep x velocity the same and stop y velocity
				player.b2body.applyLinearImpulse(new Vector2(0, 3.0f), player.b2body.getWorldCenter(), true);
			}
		}
		if(Gdx.input.isKeyPressed(Keys.A) && player.b2body.getLinearVelocity().x >= -1){
			//gamecam.position.x -= 200 * dt; //temp
			player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
		}
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)){
			//gamecam.position.y -= 200 * dt; //temp
			Gdx.app.exit();
		}
		if(Gdx.input.isKeyPressed(Keys.D) && player.b2body.getLinearVelocity().x <= 1){
			//gamecam.position.x += 200 * dt; //temp
			player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
		}
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	//this method is on a loop
	@Override
	public void render(float delta) {
		update(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderer.render();
		
		//render box2d objectsa
		b2dr.render(world, gamecam.combined); //remove this when you want to hide hitboxes on world and player objects
		
		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
		hud.stage.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		gamePort.update(width, height);
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
