package com.test.redis.hgw;/*
 * Copyright(C) 2017 Hangzhou Differsoft Co., Ltd. All rights reserved.
 */


import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 非集群的redis缓存
 *
 * @author Liumz
 * @since 2018-06-19  15:46:46
 */
@Component
public class OmsRedis implements ICacher {
    // region 变量

    /**
     * 是否完成初始化。
     */
    private boolean isInit = false;
    /**
     * 并发控制锁对象。
     */
    private final Object __LOCK__ = new Object();
    /**
     * 表示分布式锁响应
     */
    private static final String DISLOCK_SUCCESS = "OK";
    /**
     * 表示分布式锁写入指示(即当key不存在时进行set操作；若key已经存在则不做任何操作)
     */
    private static final String DISLOCK_SET_IF_NOT_EXIST = "NX";
    /**
     * 表示将分布式锁的Key加上过期时间。
     */
    private static final String DISLOCK_SET_WITH_EXPIRE_TIME = "PX";
    /**
     * Redis连接池字典集合。
     */
    private Map<String, JedisPool> mapRedisPool = null;
    /**
     * Redis连接池DB字典集合(DB为redis的db1、db2...dbn)。
     */
    private Map<String, Integer> mapRedisPoolDB = null;

    // endregion

    // region 初始化

    /**
     * 初始化。
     */
    public void init() {
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
        this.mapRedisPool = new HashMap<String, JedisPool>(2);
        this.mapRedisPoolDB = new HashMap<String, Integer>(2);

        this.mapRedisPool.put("normal", new JedisPool(config, "47.114.128.1", 6379, 5000, "123456"));
        this.mapRedisPoolDB.put("normal", 0);
        this.isInit = true;
    }

    @Override
    public void set(String cacheKey, String value) {
        this.get().set(cacheKey, value);
    }

    @Override
    public void set(String cacheKey, String value, int seconds) {
        this.get().setex(cacheKey, seconds, value);
    }

    @Override
    public String get(String cacheKey) {
        return this.get().get(cacheKey);
    }

    @Override
    public List<String> get(String... cacheKeys) {
        return null;
    }

    @Override
    public boolean has(String cacheKey) {
        return false;
    }

    @Override
    public void delete(String... cacheKeys) {

    }

    @Override
    public Boolean isRedisException(Exception ex) {
        return null;
    }

    @Override
    public int hashSyncStrValue(String cacheKey, String hashKey, String value) {
        return 0;
    }

    @Override
    public Boolean hashRemove(String cacheKey, String... hashKeys) {
        return null;
    }

    @Override
    public String hashGetStrValue(String cacheKey, String hashKey) {
        return null;
    }

    @Override
    public Long hashCount(String cacheKey) {
        return null;
    }

    @Override
    public Set<String> hashAllKeys(String cacheKey) {
        return null;
    }

    @Override
    public Boolean hashExist(String cacheKey, String hashKey) {
        return null;
    }

    @Override
    public long sortedSetRemove(String cacheKey, double startScore, double endScore) {
        return 0;
    }

    @Override
    public <T> Boolean sortedSetSync(String cacheKey, double score, T value, boolean isRemoveByScore, int expireSeconds) {
        return null;
    }

    @Override
    public <T> Boolean sortedSetSync(String cacheKey, Map<T, Double> scoreValues, boolean isRemoveByScore, int expireSeconds) {
        return null;
    }

    @Override
    public Boolean sortedSetExist(String cacheKey, double startScore, double endScore) {
        return null;
    }

    @Override
    public <T> Set<T> sortedSetGet(String cacheKey, double startScore, double endScore, Class<T> type) {
        return null;
    }

    @Override
    public <T> Set<T> sortedSetGetByIndex(String cacheKey, long startIndex, long endIndex, Class<T> type) {
        return null;
    }

    @Override
    public long sortedSetGetLength(String cacheKey) {
        return 0;
    }

    @Override
    public long hashIncrement(String cacheKey, String hashKey, long value) {
        return 0;
    }

    @Override
    public long hashGetIncrement(String cacheKey, String hashKey) {
        return 0;
    }

    @Override
    public Map<String, Long> hashGetAllIncrement(String cacheKey) {
        return null;
    }

    @Override
    public void hashRemoveIncrement(String cacheKey, String... hashKeys) {

    }

    @Override
    public long setExpire(String cacheKey, int seconds) {
        return 0;
    }


    private Jedis get() {
        if (!this.isInit) {
            synchronized (__LOCK__) {
                if (!this.isInit) {
                    this.init();
                }
            }
        }
        Jedis jedis = this.mapRedisPool.get("normal").getResource();
        //选择redis DB(DB为redis的db1、db2...dbn)。
        jedis.select(this.mapRedisPoolDB.get("normal"));

        return jedis;
    }

    // endregion
}
