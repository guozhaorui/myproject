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
        long l = 0L;
        for (;;) {
            for (;;) {
                l++;
                Person person = new Person(IdWorkerUtil.getId(), "gzr", (byte) 1, (byte) 1, (byte) 1, "");
                personList.add(person);
                if (l == 1000l) {
                    batchAddPerson(personList);
                    personList.clear();
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
        testDao.updatePerson();
        Thread.sleep(10000);
        testDao.updateTest();
    }

    public void testDbLock() {
        Thread t1 = new Thread(new MyTask());
        t1.start();
        Thread t2 = new Thread(new MyTask());
        t2.start();
    }

}
