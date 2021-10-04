package com.test.mytest.other;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class TestList {
    public static void main(String[] args) {
        List<Long> testArrayList = new ArrayList<>();

        List<Long> testLinkedList = new LinkedList<>();

        List<Long> vector = new Vector<>();

        Long current = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            testArrayList.add(Long.valueOf(i));
        }
        Long end = System.currentTimeMillis();
        System.out.println(end - current);


        current = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            testLinkedList.add(Long.valueOf(i));
        }
        end = System.currentTimeMillis();
        System.out.println(end - current);

        current = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            vector.add(Long.valueOf(i));
        }
        end = System.currentTimeMillis();
        System.out.println(end - current);


        current = System.currentTimeMillis();
        for(int i = 0; i < 10000; i++)
        testArrayList.get(i);
        end = System.currentTimeMillis();
        System.out.println("获取:"+(end - current));


        current = System.currentTimeMillis();
        for(int i = 0; i < 10000; i++)
            testLinkedList.get(i);
        end = System.currentTimeMillis();
        System.out.println("获取:"+(end - current));


        current = System.currentTimeMillis();
        for(int i = 0; i <= 5001; i++)
            testArrayList.remove(i);
        end = System.currentTimeMillis();
        System.out.println("获取:"+(end - current));


    }
}
