/*    Author: Aniv Surana - University of Texas at Arlington                                      */


import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

public class Room {
    
  private float width, depth, height;
  private GL3 gl;
  private Camera camera;
  private Light light;
  private Light light2;
  private Spotlight spotlight;

  private float windowFrameThickness = 0.2f;

  private NameNode roomNode = new NameNode("Room");

  public Room(GL3 gl, Camera camera, Light light, Light light2, Spotlight spotlight, float width, float depth, float height) {
    this.gl = gl;
    this.camera = camera;
    this.light = light;
    this.light2 = light2;
    this.spotlight = spotlight;
    this.width = width;
    this.depth = depth;
    this.height = height;
  }

  public SGNode getRootNode() {

    // define all the components required to build the scene

    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/floor2.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/wall_door.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/plaster.jpg");
    int[] textureDay = TextureLibrary.loadTexture(gl, "textures/day.jpg");
    int[] textureNight = TextureLibrary.loadTexture(gl, "textures/night.jpg");

    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Mesh cube = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "vs_tt.txt", "fs_tt_spotlight.txt");
    Shader shaderCube = new Shader(gl, "vs_cube.txt", "fs_cube.txt");
    Shader outsideShader = new Shader(gl, "vs_tt.txt", "fs_tt_mix.txt");
    Material wood = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    Material wall = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), 10.0f);
    Material city = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.0f, 0.0f, 0.0f), 1.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16,1f,16);
    Model floor = new Model(gl, camera, light, light2, spotlight, shader, wood, modelMatrix, mesh, textureId0);
    Model wall1 = new Model(gl, camera, light, light2, spotlight, shader, wall, modelMatrix, mesh, textureId1);
    Model wall2 = new Model(gl, camera, light, light2, spotlight, shader, wall, modelMatrix, mesh, textureId2);
    Model sky = new Model(gl, camera, light, light2, outsideShader, wall, modelMatrix, mesh, textureDay, textureNight, true);
    Model window = new Model(gl, camera, light, light2, shaderCube, wall, modelMatrix, cube, textureId0);

    NameNode floorNode = new NameNode("Floor");
      Mat4 m = Mat4Transform.scale(width,1.0f,depth);
      TransformNode floorTransform = new TransformNode("floor transform", m);
        ModelNode floorShape = new ModelNode("Square(floor)", floor);
    
    NameNode wall1Node = new NameNode("Wall 1");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0, -depth/2));
      m = Mat4.multiply(m, Mat4Transform.scale(width, height, 1.0f));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundX(90));
      TransformNode wall1Transform = new TransformNode("wall 1 transform", m);
        ModelNode wall1Shape = new ModelNode("Wall(1)", wall1);

    NameNode wall2Node = new NameNode("Wall 2");
    NameNode wall2NodeSW = new NameNode("Wall 2 SW");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(-width/2, 0, depth/3));
      m = Mat4.multiply(m, Mat4Transform.scale(1.0f, height/3, depth/3));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundY(90));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundX(90));
      TransformNode wall2TransformSW = new TransformNode("wall 2 transform SW", m);
        ModelNode wall2ShapeSW = new ModelNode("Wall(2) SW", wall2);

    NameNode wall2NodeW = new NameNode("Wall 2 W");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(-width/2, height/3, depth/3));
      m = Mat4.multiply(m, Mat4Transform.scale(1.0f, height/3, depth/3));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundY(90));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundX(90));
      TransformNode wall2TransformW = new TransformNode("wall 2 transform W", m); 
        ModelNode wall2ShapeW = new ModelNode("Wall(2) W", wall2);

    NameNode wall2NodeNW = new NameNode("Wall 2 NW");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(-width/2, 2*height/3, depth/3));
      m = Mat4.multiply(m, Mat4Transform.scale(1.0f, height/3, depth/3));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundY(90));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundX(90));
      TransformNode wall2TransformNW = new TransformNode("wall 2 transform NW", m); 
        ModelNode wall2ShapeNW = new ModelNode("Wall(2) NW", wall2);

    NameNode wall2NodeN = new NameNode("Wall 2 N");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(-width/2, 4*height/5, 0));
      m = Mat4.multiply(m, Mat4Transform.scale(1.0f, height/5, depth/3));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundY(90));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundX(90));
      TransformNode wall2TransformN = new TransformNode("wall 2 transform N", m); 
        ModelNode wall2ShapeN = new ModelNode("Wall(2) N", wall2);

    NameNode wall2NodeNE = new NameNode("Wall 2 NE");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(-width/2, 2*height/3, -depth/3));
      m = Mat4.multiply(m, Mat4Transform.scale(1.0f, height/3, depth/3));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundY(90));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundX(90));
      TransformNode wall2TransformNE = new TransformNode("wall 2 transform NE", m); 
        ModelNode wall2ShapeNE = new ModelNode("Wall(2) NE", wall2);

    NameNode wall2NodeE = new NameNode("Wall 2 E");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(-width/2, height/3, -depth/3));
      m = Mat4.multiply(m, Mat4Transform.scale(1.0f, height/3, depth/3));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundY(90));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundX(90));
      TransformNode wall2TransformE = new TransformNode("wall 2 transform E", m); 
        ModelNode wall2ShapeE = new ModelNode("Wall(2) E", wall2);

    NameNode wall2NodeSE = new NameNode("Wall 2 SE");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(-width/2, 0, -depth/3));
      m = Mat4.multiply(m, Mat4Transform.scale(1.0f, height/3, depth/3));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundY(90));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundX(90));
      TransformNode wall2TransformSE = new TransformNode("wall 2 transform SE", m); 
        ModelNode wall2ShapeSE = new ModelNode("Wall(2) SE", wall2);

    NameNode wall2NodeS = new NameNode("Wall 2 S");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(-width/2, 0, 0));
      m = Mat4.multiply(m, Mat4Transform.scale(1.0f, height/5, depth/3));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundY(90));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundX(90));
      TransformNode wall2TransformS = new TransformNode("wall 2 transform S", m); 
        ModelNode wall2ShapeS = new ModelNode("Wall(2) S", wall2);

    NameNode windowNode = new NameNode("Window");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(-width/2, 4*height/5, 0));
      m = Mat4.multiply(m, Mat4Transform.scale(windowFrameThickness, windowFrameThickness, depth/3 + windowFrameThickness));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode windowTopTransform = new TransformNode("Window top part transform", m);
        ModelNode windowTopShape = new ModelNode("Cube(windowTop)", window);
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(-width/2, height/5, 0));
      m = Mat4.multiply(m, Mat4Transform.scale(windowFrameThickness, windowFrameThickness, depth/3 + windowFrameThickness));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode windowBottomTransform = new TransformNode("Window bottom part transform", m);
        ModelNode windowBottomShape = new ModelNode("Cube(windowBottom)", window);
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(-width/2, height/5, depth/6));
      m = Mat4.multiply(m, Mat4Transform.scale(windowFrameThickness, 3*height/5, windowFrameThickness));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode windowLeftTransform = new TransformNode("Window left part transform", m);
        ModelNode windowLeftShape = new ModelNode("Cube(windowLeft)", window);
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(-width/2, height/5, -depth/6));
      m = Mat4.multiply(m, Mat4Transform.scale(windowFrameThickness, 3*height/5, windowFrameThickness));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));
      TransformNode windowRightTransform = new TransformNode("Window right part transform", m);
        ModelNode windowRightShape = new ModelNode("Cube(windowRight)", window);
    
    NameNode skyNode = new NameNode("Sky");
      m = new Mat4(1);
      m = Mat4.multiply(m, Mat4Transform.translate(-width, 0, 0));
      m = Mat4.multiply(m, Mat4Transform.scale(1.0f, height*3, depth*3));
      m = Mat4.multiply(m, Mat4Transform.translate(0, 0.25f, 0));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundY(90));
      m = Mat4.multiply(m, Mat4Transform.rotateAroundX(90));
      TransformNode skyTransform = new TransformNode("sky transform", m); 
        ModelNode skyShape = new ModelNode("Sky()", sky);
    
    
    roomNode.addChild(floorNode);
      floorNode.addChild(floorTransform);
        floorTransform.addChild(floorShape);
    roomNode.addChild(wall1Node);
      wall1Node.addChild(wall1Transform);
        wall1Transform.addChild(wall1Shape);
    roomNode.addChild(wall2Node);
      wall2Node.addChild(wall2NodeSW);
        wall2NodeSW.addChild(wall2TransformSW);
          wall2TransformSW.addChild(wall2ShapeSW);
      wall2Node.addChild(wall2NodeW);
        wall2NodeW.addChild(wall2TransformW);
          wall2TransformW.addChild(wall2ShapeW);
      wall2Node.addChild(wall2NodeNW);
        wall2NodeNW.addChild(wall2TransformNW);
          wall2TransformNW.addChild(wall2ShapeNW);
      wall2Node.addChild(wall2NodeN);
        wall2NodeN.addChild(wall2TransformN);
          wall2TransformN.addChild(wall2ShapeN);
      wall2Node.addChild(wall2NodeNE);
        wall2NodeNE.addChild(wall2TransformNE);
          wall2TransformNE.addChild(wall2ShapeNE);
      wall2Node.addChild(wall2NodeE);
        wall2NodeE.addChild(wall2TransformE);
          wall2TransformE.addChild(wall2ShapeE);
      wall2Node.addChild(wall2NodeSE);
        wall2NodeSE.addChild(wall2TransformSE);
          wall2TransformSE.addChild(wall2ShapeSE);
      wall2Node.addChild(wall2NodeS);
        wall2NodeS.addChild(wall2TransformS);
          wall2TransformS.addChild(wall2ShapeS);
    roomNode.addChild(windowNode);
      windowNode.addChild(windowTopTransform);
        windowTopTransform.addChild(windowTopShape);
      windowNode.addChild(windowBottomTransform);
        windowBottomTransform.addChild(windowBottomShape);
      windowNode.addChild(windowLeftTransform);
        windowLeftTransform.addChild(windowLeftShape);
      windowNode.addChild(windowRightTransform);
        windowRightTransform.addChild(windowRightShape);
    roomNode.addChild(skyNode);
      skyNode.addChild(skyTransform);
        skyTransform.addChild(skyShape);

    return roomNode;
  }
}