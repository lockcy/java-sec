package com.example.javasec.beans;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanIntrospector {
    private static final Map<Class<?>, PropertyDescriptor[]> introspected = new HashMap();
    private static final String SETTER = "set";
    private static final String GETTER = "get";
    private static final String BOOLEAN_GETTER = "is";

    private BeanIntrospector() {
    }

    private static synchronized PropertyDescriptor[] getPropertyDescriptors(final Class<?> clazz) {
        PropertyDescriptor[] descriptors = (PropertyDescriptor[])introspected.get(clazz);
        if (descriptors == null) {
            descriptors = getPDs(clazz);
            introspected.put(clazz, descriptors);
        }

        return descriptors;
    }

    public static List<PropertyDescriptor> getPropertyDescriptorsWithGetters(final Class<?> clazz) {
        List<PropertyDescriptor> relevantDescriptors = new ArrayList();
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
        if (propertyDescriptors != null) {
            PropertyDescriptor[] var3 = propertyDescriptors;
            int var4 = propertyDescriptors.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                PropertyDescriptor propertyDescriptor = var3[var5];
                Method getter = propertyDescriptor.getReadMethod();
                boolean getterExists = getter != null;
                if (getterExists) {
                    boolean getterFromObject = getter.getDeclaringClass() == Object.class;
                    boolean getterWithoutParams = getter.getParameterTypes().length == 0;
                    if (!getterFromObject && getterWithoutParams) {
                        relevantDescriptors.add(propertyDescriptor);
                    }
                }
            }
        }

        return relevantDescriptors;
    }

    public static List<PropertyDescriptor> getPropertyDescriptorsWithGettersAndSetters(final Class<?> clazz) {
        List<PropertyDescriptor> relevantDescriptors = new ArrayList();
        List<PropertyDescriptor> propertyDescriptors = getPropertyDescriptorsWithGetters(clazz);
        Iterator var3 = propertyDescriptors.iterator();

        while(var3.hasNext()) {
            PropertyDescriptor propertyDescriptor = (PropertyDescriptor)var3.next();
            Method setter = propertyDescriptor.getWriteMethod();
            boolean setterExists = setter != null;
            if (setterExists) {
                relevantDescriptors.add(propertyDescriptor);
            }
        }

        return relevantDescriptors;
    }

    private static PropertyDescriptor[] getPDs(final Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        Map<String, PropertyDescriptor> getters = getPDs(methods, false);
        Map<String, PropertyDescriptor> setters = getPDs(methods, true);
        List<PropertyDescriptor> propertyDescriptors = merge(getters, setters);
        return (PropertyDescriptor[])propertyDescriptors.toArray(new PropertyDescriptor[propertyDescriptors.size()]);
    }

    private static Map<String, PropertyDescriptor> getPDs(final Method[] methods, final boolean setters) {
        Map<String, PropertyDescriptor> pds = new HashMap();
        Method[] var3 = methods;
        int var4 = methods.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Method method = var3[var5];
            String propertyName = null;
            PropertyDescriptor propertyDescriptor = null;
            int modifiers = method.getModifiers();
            if ((modifiers & 1) != 0) {
                String methodName = method.getName();
                Class<?> returnType = method.getReturnType();
                int nrOfParameters = method.getParameterTypes().length;
                if (setters) {
                    if (methodName.startsWith("set") && returnType == Void.TYPE && nrOfParameters == 1) {
                        propertyName = decapitalize(methodName.substring(3));
                        propertyDescriptor = new PropertyDescriptor(propertyName, (Method)null, method);
                    }
                } else if (methodName.startsWith("get") && returnType != Void.TYPE && nrOfParameters == 0) {
                    propertyName = decapitalize(methodName.substring(3));
                    propertyDescriptor = new PropertyDescriptor(propertyName, method, (Method)null);
                } else if (methodName.startsWith("is") && returnType == Boolean.TYPE && nrOfParameters == 0) {
                    propertyName = decapitalize(methodName.substring(2));
                    propertyDescriptor = new PropertyDescriptor(propertyName, method, (Method)null);
                }
            }

            if (propertyName != null) {
                pds.put(propertyName, propertyDescriptor);
            }
        }

        return pds;
    }

    private static List<PropertyDescriptor> merge(final Map<String, PropertyDescriptor> getters, final Map<String, PropertyDescriptor> setters) {
        List<PropertyDescriptor> props = new ArrayList();
        Set<String> processedProps = new HashSet();
        Iterator var4 = getters.keySet().iterator();

        PropertyDescriptor setter;
        while(var4.hasNext()) {
            String propertyName = (String)var4.next();
            PropertyDescriptor getter = (PropertyDescriptor)getters.get(propertyName);
            setter = (PropertyDescriptor)setters.get(propertyName);
            if (setter != null) {
                processedProps.add(propertyName);
                PropertyDescriptor prop = new PropertyDescriptor(propertyName, getter.getReadMethod(), setter.getWriteMethod());
                props.add(prop);
            } else {
                props.add(getter);
            }
        }

        Set<String> writeOnlyProperties = new HashSet();
        writeOnlyProperties.removeAll(processedProps);
        Iterator var10 = writeOnlyProperties.iterator();

        while(var10.hasNext()) {
            String propertyName = (String)var10.next();
            setter = (PropertyDescriptor)setters.get(propertyName);
            props.add(setter);
        }

        return props;
    }

    private static String decapitalize(String name) {
        if (!name.isEmpty() && (name.length() <= 1 || !Character.isUpperCase(name.charAt(1)))) {
            char[] chars = name.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            return new String(chars);
        } else {
            return name;
        }
    }
}
