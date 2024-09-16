/*
 * Copyright (c) 2024 SPARQL Anything Contributors @ http://github.com/sparql-anything
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sparqlanything.html.org.apache.any23.validator;

import io.github.sparqlanything.html.org.apache.any23.validator.SerializationException;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReport;
import io.github.sparqlanything.html.org.apache.any23.validator.ValidationReportSerializer;
import org.w3c.dom.Element;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Default implementation of {@link io.github.sparqlanything.html.org.apache.any23.validator.ValidationReportSerializer} for <i>XML</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class XMLValidationReportSerializer implements ValidationReportSerializer {

    @Override
    public void serialize(ValidationReport vr, OutputStream os) throws io.github.sparqlanything.html.org.apache.any23.validator.SerializationException {
        PrintStream ps;
        try {
            ps = new PrintStream(os, true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error serializing the OuputStream as UTF-8 encoding.", e);
        }
        try {
            serializeObject(vr, ps);
        } finally {
            ps.flush();
        }
    }

    private void serializeObject(Object o, PrintStream ps) throws io.github.sparqlanything.html.org.apache.any23.validator.SerializationException {
        if (o == null) {
            return;
        }
        final Class<? extends Object> oClass = o.getClass();
        final String oClassName = getClassName(oClass);
        ps.printf(Locale.ROOT, "<%s>%n", oClassName);
        List<Method> getters = filterGetters(o.getClass());
        if (getters.isEmpty()) {
            ps.print(o.toString());
            return;
        }
        for (Method getter : getters) {
            serializeGetterValue(o, getter, ps);
        }
        ps.printf(Locale.ROOT, "</%s>%n", oClassName);
    }

    private String getClassName(Class<? extends Object> oClass) {
        final NodeName nodeName = oClass.getAnnotation(NodeName.class);
        if (nodeName != null) {
            return nodeName.value();
        }
        final String simpleName = oClass.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    private List<Method> filterGetters(Class<? extends Object> c) {
        Method[] methods = c.getDeclaredMethods();
        List<Method> filtered = new ArrayList<>();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            final String methodName = method.getName();
            if (method.getParameterTypes().length == 0 && ((methodName.length() > 3 && methodName.indexOf("get") == 0)
                    || (methodName.length() > 2 && methodName.indexOf("is") == 0))) {
                filtered.add(method);
            }
        }
        return filtered;
    }

    public void serializeGetterValue(Object o, Method m, PrintStream ps) throws io.github.sparqlanything.html.org.apache.any23.validator.SerializationException {
        final Object value;
        final String methodName = m.getName();
        try {
            value = m.invoke(o);
        } catch (Exception e) {
            throw new io.github.sparqlanything.html.org.apache.any23.validator.SerializationException(String.format(Locale.ROOT, "Error while reading method '%s'", methodName),
                    e);
        }
        final String property = getPropertyFromMethodName(methodName);
        if (isManaged(value)) {
            ps.printf(Locale.ROOT, "<%s>%n", property);
            printObject(value, ps);
            ps.printf(Locale.ROOT, "</%s>%n", property);
        } else {
            List<Method> getters = filterGetters(value.getClass());
            for (Method getter : getters) {
                serializeGetterValue(value, getter, ps);
            }
        }
    }

    private String getPropertyFromMethodName(String methodName) {
        int i = methodName.indexOf("is");
        if (i == 0) {
            return Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
        }
        return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
    }

    private void printObject(Object o, PrintStream ps) throws SerializationException {
        if (o == null) {
            return;
        }
        if (o instanceof Element) {
            ps.print(o.toString());
            return;
        }
        if (o instanceof Array) {
            Object[] array = (Object[]) o;
            if (array.length == 0) {
                return;
            }
            for (Object a : array) {
                serializeObject(a, ps);
            }
            return;
        }
        if (o instanceof Collection) {
            Collection<?> collection = (Collection<?>) o;
            if (collection.isEmpty()) {
                return;
            }
            for (Object e : collection) {
                serializeObject(e, ps);
            }
            return;
        }
        ps.print(o.toString());
    }

    private boolean isManaged(Object o) {
        return o == null || o instanceof String || o.getClass().isPrimitive() || (o instanceof Collection)
                || o instanceof Element;
    }

    /**
     * Allows to specify a custom node name.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface NodeName {
        String value();
    }

}
