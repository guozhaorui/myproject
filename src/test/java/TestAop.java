import com.test.TestApplication;
import com.test.aop.MyTransactional;
import com.test.dao.TestDao;
import com.test.service.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class})
public class TestAop {

    @Autowired
    private TestDao testDao;

    @Autowired
    private TestService testService;


    public void add() {
        //  List<Person> personList = new ArrayList<Person>();
        //  Person person = new Person(IdWorkerUtil.getId(), "gzr", (byte) 1, (byte) 1, (byte) 1, "sddsdsds");
        testService.test();

    }

    @Test
    public void testAop() {
        add();
    }
}
