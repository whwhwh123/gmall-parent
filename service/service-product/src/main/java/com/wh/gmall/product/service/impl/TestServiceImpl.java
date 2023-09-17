package com.wh.gmall.product.service.impl;

import com.wh.gmall.product.service.TestService;
import org.checkerframework.checker.units.qual.A;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void testLock(){
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(lock)){
            String value = stringRedisTemplate.opsForValue().get("sum");

            if (StringUtils.isEmpty(value))
                return;

            int sum = Integer.parseInt(value);
            stringRedisTemplate.opsForValue().set("sum", String.valueOf(++sum));

//            stringRedisTemplate.delete("lock");
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            //  将lua脚本放入DefaultRedisScript 对象中
            redisScript.setScriptText(script);
            //  设置DefaultRedisScript 这个对象的泛型
            redisScript.setResultType(Long.class);
            //  执行删除
            stringRedisTemplate.execute(redisScript, Arrays.asList("lock"),uuid);
        }
        else {
            try {
                Thread.sleep(100);
                testLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void testRedissonLock() {
        String skuId = "25";
        String lockKey = "lock:" + skuId;

        RLock rLock = redissonClient.getLock(lockKey);
        rLock.lock(10, TimeUnit.SECONDS);//十秒后自动释放

        String value = stringRedisTemplate.opsForValue().get("num");
        if(StringUtils.isEmpty(value)){
            return;
        }

        int num = Integer.parseInt(value);
        stringRedisTemplate.opsForValue().set("num", String.valueOf(++num));

        rLock.unlock();
    }

    @Override
    public String read() {
        RReadWriteLock rrLock = redissonClient.getReadWriteLock("readWriteLock");

        RLock readLock = rrLock.readLock();
        readLock.lock(10L, TimeUnit.SECONDS);

        String msg = stringRedisTemplate.opsForValue().get("msg");
//        readLock.unlock();
        return msg;
    }

    @Override
    public String write() {
        RReadWriteLock rrLock = redissonClient.getReadWriteLock("readWriteLock");

        RLock writeLock = rrLock.writeLock();
        writeLock.lock(10, TimeUnit.SECONDS);

        String msg = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set("msg", msg);
//        writeLock.unlock();
        return "成功写入" + msg;
    }
}
