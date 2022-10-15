package com.changgou.search.controller;

import com.changgou.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/****
 * @Author:cjy
 * @Description: com.changgou.search.controller
 * @Date 2022/8/13 14:10
 *****/
@RestController
@CrossOrigin
@RequestMapping(value = "/search")
public class SkuController {

    @Autowired
    private SkuService skuService;

    /**
     * 调用搜索实现
     * required = false用户输入的数据有可能为空，允许为空
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map<String,String> searchMap){
        Object pageNum = searchMap.get("pageNum");
        if(pageNum==null){//注意曾这里没有传pageNum报错
            searchMap.put("pageNum","1");//默认查询第一页
        }
        if(pageNum instanceof Integer){
            searchMap.put("pageNum",pageNum.toString());
    }
        return  skuService.search(searchMap);
    }

    /**
     * 数据导入
     * @return
     */
    @GetMapping(value = "/import")
    public Result importData(){
        skuService.importData();
        return new Result(true, StatusCode.OK,"执行成功");
    }
}
