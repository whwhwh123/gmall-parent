package com.wh.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    /**
     * ThreadPoolExecutor
     * 核心线程数
     * 最大线程数
     * 空闲存活时间
     * 时间单位
     * 阻塞队列
     * 线程工厂
     * 拒绝策略
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(50,   //核心线程数
                                        500,            //最大线程数
                                        30,             //空闲存活时间
                                        TimeUnit.SECONDS,//时间单位
                                        new ArrayBlockingQueue<>(10000)//阻塞队列
                                        //线程工厂-默认
                                        //拒绝策略-默认
                                        );
    }

}
