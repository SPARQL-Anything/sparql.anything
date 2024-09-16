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

package io.github.sparqlanything.html.org.apache.any23.util;

import io.github.sparqlanything.html.org.apache.any23.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class provides utility methods for discovering classes in packages.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DiscoveryUtils {

    private static final String FILE_PREFIX = "file:";
    private static final String CLASS_SUFFIX = ".class";

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and sub-packages.
     *
     * @param packageName
     *            the root package.
     *
     * @return list of matching classes.
     */
    public static List<Class> getClassesInPackage(String packageName) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        final String path = packageName.replace('.', '/');
        final Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(path);
        } catch (IOException ioe) {
            throw new IllegalStateException("Error while retrieving internal resource path.", ioe);
        }
        final List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            final URL resource = resources.nextElement();
            final String fileName = resource.getFile();
            final String fileNameDecoded;
            try {
                fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException uee) {
                throw new IllegalStateException("Error while decoding class file name.", uee);
            }
            dirs.add(new File(fileNameDecoded));
        }
        @SuppressWarnings("rawtypes")
        final ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and sub-packages and
     * filter them by ones implementing the specified interface <code>iface</code>.
     *
     * @param packageName
     *            the root package.
     * @param filter
     *            the interface/class filter.
     *
     * @return list of matching classes.
     */
    public static List<Class> getClassesInPackage(String packageName, Class<?> filter) {
        final List<Class> classesInPackage = getClassesInPackage(packageName);
        @SuppressWarnings("rawtypes")
        final List<Class> result = new ArrayList<Class>();
        Class<?> superClazz;
        for (Class<?> clazz : classesInPackage) {
            if (clazz.equals(filter)) {
                continue;
            }
            superClazz = clazz.getSuperclass();
            if ((superClazz != null && superClazz.equals(filter)) || contains(clazz.getInterfaces(), filter)) {
                result.add(clazz);
            }
        }
        return result;
    }

    /**
     * Find all classes within the specified location by package name.
     *
     * @param location
     *            class location.
     * @param packageName
     *            package name.
     *
     * @return list of detected classes.
     */
    private static List<Class> findClasses(File location, String packageName) {
        final String locationPath = location.getPath();
        if (locationPath.indexOf(FILE_PREFIX) == 0) {
            return findClassesInJAR(locationPath);
        }
        return findClassesInDir(location, packageName);
    }

    /**
     * Find all classes within a JAR in a given prefix addressed with syntax <code>file:<path/to.jar>!<path/to/package>.
     *
     * @param location
     *            package location.
     *
     * @return list of detected classes.
     */
    private static List<Class> findClassesInJAR(String location) {
        final String[] sections = location.split("!");
        if (sections.length != 2) {
            throw new IllegalArgumentException("Invalid JAR location.");
        }
        final String jarLocation = sections[0].substring(FILE_PREFIX.length());
        final String packagePath = sections[1].substring(1);

        try {
            @SuppressWarnings("resource")
            final JarFile jarFile = new JarFile(jarLocation);
            final Enumeration<JarEntry> entries = jarFile.entries();
            @SuppressWarnings("rawtypes")
            final List<Class> result = new ArrayList<Class>();
            JarEntry current;
            String entryName;
            String clazzName;
            Class<?> clazz;
            while (entries.hasMoreElements()) {
                current = entries.nextElement();
                entryName = current.getName();
                if (StringUtils.isPrefix(packagePath, entryName) && StringUtils.isSuffix(CLASS_SUFFIX, entryName)
                        && !entryName.contains("$")) {
                    try {
                        clazzName = entryName.substring(0, entryName.length() - CLASS_SUFFIX.length()).replaceAll("/",
                                ".");
                        clazz = Class.forName(clazzName);
                    } catch (ClassNotFoundException cnfe) {
                        throw new IllegalStateException("Error while loading detected class.", cnfe);
                    }
                    result.add(clazz);
                }
            }
            return result;
        } catch (IOException ioe) {
            throw new RuntimeException("Error while opening JAR file.", ioe);
        }
    }

    /**
     * Recursive method used to find all classes in a given directory and sub-dirs.
     *
     * @param directory
     *            The base directory
     * @param packageName
     *            The package name for classes found inside the base directory
     *
     * @return The classes
     */
    private static List<Class> findClassesInDir(File directory, String packageName) {
        if (!directory.exists()) {
            return Collections.emptyList();
        }
        @SuppressWarnings("rawtypes")
        final List<Class> classes = new ArrayList<Class>();
        File[] files = directory.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                classes.addAll(findClassesInDir(file, packageName + "." + fileName));
            } else if (fileName.endsWith(".class") && !fileName.contains("$")) {
                try {
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
                    } catch (ExceptionInInitializerError e) {
                        /*
                         * happen, for example, in classes, which depend on Spring to inject some beans, and which fail,
                         * if dependency is not fulfilled
                         */
                        clazz = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6), false,
                                Thread.currentThread().getContextClassLoader());
                    }
                    classes.add(clazz);
                } catch (ClassNotFoundException cnfe) {
                    throw new IllegalStateException("Error while loading detected class.", cnfe);
                }
            }
        }
        return classes;
    }

    private static boolean contains(Object[] list, Object t) {
        for (Object o : list) {
            if (o.equals(t)) {
                return true;
            }
        }
        return false;
    }

    private DiscoveryUtils() {
    }

}
