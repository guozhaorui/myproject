package com.test.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.dao.TestDao;
import com.test.model.Person;
import com.test.task.MyTask;
import com.test.util.id.IdWorkerUtil;

@Service
public class TestService {

    @Autowired
    private TestDao testDao;

    public Object query() {
        return testDao.query();
    }

    public void startInsert() {
        List<Person> personList = new ArrayList<Person>();
        
        String[] random = new String[] { "的顶峰的反对发射点发射点发", "的幅度萨芬撒旦发射点发射点发", "的反对发射点发射点发射点发生飞洒", "打发士大夫的撒旦发生啊发射点发射点", "d打法十士大夫撒旦发生发射点发射点", "的发生发射点发生发射点发生",
                "的发射点发射点方式打发士大夫", "对方防守对方的反对大幅度反对法", "大幅度反对法啊范德萨发到付", "的犯得上反对法大幅度" };
        
        long l = 0L;
        for (;;) {
            for (;;) {
                l++;
                int i = (int) (l % 10);
                Person person = new Person(IdWorkerUtil.getId(), "gzr", (byte) 1, (byte) 1, (byte) 1, random[i]);
                personList.add(person);
                if (l == 1000l) {
                    int m = batchAddPerson(personList);
                    System.out.println(m);
                    personList.clear();
                    l = 0;
                    break;
                }
            }
        }
    }

    public int batchAddPerson(List<Person> personList) {
        return testDao.batchAddPerson(personList);
    }


    @Transactional(rollbackFor = { RuntimeException.class, Exception.class, Error.class })
    public void updateTest() throws InterruptedException {
        testDao.updatePerson1();
        testDao.updatePerson2();
    }

    public void testDbLock() {
        Thread t1 = new Thread(new MyTask());
        t1.start();
        Thread t2 = new Thread(new MyTask());
        t2.start();
    }

}
