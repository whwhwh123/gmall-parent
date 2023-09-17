package com.wh.gmall.all.controller;

import com.wh.gmall.common.result.Result;
import com.wh.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class ItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;

    @GetMapping("/abc")
    public String abc(){
        return "index";
    }

    /**
     * sku详情页面
     * @param skuId
     * @param model
     * @return
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


}
