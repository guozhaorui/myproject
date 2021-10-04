package com.test.mytest.abouthash;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class HashSetTest {

    public static void main(String[] args) {

        /**
         HashSet set = new HashSet();
         set.add(112);
         List<Long> list = Arrays.asList(11L, 22L, 33L, 55L, 11L, 22L);
         set.addAll(list);
         Set treeSet = new TreeSet();
         System.out.println(set);
         **/

        TestSortBean bean1 = new TestSortBean("test",2);
        TestSortBean bean2 = new TestSortBean("jack",1);
        TestSortBean bean3 = new TestSortBean("slivery",5);
        TestSortBean bean4 = new TestSortBean("ssd",7);
        TestSortBean bean5 = new TestSortBean("cxcx",3);

        Set testHashSet = new HashSet<>();
        testHashSet.add(bean1);
        testHashSet.add(bean2);
        testHashSet.add(bean3);
        testHashSet.add(bean4);
        testHashSet.add(bean5);

        TreeSet  testTreeSet = new TreeSet();
        testTreeSet.add(bean1);
        testTreeSet.add(bean2);
        testTreeSet.add(bean3);
        testTreeSet.add(bean4);
        testTreeSet.add(bean5);

        System.out.println(testHashSet);
        System.out.println(testTreeSet);
    }
}
