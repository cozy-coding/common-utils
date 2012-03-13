package com.suiveg.utils.http;

import com.suiveg.utils.abs.AbstractUtil;
import com.suiveg.utils.http.model.HttpStatusCodes;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;

import static com.suiveg.utils.encryption.EncryptionUtils.convertToHex;
import static com.suiveg.utils.file.FileUtils.getFileOutputStream;

/**
 *
 * @author <a href="mailto:vegaasen@gmail.com">Vegard Aasen</a>
 * @author <a href="mailto:marius.kristensen@gmail.com">Marius Kristensen</a>
 * @version see system.properties
 * @since 0.2
 */
public class HttpUtils extends AbstractUtil {

    private static final Logger LOGGER = Logger.getLogger(HttpUtils.class);
    private static final String URL_SLASH = "/";

    public static synchronized File getFileFromURL(final URL sourceUrl, File target)
            throws IOException, IllegalArgumentException {
        if (verifyNotNull(sourceUrl)) {
            return getFileUsingURL(sourceUrl, target);
        }
        throw new IllegalArgumentException("String is empty, missing or illegal.");
    }

    public static synchronized File getFileFromURI(final URI sourceUri, File target)
            throws IOException, IllegalArgumentException {
        if (verifyNotNull(sourceUri)) {
            return getFileUsingURL(sourceUri.toURL(), target);
        }
        throw new IllegalArgumentException("String is empty, missing or illegal.");
    }

    public static synchronized InputStream getInputStreamFromURL(final URL sourceUrl)
            throws IOException, NullPointerException {
        if (verifyNotNull(sourceUrl)) {
            HttpURLConnection httpUrlConnection = (HttpURLConnection) sourceUrl.openConnection();
            if (httpUrlConnection.getResponseCode() != HttpStatusCodes.OK_FOUND.getCode()) {
                return httpUrlConnection.getInputStream();
            }
            throw new ConnectException(String.format("Could not fetch InputStream. Http returned status %s, not %s",
                    httpUrlConnection.getResponseCode(), HttpStatusCodes.OK_FOUND.getCode()));
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    public static String getFileName(final URL url) throws NullPointerException {
        if(verifyNotNull(url)) {
            String path = url.getFile();
            if(StringUtils.isNotEmpty(path)) {
                String[] splittedPath = path.split(URL_SLASH);
                if(splittedPath.length>0) {
                    return splittedPath[splittedPath.length - 1];
                }
            }
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    public static String getFileExtension(final URL url) throws NullPointerException {
        if(verifyNotNull(url)) {
            String location = (
                    (StringUtils.isNotEmpty(url.getFile()))?
                            url.getFile():
                            (StringUtils.isNotEmpty(url.getPath())?
                                    url.getPath():
                                    ""));
            if(StringUtils.isNotBlank(location)) {
                if(location.contains(".")) {
                    return location.substring(location.lastIndexOf("."), location.length()-1);
                }
            }
            throw new Error("Unable to parse path, it might be null or empty. " +
                    "Reconfirm the URL-object. [URL was not null]");
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    private static File getFileUsingURL(URL sourceUrl, File target) throws IOException {
        if (verifyNull(target)) {
            target = new File(TEMP_DIRECTORY + File.separator + convertToHex(
                    (DEFAULT_STRING + new Date().getTime()).getBytes())
                    .toString());
        }
        InputStream urlStream = sourceUrl.openStream();
        FileOutputStream targetStream = getFileOutputStream(target);
        try {
            IOUtils.copy(urlStream, targetStream);
            IOUtils.closeQuietly(targetStream);
        } catch (IOException e) {
            LOGGER.error(String.format("Unable to copy the opened stream for {%s}", "{..link..}"));
            throw new IOException();
        } finally {
            IOUtils.closeQuietly(targetStream);
        }
        return target;
    }

}
