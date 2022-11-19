package com.beans;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class Beans{
    private Object obj;
    private Class<?> beanClass;

    public Beans() {
    }

    public Beans(Object obj, Class<?> beanClass) {
        this.obj = obj;
        this.beanClass = beanClass;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(128);

        try {
            List<PropertyDescriptor> propertyDescriptors = BeanIntrospector.getPropertyDescriptorsWithGetters(this.beanClass);
            Iterator flag = propertyDescriptors.iterator();

            while(flag.hasNext()) {
                PropertyDescriptor propertyDescriptor = (PropertyDescriptor)flag.next();
                Method getter = propertyDescriptor.getReadMethod();
                // getDataSourceJNDI.invoke(new HikariDataSource())
                getter.invoke(this.obj);
                // Object value = getter.invoke(this.obj, new Object[0]);
            }
        } catch (Exception var6) {
            System.out.println(var6);
            Class<? extends Object> clazz = this.obj.getClass();
            String errorMessage = var6.getMessage();
            sb.append(String.format("\n\nEXCEPTION: Could not complete %s.toString(): %s\n", clazz, errorMessage));
        }

        return sb.toString();
    }

    public Object getObj() {
        return this.obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }
}
