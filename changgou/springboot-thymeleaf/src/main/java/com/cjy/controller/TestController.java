package com.cjy.controller;

import com.cjy.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/****
 * @Author:cjy
 * @Description: com.cjy.controller
 * @Date 2022/8/16 8:20
 * Thyemleaf 模板引擎
 *****/
@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    public String hello(Model model){
        model.addAttribute("messages","hello thymeleaf");
        //创建一个List<User>,并将List<User>存入到Model，到页面使用Thymeleaf标签显示
        //集合数据
        List<User> users = new ArrayList<User>();
        users.add(new User(1,"张三","深圳"));
        users.add(new User(2,"李四","北京"));
        users.add(new User(3,"王五","武汉"));
        model.addAttribute("users",users);

        //Map定义
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("No","123");
        dataMap.put("address","深圳");
        model.addAttribute("dataMap",dataMap);

        //日期
        model.addAttribute("now",new Date());
        //if条件
        model.addAttribute("age",22);


        return "demo1";
    }
}
