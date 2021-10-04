package com.test.service.testtransaction;

import com.test.dao.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestTransactionService {

    @Autowired
    private TestDao testDao;

    public void testTransaction() {

        testDao.updatePerson1();

        testDao.updatePerson2();

        //
        double j = 8 / 0;
    }

    @Transactional
    public   void test2() {
        testTransaction();
    }

}
