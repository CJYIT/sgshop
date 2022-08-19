package com.changgou.search.service;

import java.util.Map;

/****
 * @Author:cjy
 * @Description: com.changgou.search.service
 * @Date 2022/8/13 12:44
 *****/
public interface SkuService {

    /**
     * 条件搜索
     * @param 入参 searchMap
     * @return    Map
     */
    Map<String,Object> search(Map<String,String> searchMap);

    /**我写的接口，换会教程的名字了
     * 导入数据库到索引中
     */
    void importData();
//    void  importEs();
}
