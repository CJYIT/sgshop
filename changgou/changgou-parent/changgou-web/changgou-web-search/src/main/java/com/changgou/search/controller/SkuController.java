package com.changgou.search.controller;

import com.changgou.search.feign.SkuFeign;
import com.changgou.search.pojo.SkuInfo;
import entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/****
 * @Author:cjy
 * @Description: com.changgou.search.controller
 * @Date 2022/8/16 14:07
 *****/
@Controller          //使用controller不是RestController,因为需要跳转页面
@RequestMapping(value = "/search")
@CrossOrigin
public class SkuController {

    @Autowired
    private SkuFeign skuFeign;  //注意这里的Feign使用的是search里面的

    /**
     * 实现搜索调用
     */
    @GetMapping(value = "/list")
    public String search(@RequestParam(required = false) Map<String, String> searchMap, Model model) throws Exception {
        //调用搜索微服务changgou-service-search
        Map<String, Object> resultMap = skuFeign.search(searchMap);
        model.addAttribute("result", resultMap);//将内容渲染到页面

        //计算分页
        Page<SkuInfo> pageInfo = new Page<SkuInfo>(
                Long.parseLong(resultMap.get("total").toString()),
                Integer.parseInt(resultMap.get("pageNumber").toString())+1,
                Integer.parseInt(resultMap.get("pageSize").toString()));//当前页，总记录数，每页显示多少条数据，第一个参数的long，类型，后面的是int
        model.addAttribute("pageInfo", pageInfo);//将分页信息pageInfo返回到前端页面model


        //将条件存储用于页面回显数据
        model.addAttribute("searchMap", searchMap);

        //获取上次请求地址   两url地址    2个url：不带排序 参数      url带排序参数
        String[] urls = url(searchMap);
        model.addAttribute("url",urls[0]);
        model.addAttribute("sorturl",urls[1]);

        return "search";//返回到页面
    }

    /**
     * 拼接组装用户请求的url地址
     * 获取用户每次请求的地址,页面需要在这次请求的基础上添加额外的搜索条件
     * http://localhost:18086/search/list
     * http://localhost:18086/search/list?keywords=华为
     * http://localhost:18086/search/list?keywords=华为&category=语言文字
     * 把请求的参数Map<String,String> searchMap传递过来,
     * @return
     */

    public String[] url(Map<String, String> searchMap) {
        String url = "/search/list";    //初始化地址
        String sorturl = "/search/list";//排序地址
        if (searchMap!=null && searchMap.size()>0){
            url+="?";
            sorturl+="?";
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                //key是搜索的条件对象
                String key = entry.getKey();

                //跳过分页参数
                if (key.equalsIgnoreCase("pageNum")){
                    continue;
                }
                //value是搜索的值
                String value = entry.getValue();
                url+=key+"="+value+"&";

                //排序参数跳过
                if (key.equalsIgnoreCase("sortField") || key.equalsIgnoreCase("sortRule")){
                    continue;//跳过
                }

                sorturl+=key+"="+value+"&";
            }
//            去掉最后一个&
            url=url.substring(0,url.length()-1);
            sorturl=sorturl.substring(0,sorturl.length()-1);//sorturl需要返回去，所以整个方法使用集合string[]接收
        }
//        return url;
        return new String[]{url,sorturl};
    }


}
