package com.test.redis.hgw;


import com.test.distributedlock.SimpleDistributedLockInterface;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;

@Component
public class SimpleDistributedLock implements SimpleDistributedLockInterface {

    private HashMap<String, JedisPool> poolMap = null;

    private static Object lock = new Object();

    private static Boolean isInit = false;

    private void init() {
        //Redis连接池配置
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大连接数
        config.setMaxTotal(100);
        // 最大空闲连接数
        config.setMaxIdle(20);
        // 最小空闲连接数
        config.setMinIdle(8);
        // 获取连接时的最大等待毫秒数,小于零:阻塞不确定的时间,默认-1
        config.setMaxWaitMillis(10000);
        // 在获取连接的时候检查有效性, 默认false
        config.setTestOnBorrow(true);
        // 在归还给pool时，是否提前进行validate操作
        config.setTestOnReturn(true);
        // Idle时进行连接扫描
        config.setTestWhileIdle(true);
        // 释放连接的扫描间隔（毫秒）
        config.setTimeBetweenEvictionRunsMillis(30000);
        // 每次释放连接的最大数目
        config.setNumTestsPerEvictionRun(10);
        // 连接最小空闲时间
        config.setMinEvictableIdleTimeMillis(60000);
        //初始化Redis连接池。
        poolMap = new HashMap<String, JedisPool>(2);

        JedisPool pool = new JedisPool(config, "47.114.128.1", 6379, 5000, "123456",3);

        poolMap.put("gzr", pool);

        isInit = true;
    }

    @Override
    public boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
        String result = this.get().set(lockKey, requestId, "NX", "PX", expireTime);
        if ("OK".equals(result)) {
            return true;
        }
        return false;
    }

    private Jedis get() {
        if (!isInit) {
            synchronized (lock) {
                this.init();
            }
        }
        Jedis jedis = this.poolMap.get("gzr").getResource();
        return jedis;
    }
}
