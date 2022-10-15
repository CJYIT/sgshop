package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.goods.feign *
 * @since 1.0
 */
@FeignClient(value="goods")//name属性表示具体为哪个服务配置
@RequestMapping("/sku") //goods服务提供者Controller中方法的@RequestMapping路径映射要和Feign接口中方法的路径映射一样
public interface SkuFeign {
    /**
     * 查询符合条件的状态的SKU的列表
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    public Result<List<Sku>> findByStatus(@PathVariable(name="status") String status);

    /**
     * 查询sku全部数据
     */
    @GetMapping
    Result<List<Sku>> findAll();

    /**
     * 根据条件搜索的SKU的列表
     * @param sku
     * @return
     */
//    根据SpuID查询Sku集合
    @PostMapping(value = "/search" )
    public Result<List<Sku>> findList(@RequestBody(required = false) Sku sku);
}
