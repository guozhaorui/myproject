import com.test.TestApplication;
import com.test.circularreference.ClassA;
import com.test.distributedlock.SimpleDistributedLockInterface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class})
public class DistributedLockTest {

    @Autowired
    private SimpleDistributedLockInterface distributedLock;

    @Autowired
    private ClassA classA;

    @Test
    public void test() {
        distributedLock.tryGetDistributedLock("测是分布式锁的key", "111222333", 60000);
    }

    @Test
    public void test1(){
        classA.equals("1");
        System.out.println(classA);
    }
}
