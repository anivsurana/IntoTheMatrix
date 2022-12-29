/*  Author: Aniv Surana - University of Texas at Arlington    */

import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class Museum_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public Museum_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(3.5f, 5f, 12f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   * adapted from the tutorials provided
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
    floor.dispose(gl);
    sphere.dispose(gl);
    cube.dispose(gl);
  }
  
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */

  public void changeRobotPosition(int pos) {
    // change targets depending on which robot position is selected
    // each part of the robot can be separatly controlled here.
    if (pos == 1) {
      robot.xPositionTarget = -3.2f;
      robot.zPositionTarget = -4f;
      robot.bodyTiltAngleXTarget = 0f;
      robot.bodyTiltAngleZTarget = -15f;
      robot.robotRotationTarget = 10f;
      robot.headTiltAngleXTarget = 0f;
      robot.headTiltAngleZTarget = 10f;
      robot.eyeTiltXTarget = 0f;
      robot.eyeTiltYTarget = 45f;
      robot.eyebrowRaiseTarget = -0.1f;
      robot.mouthOpenTarget = 0.12f;
      robot.hairAngleTarget = 0f;
      robot.animationStepsRemaining = 100;
    } else if (pos == 2) {
      robot.xPositionTarget = 2f;
      robot.zPositionTarget = -2.8f;
      robot.bodyTiltAngleXTarget = 25f;
      robot.bodyTiltAngleZTarget = 0f;
      robot.robotRotationTarget = 135f;
      robot.headTiltAngleXTarget = 15f;
      robot.headTiltAngleZTarget = 0f;
      robot.eyeTiltXTarget = 35f;
      robot.eyeTiltYTarget = 0f;
      robot.eyebrowRaiseTarget = 0.0f;
      robot.mouthOpenTarget = 0f;
      robot.hairAngleTarget = 30f;
      robot.animationStepsRemaining = 100;
    } else if (pos == 3) {
      robot.xPositionTarget = 3.25f;
      robot.zPositionTarget = 3.3f;
      robot.bodyTiltAngleXTarget = -15f;
      robot.bodyTiltAngleZTarget = 0f;
      robot.robotRotationTarget = 45f;
      robot.headTiltAngleXTarget = 0f;
      robot.headTiltAngleZTarget = 15f;
      robot.eyeTiltXTarget = -25f;
      robot.eyeTiltYTarget = 25f;
      robot.eyebrowRaiseTarget = 0.15f;
      robot.mouthOpenTarget = 0.15f;
      robot.hairAngleTarget = -30f;
      robot.animationStepsRemaining = 100;
    } else if (pos == 4) {
      robot.xPositionTarget = 0f;
      robot.zPositionTarget = 4f;
      robot.bodyTiltAngleXTarget = 0f;
      robot.bodyTiltAngleZTarget = 15f;
      robot.robotRotationTarget = 180f;
      robot.headTiltAngleXTarget = 10f;
      robot.headTiltAngleZTarget = 0f;
      robot.eyeTiltXTarget = 0f;
      robot.eyeTiltYTarget = 10f;
      robot.eyebrowRaiseTarget = -0.1f;
      robot.mouthOpenTarget = 0f;
      robot.hairAngleTarget = 10f;
      robot.animationStepsRemaining = 100;
    } else if (pos == 5) {
      robot.xPositionTarget = -4.5f;
      robot.zPositionTarget = -0.5f;
      robot.bodyTiltAngleXTarget = 0f;
      robot.bodyTiltAngleZTarget = 0f;
      robot.robotRotationTarget = -60f;
      robot.headTiltAngleXTarget = 15f;
      robot.headTiltAngleZTarget = 0f;
      robot.eyeTiltXTarget = 0f;
      robot.eyeTiltYTarget = 0f;
      robot.eyebrowRaiseTarget = 0.15f;
      robot.mouthOpenTarget = 0f;
      robot.hairAngleTarget = 10f;
      robot.animationStepsRemaining = 100;
    }
  }

  public void toggleLight(int whichLight) {
    if (whichLight == 1) {
      light.toggle();
    } else if (whichLight == 2) {
      light2.toggle();
    } else if (whichLight == 3) {
      spotlight.toggle();
    }
  }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective;
  private Model floor, sphere, cube;
  private Light light, light2;
  private Spotlight spotlight;
  private SGNode sceneRoot;
  
  private Robot robot;
  private Lamp lamp;
  
  private void initialise(GL3 gl) {

    // define the lights and the objects in the room
    light = new Light(gl);
    light.setCamera(camera);
    light.setPosition(new Vec3(0f, 4.75f, 0f));
    Material lightMaterial = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.7f, 0.7f, 0.7f), 32f);
    light.setMaterial(lightMaterial);

    light2 = new Light(gl);
    light2.setCamera(camera);
    light2.setPosition(new Vec3(-11.85f, 4f, 2f));
    Material light2Material = new Material(new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.7f, 0.7f, 0.7f), 32f);
    light2.setMaterial(light2Material);

    spotlight = new Spotlight(gl, 15f, new Vec3(0f, -1f, 0f));
    spotlight.setCamera(camera);
    spotlight.setPosition(new Vec3(4f, 4f, 4f));
    Material spotlightMaterial = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.7f, 0.7f, 0.7f), 32f);
    spotlight.setMaterial(spotlightMaterial);

    Room room = new Room(gl, camera, light, light2, spotlight, 12, 12, 5);
    SGNode roomRoot = room.getRootNode();

    Phone phone = new Phone(gl, camera, light, light2, 4.2f, -5f);
    SGNode phoneRoot = phone.getRootNode();

    Egg egg = new Egg(gl, camera, light, light2, 0f, 0.5f);
    SGNode eggRoot = egg.getRootNode();
    
    robot = new Robot(gl, camera, light, light2, spotlight, -3.2f, -4f);
    SGNode robotRoot = robot.getRootNode();

    lamp = new Lamp(gl, camera, light, light2, spotlight, 5.25f, 4f, -90f);
    SGNode lampRoot = lamp.getRootNode();

    // create the room tree
    sceneRoot = new NameNode("scene");

    sceneRoot.addChild(roomRoot);
      roomRoot.addChild(robotRoot);
      roomRoot.addChild(phoneRoot);
      roomRoot.addChild(eggRoot);
      roomRoot.addChild(lampRoot);

    sceneRoot.update();
    // sceneRoot.print(0, false);
    // System.exit(0);
  }
 
  private void render(GL3 gl) {
    double elapsedTime = getSeconds()-startTime;

    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.render(gl);
    robot.animate();
    robot.waveHairWithTime(elapsedTime);
    lamp.updateSwingPosition(elapsedTime);
    sceneRoot.draw(gl);
  }
  
  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }
  
}