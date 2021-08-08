package com.test.mytest.lambda;

import java.util.function.Function;
import java.util.function.Supplier;

public class Test {

    public static void test(Supplier<String> s) {
        System.out.println(s.get());
        ;
    }

//    public static void main(String[] args) {
//        test(()->{
//           System.out.println("我是一个Supplier");
//            return "ss";
//        });
//    }

//
//    public static void testC(Consumer<Object> f){
//        f.accept( "ddfdf");
//    }

    // 字符串去空格

    public static String testF(String sst, Function<String, String> f,Function<String,String> f1) {
        return f.andThen(f1).apply(sst);
    }

    public static void main(String[] args) {
        String g = "  gzr  ";
        System.out.println(testF(g, (c) -> g.trim(), (c)->"ddd"+c));
    }


}
