package com.example.javasec.cc1;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.InvokerTransformer;

public class VulMain {

    /*
    Transformer 本地测试
     */
    public static void demo1(){
        Transformer transformer = new InvokerTransformer(
                "exec",new Class[]{String.class}, new Object[]{"calc"});
        transformer.transform(Runtime.getRuntime());
    }
    public static void main(String[] args) {
        try{
            demo1();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
