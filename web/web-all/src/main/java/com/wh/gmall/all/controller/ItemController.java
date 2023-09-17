package com.wh.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.wh.gmall.common.result.Result;
import com.wh.gmall.item.client.ItemFeignClient;
import com.wh.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * sku详情页面
     * @param skuId
     * @param model
     */
    @GetMapping("/{skuId}.html")
    public String getItem(@PathVariable Long skuId, Model model){
        System.out.println("into controller");
        // 通过skuId 查询skuInfo
        Result<Map<String, Object>> result = itemFeignClient.getItem(skuId);
        System.out.println(result.getData());
        model.addAllAttributes(result.getData());
        return "item/item";
    }

    @GetMapping("/createIndex")
    @ResponseBody
    public Result createIndex(){
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
