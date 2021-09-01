package com.test.redis.hgw;

/*
 * Copyright(C) 2017 Hangzhou Differsoft Co., Ltd. All rights reserved.
 */

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 缓存接口
 *
 * @author 洪光旺
 * @since 2017年12月11日 下午2:00:04
 */
public interface ICacher {

    /**
     * 设置缓存
     *
     * @param cacheKey 缓存键
     * @param value    缓存值
     * @
     */
    void set(String cacheKey, String value);

    /**
     * 设置缓存
     *
     * @param cacheKey 缓存键
     * @param value    缓存值
     * @param seconds  过期时间(秒)
     * @
     */
    void set(String cacheKey, String value, int seconds);

    /**
     * 获取缓存
     *
     * @param cacheKey 缓存键
     * @return 缓存值 @
     */
    String get(String cacheKey);

    /**
     * 批量获取缓存(如果key不存在会返回null项)。
     *
     * @param cacheKeys 缓存键集
     * @return 缓存值集
     */
    List<String> get(String... cacheKeys);

    /**
     * 判断是否具有某个缓存键
     *
     * @param cacheKey 缓存键
     * @return 是否具有某个缓存键 @
     */
    boolean has(String cacheKey);

    /**
     * 删除缓存
     *
     * @param cacheKeys 缓存键集合
     */
    void delete(String... cacheKeys);

    /**
     * 是否为请求Redis服务异常
     *
     * @param ex 异常对象
     * @return 是否Redis异常
     */
    Boolean isRedisException(Exception ex);

    /**
     * 缓存同步字符串。
     *
     * @param cacheKey 缓存键
     * @param hashKey  Hash键
     * @param value    缓存值
     * @return 同步成功数量
     */
    int hashSyncStrValue(String cacheKey, String hashKey, String value);

    /**
     * 批量移除Hash缓存多个项。
     *
     * @param cacheKey 缓存键
     * @param hashKeys Hash键集合
     * @return 移除是否成功
     */
    Boolean hashRemove(String cacheKey, String... hashKeys);

    /**
     * 获取Hash缓存指定键值
     *
     * @param cacheKey 缓存键
     * @param hashKey  Hash键
     * @return 值
     */
    String hashGetStrValue(String cacheKey, String hashKey);

    /**
     * 获取Hash集合数量
     *
     * @param cacheKey 缓存键
     * @return 值集合数量
     */
    Long hashCount(String cacheKey);

    /**
     * 获取Hash集合的键集合
     *
     * @param cacheKey 缓存键
     * @return 键集合
     */
    Set<String> hashAllKeys(String cacheKey);

    /**
     * 判断Hash键是否存在
     *
     * @param cacheKey 缓存键
     * @param hashKey  hash键
     * @return 键集合
     */
    Boolean hashExist(String cacheKey, String hashKey);

    /**
     * SortedSet缓存删除
     *
     * @param cacheKey   缓存键
     * @param startScore score(超过17位会丢失精度，请慎重使用)
     * @param endScore   score(超过17位会丢失精度，请慎重使用)
     * @return 删除的个数
     */
    long sortedSetRemove(String cacheKey, double startScore, double endScore);

    /**
     * SortedSet缓存同步(SortedSet数据值重复会覆盖)。。
     *
     * @param cacheKey        缓存键
     * @param score           score(超过17位会丢失精度，请慎重使用)
     * @param value           值(唯一)
     * @param isRemoveByScore 是否根据Score删除旧数据
     * @param expireSeconds   过期时间(秒)，大于0则设置过期时间；否则为不过期
     * @return 同步是否成功
     */
    <T> Boolean sortedSetSync(String cacheKey, double score, T value, boolean isRemoveByScore, int expireSeconds);

    /**
     * 批量SortedSet缓存同步(SortedSet数据值重复会覆盖)。
     *
     * @param cacheKey        缓存键
     * @param scoreValues     值和score的键值对
     * @param isRemoveByScore 是否根据Score删除旧数据
     * @param expireSeconds   过期时间(秒)，大于0则设置过期时间；否则为不过期
     * @return 同步是否成功
     */
    <T> Boolean sortedSetSync(String cacheKey, Map<T, Double> scoreValues, boolean isRemoveByScore, int expireSeconds);

    /**
     * 根据Score判断SortedSet缓存是否存在
     *
     * @param cacheKey   缓存键
     * @param startScore 起始score(-1/0)
     * @param endScore   截止score(1/0)
     * @return 是否存在
     */
    Boolean sortedSetExist(String cacheKey, double startScore, double endScore);

    /**
     * 根据Score获取SortedSet缓存
     *
     * @param cacheKey   缓存键
     * @param startScore 起始score(-1/0)
     * @param endScore   截止score(1/0)
     * @return SortedSet缓存
     */
    <T> Set<T> sortedSetGet(String cacheKey, double startScore, double endScore, Class<T> type);

    /**
     * 根据index获取SortedSet缓存
     *
     * @param cacheKey   缓存键
     * @param startIndex 起始位置
     * @param endIndex   截止位置   (-1为最后一个，-2为倒数第二个...)
     * @return SortedSet缓存
     */
    <T> Set<T> sortedSetGetByIndex(String cacheKey, long startIndex, long endIndex, Class<T> type);

    /**
     * 获取长度
     *
     * @param cacheKey 缓存键
     * @return SortedSet缓存
     */
    long sortedSetGetLength(String cacheKey);

    /**
     * 统计自增。
     *
     * @param cacheKey 缓存键
     * @param hashKey  Hash键
     * @param value    计数
     * @return 当前计数
     */
    long hashIncrement(String cacheKey, String hashKey, long value);

    /**
     * 获取统计。
     *
     * @param cacheKey 缓存键
     * @param hashKey  Hash键
     * @return 当前计数
     */
    long hashGetIncrement(String cacheKey, String hashKey);

    /**
     * 获取指定缓存键下所有的统计。
     *
     * @param cacheKey 缓存键
     * @return 所有计数
     */
    Map<String, Long> hashGetAllIncrement(String cacheKey);

    /**
     * 批量删除统计。
     *
     * @param cacheKey 缓存键
     * @return 所有计数
     */
    void hashRemoveIncrement(String cacheKey, String... hashKeys);

    /**
     * 设置缓存键超时时间(单位:秒)
     *
     * @param cacheKey 缓存键
     * @param seconds  过期时间(秒)
     * @return 是否成功设置 1 成功；0 失败
     */
    long setExpire(String cacheKey, int seconds);
}
