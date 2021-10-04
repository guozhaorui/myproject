import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class GzrTest {
    public static int m = 0;

    private static Object lock = new Object();

    private static AtomicBoolean atomicBoolean = new AtomicBoolean(false);


    public synchronized static void add() {
        for (int j = 0; j < 1000; j++) {
            m++;
        }
    }

    public static void add2() {
        for (int j = 0; j < 1000; j++) {
            m++;
        }
    }

    public static void lock() {
        while (!atomicBoolean.compareAndSet(false, true)) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void unlock() {
        atomicBoolean.compareAndSet(true, false);
    }

    @Test
    public void testLock() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                    // lock();
                add2();
                   //   unlock();
            }).start();
        }

        Thread.sleep(1000);
        System.out.println(m);
    }

}
