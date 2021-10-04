import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicStampedReference;

public class testCAS {


    private static AtomicLong aLong = new AtomicLong(0);

    private static AtomicStampedReference<Long> stampedLong = new AtomicStampedReference(0,1);

    public static void main(String[] args) {

        aLong.compareAndSet(1,2);

        System.out.println(aLong);

        aLong.compareAndSet(0,4);

        System.out.println(aLong);

        //stampedLong.compareAndSet(stampedLong.get(),1,1,2);

    }
}
