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

import io.github.sparqlanything.html.org.apache.any23.util.StreamUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for handling files.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FileUtils {

    /**
     * Moves a <code>target</code> file to a new <code>dest</code> location.
     *
     * @param target
     *            file to be moved.
     * @param dest
     *            dest dir.
     *
     * @return destination file.
     */
    public static File mv(File target, File dest) {
        if (!dest.isDirectory()) {
            throw new IllegalArgumentException("destination must be a directory.");
        }

        final File newFile = new File(dest, target.getName());
        boolean success = target.renameTo(newFile);
        if (!success) {
            throw new IllegalStateException(
                    String.format(Locale.ROOT, "Cannot move target file [%s] to destination [%s]", target, newFile));
        }
        return newFile;
    }

    /**
     * Copies the content of the input stream within the given dest file. The dest file must not exist.
     *
     * @param is
     *            {@link InputStream} to copy
     * @param dest
     *            detination to copy it to.
     */
    public static void cp(InputStream is, File dest) {
        if (dest.exists()) {
            throw new IllegalArgumentException("Destination must not exist.");
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            FileOutputStream fos = new FileOutputStream(dest);
            bos = new BufferedOutputStream(fos);
            final byte[] buffer = new byte[1024 * 4];
            int read;
            while (true) {
                read = bis.read(buffer);
                if (read == -1) {
                    break;
                }
                bos.write(buffer, 0, read);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while copying stream into file.", e);
        } finally {
            StreamUtils.closeGracefully(bis);
            StreamUtils.closeGracefully(bos);
        }
    }

    /**
     * Copies a file <code>src</code> to the <code>dest</code>.
     *
     * @param src
     *            source file.
     * @param dest
     *            destination file.
     *
     * @throws FileNotFoundException
     *             if file cannot be copied or created.
     */
    public static void cp(File src, File dest) throws FileNotFoundException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(src);
            cp(fis, dest);
        } finally {
            StreamUtils.closeGracefully(fis);
        }
    }

    /**
     * Dumps the given string within a file.
     *
     * @param f
     *            file target.
     * @param content
     *            content to be dumped.
     *
     * @throws IOException
     *             if there is an error dumping the content
     */
    public static void dumpContent(File f, String content) throws IOException {
        Writer fw = null;
        try {
            fw = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);
            fw.write(content);
        } finally {
            StreamUtils.closeGracefully(fw);
        }
    }

    /**
     * Dumps the stack trace of the given exception into the specified file.
     *
     * @param f
     *            file to generate dump.
     * @param t
     *            exception to be dumped.
     *
     * @throws IOException
     *             if there is an error dumping the content
     */
    public static void dumpContent(File f, Throwable t) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8), true);
        t.printStackTrace(pw);
        pw.close();
        dumpContent(f, baos.toString("UTF-8"));
    }

    /**
     * Reads a resource file and returns the content as a string.
     *
     * @param clazz
     *            the class to use load the resource.
     * @param resource
     *            the resource to be load.
     *
     * @return the string representing the file content.
     *
     * @throws IOException
     *             if there is an error loading the resource
     */
    public static String readResourceContent(Class clazz, String resource) throws IOException {
        return StreamUtils.asString(clazz.getResourceAsStream(resource));
    }

    /**
     * Reads a resource file and returns the content as a string.
     *
     * @param resource
     *            the resource to be load.
     *
     * @return the string representing the file content.
     *
     * @throws IOException
     *             if there is an error loading the resource
     */
    public static String readResourceContent(String resource) throws IOException {
        return readResourceContent(FileUtils.class, resource);
    }

    /**
     * Returns the content of a file a single string.
     *
     * @param f
     *            the file to read.
     *
     * @return the content of file.
     *
     * @throws IOException
     *             if an error occurs while locating or accessing the file.
     */
    public static String readFileContent(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        return StreamUtils.asString(fis, true);
    }

    /**
     * Returns all the lines of a file.
     *
     * @param f
     *            the file to read.
     *
     * @return a not <code>null</code> array with not <code>null</code> line strings.
     *
     * @throws IOException
     *             if an error occurs while locating or accessing the file.
     */
    public static String[] readFileLines(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        return StreamUtils.asLines(fis);
    }

    /**
     * Lists the content of a dir applying the specified filter.
     *
     * @param dir
     *            directory root.
     * @param filenameFilter
     *            filter to be applied.
     *
     * @return list of matching files.
     */
    public static File[] listFilesRecursively(File dir, FilenameFilter filenameFilter) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(dir.getAbsolutePath() + " must be a directory.");
        }
        final List<File> result = new ArrayList<File>();
        visitFilesRecursively(dir, filenameFilter, result);
        return result.toArray(new File[result.size()]);
    }

    /**
     * Visits a directory recursively, applying the given filter and adding matches to the result list.
     *
     * @param dir
     *            directory to find.
     * @param filenameFilter
     *            filter to apply.
     * @param result
     *            result list.
     */
    private static void visitFilesRecursively(File dir, FilenameFilter filenameFilter, List<File> result) {
        for (File file : dir.listFiles()) {
            if (!file.isDirectory()) {
                if (filenameFilter == null || filenameFilter.accept(dir, file.getName())) {
                    result.add(file);
                }
            } else {
                visitFilesRecursively(file, filenameFilter, result);
            }
        }
    }

    /**
     * Function class.
     */
    private FileUtils() {
    }

}
