/*    Author: Dr. Steve Maddock
 *    Adapted by: Aniv Surana                                     */


import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class Phone {

  private GL3 gl;
  private Camera camera;
  private Light light;
  private Light light2;
  
  // define body variables
  private float xPosition, zPosition;

  private float standHeigth = 0.35f;
  private float standWidth = 2f;
  private float standDepth = 1.25f;

  private float phoneHeigth = 3.7f;
  private float phoneWidth = 1.8f;
  private float phoneDepth = 0.2f;

  public Phone(GL3 gl, Camera camera, Light light, Light light2, float xPosition, float zPosition) {
    this.gl = gl;
    this.camera = camera;
    this.light = light;
    this.light2 = light2;
    this.xPosition = xPosition;
    this.zPosition = zPosition;
  }

  public SGNode getRootNode() {

    // define all the components required to build the scene

    int[] standTexture = TextureLibrary.loadTexture(gl, "textures/plaster.jpg");
    int[] phoneTexture = TextureLibrary.loadTexture(gl, "textures/phone1.jpg");

    Mesh cube = new Mesh(gl, CubeDifferentSides.vertices.clone(), CubeDifferentSides.indices.clone());
    Shader shader = new Shader(gl, "vs_cube.txt", "fs_cube.txt");
    Material pedestal = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Material glass = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), 150.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16, 1f, 16);

    Model stand = new Model(gl, camera, light, light2, shader, pedestal, modelMatrix, cube, standTexture);
    Model phone = new Model(gl, camera, light, light2, shader, glass, modelMatrix, cube, phoneTexture);


    // define nodes

    NameNode phoneRoot = new NameNode("phone root");
    TransformNode moveTransform = new TransformNode("phone move transform", Mat4Transform.translate(xPosition, 0, zPosition));

    NameNode standNode = new NameNode("stand");
      Mat4 m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(standWidth, standHeigth, standDepth));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode standTransform = new TransformNode("stand transform", m);
        ModelNode standShape = new ModelNode("Cube(stand)", stand);
    
    NameNode phoneNode = new NameNode("phone");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(0, standHeigth, 0));
      m = Mat4.multiply(m, Mat4Transform.scale(phoneWidth, phoneHeigth, phoneDepth));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode phoneTransform = new TransformNode("phone transform", m);
        ModelNode phoneShape = new ModelNode("Cube(phone)", phone);

    phoneRoot.addChild(moveTransform);
      moveTransform.addChild(standNode);
        standNode.addChild(standTransform);
          standTransform.addChild(standShape);
      moveTransform.addChild(phoneNode);
        phoneNode.addChild(phoneTransform);
          phoneTransform.addChild(phoneShape);
    
    return phoneRoot;
  }
}