/*    Author: Aniv Surana - University of Texas at Arlington                                    */


import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class Lamp {

  private GL3 gl;
  private Camera camera;
  private Light light;
  private Light light2;
  private Spotlight spotlight;
  
  // define body variables
  private float xPosition, zPosition, rotation;

  private float baseHeigth = 0.25f;
  private float baseWidth = 0.5f;
  private float baseDepth = 0.5f;

  private float poleHeigth = 4.2f;
  private float poleThickness = 0.15f;
  private float poleTop = 1f;

  private float topHeigth = 0.4f;
  private float topThickness = 0.25f;

  private Vec3 bulbPosition;

  TransformNode topSwingTransform;

  public Lamp(GL3 gl, Camera camera, Light light, Light light2, Spotlight spotlight, float xPosition, float zPosition, float rotation) {
    this.gl = gl;
    this.camera = camera;
    this.light = light;
    this.light2 = light2;
    this.spotlight = spotlight;
    this.xPosition = xPosition;
    this.zPosition = zPosition;
    this.rotation = rotation;

    this.bulbPosition = new Vec3(xPosition-poleTop, baseHeigth+poleHeigth-topHeigth/2, zPosition);
    this.spotlight.setPosition(bulbPosition);
  }

  /*  update the swing position of the top piece of the lamp depenging on the current time  */
  public void updateSwingPosition(double elapsedTime) {
    // calculate the transformations and update the Transform Node's transform
    float timeAngleX = -10f*(float)(Math.abs(Math.sin(Math.toRadians(elapsedTime*50))));
    float timeAngleZ = 20f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,
                                                 baseHeigth+poleHeigth+poleThickness+(topThickness-poleThickness)/2,
                                                 poleTop-poleThickness/2));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundX(timeAngleX));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(timeAngleZ));
    topSwingTransform.setTransform(m);
    topSwingTransform.update();

    // calculate and change spotlight target
    Vec3 lightTarget = new Vec3(xPosition-poleTop + timeAngleX/20, 0f, zPosition + timeAngleZ/20);
    Vec3 lightDirection = Vec3.subtract(lightTarget, bulbPosition);
    spotlight.setDirection(lightDirection);
  }

  public SGNode getRootNode() {

    // define all the components required to build the scene

    int[] poleTexture = TextureLibrary.loadTexture(gl, "textures/stone.jpg");
    int[] bulbTexture = TextureLibrary.loadTexture(gl, "textures/head.jpg");

    Mesh cube = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Mesh sphere = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shaderC = new Shader(gl, "vs_cube.txt", "fs_spotlight.txt");
    Shader shaderS = new Shader(gl, "vs_sphere.txt", "fs_sphere.txt");
    Material poleMaterial = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), 102.0f);
    Material bulbMaterial = new Material(new Vec3(0.8f, 0.8f, 0.0f), new Vec3(0.8f, 0.8f, 0.0f), new Vec3(0.8f, 0.8f, 0.0f), 102.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16, 1f, 16);

    Model pole = new Model(gl, camera, light, light2, spotlight, shaderC, poleMaterial, modelMatrix, cube, poleTexture);
    Model bulb = new Model(gl, camera, light, light2, shaderS, bulbMaterial, modelMatrix, sphere, bulbTexture);


    // define nodes

    NameNode lampRoot = new NameNode("lamp root");

    Mat4 m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(xPosition, 0, zPosition));
    m = Mat4.multiply(m, Mat4Transform.rotateAroundY(rotation));
    TransformNode moveTransform = new TransformNode("lamp move transform", m);

    NameNode baseNode = new NameNode("base");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(baseWidth, baseHeigth, baseDepth));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode baseTransform = new TransformNode("base transform", m);
        ModelNode baseShape = new ModelNode("Cube(base)", pole);
    
    NameNode pole1Node = new NameNode("pole 1");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(0, baseHeigth, 0));
      m = Mat4.multiply(m, Mat4Transform.scale(poleThickness, poleHeigth, poleThickness));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode pole1Transform = new TransformNode("pole 1 transform", m);
        ModelNode pole1Shape = new ModelNode("Cube(pole1)", pole);

    NameNode pole2Node = new NameNode("pole 2");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(0, baseHeigth+poleHeigth, poleTop/2-poleThickness/2));
      m = Mat4.multiply(m, Mat4Transform.scale(poleThickness, poleThickness, poleTop));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode pole2Transform = new TransformNode("pole 2 transform", m);
        ModelNode pole2Shape = new ModelNode("Cube(pole2)", pole);

    m = new Mat4(1);
    m = Mat4.multiply(m, Mat4Transform.translate(0,
                                                 baseHeigth+poleHeigth+poleThickness+(topThickness-poleThickness)/2,
                                                 poleTop-poleThickness/2));
    topSwingTransform = new TransformNode("top swing transform", m);
    
    NameNode topNode = new NameNode("top");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(topThickness, topHeigth, topThickness));
      m = Mat4.multiply(m, Mat4Transform.translate(0, -0.5f, 0));
      TransformNode topTransform = new TransformNode("top transform", m);
        ModelNode topShape = new ModelNode("Cube(top)", pole);

    NameNode bulbNode = new NameNode("bulb");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(0, -topHeigth, 0));
      m = Mat4.multiply(m, Mat4Transform.scale(topThickness, topThickness, topThickness));
      TransformNode bulbTransform = new TransformNode("bulb transform", m);
        ModelNode bulbShape = new ModelNode("Sphere(bulb)", bulb);

    lampRoot.addChild(moveTransform);
      moveTransform.addChild(baseNode);
        baseNode.addChild(baseTransform);
          baseTransform.addChild(baseShape);
      moveTransform.addChild(pole1Node);
        pole1Node.addChild(pole1Transform);
          pole1Transform.addChild(pole1Shape);
      moveTransform.addChild(pole2Node);
        pole2Node.addChild(pole2Transform);
          pole2Transform.addChild(pole2Shape);
      moveTransform.addChild(topSwingTransform);
        topSwingTransform.addChild(topNode);
          topNode.addChild(topTransform);
            topTransform.addChild(topShape);
        topSwingTransform.addChild(bulbNode);
          bulbNode.addChild(bulbTransform);
            bulbTransform.addChild(bulbShape);
    
    return lampRoot;
  }
}