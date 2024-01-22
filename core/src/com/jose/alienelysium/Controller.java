package com.jose.alienelysium;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

public class Controller extends Touchpad {
    
Files files;
    public Controller(Texture touchBackground, Texture touchKnob){

        super(30, Controller.getTouchPadStyle( touchBackground, touchKnob));
        setBounds(100, 100, 100, 100);

        Gdx.app.log("MENSAJE",AlienElysiumGame.scene.cameras.toString());
    }
    private static TouchpadStyle getTouchPadStyle(Texture touchBackground, Texture touchKnob){
        Skin mitouchpadSkin = new Skin();

       // File back= Gdx.files.internal("JoystickSplitted.png").file();
        //mitouchpadSkin.add("touchBackground", new Texture(Gdx.files.internal("badlogic.jpg").path()));
       // mitouchpadSkin.add("touchKnob", new Texture(Gdx.files.internal("badlogic.jpg").path()));

        mitouchpadSkin.add("touchBackground",touchBackground);
        mitouchpadSkin.add("touchKnob",touchKnob);

        TouchpadStyle mitouchpadStyle = new TouchpadStyle();
        mitouchpadStyle.background = mitouchpadSkin.getDrawable("touchBackground");
        mitouchpadStyle.knob = mitouchpadSkin.getDrawable("touchKnob");
        return mitouchpadStyle;
    }
    @Override
    public void act (float delta) {
        super.act(delta);
        if(isTouched()){
            // Mover al personaje o cualquier otra cosa que quieras hacer
            Gdx.app.log("MyTag", "Touched");
            // Obtener la proporción de movimiento del Touchpad
            float knobPercentX = getKnobPercentX();
            float knobPercentY = getKnobPercentY();

            // Ajustar la velocidad de movimiento según tus necesidades
            float movementSpeed = 0.05f;
// Crear un Vector3 para el desplazamiento en los ejes X e Y
//            Vector3 translation = new Vector3(-knobPercentX * movementSpeed, knobPercentY * movementSpeed, 0);
//
//            // Mover la cámara con el Vector3 creado
//            MyGdxGame.scene.cam.translate(translation);


            // Obtener la dirección de la cámara
          //  Vector3 cameraDirection = AlienElysiumGame.scene.camera.direction.cpy().nor();
            Vector3 cameraDirection=  AlienElysiumGame.scene.getCamera("camera").direction.cpy().nor();
            // Obtener la dirección perpendicular al plano horizontal (cruzando con el vector Y)
            Vector3 right = cameraDirection.cpy().crs(Vector3.Y).nor();

            // Crear un Vector3 para el desplazamiento en los ejes X e Y
            Vector3 translation = new Vector3(
                    right.x * knobPercentX * movementSpeed,
                    0,
                    right.z * knobPercentX * movementSpeed
            );

            // Desplazar la cámara hacia adelante/atrás
            AlienElysiumGame.scene.getCamera("camera").translate(cameraDirection.scl(movementSpeed * knobPercentY));
           // AlienElysiumGame.scene.camera.translate(cameraDirection.scl(movementSpeed * knobPercentY));

            // Mover la cámara hacia los lados
            AlienElysiumGame.scene.getCamera("camera").translate(translation);
           // AlienElysiumGame.scene.camera.translate(translation);
            AlienElysiumGame.scene.getCamera("camera").update();
          //  AlienElysiumGame.scene.cam.update();


        }
    }
}
            // Mover la cámara según la posición del Touchpad
          //  MyGdxGame.scene.cam.translate(MyGdxGame.scene.cam.direction.cpy().scl(knobPercentY * movementSpeed));
            //MyGdxGame.scene.cam.translate(MyGdxGame.scene.cam.up.cpy().scl(-knobPercentX * movementSpeed));

            // Ajustar la orientación de la cámara según la posición vertical del Touchpad
           // float deltaYaw = -knobPercentY * movementSpeed;
            //float deltaPitch = 2; // Ajusta según sea necesario
            //MyGdxGame.scene.cam.rotate(new Quaternion().setEulerAngles(deltaPitch, deltaYaw, 0));
