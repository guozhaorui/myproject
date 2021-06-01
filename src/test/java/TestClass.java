import com.test.TestApplication;
import com.test.mq.MQSender;
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

    @Test
    public void testList() {
        Object object = testService.query();
        System.out.println(object);
    }

    @Test
    public  void testMq(){
        for(int i=0;i<10;i++){
            mqSender.sendSeckillMessage(String.valueOf(i));
        }
    }
}
