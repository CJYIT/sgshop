package com.changgou.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.search.feign *
 * @since 1.0
 * 这里的Feign去调用search微服务
 */
@FeignClient(name="search")   //指定服务名字
@RequestMapping("/search")     //调用controller的search方法
public interface SkuFeign {
    @GetMapping
    Map search(@RequestParam(required = false) Map<String,String> searchMap);//controller里面search方法
}
