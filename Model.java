/*  The following code is adapted from
the tutorials provided
I added the extra Light and Spotlight objects, and the texture mix boolean.
I included these objects in the initialiser declarations.
Those new objects added by me also get set in the shader.
*/

import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class Model {
  
  private Mesh mesh;
  private int[] textureId1; 
  private int[] textureId2; 
  private Material material;
  private Shader shader;
  private Mat4 modelMatrix;
  private Camera camera;
  private Light light;
  private Light light2;
  private Spotlight spotlight;
  private boolean changeMix;
  
  public Model(GL3 gl, Camera camera, Light light, Light light2, Spotlight spotlight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2, boolean changeMix) {
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.camera = camera;
    this.light = light;
    this.light2 = light2;
    this.spotlight = spotlight;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
    this.changeMix = changeMix;
    this.startTime = getSeconds();
  }

  public Model(GL3 gl, Camera camera, Light light, Light light2, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2, boolean changeMix) {
    this(gl, camera, light, light2, null, shader, material, modelMatrix, mesh, textureId1, textureId2, changeMix);
  }

  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2, boolean changeMix) {
    this(gl, camera, light, null, null, shader, material, modelMatrix, mesh, textureId1, textureId2, changeMix);
  }

  public Model(GL3 gl, Camera camera, Light light, Light light2, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2) {
    this(gl, camera, light, light2, null, shader, material, modelMatrix, mesh, textureId1, textureId2, false);
  }

  public Model(GL3 gl, Camera camera, Light light, Light light2, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1) {
    this(gl, camera, light, light2, null, shader, material, modelMatrix, mesh, textureId1, null, false);
  }

  public Model(GL3 gl, Camera camera, Light light, Light light2, Spotlight spotlight, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1) {
    this(gl, camera, light, light2, spotlight, shader, material, modelMatrix, mesh, textureId1, null, false);
  }

  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2) {
    this(gl, camera, light, null, null, shader, material, modelMatrix, mesh, textureId1, textureId2, false);
  }
  
  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1) {
    this(gl, camera, light, null, null, shader, material, modelMatrix, mesh, textureId1, null, false);
  }
  
  public Model(GL3 gl, Camera camera, Light light, Shader shader, Material material, Mat4 modelMatrix, Mesh mesh) {
    this(gl, camera, light, null, null, shader, material, modelMatrix, mesh, null, null, false);
  }
  
  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }
  
  public void setCamera(Camera camera) {
    this.camera = camera;
  }
  
  public void setLight(Light light) {
    this.light = light;
  }

  public void render(GL3 gl, Mat4 modelMatrix) {
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
    shader.use(gl);
    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
    
    shader.setVec3(gl, "viewPos", camera.getPosition());

    shader.setVec3(gl, "light.position", light.getPosition());
    shader.setVec3(gl, "light.ambient", light.getMaterial().getAmbient());
    shader.setVec3(gl, "light.diffuse", light.getMaterial().getDiffuse());
    shader.setVec3(gl, "light.specular", light.getMaterial().getSpecular());

    // if light 2 exists, save it's properties to the shader
    if (light2 != null) {
      shader.setVec3(gl, "light2.position", light2.getPosition());
      shader.setVec3(gl, "light2.ambient", light2.getMaterial().getAmbient());
      shader.setVec3(gl, "light2.diffuse", light2.getMaterial().getDiffuse());
      shader.setVec3(gl, "light2.specular", light2.getMaterial().getSpecular());
    }

    // if shader exists, save it's properties to the shader
    if (spotlight != null) {
      shader.setVec3(gl, "spotlight.position", spotlight.getPosition());
      shader.setVec3(gl, "spotlight.ambient", spotlight.getMaterial().getAmbient());
      shader.setVec3(gl, "spotlight.diffuse", spotlight.getMaterial().getDiffuse());
      shader.setVec3(gl, "spotlight.specular", spotlight.getMaterial().getSpecular());
      shader.setFloat(gl, "spotlight.cutOff", spotlight.getCutOff());
      shader.setVec3(gl, "spotlight.direction", spotlight.getDirection());
    }

    shader.setVec3(gl, "material.ambient", material.getAmbient());
    shader.setVec3(gl, "material.diffuse", material.getDiffuse());
    shader.setVec3(gl, "material.specular", material.getSpecular());
    shader.setFloat(gl, "material.shininess", material.getShininess());  

    if (textureId1!=null) {
      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
      gl.glActiveTexture(GL.GL_TEXTURE0);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId1[0]);
    }
    if (textureId2!=null) {
      shader.setInt(gl, "second_texture", 1);
      gl.glActiveTexture(GL.GL_TEXTURE1);
      gl.glBindTexture(GL.GL_TEXTURE_2D, textureId2[0]);
    }
    // change the amount of mix between textures as time passess
    if (changeMix) {
      double elapsedTime = getSeconds() - startTime;
      float mixAmount = 0.5f + 0.5f*(float)(Math.sin(Math.toRadians(elapsedTime*35)));
      shader.setFloat(gl, "mix_amount", mixAmount);
    }
    mesh.render(gl);
  }

  private double startTime;

  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }
  
  public void render(GL3 gl) {
    render(gl, modelMatrix);
  }
  
  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1!=null) gl.glDeleteBuffers(1, textureId1, 0);
    if (textureId2!=null) gl.glDeleteBuffers(1, textureId2, 0);
  }
  
}