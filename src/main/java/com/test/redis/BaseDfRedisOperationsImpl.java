/*
package com.test.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ScanOptions.ScanOptionsBuilder;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;


*/
/**
 * @param <K>
 * @param <V>
 * @author CaiYH
 * @desc redis操作基类
 * @since 2018年1月4日 下午4:22:39
 *//*

public abstract class BaseDfRedisOperationsImpl<K, V> implements DfRedisOperations<K, V> {

    */
/**
     * 日志
     **//*

    private final static Logger LOG = LoggerFactory.getLogger(BaseDfRedisOperationsImpl.class);

    */
/**
     * 子类重写template
     *
     * @return
     *//*

    protected abstract RedisTemplate<K, V> getOperationRedisTemplate();

    @Override
    public ValueOperations<K, V> opsForValue() {

        RedisTemplate<K, V> template = getOperationRedisTemplate();

        return template.opsForValue();
    }

    @Override
    public <HK extends K, HV extends V> HashOperations<K, HK, HV> opsForHash() {
        return getOperationRedisTemplate().opsForHash();
    }

    @Override
    public ListOperations<K, V> opsForList() {
        return getOperationRedisTemplate().opsForList();
    }

    @Override
    public SetOperations<K, V> opsForSet() {
        return getOperationRedisTemplate().opsForSet();
    }

    @Override
    public ZSetOperations<K, V> opsForZSet() {
        return getOperationRedisTemplate().opsForZSet();
    }

    @Override
    public HyperLogLogOperations<K, V> opsForHyperLogLog() {
        return getOperationRedisTemplate().opsForHyperLogLog();
    }

    @Override
    public GeoOperations<K, V> opsForGeo() {
        return getOperationRedisTemplate().opsForGeo();
    }

    @Override
    public void set(K name, V value) throws Exception {
        opsForValue().set(name, value);
    }

    @Override
    public void set(K name, V value, long timeout) throws Exception {
        opsForValue().set(name, value, timeout, TimeUnit.SECONDS);
    }

    @Override
    public V get(K name) throws Exception {
        return opsForValue().get(name);
    }

    @Override
    public List<V> multiGet(Collection<K> key) throws Exception {

        return opsForValue().multiGet(key);

    }

    @Override
    public boolean has(K name) throws Exception {
        return getOperationRedisTemplate().hasKey(name);
    }

    @Override
    public void delete(K name) throws Exception {
        getOperationRedisTemplate().delete(name);
    }

    @Override
    public Long deleteByHashKeys(K name, K[] hashKeys) throws Exception {
        SessionCallback<Long> callback = new SessionCallback<Long>() {

            @SuppressWarnings("unchecked")
            @Override
            public Long execute(@SuppressWarnings("rawtypes") RedisOperations operations) throws DataAccessException {
                operations.multi();

                operations.opsForHash().delete(name, hashKeys);

                List<Long> count = operations.exec();

                if (count != null && count.size() > 0) {
                    return count.get(0);
                }

                return 0L;
            }
        };

        RedisTemplate<K, V> template = getOperationRedisTemplate();

        Long count = template.execute(callback);

        return count;
    }

    @Override
    public Map<K, V> getHashValues(K name) throws Exception {

        Map<K, V> data = opsForHash().entries(name);

        return data;
    }

    @Override
    public List<V> getHashValues(K name, List<K> hashKeys) throws Exception {

        List<V> datas = opsForHash().multiGet(name, hashKeys);

        return datas;
    }

    @Override
    public void deleteByKeys(Set<K> keys) throws Exception {
        getOperationRedisTemplate().delete(keys);
    }

    @Override
    public void setHash(K key, K hashKey, V value) {
        opsForHash().put(key, hashKey, value);
    }

    @Override
    public void setHash(K key, K hashKey, V hashValue, long timeout) throws Exception {
        this.setHash(key, hashKey, hashValue);
        this.expire(key, timeout);

    }

    @Override
    public void setHash(K name, Map<? extends K, ? extends V> datas) throws Exception {
        opsForHash().putAll(name, datas);
    }

    @Override
    public V getHashValue(K key, K hashKey) {
        return opsForHash().get(key, hashKey);
    }

    @Override
    public Long incr(String key, long liveTime) {
        Long count = this.incrby(key, 1, liveTime);
        return count;
    }

    @Override
    public Long incrby(String key, long increment, long liveTime) {
        RedisTemplate<K, V> redisTemplate = getOperationRedisTemplate();

        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long result = entityIdCounter.getAndAdd(increment) + increment;

        */
/** 初始设置过期时间 **//*

        if (null == result || result.longValue() == 0) {
            if (liveTime > 0) {
                entityIdCounter.expire(liveTime, TimeUnit.SECONDS);
            }
        }
        return result;
    }

    @Override
    public void expire(K name, long timeout) throws Exception {
        getOperationRedisTemplate().expire(name, timeout, TimeUnit.SECONDS);
    }

    @Override
    public void flushDB() throws Exception {
        getOperationRedisTemplate().execute((RedisCallback<?>) connection -> {
            connection.flushDb();
            connection.close();
            return null;
        });
    }

    @Override
    public Set<String> getKeysByPattern(String pattern) throws Exception {

        Set<String> matchedKeys = new HashSet<>();
        getOperationRedisTemplate().execute((RedisCallback<?>) connection -> {

            ScanOptionsBuilder build = new ScanOptionsBuilder();

            build.match(pattern);

            Cursor<byte[]> cursor = connection.scan(build.build());

            try {
                while (cursor.hasNext()) {

                    matchedKeys.add(new String(cursor.next(), "UTF-8"));

                }
            } catch (UnsupportedEncodingException e) {
                LOG.error("UnsupportedEncodingException", e);
            } finally {
                connection.close();
            }

            return matchedKeys;
        });

        return matchedKeys;
    }

    @Override
    public List<String> getKeys(String pattern) throws Exception {

        List<String> matchedKeys = new ArrayList<>();
        getOperationRedisTemplate().execute((RedisCallback<?>) connection -> {

            ScanOptionsBuilder build = new ScanOptionsBuilder();

            build.match(pattern);

            Cursor<byte[]> cursor = connection.scan(build.build());

            try {
                while (cursor.hasNext()) {

                    matchedKeys.add(new String(cursor.next(), "UTF-8"));

                }
            } catch (UnsupportedEncodingException e) {
                LOG.error("UnsupportedEncodingException", e);
            } finally {
                connection.close();
            }
            return matchedKeys;
        });

        return matchedKeys;
    }

    @Override
    public Map<K, V> getHashValuesByPattern(K key, String pattern) throws Exception {

        Map<K, V> result = new HashMap<>(20);

        ScanOptionsBuilder build = new ScanOptionsBuilder();

        build.match(pattern);

        ScanOptions options = build.build();

        Cursor<Entry<K, V>> cursor = opsForHash().scan(key, options);

        try {
            while (cursor.hasNext()) {
                Entry<K, V> data = cursor.next();

                K k = data.getKey();

                V v = data.getValue();

                result.put(k, v);
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    @Override
    public void begin() {
        getOperationRedisTemplate().multi();
    }

    @Override
    public List<Object> commit() {
        return getOperationRedisTemplate().exec();
    }

    @Override
    public void rollback() {
        getOperationRedisTemplate().discard();
    }
}
*/
