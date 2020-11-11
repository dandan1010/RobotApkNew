package com.dcm360.controller.gs.controller.bean;

import android.widget.Checkable;
import java.io.Serializable;

public class PositionListBean
  implements Cloneable, Checkable, Serializable
{
  private double angle;
  private String createdAt;
  private int gridX;
  private int gridY;
  private int id;
  private String mapId;
  private String mapName;
  private String name;
  private boolean selected;
  private int time;
  private int type;
  private WorldPoseBean worldPose;

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public PositionListBean clone()
  {
    try
    {
      PositionListBean localPositionListBean = (PositionListBean)super.clone();
      return localPositionListBean;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localCloneNotSupportedException.printStackTrace();
    }
    return null;
  }

  public PositionListBean fixY(int paramInt)
  {
    setGridY(paramInt - getGridY());
    return this;
  }

  public double getAngle()
  {
    return this.angle;
  }

  public String getCreatedAt()
  {
    return this.createdAt;
  }

  public int getGridX()
  {
    return this.gridX;
  }

  public int getGridY()
  {
    return this.gridY;
  }

  public int getId()
  {
    return this.id;
  }

  public String getMapId()
  {
    return this.mapId;
  }

  public String getMapName()
  {
    return this.mapName;
  }

  public String getName()
  {
    return this.name;
  }

  public int getType()
  {
    return this.type;
  }

  public WorldPoseBean getWorldPose()
  {
    return this.worldPose;
  }

  public boolean isChecked()
  {
    return this.selected;
  }

  public boolean isSelected()
  {
    return this.selected;
  }

  public void selectedToggle()
  {
    if (!this.selected);
    for (int i = 1; ; i = 0)
    {
      this.selected = Boolean.parseBoolean(i+"");
      return;
    }
  }

  public void setAngle(double paramDouble)
  {
    this.angle = paramDouble;
  }

  public void setChecked(boolean paramBoolean)
  {
    this.selected = paramBoolean;
  }

  public void setCreatedAt(String paramString)
  {
    this.createdAt = paramString;
  }

  public void setGridX(int paramInt)
  {
    this.gridX = paramInt;
  }

  public void setGridY(int paramInt)
  {
    this.gridY = paramInt;
  }

  public void setId(int paramInt)
  {
    this.id = paramInt;
  }

  public void setMapId(String paramString)
  {
    this.mapId = paramString;
  }

  public void setMapName(String paramString)
  {
    this.mapName = paramString;
  }

  public void setName(String paramString)
  {
    this.name = paramString;
  }

  public void setSelected(boolean paramBoolean)
  {
    this.selected = paramBoolean;
  }

  public void setType(int paramInt)
  {
    this.type = paramInt;
  }

  public void setWorldPose(WorldPoseBean paramWorldPoseBean)
  {
    this.worldPose = paramWorldPoseBean;
  }

  public void toggle()
  {
    if (!this.selected);
    for (int i = 1; ; i = 0)
    {
      this.selected = Boolean.parseBoolean(i+"");
      return;
    }
  }
}

/* Location:           C:\Users\EDZ\Desktop\classes-dex2jar.jar
 * Qualified Name:     com.gs.gscontrol.bean.PositionListBean
 * JD-Core Version:    0.5.4
 */