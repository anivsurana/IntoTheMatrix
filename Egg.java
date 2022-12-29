/*    Author: Aniv Surana                                          */

import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class Egg {

  private GL3 gl;
  private Camera camera;
  private Light light;
  private Light light2;
  
  // define body variables
  private float xPosition, zPosition;

  private float standHeigth = 0.35f;
  private float standWidth = 1.5f;
  private float standDepth = 1.5f;

  private float eggHeigth = 2.25f;
  private float eggRadius = 1.4f;

  public Egg(GL3 gl, Camera camera, Light light, Light light2, float xPosition, float zPosition) {
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
    int[] eggTexture = TextureLibrary.loadTexture(gl, "textures/egg.jpg");
    int[] eggSpecular = TextureLibrary.loadTexture(gl, "textures/egg_specular.jpg");

    Mesh cube = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Mesh sphere = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Shader shaderC = new Shader(gl, "vs_cube.txt", "fs_cube.txt");
    Shader shaderS = new Shader(gl, "vs_sphere.txt", "fs_sphere.txt");
    Material pedestal = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Material eggMaterial = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.8f, 0.8f), 150.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16, 1f, 16);

    Model stand = new Model(gl, camera, light, light2, shaderC, pedestal, modelMatrix, cube, standTexture);
    Model egg = new Model(gl, camera, light, light2, shaderS, eggMaterial, modelMatrix, sphere, eggTexture, eggSpecular);


    // define nodes

    NameNode eggRoot = new NameNode("egg root");
    TransformNode moveTransform = new TransformNode("egg move transform", Mat4Transform.translate(xPosition, 0, zPosition));

    NameNode standNode = new NameNode("stand");
      Mat4 m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.scale(standWidth, standHeigth, standDepth));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode standTransform = new TransformNode("stand transform", m);
        ModelNode standShape = new ModelNode("Cube(stand)", stand);
    
    NameNode eggNode = new NameNode("egg");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(0, standHeigth, 0));
      m = Mat4.multiply(m, Mat4Transform.scale(eggRadius, eggHeigth, eggRadius));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode eggTransform = new TransformNode("egg transform", m);
        ModelNode eggShape = new ModelNode("Sphere(egg)", egg);

    eggRoot.addChild(moveTransform);
      moveTransform.addChild(standNode);
        standNode.addChild(standTransform);
          standTransform.addChild(standShape);
      moveTransform.addChild(eggNode);
        eggNode.addChild(eggTransform);
          eggTransform.addChild(eggShape);
    
    return eggRoot;
  }
}