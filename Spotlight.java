/*  The following code is adapted from
the tutorials provided in University of Sheffield's assignment for COM4503*/

import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

// subclass of Light containing the cutoff angle and the direction of the light
class Spotlight extends Light {
  private float cutOffAngle;
  private Vec3 direction;

  public Spotlight(GL3 gl, float cutOffAngle, Vec3 direction) {
    super(gl);
    this.cutOffAngle = cutOffAngle;
    this.direction = direction;
  }

  public float getCutOffAngle() {
    return cutOffAngle;
  }

  public float getCutOff() {
    return (float)(Math.cos(Math.toRadians(cutOffAngle)));
  }

  public Vec3 getDirection() {
    return direction;
  }

  public void setDirection(Vec3 direction) {
    this.direction = direction;
  }
}