package com.wh.gmall.product;

import com.wh.gmall.common.constant.RedisConst;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.wh.gmall")
@EnableDiscoveryClient
public class ServiceProductApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication.class, args);
    }

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void run(String... args) throws Exception {
        RBloomFilter<Object> rBloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER);
        rBloomFilter.tryInit(100000, 0.01);
    }
}
