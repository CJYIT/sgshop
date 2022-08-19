package com.changgou.canal;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.xpand.starter.canal.annotation.*;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/****
 * @Author:cjy
 * @Description: 实现数据库监听
 * @Date 2022/8/12 11:23
 *****/
@CanalEventListener
public class CanalDataEventListener {

    /**
     * @param eventType :当前操作的类型，增加数据
     * @param rowData   :发生变更发一行数据
     * @InsertListenPoint:监听增加 只有增加后的数据
     * rowData.getAfterColumnsList()增加、修改
     * rowData.getBeforeColumnsList()删除、修改
     */
    /*@InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {

        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {//当前这条数据的每一列数据
            System.out.println("列名：" + column.getName() + "------------变更的数据：" + column.getValue());
        }
    }

    *//**
     * @param eventType :当前操作的类型，修改数据
     * @param rowData   :发生变更发一行数据
     * @UpdateListenPoint:监听修改 rowData.getAfterColumnsList()增加、修改
     * rowData.getBeforeColumnsList()删除、修改
     *//*
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {

        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {//当前这条数据的每一列数据
            System.out.println("修改前：" + column.getName() + "------------修改前的数据：" + column.getValue());
        }
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {//当前这条数据的每一列数据
            System.out.println("修改后：" + column.getName() + "------------修改后的数据：" + column.getValue());
        }
    }

    *//**
     * @param eventType :当前操作的类型，删除数据
     * @param rowData   :发生变更发一行数据
     * @UpdateListenPoint:监听修改 rowData.getAfterColumnsList()增加、修改
     * rowData.getBeforeColumnsList()删除、修改
     *//*
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {

        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {//当前这条数据的每一列数据
            System.out.println("删除前：" + column.getName() + "------------删除前的数据：" + column.getValue());
        }
    }

    *//**
     * 自定义监听
     * @param eventType :当前操作的类型，删除数据
     * @param rowData   :发生变更发一行数据
     * @UpdateListenPoint:监听修改 rowData.getAfterColumnsList()增加、修改
     * rowData.getBeforeColumnsList()删除、修改
     *//*
    @ListenPoint(
            eventType = {CanalEntry.EventType.DELETE,CanalEntry.EventType.UPDATE},//监听类型
            schema = {"changgou_content"},//监听的数据
            table = {"tb_content"},//指定的表
            destination = "example"  //指定的实例地址
    )
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {

        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {//当前这条数据的每一列数据
            System.out.println("===自定义操作前：" + column.getName() + "------------自定义操作前的数据：" + column.getValue());
        }
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {//当前这条数据的每一列数据
            System.out.println("===自定义操作后：" + column.getName() + "------------自定义操作后的数据：" + column.getValue());
        }
    }*/


    @Autowired
    private ContentFeign contentFeign;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example", schema = "changgou_content", table = {"tb_content"}, eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.INSERT, CanalEntry.EventType.DELETE})
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //1.获取到被修改的category_id
        String categoryId = getColumnValue(eventType, rowData);

        //2.调用feign 获取数据
        Result<List<Content>> byCategory = contentFeign.findByCategory(Long.valueOf(categoryId));
        //3.存储到redis中
        List<Content> data = byCategory.getData();//List
        //4.
        stringRedisTemplate.boundValueOps("content_"+categoryId).set(JSON.toJSONString(data));
    }

    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //1.判断更改类型 如果是删除 则需要获取到before的数据
        String categoryId = "";
        if (CanalEntry.EventType.DELETE == eventType) {
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                //column.getName(列的名称   column.getValue() 列对应的值
                if (column.getName().equals("category_id")) {
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        } else {
            //2判断是 更新 新增 获取after的数据
            List<CanalEntry.Column> beforeColumnsList = rowData.getAfterColumnsList();
            for (CanalEntry.Column column : beforeColumnsList) {
                //column.getName(列的名称   column.getValue() 列对应的值
                if (column.getName().equals("category_id")) {
                    categoryId = column.getValue();
                    return categoryId;
                }
            }
        }
        //3.返回
        return categoryId;
    }
}
