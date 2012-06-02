package com.suiveg.utils.properties;

import com.suiveg.utils.exceptions.NoSuchFileException;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Simple utility that helps loading properties.
 * These options loading a properties-file is supported in this class:
 * -ClassLoader
 * -ResourceBundle
 *
 * @author <a href="mailto:vegaasen@gmail.com">Vegard Aasen</a>
 * @author <a href="mailto:marius.kristensen@gmail.com">Marius Kristensen</a>
 * @version see system.properties
 * @since 0.2-SNAPSHOT
 */
public class PropUtils {

    private static final Logger LOGGER = Logger.getLogger(PropUtils.class);
    private static final String SUFFIX = ".properties";

    /**
     * @param propertiesFileName Properties File Name - e.g system.properties
     * @param loader             - the ClassLoader to fetch the properties-file from
     * @return Properties-object containing the loaded properties
     */
    public static Properties loadProperties(String propertiesFileName,
                                            ClassLoader loader,
                                            final boolean loadAsResourceBundle) throws NoSuchFileException, IllegalArgumentException {
        if (propertiesFileName == null) {
            throw new IllegalArgumentException("Missing argument, \"name\"");
        }

        if (propertiesFileName.startsWith("/")) {
            propertiesFileName = propertiesFileName.substring(1);
        }

        if (propertiesFileName.endsWith(SUFFIX)) {
            propertiesFileName = propertiesFileName.substring(0, propertiesFileName.length() - SUFFIX.length());
        }

        Properties result = null;
        InputStream in = null;

        try {
            if (loader == null) {
                loader = ClassLoader.getSystemClassLoader();
            }

            if (loadAsResourceBundle) {
                propertiesFileName = propertiesFileName.replace('/', '.');
                final ResourceBundle rb = ResourceBundle.getBundle(propertiesFileName, Locale.getDefault(), loader);
                result = new Properties();
                for (Enumeration keys = rb.getKeys(); keys.hasMoreElements(); ) {
                    final String key = (String) keys.nextElement();
                    final String value = rb.getString(key);
                    result.put(key, value);
                }
            } else {
                propertiesFileName = propertiesFileName.replace('.', '/');

                if (!propertiesFileName.endsWith(SUFFIX))
                    propertiesFileName = propertiesFileName.concat(SUFFIX);

                // Returns null on lookup failures:
                in = loader.getResourceAsStream(propertiesFileName);
                if (in != null) {
                    result = new Properties();
                    result.load(in); // Can throw IOException
                }
            }
        } catch (Exception e) {
            result = null;
        } finally {
            if (in != null) try {
                in.close();
            } catch (Throwable ignore) {
            }
        }

        if (result == null) {
            throw new NoSuchFileException("No such file. Could not load [" + propertiesFileName + "]" +
                    " as " + (loadAsResourceBundle
                    ? " resource bundle"
                    : " classloader resource"));
        }

        return result;
    }

    /**
     * A convenience overload of {@link #loadProperties(String, ClassLoader, boolean)}
     * that uses the current thread's context classloader.
     */
    public static Properties loadProperties(final String name, final boolean loadAsResourceBundle) {
        try {
            return loadProperties(name, Thread.currentThread().getContextClassLoader(), loadAsResourceBundle);
        } catch (Exception e) {
            LOGGER.error("No such file.");
        }
        throw new IllegalArgumentException("Verify input parameters");
    }

    /**
     * A convenience overload of {@link #loadProperties(String, ClassLoader, boolean)}
     * that uses the current thread's context classloader and no resource-bundle
     */
    public static Properties loadProperties(final String name) {
        try {
            return loadProperties(name, Thread.currentThread().getContextClassLoader(), false);
        } catch (Exception e) {
            LOGGER.error("No such file.");
        }
        throw new IllegalArgumentException("Verify input parameters");
    }

}