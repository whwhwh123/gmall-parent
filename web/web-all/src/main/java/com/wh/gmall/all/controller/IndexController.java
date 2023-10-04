package com.wh.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.wh.gmall.common.result.Result;
import com.wh.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private TemplateEngine templateEngine;

    @GetMapping({"/","index.html"})
    public String index(HttpServletRequest request){
        // 获取首页分类数据
        List<JSONObject> result = productFeignClient.getBaseCategoryList();
        request.setAttribute("list",result);
        return "index/index";
    }


    @GetMapping("/createIndex")
    @ResponseBody
    public Result<Void> createIndex(){
        //  获取后台存储的数据
        List<JSONObject> baseCategoryList = productFeignClient.getBaseCategoryList();
        //  设置模板显示的内容
        Context context = new Context();
        context.setVariable("list",baseCategoryList);

        //  定义文件输入位置
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("F:\\Codes\\Vue\\gmall_static\\index.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //  调用process();方法创建模板
        templateEngine.process("index/index",context, fileWriter);
        return Result.ok();
    }


}
