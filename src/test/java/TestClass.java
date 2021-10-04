import com.test.TestApplication;
import com.test.mq.myTest.MyMqSender;
import com.test.redis.hgw.ICacher;
import com.test.service.TestService;
import com.test.service.testtransaction.TestTransactionInterface;
import com.test.service.testtransaction.TestTransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class})
public class TestClass {

    @Autowired
    private TestService testService;

    @Autowired
    private MyMqSender sender;

    @Autowired
    private ICacher cacher;

    @Autowired
    private TestTransactionService testTransactionService;


    @Autowired

    @Test
    public void testList() {
        Object object = testService.query();
        System.out.println(object);
    }

    @Test
    public void testRedis() {
        System.out.println(cacher.get("a"));

        cacher.set("测试键不会过期", "不会过期");

        cacher.set("测试键30秒过期", "30秒过期", 30);

        //存一个hash
        /**
         Map<String,String> hm = new HashMap<>();
         hm.put("name","gzr");
         hm.put("age","32");
         hm.put("sex","男");
         cacher.get().hmset("hashTest",hm);
         **/

        System.out.println(cacher.get().hget("hashTest", "sex"));
        ;

        //cacher.get().lpush("gzr","2");

        cacher.get().lpush("gzr", "4");

        cacher.get().rpush("gzr", "3");


        RestTemplate restTemplate = new RestTemplate();
    }

    @Test
    public void testMq() {
        for (int m = 0; m < 10; m++) {
            sender.send("test.exchange", "routing.key.test", String.valueOf(m));
        }
    }

    @Test
    public void testMq2() {
        sender.send("test.exchange.fanout", "", "testMessage");
    }

    @Test
    public void testMq3() {
        sender.send("test.exchange.topic", "stock.usd.1123", "testMessage");
    }

    @Test
    public void test() {
        testTransactionService.test2();
    }


}
