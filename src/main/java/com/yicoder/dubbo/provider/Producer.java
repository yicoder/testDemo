package com.yicoder.dubbo.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Producer {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] {"applicationContext-producer.xml"});
        context.start();
        System.out.println("start");
        System.in.read(); // 为保证服务一直开着，利用输入流的阻塞来模拟
    }
}