/*    Author: Aniv Surana - University of Texas at Arlington                                 */


import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class Robot {

  private GL3 gl;
  private Camera camera;
  private Light light;
  private Light light2;
  private Spotlight spotlight;

  // define body variables
  private float footRadius = 0.8f;

  private float bodyHeigth = 0.6f;
  private float bodyWidth = 1.5f;
  private float bodyDepth = 0.75f;

  private float neckHeigth = 0.25f;
  private float neckRadius = 0.1f;

  private float headHeigth = 1.6f;
  private float headRadius = 0.7f;

  private float eyeRadius = 0.3f;
  private float eyeOut = 0.25f;
  private float eyeUp = headHeigth/2 + 0.2f;

  private float eyebrowThickness = 0.075f;
  private float eyebrowLength = 0.4f;
  private float eyebrowOut = 0.3f;
  private float eyebrowUp = eyeUp + 0.22f;

  private float mouthWidth = 0.225f;
  private float mouthDepth = 0.15f;
  private float mouthHeigth = 0.07f;
  private float mouthUp = eyeUp - 0.3f;
  private float mouthOut = 0.3f;

  private float hairMainHeigth = 0.65f;
  private float hairMainThickness = 0.08f;
  private float hairBranchHeigth = hairMainHeigth/3;
  private float hairBranchThickness = 0.06f;
  private float hairBranchAngle = 45f;

  // define variables for the moving parts of the robot
  private float xPosition = 0;
  private float zPosition = 0;
  private TransformNode moveTranslate;

  private float robotRotation = 0;
  private TransformNode robotTransform;

  private float bodyTiltAngleX = 0;
  private float bodyTiltAngleZ = 0;
  private TransformNode bodyTiltTransform;

  private float headTiltAngleX = 0;
  private float headTiltAngleZ = 0;
  private TransformNode headTiltTransform;

  private float eyeTiltX = 0;
  private float eyeTiltY = 0;
  private TransformNode eyeTiltTransform;

  private float eyebrowRaise = 0f;
  private TransformNode eyebrowRaiseTransform;

  private float mouthOpen = 0f;
  private TransformNode mouthTransform;

  private float hairAngle = 0f;
  private TransformNode hairAngleTransform;

  // define target variables for the animation
  public int animationStepsRemaining = 0; // steps remaining in an animation seqence
                                          // (used for calculating even step sizes)
  public float xPositionTarget = xPosition;
  public float zPositionTarget = zPosition;

  public float robotRotationTarget = robotRotation;

  public float bodyTiltAngleXTarget = bodyTiltAngleX;
  public float bodyTiltAngleZTarget = bodyTiltAngleZ;

  public float headTiltAngleXTarget = headTiltAngleX;
  public float headTiltAngleZTarget = headTiltAngleZ;

  public float eyeTiltXTarget = eyeTiltX;
  public float eyeTiltYTarget = eyeTiltY;

  public float eyebrowRaiseTarget = eyebrowRaise;

  public float mouthOpenTarget = mouthOpen;

  public float hairAngleTarget = hairAngle;

  public Robot(GL3 gl, Camera camera, Light light, Light light2, Spotlight spotlight, float xPosition, float zPosition) {
    this.gl = gl;
    this.camera = camera;
    this.light = light;
    this.light2 = light2;
    this.spotlight = spotlight;
    this.xPosition = xPosition;
    this.zPosition = zPosition;
    
    this.xPositionTarget = xPosition;
    this.zPositionTarget = zPosition;
  }

  public void animate() {
    // animate only if there's animation steps remaining
    if (animationStepsRemaining > 0) {
      // check which parts of the robot need to be animated and for each:

      // position
      if ((xPositionTarget != xPosition) || (zPositionTarget != zPosition)) {
        // calculate the differences between the target and current positoins
        float xDiff = xPositionTarget - xPosition;
        float zDiff = zPositionTarget - zPosition;
        // increase the current position by the difference divided by steps remaining
        changePosition(xPosition + xDiff/animationStepsRemaining, zPosition + zDiff/animationStepsRemaining);
      }

      // rotation
      if (robotRotationTarget != robotRotation) {
        float diff = robotRotationTarget - robotRotation;
        changeRotation(robotRotation + diff/animationStepsRemaining);
      }

      // body tilt
      if ((bodyTiltAngleXTarget != bodyTiltAngleX) || (bodyTiltAngleZTarget != bodyTiltAngleZ)) {
        float xDiff = bodyTiltAngleXTarget - bodyTiltAngleX;
        float zDiff = bodyTiltAngleZTarget - bodyTiltAngleZ;
        changeTilt(bodyTiltAngleX + xDiff/animationStepsRemaining, bodyTiltAngleZ + zDiff/animationStepsRemaining);
      }

      // head tilt
      if ((headTiltAngleXTarget != headTiltAngleX) || (headTiltAngleZTarget != headTiltAngleZ)) {
        float xDiff = headTiltAngleXTarget - headTiltAngleX;
        float zDiff = headTiltAngleZTarget - headTiltAngleZ;
        changeHeadTilt(headTiltAngleX + xDiff/animationStepsRemaining, headTiltAngleZ + zDiff/animationStepsRemaining);
      }

      // eye tilt
      if ((eyeTiltXTarget != eyeTiltX) || (eyeTiltYTarget != eyeTiltY)) {
        float xDiff = eyeTiltXTarget - eyeTiltX;
        float yDiff = eyeTiltYTarget - eyeTiltY;
        changeEyeTilt(eyeTiltX + xDiff/animationStepsRemaining, eyeTiltY + yDiff/animationStepsRemaining);
      }

      // eyebrow raise
      if (eyebrowRaiseTarget != eyebrowRaise) {
        float diff = eyebrowRaiseTarget - eyebrowRaise;
        changeEyebrowRaise(eyebrowRaise + diff/animationStepsRemaining);
      }

      // mouth open
      if (mouthOpenTarget != mouthOpen) {
        float diff = mouthOpenTarget - mouthOpen;
        changeMouthOpen(mouthOpen + diff/animationStepsRemaining);
      }

      // hair angle
      if (hairAngleTarget != hairAngle) {
        float diff = hairAngleTarget - hairAngle;
        changeHairAngle(hairAngle + diff/animationStepsRemaining);
      }

      // decrease the animation steps remaining
      animationStepsRemaining -= 1;
    }
  }

  /////////////////////////////////////
  ///// BODY MOVEMENT FUNCTIONS ///////
  //
  // below are the functions to change the positions of certain robot parts

  private void changePosition(float x, float z) {
    xPosition = x;
    zPosition = z;
    moveTranslate.setTransform(Mat4Transform.translate(xPosition, 0, zPosition));
    moveTranslate.update();
  }

  private void changeTilt(float angleX, float angleZ) {
    bodyTiltAngleX = angleX;
    bodyTiltAngleZ = angleZ;
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0, footRadius/2, 0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(bodyTiltAngleZ));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(bodyTiltAngleX));
    bodyTiltTransform.setTransform(m);
    bodyTiltTransform.update();
  }

  private void changeRotation(float angle) {
    robotRotation = angle;
    robotTransform.setTransform(Mat4Transform.rotateAroundY(robotRotation));
    robotTransform.update();
  }

  private void changeHeadTilt(float angleX, float angleZ) {
    headTiltAngleX = angleX;
    headTiltAngleZ = angleZ;
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0, footRadius/2+bodyHeigth+neckHeigth/2, 0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(headTiltAngleZ));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(headTiltAngleX));
    headTiltTransform.setTransform(m);
    headTiltTransform.update();
  }

  private void changeEyeTilt(float angleX, float angleY) {
    eyeTiltX = angleX;
    eyeTiltY = angleY;
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0, eyeUp, eyeOut));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundY(eyeTiltY));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(eyeTiltX));
    eyeTiltTransform.setTransform(m);
    eyeTiltTransform.update();
  }

  private void changeEyebrowRaise(float raise) {
    eyebrowRaise = raise;
    eyebrowRaiseTransform.setTransform(Mat4Transform.translate(0, eyebrowRaise, 0));
    eyebrowRaiseTransform.update();
  }

  private void changeMouthOpen(float open) {
    mouthOpen = open;
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0, mouthUp, mouthOut));
    m = Mat4.multiply(m, Mat4Transform.scale(mouthWidth, mouthHeigth+mouthOpen, mouthDepth));
    mouthTransform.setTransform(m);
    mouthTransform.update();
  }

  private void changeHairAngle(float angle) {
    hairAngle = angle;
    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0, headHeigth-0.05f, 0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(hairAngle));
    hairAngleTransform.setTransform(m);
    hairAngleTransform.update();
  }

  public void waveHairWithTime(double elapsedTime) {
    float timeAngle = 10f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0, headHeigth-0.05f, 0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(hairAngle + timeAngle));
    hairAngleTransform.setTransform(m);
    hairAngleTransform.update();
  }

  public SGNode getRootNode() {
    
    // define all the components required to build the scene

    int[] footTexture = TextureLibrary.loadTexture(gl, "textures/tyre.jpg");
    int[] eyeTexture = TextureLibrary.loadTexture(gl, "textures/eye.jpg");
    int[] headTexture = TextureLibrary.loadTexture(gl, "textures/head.jpg");
    int[] bodyTexture = TextureLibrary.loadTexture(gl, "textures/body.jpg");
    int[] neckTexture = TextureLibrary.loadTexture(gl, "textures/neck.jpg");
    int[] hairTexture = TextureLibrary.loadTexture(gl, "textures/hair.jpg");
    int[] mouthTexture = TextureLibrary.loadTexture(gl, "textures/mouth.jpg");

    Mesh twoTriangles = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Mesh sphere = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Mesh cube = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shaderC = new Shader(gl, "vs_cube.txt", "fs_spotlight.txt");
    Shader shaderS = new Shader(gl, "vs_sphere.txt", "fs_spotlight.txt");
    Material material = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), 50.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16, 1f, 16);

    Model foot = new Model(gl, camera, light, light2, spotlight, shaderS, material, modelMatrix, sphere, footTexture);
    Model body = new Model(gl, camera, light, light2, spotlight, shaderS, material, modelMatrix, sphere, bodyTexture);
    Model neck = new Model(gl, camera, light, light2, spotlight, shaderS, material, modelMatrix, sphere, neckTexture);
    Model head = new Model(gl, camera, light, light2, spotlight, shaderS, material, modelMatrix, sphere, headTexture);
    Model eye = new Model(gl, camera, light, light2, spotlight, shaderS, material, modelMatrix, sphere, eyeTexture);
    Model eyebrow = new Model(gl, camera, light, light2, spotlight, shaderC, material, modelMatrix, cube, hairTexture);
    Model mouth = new Model(gl, camera, light, light2, spotlight, shaderC, material, modelMatrix, cube, mouthTexture);
    Model hair = new Model(gl, camera, light, light2, spotlight, shaderC, material, modelMatrix, cube, hairTexture);


    // define nodes

    NameNode robotRoot = new NameNode("Robot Root");
    robotTransform = new TransformNode("robot transform", Mat4Transform.rotateAroundY(robotRotation));
    moveTranslate = new TransformNode("move translate", Mat4Transform.translate(xPosition, 0, zPosition));

    NameNode footNode = new NameNode("foot");
      Mat4 m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(footRadius, footRadius, footRadius));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode footTransform = new TransformNode("foot transform", m);
        ModelNode footShape = new ModelNode("Sphere(foot)", foot);

    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0, footRadius/2, 0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(bodyTiltAngleZ));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(bodyTiltAngleX));
    bodyTiltTransform = new TransformNode("body tilt transform", m);
    
    NameNode bodyNode = new NameNode("body");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(0, footRadius/2, 0));
      m = Mat4.multiply(m, Mat4Transform.scale(bodyWidth, bodyHeigth, bodyDepth));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode bodyTransform = new TransformNode("body transform", m);
        ModelNode bodyShape = new ModelNode("Sphere(body)", body);

    NameNode neckNode = new NameNode("neck");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(0, footRadius/2+bodyHeigth-neckHeigth/4, 0));
      m = Mat4.multiply(m, Mat4Transform.scale(neckRadius, neckHeigth, neckRadius));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode neckTransform = new TransformNode("neck transform", m);
        ModelNode neckShape = new ModelNode("Sphere(neck)", neck);
    
    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0, footRadius/2+bodyHeigth+neckHeigth/2, 0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(headTiltAngleZ));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(headTiltAngleX));
    headTiltTransform = new TransformNode("head tilt transform", m);

    NameNode headNode = new NameNode("head");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(headRadius, headHeigth, headRadius));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode headTransform = new TransformNode("head transform", m);
        ModelNode headShape = new ModelNode("Sphere(head)", head);

    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0, eyeUp, eyeOut));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundY(eyeTiltY));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(eyeTiltX));
    eyeTiltTransform = new TransformNode("eye tilt transform", m);

    NameNode eyeNode = new NameNode("eye");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.rotateAroundY(180f));
      m = Mat4.multiply(m, Mat4Transform.scale(eyeRadius, eyeRadius, eyeRadius));
      TransformNode eyeTransform = new TransformNode("eye transform", m);
        ModelNode eyeShape = new ModelNode("Sphere(eye)", eye);

    eyebrowRaiseTransform = new TransformNode("eyebrow raise transform", Mat4Transform.translate(0, eyebrowRaise, 0));

    NameNode eyebrowNode = new NameNode("eyebrow");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(0, eyebrowUp, eyebrowOut));
      m = Mat4.multiply(m, Mat4Transform.scale(eyebrowLength, eyebrowThickness, eyebrowThickness));
      TransformNode eyebrowTransform = new TransformNode("eyebrow transform", m);
        ModelNode eyebrowShape = new ModelNode("Cube(eyebrow)", eyebrow);

    NameNode mouthNode = new NameNode("mouth");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(0, mouthUp, mouthOut));
      m = Mat4.multiply(m, Mat4Transform.scale(mouthWidth, mouthHeigth+mouthOpen, mouthDepth));
      mouthTransform = new TransformNode("mouth transform", m);
        ModelNode mouthShape = new ModelNode("Cube(mouth)", mouth);

    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0, headHeigth-0.05f, 0));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(hairAngle));
    hairAngleTransform = new TransformNode("hair angle transform", m);

    NameNode hairNode = new NameNode("hair");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(hairMainThickness, hairMainHeigth, hairMainThickness));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode hairMainTransform = new TransformNode("hair main transform", m);
        ModelNode hairMainShape = new ModelNode("Cube(hairMain)", hair);
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(0, hairMainHeigth/10, 0));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(hairBranchAngle));
      m = Mat4.multiply(m, Mat4Transform.scale(hairBranchThickness, hairBranchHeigth, hairBranchThickness));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode hairBranch1Transform = new TransformNode("hair branch 1 transform", m);
        ModelNode hairBranch1Shape = new ModelNode("Cube(hairBranch1)", hair);
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(0, hairMainHeigth/2, 0));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(-hairBranchAngle));
      m = Mat4.multiply(m, Mat4Transform.scale(hairBranchThickness, hairBranchHeigth, hairBranchThickness));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode hairBranch2Transform = new TransformNode("hair branch 2 transform", m);
        ModelNode hairBranch2Shape = new ModelNode("Cube(hairBranch2)", hair);
    
    
    // tree hierarchy

    robotRoot.addChild(moveTranslate);
      moveTranslate.addChild(robotTransform);
        robotTransform.addChild(footNode);
          footNode.addChild(footTransform);
            footTransform.addChild(footShape);
        robotTransform.addChild(bodyTiltTransform);
          bodyTiltTransform.addChild(bodyNode);
            bodyNode.addChild(bodyTransform);
              bodyTransform.addChild(bodyShape);
          bodyTiltTransform.addChild(neckNode);
            neckNode.addChild(neckTransform);
              neckTransform.addChild(neckShape);
            neckNode.addChild(headTiltTransform);
              headTiltTransform.addChild(headNode);
                headNode.addChild(headTransform);
                  headTransform.addChild(headShape);
              headTiltTransform.addChild(eyeTiltTransform);
                eyeTiltTransform.addChild(eyeTransform);
                  eyeTransform.addChild(eyeShape);
              headTiltTransform.addChild(eyebrowRaiseTransform);
                eyebrowRaiseTransform.addChild(eyebrowTransform);
                  eyebrowTransform.addChild(eyebrowShape);
              headTiltTransform.addChild(mouthTransform);
                mouthTransform.addChild(mouthShape);
              headTiltTransform.addChild(hairAngleTransform);
                hairAngleTransform.addChild(hairNode);
                  hairNode.addChild(hairMainTransform);
                    hairMainTransform.addChild(hairMainShape);
                  hairNode.addChild(hairBranch1Transform);
                    hairBranch1Transform.addChild(hairBranch1Shape);
                  hairNode.addChild(hairBranch2Transform);
                    hairBranch2Transform.addChild(hairBranch2Shape);

    return robotRoot;
  }
}