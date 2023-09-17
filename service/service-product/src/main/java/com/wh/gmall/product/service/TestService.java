package com.wh.gmall.product.service;

public interface TestService {
    void testLock();
    void testRedissonLock();
    String read();
    String write();
}
