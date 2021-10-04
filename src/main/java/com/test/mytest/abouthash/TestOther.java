package com.test.mytest.abouthash;

import java.util.HashMap;

public class TestOther {
    public static void main(String[] args) {

        HashMap map = new HashMap();


        String gzr = "gzrffsfffwefwfe";
        int h = 0;
        System.out.println(gzr.hashCode());
        System.out.println(Integer.toBinaryString(gzr.hashCode()));

        map.put(gzr,11);

        //(h = key.hashCode()) ^ (h >>> 16)

        // 0000000000000000111110110111111^1111101101111111000000101010000

          System.out.println(32191^2109702480);
    }
}
