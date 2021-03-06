package com.test.util.id;

/**
 * 这是一个ID生成器
 * ****************************************************************************************************
 * Twitter_Snowflake SnowFlake的结构如下(每部分用-分开): 0 - 0000000000 0000000000 0000000000 0000000000 0 -
 * 00000 - 00000 - 000000000000 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T =
 * (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号 加起来刚好64位，为一个Long型。
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 * 
 * ****************************************************************************************************
 * IdWorker的算法 0 - 0000000000 0000000000 0000000000 0000000000 - 0000000000 0000 - 000000000
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0
 * 40位时间截(毫秒级)，注意，40位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。40位的时间截，可以使用34年，年T =
 * (1L << 40) / (1000L * 60 * 60 * 24 * 365) = 34 14位的服务位，可以部署在16384个服务,
 * 7位序列，毫秒内的计数，9位的计数顺序号支持每个节点每毫秒(同一服务，同一时间截)产生512个ID序号 加起来刚好64位，为一个Long型。
 * DifferIdWorker的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由服务)，并且效率较高。 <br>
 * <br>
 * 
 * @author 李峥嵘<br>
 */
final class IdWorker {
    // ==============================Fields===========================================
    /** 开始时间截 (2017-01-01) */
    private final long twepoch = 1483200000000L;

    /** 服务id所占的位数 */
    private final long serviceID = 16L;

    /** 支持的最大服务id，结果是4096 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final long maxServiceID = -1L ^ (-1L << serviceID);

    /** 序列在id中占的位数 */
    private final long sequenceBits = 7L;

    /** 服务ID向左移11位 */
    private final long workerIdShift = sequenceBits;

    /** 时间截向左移23位(12+11) */
    private final long timestampLeftShift = sequenceBits + serviceID;

    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /** 服务ID(0~16383) */
    private long serviceId;

    /** 毫秒内序列(0~511) */
    private long sequence = 0L;

    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;

    // ==============================Constructors=====================================
    /**
     * 构造函数
     * 
     * @param serviceId
     *            服务ID (0~16383)
     */
    public IdWorker(long serviceId) {
        if (serviceId > maxServiceID || serviceId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxServiceID));
        }
        this.serviceId = serviceId;
    }

    // ==============================Methods==========================================
    /**
     * 获得下一个ID (该方法是线程安全的)
     * 
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        // 上次生成ID的时间截
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift) | (serviceId << workerIdShift) | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * 
     * @param lastTimestamp
     *            上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * 
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
