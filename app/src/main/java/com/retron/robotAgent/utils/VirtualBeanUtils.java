package com.retron.robotAgent.utils;

import android.content.Context;

import com.dcm360.controller.gs.controller.bean.paths_bean.UpdataVirtualObstacleBean;
import com.retron.robotAgent.task.TaskManager;

import java.util.ArrayList;
import java.util.List;

public class VirtualBeanUtils {
    private Context mContext;

    public VirtualBeanUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void updateVirtual(int type, String mapNameUuid,String mapName, String obstacle_name, List<List<UpdataVirtualObstacleBean.ObstaclesEntity.PolylinesEntity>> polylinesEntities) {
        UpdataVirtualObstacleBean updataVirtualObstacleBean = new UpdataVirtualObstacleBean();
        updataVirtualObstacleBean.setMapName(mapNameUuid);
        updataVirtualObstacleBean.setType(type);
        List<?> arrayList = new ArrayList<>();

        UpdataVirtualObstacleBean.CarpetsEntity carpetsEntity = new UpdataVirtualObstacleBean.CarpetsEntity();
        carpetsEntity.setCircles(arrayList);
        carpetsEntity.setLines(arrayList);
        carpetsEntity.setPolygons(arrayList);
        carpetsEntity.setPolylines(arrayList);
        carpetsEntity.setRectangles(arrayList);

        UpdataVirtualObstacleBean.DecelerationsEntity decelerationsEntity = new UpdataVirtualObstacleBean.DecelerationsEntity();
        decelerationsEntity.setCircles(arrayList);
        decelerationsEntity.setLines(arrayList);
        decelerationsEntity.setPolygons(arrayList);
        decelerationsEntity.setPolylines(arrayList);
        decelerationsEntity.setRectangles(arrayList);

        UpdataVirtualObstacleBean.DisplaysEntity displaysEntity = new UpdataVirtualObstacleBean.DisplaysEntity();
        displaysEntity.setCircles(arrayList);
        displaysEntity.setLines(arrayList);
        displaysEntity.setPolygons(arrayList);
        displaysEntity.setPolylines(arrayList);
        displaysEntity.setRectangles(arrayList);

        UpdataVirtualObstacleBean.SlopesEntity slopesEntity = new UpdataVirtualObstacleBean.SlopesEntity();
        slopesEntity.setCircles(arrayList);
        slopesEntity.setLines(arrayList);
        slopesEntity.setPolygons(arrayList);
        slopesEntity.setPolylines(arrayList);
        slopesEntity.setRectangles(arrayList);

        UpdataVirtualObstacleBean.ObstaclesEntity obstaclesEntity = new UpdataVirtualObstacleBean.ObstaclesEntity();
        //[{"x":104.0,"y":98.0},{"x":83.0,"y":64.0}]

        obstaclesEntity.setCircles(arrayList);
        obstaclesEntity.setLines(arrayList);
        obstaclesEntity.setPolygons(arrayList);
        obstaclesEntity.setRectangles(arrayList);
        if (polylinesEntities != null) {
            obstaclesEntity.setPolylines(polylinesEntities);
        } else {
            obstaclesEntity.setPolylines(arrayList);
        }

        updataVirtualObstacleBean.setObstacles(obstaclesEntity);
        updataVirtualObstacleBean.setCarpets(carpetsEntity);
        updataVirtualObstacleBean.setDecelerations(decelerationsEntity);
        updataVirtualObstacleBean.setDisplays(displaysEntity);
        updataVirtualObstacleBean.setSlopes(slopesEntity);
        TaskManager.getInstances(mContext).update_virtual_obstacles(updataVirtualObstacleBean, mapNameUuid, mapName, obstacle_name);
    }
}
