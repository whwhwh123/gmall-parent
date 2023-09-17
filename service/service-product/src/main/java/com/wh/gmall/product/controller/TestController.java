package com.wh.gmall.product.controller;

import com.wh.gmall.common.result.Result;
import com.wh.gmall.product.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/product/test")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/testLock")
    public Result<Object> testLock(){
        testService.testLock();
        return Result.ok();
    }

    @GetMapping("/testRedissonLock")
    public Result<Object> testRedissonLock(){
        testService.testRedissonLock();
        return Result.ok();
    }

    @GetMapping("/read")
    public Result<Object> read(){
        testService.read();
        return Result.ok();
    }

    @GetMapping("/write")
    public Result<Object> write(){
        testService.write();
        return Result.ok();
    }

}
