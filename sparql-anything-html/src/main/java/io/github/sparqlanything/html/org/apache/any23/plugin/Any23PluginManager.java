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

package io.github.sparqlanything.html.org.apache.any23.plugin;

import io.github.sparqlanything.html.org.apache.any23.cli.Tool;
import io.github.sparqlanything.html.org.apache.any23.configuration.DefaultConfiguration;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorFactory;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorGroup;
import io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * The <i>Any23PluginManager</i> is responsible for inspecting dynamically the classpath and retrieving useful classes.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Any23PluginManager {

    /**
     * Any23 Command Line Interface package.
     */
    public static final String CLI_PACKAGE = Tool.class.getPackage().getName();

    /**
     * Property where look for plugins.
     */
    public static final String PLUGIN_DIRS_PROPERTY = "any23.plugin.dirs";

    /**
     * List separator for the string declaring the plugin list.
     */
    public static final String PLUGIN_DIRS_LIST_SEPARATOR = ":";

    /**
     * Internal logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Any23PluginManager.class);

    /**
     * Singleton lazy instance.
     */
    private static final Any23PluginManager instance = new Any23PluginManager();

    /**
     * Internal class loader used to dynamically load classes.
     */
    private final DynamicClassLoader dynamicClassLoader;

    /**
     * Constructor.
     */
    private Any23PluginManager() {
        dynamicClassLoader = new DynamicClassLoader();
    }

    /**
     * @return a singleton instance of {@link Any23PluginManager}.
     */
    public static synchronized Any23PluginManager getInstance() {
        return instance;
    }

    /**
     * Loads a <i>JAR</i> file in the classpath.
     *
     * @param jar
     *            the JAR file to be loaded.
     *
     * @return <code>true</code> if the JAR is added for the first time to the classpath, <code>false</code> otherwise.
     */
    public synchronized boolean loadJAR(File jar) {
        if (jar == null) {
            throw new NullPointerException("jar file cannot be null.");
        }
        if (!jar.isFile() && !jar.exists()) {
            throw new IllegalArgumentException(String.format(java.util.Locale.ROOT,
                    "Invalid JAR [%s], must be an existing file.", jar.getAbsolutePath()));
        }
        return dynamicClassLoader.addJAR(jar);
    }

    /**
     * Loads a list of <i>JAR</i>s in the classpath.
     *
     * @param jars
     *            list of JARs to be loaded.
     *
     * @return list of exceptions raised during the loading.
     */
    public synchronized Throwable[] loadJARs(File... jars) {
        final List<Throwable> result = new ArrayList<>();
        for (File jar : jars) {
            try {
                loadJAR(jar);
            } catch (Throwable t) {
                result.add(new IllegalArgumentException(
                        String.format(java.util.Locale.ROOT, "Error while loading jar [%s]", jar.getAbsolutePath()),
                        t));
            }
        }
        return result.toArray(new Throwable[result.size()]);
    }

    /**
     * Loads a <i>classes</i> directory in the classpath.
     *
     * @param classDir
     *            the directory to be loaded.
     *
     * @return <code>true</code> if the directory is added for the first time to the classpath, <code>false</code>
     *         otherwise.
     */
    public synchronized boolean loadClassDir(File classDir) {
        if (classDir == null) {
            throw new NullPointerException("classDir cannot be null.");
        }
        if (!classDir.isDirectory() && !classDir.exists()) {
            throw new IllegalArgumentException(String.format(java.util.Locale.ROOT,
                    "Invalid class dir [%s], must be an existing file.", classDir.getAbsolutePath()));
        }
        return dynamicClassLoader.addClassDir(classDir);
    }

    /**
     * Loads a list of class dirs in the classpath.
     *
     * @param classDirs
     *            list of class dirs to be loaded.
     *
     * @return list of exceptions raised during the loading.
     */
    public synchronized Throwable[] loadClassDirs(File... classDirs) {
        final List<Throwable> result = new ArrayList<>();
        for (File classDir : classDirs) {
            try {
                loadClassDir(classDir);
            } catch (Throwable t) {
                result.add(new IllegalArgumentException(String.format(java.util.Locale.ROOT,
                        "Error while loading class dir [%s]", classDir.getAbsolutePath()), t));
            }
        }
        return result.toArray(new Throwable[result.size()]);
    }

    /**
     * Loads all the JARs detected in a given directory.
     *
     * @param jarDir
     *            directory containing the JARs to be loaded. Example
     *            '/usr/local/apache-tomcat-7.0.72/webapps/apache-any23-service-2.2-SNAPSHOT/WEB-INF/lib/apache-any23-openie'
     *
     * @return <code>true</code> if all JARs in dir are loaded.
     */
    public synchronized boolean loadJARDir(File jarDir) {
        if (jarDir == null)
            throw new NullPointerException("JAR dir must be not null.");
        if (!jarDir.exists())
            throw new IllegalArgumentException("Given directory doesn't exist:" + jarDir.getAbsolutePath());
        if (!jarDir.isDirectory())
            throw new IllegalArgumentException(
                    "given file exists and it is not a directory: " + jarDir.getAbsolutePath());
        boolean loaded = true;
        for (File jarFile : jarDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        })) {
            loaded &= loadJAR(jarFile);
        }
        return loaded;
    }

    /**
     * Loads a generic list of files, trying to determine the type of every file.
     *
     * @param files
     *            list of files to be loaded.
     *
     * @return list of errors occurred during loading.
     */
    public synchronized Throwable[] loadFiles(File... files) {
        final List<Throwable> errors = new ArrayList<>();
        for (File file : files) {
            try {
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    loadJAR(file);
                } else if (file.isDirectory()) {
                    if (file.getName().endsWith("classes")) {
                        loadClassDir(file);
                    } else {
                        loadJARDir(file);
                    }
                } else {
                    throw new IllegalArgumentException("Cannot handle file " + file.getAbsolutePath());
                }
            } catch (Throwable t) {
                errors.add(t);
            }
        }
        return errors.toArray(new Throwable[errors.size()]);
    }

    /**
     * Returns all classes within the specified <code>packageName</code> satisfying the given class <code>filter</code>.
     * The search is performed on the static classpath (the one the application started with) and the dynamic classpath
     * (the one specified using the load methods).
     *
     * @param <T>
     *            type of filtered class.
     * @param type
     *            of filtered class.
     *
     * @return list of matching classes.
     *
     * @throws IOException
     *             if there is an error obtaining plugins.
     */
    public synchronized <T> Iterator<T> getPlugins(final Class<T> type) throws IOException {
        return ServiceLoader.load(type, dynamicClassLoader).iterator();
    }

    /**
     * Returns the list of all the {@link Tool} classes declared within the classpath.
     *
     * @return not <code>null</code> list of tool classes.
     *
     * @throws IOException
     *             if there is an error obtaining {@link io.github.sparqlanything.html.org.apache.any23.cli.Tool}'s from the classpath.
     */
    public synchronized Iterator<Tool> getTools() throws IOException {
        return getPlugins(Tool.class);
    }

    /**
     * List of {@link ExtractorPlugin} classes declared within the classpath.
     *
     * @return not <code>null</code> list of plugin classes.
     *
     * @throws IOException
     *             if there is an error obtaining Extractors.
     */
    @SuppressWarnings("rawtypes")
    public synchronized Iterator<ExtractorFactory> getExtractors() throws IOException {
        return getPlugins(ExtractorFactory.class);
    }

    /**
     * Loads plugins from a list of specified locations.
     *
     * @param pluginLocations
     *            list of locations.
     *
     * @return a report about the loaded plugins.
     */
    public synchronized String loadPlugins(File... pluginLocations) {
        final StringBuilder report = new StringBuilder();
        report.append("\nLoading plugins from locations {\n");
        for (File pluginLocation : pluginLocations) {
            report.append(pluginLocation.getAbsolutePath()).append('\n');
        }
        report.append("}\n");

        final Throwable[] errors = loadFiles(pluginLocations);
        if (errors.length > 0) {
            report.append("The following errors occurred while loading plugins {\n");
            for (Throwable error : errors) {
                report.append(error);
                report.append("\n\n\n");
            }
            report.append("}\n");
        }
        return report.toString();
    }

    /**
     * Configures a new list of extractors containing the extractors declared in <code>initialExtractorGroup</code> and
     * also the extractors detected in classpath specified by <code>pluginLocations</code>.
     *
     * @param pluginLocations
     *            path locations of plugins.
     *
     * @return full list of extractors.
     *
     * @throws IOException
     *             if there is an error locating the plugin(s).
     * @throws IllegalAccessException
     *             if there are access permissions for plugin(s).
     * @throws InstantiationException
     *             if there is an error instantiating plugin(s).
     */
    public synchronized ExtractorGroup configureExtractors(final File... pluginLocations)
            throws IOException, IllegalAccessException, InstantiationException {

        final String pluginsReport = loadPlugins(pluginLocations);
        logger.info(pluginsReport);

        final StringBuilder report = new StringBuilder();
        try {
            final List<ExtractorFactory<?>> newFactoryList = new ArrayList<>();
            @SuppressWarnings("rawtypes")
            Iterator<ExtractorFactory> extractors = getExtractors();
            while (extractors.hasNext()) {
                ExtractorFactory<?> factory = extractors.next();

                report.append("\n - found plugin: ").append(factory.getExtractorName()).append("\n");

                newFactoryList.add(factory);
            }

            if (newFactoryList.isEmpty()) {
                report.append("\n=== No plugins have been found.===\n");
            }

            return new ExtractorGroup(newFactoryList);
        } finally {
            logger.info(report.toString());
        }
    }

    /**
     * Configures a new list of extractors containing the extractors declared in <code>initialExtractorGroup</code> and
     * also the extractors detected in classpath specified by the default configuration.
     *
     * @param initialExtractorGroup
     *            initial list of extractors.
     *
     * @return full list of extractors.
     *
     * @throws IOException
     *             if there is an error locating the extractor(s).
     * @throws IllegalAccessException
     *             if there are access permissions for extractor(s).
     * @throws InstantiationException
     *             if there is an error instantiating extractor(s).
     */
    public synchronized ExtractorGroup configureExtractors(ExtractorGroup initialExtractorGroup)
            throws IOException, InstantiationException, IllegalAccessException {
        final String pluginDirs = DefaultConfiguration.singleton().getPropertyOrFail(PLUGIN_DIRS_PROPERTY);
        final File[] pluginLocations = getPluginLocations(pluginDirs);
        return configureExtractors(pluginLocations);
    }

    /**
     * Returns an extractor group containing both the default extractors declared by the
     * {@link io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorRegistry} and the {@link ExtractorPlugin}s.
     *
     * @param registry
     *            an {@link io.github.sparqlanything.html.org.apache.any23.extractor.ExtractorRegistry}
     * @param pluginLocations
     *            optional list of plugin locations.
     *
     * @return a not <code>null</code> and not empty extractor group.
     *
     * @throws IOException
     *             if there is an error locating the extractor group.
     * @throws IllegalAccessException
     *             if there are access permissions for the extractor group.
     * @throws InstantiationException
     *             if there is an error instantiating the extractor group.
     */
    public synchronized ExtractorGroup getApplicableExtractors(ExtractorRegistry registry, File... pluginLocations)
            throws IOException, IllegalAccessException, InstantiationException {
        return configureExtractors(pluginLocations);
    }

    /**
     * Returns an {@link Iterator} of tools that have been detected within the given list of locations.
     *
     * @param pluginLocations
     *            list of plugin locations.
     *
     * @return set of detected tools.
     *
     * @throws IOException
     *             if there is an error acessing {@link io.github.sparqlanything.html.org.apache.any23.cli.Tool}'s.
     */
    public synchronized Iterator<Tool> getApplicableTools(File... pluginLocations) throws IOException {
        final String report = loadPlugins(pluginLocations);
        logger.info(report);
        return getTools();
    }

    /**
     * Converts a column separated list of dirs in a list of files.
     *
     * @param pluginDirsList
     *
     * @return
     */
    private File[] getPluginLocations(String pluginDirsList) {
        final String[] locationsStr = pluginDirsList.split(PLUGIN_DIRS_LIST_SEPARATOR);
        final List<File> locations = new ArrayList<>();
        for (String locationStr : locationsStr) {
            final File location = new File(locationStr);
            if (!location.exists()) {
                throw new IllegalArgumentException(
                        String.format(java.util.Locale.ROOT, "Plugin location '%s' cannot be found.", locationStr));
            }
            locations.add(location);
        }
        return locations.toArray(new File[locations.size()]);
    }

    /**
     * Dynamic local file class loader.
     */
    private static final class DynamicClassLoader extends URLClassLoader {

        private final Set<String> addedURLs = new HashSet<>();

        private final List<File> jars;

        private final List<File> dirs;

        public DynamicClassLoader(URL[] urls) {
            super(urls, Any23PluginManager.class.getClassLoader());
            jars = new ArrayList<>();
            dirs = new ArrayList<>();
        }

        public DynamicClassLoader() {
            this(new URL[0]);
        }

        public boolean addClassDir(File classDir) {
            final String urlPath = "file://" + classDir.getAbsolutePath() + "/";
            try {
                if (addURL(urlPath)) {
                    dirs.add(classDir);
                    return true;
                }
                return false;
            } catch (MalformedURLException murle) {
                throw new RuntimeException("Invalid dir URL.", murle);
            }
        }

        public boolean addJAR(File jar) {
            final String urlPath = "jar:file://" + jar.getAbsolutePath() + "!/";
            try {
                if (addURL(urlPath)) {
                    jars.add(jar);
                    return true;
                }
                return false;
            } catch (MalformedURLException murle) {
                throw new RuntimeException("Invalid JAR URL.", murle);
            }
        }

        private boolean addURL(String urlPath) throws MalformedURLException {
            if (addedURLs.contains(urlPath)) {
                return false;
            }
            super.addURL(new URL(urlPath));
            addedURLs.add(urlPath);
            return true;
        }
    }

}
