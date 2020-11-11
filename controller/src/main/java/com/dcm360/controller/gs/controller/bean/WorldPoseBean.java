package com.dcm360.controller.gs.controller.bean;

import java.io.Serializable;

public class WorldPoseBean
  implements Serializable
{
  private OrientationBean orientation;
  private WorldPhitsBean position;

  public OrientationBean getOrientation()
  {
    return this.orientation;
  }

  public WorldPhitsBean getPosition()
  {
    return this.position;
  }

  public void setOrientation(OrientationBean paramOrientationBean)
  {
    this.orientation = paramOrientationBean;
  }

  public void setPosition(WorldPhitsBean paramWorldPhitsBean)
  {
    this.position = paramWorldPhitsBean;
  }

  public static class OrientationBean
    implements Serializable
  {
    private double w;
    private int x;
    private int y;
    private double z;

    public double getW()
    {
      return this.w;
    }

    public int getX()
    {
      return this.x;
    }

    public int getY()
    {
      return this.y;
    }

    public double getZ()
    {
      return this.z;
    }

    public void setW(double paramDouble)
    {
      this.w = paramDouble;
    }

    public void setX(int paramInt)
    {
      this.x = paramInt;
    }

    public void setY(int paramInt)
    {
      this.y = paramInt;
    }

    public void setZ(double paramDouble)
    {
      this.z = paramDouble;
    }
  }
}

/* Location:           C:\Users\EDZ\Desktop\classes-dex2jar.jar
 * Qualified Name:     com.gs.gscontrol.bean.WorldPoseBean
 * JD-Core Version:    0.5.4
 */