import com.test.TestApplication;
import com.test.mq.MQSender;
import com.test.redis.hgw.ICacher;
import com.test.service.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class})
public class TestClass {

    @Autowired
    private TestService testService;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private ICacher cacher;

    @Test
    public void testList() {
        Object object = testService.query();
        System.out.println(object);
    }

    @Test
    public void testMq() {
        for (int i = 0; i < 10; i++) {
            mqSender.sendSeckillMessage(String.valueOf(i));
        }
    }

    @Test
    public void testRedis() {
        System.out.println(cacher.get("a"));

        cacher.set("测试键不会过期","不会过期");

        cacher.set("测试键30秒过期","30秒过期",30);
    }
}
