package com.example.javasec.hessian2;

import javax.naming.Context;
import javax.naming.InitialContext;

public class JNDIBypassHighJavaClient {
    public static void main(String[] args) throws Exception {
        String uri = "rmi://127.0.0.1:1099/exp";
        Context context = new InitialContext();
        context.lookup(uri);
    }
}