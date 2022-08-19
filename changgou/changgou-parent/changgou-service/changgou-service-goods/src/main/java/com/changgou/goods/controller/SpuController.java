package com.changgou.goods.controller;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/spu")
@CrossOrigin
public class SpuController {

    @Autowired
    private SpuService spuService;

    /**
     *  批量上架
     * @param ids
     * @return
     */
    @PutMapping("/put/many")
    public Result putMany(@RequestBody Long[] ids){
        spuService.putMany(ids);
        return new Result(true,StatusCode.OK,"批量上架成功");
    }

    /**
     *  批量下架
     * @param ids
     * @return
     */
    @PutMapping("/pull/many")
    public Result pullMany(@RequestBody Long[] ids){
        spuService.pullMany(ids);
        return new Result(true,StatusCode.OK,"批量下架成功");
    }

    /**特别注意，这个方法在代码生成器中生成了，这里再写会报方法重复还会报错如下：
     * Error starting ApplicationContext. To display the conditions report re-run your application with 'debug' enabled.
     * 增加商品实现
     */
    /*@PostMapping("/save")
    public Result saveGoods(@RequestBody Goods goods){//接收前端的jsion数据
        spuService.saveGoods(goods);
        return new Result(true,StatusCode.OK,"增加商品成功");
    }*/

    /***
     * Spu分页条件搜索实现
     * @param spu   条件查询
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    //条件查询+分页数据
    public Result<PageInfo> findPage(@RequestBody(required = false)  Spu spu, @PathVariable  int page, @PathVariable  int size){
        //调用SpuService实现分页条件查询Spu
        PageInfo<Spu> pageInfo = spuService.findPage(spu, page, size);//分页搜索
        return new Result(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * Spu分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用SpuService实现分页查询Spu
        PageInfo<Spu> pageInfo = spuService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param spu
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<Spu>> findList(@RequestBody(required = false)  Spu spu){
        //调用SpuService实现条件查询Spu
        List<Spu> list = spuService.findList(spu);
        return new Result<List<Spu>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Long id){
        //调用SpuService实现根据主键删除
        spuService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改Spu数据
     * @param spu
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody  Spu spu,@PathVariable Long id){
        //设置主键值
        spu.setId(id);
        //调用SpuService实现修改Spu
        spuService.update(spu);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增Spu数据
     * @param spu
     * @return
     */
    @PostMapping
    public Result add(@RequestBody   Spu spu){
        //调用SpuService实现添加Spu
        spuService.add(spu);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 根据ID查询Spu数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Spu> findById(@PathVariable Long id){
        //调用SpuService实现根据主键查询Spu
        Spu spu = spuService.findById(id);
        return new Result<Spu>(true,StatusCode.OK,"查询成功",spu);
    }

    /***
     * 查询Spu全部数据
     * @return
     */
    @GetMapping
    public Result<List<Spu>> findAll(){
        //调用SpuService实现查询所有Spu
        List<Spu> list = spuService.findAll();
        return new Result<List<Spu>>(true, StatusCode.OK,"查询成功",list) ;
    }

    /**商品添加
     * 无spuid增加商品有spuid修改商品
     * Goods(SPU+SKU)增加方法详情
     */
    @PostMapping("/save")
    public Result save(@RequestBody Goods goods){
        spuService.save(goods);
        return new Result(true,StatusCode.OK,"保存商品成功",null);
    }

    /**
     * 根据SpuId查询goods信息
     * @param id
     * @return
     */
    //根据点击到的商品(SPU)的ID 获取到GOODS数据返回给页面展示
    @GetMapping("/goods/{id}")
    public Result<Goods> findGoodsById(@PathVariable(value="id") Long id){
        Goods goods = spuService.findGoodsById(id);
        return new Result<Goods>(true,StatusCode.OK,"查询goods数据成功",goods);
    }

    @PutMapping("/put/{id}")
    public Result put(@PathVariable(value="id")Long id){
        spuService.put(id);
        return new Result(true,StatusCode.OK,"上架成功");
    }

    /**
     * //审核商品 自动上架
     * @param id  spu的ID
     * @return
     */
    @PutMapping("/audit/{id}")
    public Result auditSpu(@PathVariable(value="id")Long id){
        spuService.auditSpu(id);
        return new Result(true,StatusCode.OK,"审核通过");
    }

    @PutMapping("/pull/{id}")
    public Result pullSpu(@PathVariable(value="id")Long id){
        spuService.pullSpu(id);
        return new Result(true,StatusCode.OK,"下架成功");
    }

    /**
     * 逻辑删除
     * @param id
     * @return
     */
    @DeleteMapping("/logic/delete/{id}")
    public Result logicDeleteSpu(@PathVariable(value="id")Long id){
        spuService.logicDeleteSpu(id);
        return new Result(true,StatusCode.OK,"逻辑删除成功");
    }

    @PutMapping("/restore/{id}")
    public Result restore(@PathVariable(name="id")Long id){
        spuService.restoreSpu(id);
        return new Result(true,StatusCode.OK,"还原成功");
    }

}
