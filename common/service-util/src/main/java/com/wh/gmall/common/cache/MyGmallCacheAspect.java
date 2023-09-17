package com.wh.gmall.common.cache;

import com.alibaba.fastjson.JSON;
import com.wh.gmall.common.constant.RedisConst;
import javassist.compiler.ProceedHandler;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class MyGmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
/*
         业务逻辑！
         1. 必须先知道这个注解在哪些方法 || 必须要获取到方法上的注解
         2. 获取到注解上的前缀
         3. 必须要组成一个缓存的key！
         4. 可以通过这个key 获取缓存的数据
            true:
                直接返回！
            false:
                分布式锁业务逻辑！
         */
    @SneakyThrows // ?
    @Around("@annotation(com.wh.gmall.common.cache.MyGmallCache)")
    public Object gmallCacheAspectMethod(ProceedingJoinPoint joinPoint){
        Object obj = new Object();
        //获取方法签名和redis前缀
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        String prefix = methodSignature.getMethod().getAnnotation(MyGmallCache.class).prefix();
        String key = prefix + Arrays.asList(joinPoint.getArgs()).toString();

        //从redis中获取数据
        obj = getRedisData(key, methodSignature);

        //获取到了直接返回
        if (obj != null) {
            return obj;
        } else {
            //拿锁尝试加锁
            RLock lock = redissonClient.getLock(prefix + ":lock");
            boolean result = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1,
                                            RedisConst.SKULOCK_EXPIRE_PX2,
                                            TimeUnit.SECONDS);
            if (result){
                //拿到锁，从数据库获取数据
                obj = joinPoint.proceed(joinPoint.getArgs());
                if(obj == null){
                    //没获取到就返回空对象
                    //Object o = new Object();
                    Object o = ((MethodSignature) joinPoint.getSignature()).getReturnType().newInstance();

                    redisTemplate.opsForValue().set(key, JSON.toJSONString(o),
                                                    RedisConst.SKUKEY_TEMPORARY_TIMEOUT,
                                                    TimeUnit.SECONDS);
                    lock.unlock();
                    return o;
                }
                redisTemplate.opsForValue().set(key, JSON.toJSONString(obj), RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                lock.unlock();
                return obj;
            } else {
                //没有拿到锁
                try {
                    Thread.sleep(100);
                    return gmallCacheAspectMethod(joinPoint);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //兜底
        return joinPoint.proceed(joinPoint.getArgs());
    }

    //从缓存中获取数据
    private Object getRedisData(String key, MethodSignature methodSignature) {
        String json = (String)redisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(json)){
            return JSON.parseObject(json, methodSignature.getReturnType());
        }
        return null;
    }


}
