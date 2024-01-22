package com.jose.alienelysium;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

public class AlienElysiumGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	private Cubemap environmentCubemap;
	private SceneSkybox skybox;
	private SceneManager sceneManager;
	private SceneAsset sceneAsset;
	private Scene playerScene;
	private PerspectiveCamera camera;
	private Cubemap diffuseCubemap;

	private Cubemap specularCubemap;
	private Texture brdfLUT;
	private float time;

	private DirectionalLightEx light;
	private FirstPersonCameraController cameraController;

	// Player Movement
	float speed = 5f;
	float rotationSpeed = 80f;
	private final Matrix4 playerTransform = new Matrix4();
	private final Vector3 moveTranslation = new Vector3();
	private final Vector3 currentPosition = new Vector3();

	// Camera
	private CameraMode cameraMode = CameraMode.BEHIND_PLAYER;
	//private float camPitch = Settings.CAMERA_START_PITCH;
	private float distanceFromPlayer = 35f;
	private float angleAroundPlayer = 0f;
	private float angleBehindPlayer = 0f;

//	private Terrain terrain;
	private Scene terrainScene;
	Texture front = new Texture("skybox/front.png");
	Texture back = new Texture("skybox/back.png");
	Texture left = new Texture("skybox/left.png");
	Texture right = new Texture("skybox/right.png");
	Texture up = new Texture("skybox/up.png");
	Texture down = new Texture("skybox/down.png");
	
	@Override
	public void create () {

		sceneManager = new SceneManager(new CustomShaderProvider(), PBRShaderProvider.createDefaultDepth(24));
		// setup quick IBL (image based lighting)
		IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
	//	environmentCubemap = iblBuilder.buildEnvMap(1024);
		environmentCubemap= new Cubemap((TextureData) front, (TextureData) back, (TextureData) up, (TextureData) down, (TextureData) right, (TextureData) left);
		diffuseCubemap = iblBuilder.buildIrradianceMap(256);
		specularCubemap = iblBuilder.buildRadianceMap(10);
		iblBuilder.dispose();
		skybox = new SceneSkybox(environmentCubemap);
		sceneManager.setSkyBox(skybox);
	}

	@Override
	public void render () {

		batch.begin();

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
