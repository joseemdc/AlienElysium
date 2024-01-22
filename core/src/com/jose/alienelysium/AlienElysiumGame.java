package com.jose.alienelysium;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.jose.alienelysium.enums.CameraMode;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;

public class AlienElysiumGame extends ApplicationAdapter implements GestureDetector.GestureListener,InputProcessor {
	private SceneManager sceneManager;
	private SceneAsset sceneAsset;
	public static Scene scene;
	public static  PerspectiveCamera camera;
	private Cubemap diffuseCubemap;
	private Cubemap environmentCubemap;
	private Cubemap specularCubemap;
	private Texture brdfLUT;
	private float time;
	private SceneSkybox skybox;
	private DirectionalLightEx light;
	AssetManager manager = new AssetManager();
	public static FirstPersonCameraController controller;
	InputMultiplexer inputMultiplexer;
	private Stage stage;
	private Skin touchpadSkin;
	GestureDetector gd;

	
	@Override
	public void create () {
		manager.load("skybox/front.png", Texture.class);
		manager.load("skybox/back.png", Texture.class);
		manager.load("skybox/left.png", Texture.class);
		manager.load("skybox/right.png", Texture.class);
		manager.load("skybox/top.png", Texture.class);
		manager.load("skybox/bottom.png", Texture.class);
		manager.load("ui/JoystickSplitted.png", Texture.class);
		manager.load("ui/SmallHandleFilledGrey.png", Texture.class);
		stage = new Stage();
		while(!manager.update()){

		}
		Texture front = manager.get("skybox/front.png", Texture.class);
		Texture back = manager.get("skybox/back.png", Texture.class);
		Texture left = manager.get("skybox/left.png", Texture.class);
		Texture right = manager.get("skybox/right.png", Texture.class);
		Texture up = manager.get("skybox/top.png", Texture.class);
		Texture down = manager.get("skybox/bottom.png", Texture.class);
		Texture tex = manager.get("ui/JoystickSplitted.png", Texture.class);
		Texture tex2 = manager.get("ui/SmallHandleFilledGrey.png", Texture.class);
// create scene
		sceneAsset = new GLTFLoader().load(Gdx.files.internal("models/pasillos.gltf"));
		scene = new Scene(sceneAsset.scene);
		sceneManager = new SceneManager();
		sceneManager.addScene(scene);

		// setup camera (The BoomBox model is very small so you may need to adapt camera settings for your scene)
		camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		float d = .02f;
		camera.near = d / 1000f;
		camera.far = d * 4;
		sceneManager.setCamera(camera);
		inputMultiplexer = new InputMultiplexer();
		controller = new FirstPersonCameraController(camera);
		controller.setVelocity(0.05f);

		// setup light
		light = new DirectionalLightEx();
		light.direction.set(1, -3, 1).nor();
		light.color.set(Color.WHITE);
		sceneManager.environment.add(light);

		// setup quick IBL (image based lighting)
		IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
		environmentCubemap = iblBuilder.buildEnvMap(1024);
		diffuseCubemap = iblBuilder.buildIrradianceMap(256);
		specularCubemap = iblBuilder.buildRadianceMap(10);
		iblBuilder.dispose();

		// This texture is provided by the library, no need to have it in your assets.
		brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

		sceneManager.setAmbientLight(1f);
		sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
		sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
		sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

		// setup skybox
		skybox = new SceneSkybox(environmentCubemap);
		sceneManager.setSkyBox(skybox);

		//setup touchpad
		Controller control = new Controller(tex,tex2);
		stage.addActor(control);
		control.setPosition(100, 100);
		control.setBounds(100,100,350,350);
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(controller);
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	@Override
	public void render () {

		float deltaTime = Gdx.graphics.getDeltaTime();
		time += deltaTime;

		// animate camera
//		camera.position.setFromSpherical(MathUtils.PI/4, time * .3f).scl(.02f);
//		camera.up.set(Vector3.Y);
//		camera.lookAt(Vector3.Zero);
//		camera.update();

		// render
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		sceneManager.update(deltaTime);
		sceneManager.render();

		stage.act(Gdx.graphics.getDeltaTime());
		controller.update();
		stage.draw();

	}
	
	@Override
	public void dispose () {
		sceneManager.dispose();
		sceneAsset.dispose();
		environmentCubemap.dispose();
		diffuseCubemap.dispose();
		specularCubemap.dispose();
		brdfLUT.dispose();
		skybox.dispose();

	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	@Override
	public void pinchStop() {

	}
}
