package com.uslam.service;

import com.dcm360.controller.gs.controller.bean.PositionListBean;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotPositions;
import com.dcm360.controller.gs.controller.bean.data_bean.RobotWorkStatus;
import com.dcm360.controller.gs.controller.bean.map_bean.RobotMap;
import com.dcm360.controller.robot_interface.bean.Status;
import com.uslam.bean.MapListBean;
import com.uslam.bean.MoveBean;
import com.uslam.bean.NavigationStatus;
import com.uslam.bean.PointBean;
import com.uslam.bean.Relocalization;
import com.uslam.bean.RobotStatus;
import com.uslam.bean.TargetPointBean;

import org.json.JSONObject;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UsLamControllerService {



    /**
     *  连接机器人[GET] http://{{base_url}}/robot/connect/
     */
    @GET("/robot/connect")
    Call<RobotStatus> robot_connect(@Header("access-token") String token);

    /**
     * 和机器人断开连接 [DELETE] http://{{base_url}}/robot/connect/
     */
    @DELETE("/robot/connect")
    Call<RobotStatus> dis_connect();

    /**
     * 获得机器人状态 [GET] http://{{base_url}}/robot/status/
     */
    @GET("/robot/status")
    Call<RobotStatus> robot_status();

    /**
     * 控制机器人行走 [POST] http://{{base_url}}/robot/control?simple=true
     */
    @POST("/robot/control?simple=true")
    Call<Status> robot_move(@Body MoveBean moveBean);


    /**
     * 重定位[POST] http://{{base_Url}}/robot/relocalization/
     */
    @POST("/robot/relocalization")
    Call<Status> relocalization(@Query("x")float x, @Query("y")float y, @Query("theta")float theta);

    /**
     * 获得机器人状态 [GET] http://{{base_url}}/robot/status/?relocalization=true
     */
    @GET("/robot/status")
    Call<Status> relocalization_status(@Query("relocalization") boolean relocalization);

    /**
     * 取消重定位[DELETE] http://{{base_Url}}/robot/relocalization/
     */
    @DELETE("/robot/relocalization")
    Call<Status> stop_relocalization();

    /**
     * 查询重定位[GET] http://{{base_Url}}/robot/relocalization/
     */
    @GET("/robot/relocalization")
    Call<Relocalization> search_relocalization();

    /**
     * 设置目标点[POST] http://{{base_Url}}/robot/navigation/
     */
    @POST("/robot/navigation")
    Call<Status> navigation(@Body TargetPointBean targetPointBean);

    /**
     * [GET] http://{{base_url}}/robot/status/?navigation=true机器人状态
     */
    @GET("/robot/status/?navigation=true")
    Call<Status> navigation_status();

    /**
     * 取消导航[DELETE] http://{{base_Url}}/robot/navigation/
     */
    @DELETE("/robot/navigation")
    Call<Status> stop_navigation();

    /**
     * 查询导航状态[GET] http://{{base_Url}}/robot/navigation/
     */
    @GET("/robot/navigation")
    Call<NavigationStatus> search_navigation();

    /**
     * 设置流畅模式[UPDATE] http://{{base_Url}}/robot/navigation/
     */
//    @UPDATE("/robot/status/")
    Call<Status> update_navigation();

    /**
     * 获得地图列表[GET] http://{{base_Url}}/robot/map_list
     */
    @GET("/robot/map_list")
    Call<MapListBean> getMapList();

    /**
     * 检查地图名称重复性[POST] http://{{base_Url}}/robot/map_list
     */
    @POST("/robot/status")
    Call<Status> check_mapName(@Query("map_name") String map_name);

    /**
     * 获得地图[GET] http://{{base_Url}}/robot/map/?map_name="map_name"&png_map="boolean"&umap="boolean"
     */
    @GET("/robot/map")
    Call<ResponseBody> get_map(@Query("map_name") String map_name,
                               @Query("png_map") boolean png_map,
                               @Query("umap") boolean umap);

    /**
     * 导出地图[GET] http://{{base_Url}}/robot/map/?map_name="map_name"&archive=true
     */
    @GET("/robot/status")
    Call<Status> export_map(@Query("map_name") String map_name,
                            @Query("archive") boolean archive);

    /**
     * 更新地图[UPDATE] http://{{base_Url}}/robot/map/?map_name="map_name"
     */
//    @UPDATE("/robot/map")
    Call<Status> update_map(@Query("map_name") String map_name, @Query("umap") JSONObject umap);

    /**
     * 删除地图[DELETE] http://{{base_Url}}/robot/map/?map_name="map_name"
     */
    @DELETE("/robot/map")
    Call<Status> delete_map(@Query("map_name") String map_name);

    /**
     * 导入地图[POST] http://{{base_Url}}/robot/map/
     */
    @POST("/robot/map")
    Call<Status> import_map(@Query("import_map_force") boolean import_map_force,
                            @Query("map_name") String map_name,
                            @Query("umap") JSONObject umap);

    /**
     * 开始构图[POST] http://{{base_Url}}/robot/mapping/
     */
    @POST("/robot/mapping")
    Call<Status> start_scan_map();

    /**
     * 结束构图[DELETE] http://{{base_Url}}/robot/mapping?map_name="map_name"
     */
    @DELETE("/robot/mapping")
    Call<Status> stop_scan_map(@Query("map_name") String map_name,
                               @Query("saveMap") boolean saveMap,
                               @Query("save_map_force") boolean save_map_force);

    /**
     * 获取构图状态[GET] http://{{base_Url}}/robot/mapping/
     */
    @GET("/robot/mapping")
    Call<RobotWorkStatus> get_scan_map();

    /**
     * 增量式构图/扩展构图[UPDATE] http://{{base_Url}}/robot/mapping/
     */
//    @UPDATE("/robot/mapping")
    Call<Status> develop_map();

    /**
     * 导出地图压缩文件[GET] http://{{base_Url}}/robot/download?map_name="map_name"
     */
    @GET("/robot/download")
    Call<ResponseBody> export_mapZip(@Query("map_name") String map_name);

    /**
     * 导入地图压缩文件[POST] http://{{base_Url}}/robot/download?cover_force=true
     */
    @POST("/robot/download")
    Call<Status> import_mapZip(@Query("cover_force") boolean cover_force, @Body RequestBody file);

    /**
     * 本地地图.tar.gz压缩文件导入[POST] http://{{base_Url}}/robot/download?cover_force=true&@@PATH=/home/cruiser/ftpDownload/map/mapname.tar.gz
     */
    @POST("/robot/download")
    Call<Status> import_local_mapZip(@Query("cover_force") boolean cover_force,
                                     @Query("PATH") String path);

    /**
     * 添加目标点[POST] http://{{base_Url}}/robot/point?
     */
    @POST("/robot/point")
    Call<Status> add_point(@Body PositionListBean positionListBean);

    /**
     * 删除目标点[DELETE] http://{{base_Url}}/robot/point?
     */
    @DELETE("/robot/point")
    Call<Status> delete_point(@Body PositionListBean positionListBean);

    /**
     * 编辑目标点[UPDATE] http://{{base_Url}}/robot/point?
     */
//    @UPDATE("/robot/point")
    Call<Status> update_point(@Body PositionListBean positionListBean);

    /**
     * 获取所有目标点[GET] http://{{base_Url}}/robot/point?
     */
    @GET("/robot/point")
    Call<RobotPositions> get_all_points();

    /**
     * 获取自主建图状态[GET] http://{{base_Url}}/robot/auto_mapping?status="boolean"
     */
    @GET("/robot/auto_mapping")
    Call<Status> get_auto_mapping(@Query("status") boolean status);

    /**
     * 控制自主构图[POST] http://{{base_Url}}/robot/auto_mapping
     */
    @POST("/robot/auto_mapping")
    Call<Status> set_auto_mapping(@Query("distance") double distance,
                                  @Query("area") double area,
                                  @Query("fov") int fov);

    /**
     * 结束构图[DELETE] http://{{base_Url}}/robot/auto_mapping
     */
    @DELETE("/robot/auto_mapping")
    Call<Status> stop_auto_mapping(@Query("map_name") String map_name,
                                   @Query("saveMap") boolean saveMap,
                                   @Query("save_map_force") boolean save_map_force);


}
