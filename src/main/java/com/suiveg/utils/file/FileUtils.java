package com.suiveg.utils.file;

import com.suiveg.utils.abs.AbstractUtil;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.rmi.NoSuchObjectException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * FileUtils is a class that contains static methods for performing File-based actions. These file based actions can be:
 * -Copy
 * -Delete folder/folder structure
 * -Get file from urls
 * -Verifications of file
 * -FileToByte
 * -Size
 *
 * This is constantly under development.
 *
 * Note: Not Java 1.7 / Java 7 certified. An overloading class for using NIO will be written on the side
 *
 *
 * @author <a href="mailto:vegaasen@gmail.com">Vegard Aasen</a>
 * @author <a href="mailto:marius.kristensen@gmail.com">Marius Kristensen</a>
 * @version see system.properties
 * @since 0.1
 */
public class FileUtils extends AbstractUtil {

    private static final Logger LOGGER = Logger.getLogger(FileUtils.class);

    public static final long KB = 1024;
    public static final long MB = (KB * KB);
    public static final long GB = (KB * MB);
    public static final long TB = (KB * GB);

    private static final String NAN = "NaN";
    private static final String NAF = "NaF";

    public static final boolean HIDE_SIZE_TYPE = Boolean.TRUE;
    public static final boolean SHOW_SIZE_TYPE = Boolean.FALSE;

    public FileUtils() {
        super();
    }

    /**
     * Get the filename of a file
     *
     * @param file File to investigate
     * @param omitFileExtension if set to false, shows the extension and vice versa
     * @return fileName
     * @throws NullPointerException _
     * @throws IOException _
     */
    public static String getFileName(final File file, final boolean omitFileExtension)
            throws NullPointerException, IOException{
        if(verifyNotNull(file)) {
            if(file.exists()) {
                String fileName = file.getName();
                return generateFileName(omitFileExtension, fileName);
            }
            throw new IOException(E_FILE_NOT_EXIST);
        }
        throw new NullPointerException(String.format(E_OBJECT_WAS_NULL));
    }

    /**
     * Get filename based on a path/filename
     *
     * @param fileName path/filname
     * @param omitFileExtension if set to false, shows the extension and vice versa
     * @return
     * @throws NullPointerException
     */
    public static String getFileName(final String fileName, final boolean omitFileExtension)
            throws NullPointerException {
        if(verifyNotNull(fileName)) {
            return generateFileName(omitFileExtension, fileName);
        }
        throw new NullPointerException(String.format(E_OBJECT_WAS_NULL));
    }

    /**
     * Returns a file as FileOutputStream
     *
     * @param file the file in question
     * @return FileOutputStream
     * @throws IOException _
     * @throws NullPointerException _
     */
    public static FileOutputStream getFileOutputStream(final File file)
            throws IOException,
            NullPointerException {
        if (verifyNotNull(file)) {
            if (file.exists()) {
                if (file.isDirectory()) {
                    throw new IOException(String.format("The file <%s> is not a file, its a directory!", file));
                }
                if (!file.canWrite()) {
                    throw new IOException(String.format("The file <%s> could not be altered", file));
                }
            } else {
                final File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    if (!parent.mkdirs()) {
                        throw new IOException(String.format("The file <%s> could not be created.", file));
                    }
                }
            }
            return new FileOutputStream(file);
        }
        throw new NullPointerException(String.format(E_OBJECT_WAS_NULL));
    }

    /**
     * Returns a file as FileInputStream
     *
     * @param file the file in question
     * @return FileInputStream
     * @throws IOException _
     * @throws NullPointerException _
     */
    public static FileInputStream getFileInputStream(final File file)
            throws IOException,
            NullPointerException {
        if (verifyNotNull(file)) {
            if (file.exists()) {
                if (file.isDirectory()) {
                    throw new IOException(String.format("The file <%s> is not a file, its a directory!", file));
                }
                if (!file.canRead()) {
                    throw new IOException(String.format("The file <%s> could not be altered", file));
                }
            } else {
                throw new IOException(E_FILE_NOT_EXIST);
            }
            return new FileInputStream(file);
        }
        throw new NullPointerException(String.format(E_OBJECT_WAS_NULL));
    }

    /**
     * Get the size of a file as Human Readable Size
     *
     * @param fileSize filesize
     * @param fileSizeType the conversion-size
     * @param omitFormatType if to omit the formatType (e.g KB, MB, GB etc)
     * @return the size
     * @throws NullPointerException
     */
    public static String getHumanReadableFileSize(final Long fileSize,
                                                  final FileSizeType fileSizeType,
                                                  final boolean omitFormatType)
            throws NullPointerException {
        if (fileSize != null && fileSizeType != null) {
            if (fileSize > 0) {
                switch (fileSizeType) {
                    case BYTE:
                        return Long.toString(fileSize);
                    case KILOBYTE:
                        return Long.toString(Math.round(fileSize / (KB))) + ((omitFormatType) ? "" : "KB");
                    case MEGABYTE:
                        return Long.toString(Math.round(fileSize / (MB))) + ((omitFormatType) ? "" : "MB");
                    case GIGABYTE:
                        return Long.toString(Math.round(fileSize / (GB))) + ((omitFormatType) ? "" : "GB");
                    case TERABYTE:
                        return Long.toString(Math.round(fileSize / (TB))) + ((omitFormatType) ? "" : "TB");
                    default:
                        return NAF;
                }
            }
            return NAN;
        }
        throw new NullPointerException("The size provided was null");
    }

    /**
     * Perform a UNIX "touch" on a file to set last modified
     *
     * @param file the file in question
     * @throws NullPointerException _
     * @throws IOException _
     */
    public static void setLastModifiedForFile(final File file)
            throws NullPointerException, IOException {
        if (verifyNotNull(file)) {
            if (file.exists()) {
                FileOutputStream outputStream = getFileOutputStream(file);
                IOUtils.closeQuietly(outputStream);
            }
            if (file.canWrite()) {
                if (!file.setLastModified(new Date().getTime())) {
                    throw new IOException(String.format(""));
                }
            } else {
                throw new IOException(String.format(""));
            }
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    /**
     * Read a file to a string
     *
     * @param file _
     * @param encoding the encoding type
     * @return _
     * @throws IOException _
     * @throws NoSuchObjectException _
     */
    public static String readFileToString(final File file, final FileEncoding encoding)
            throws IOException,
            NoSuchObjectException {
        if (verifyNotNull(file)) {
            if (file.exists()) {
                FileInputStream inputStream = null;
                try {
                    inputStream = getFileInputStream(file);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                }
                return IOUtils.toString(inputStream, encoding.toString());
            }
            throw new IOException(E_FILE_NOT_EXIST);
        }
        throw new NoSuchObjectException(E_OBJECT_WAS_NULL);
    }

    /**
     * Read a file to a byte array
     *
     * @param file _
     * @return byte array containing the contents of a file
     * @throws FileNotFoundException _
     */
    public static byte[] readFileToByteArray(final File file)
            throws FileNotFoundException {
        if (verifyNotNull(file)) {
            if (file.isDirectory())
                throw new RuntimeException(String.format("File %s is a directory.", file.getAbsolutePath()));
            if (file.length() > Integer.MAX_VALUE)
                throw new RuntimeException(String.format("File %s is too large to process.", file.getAbsolutePath()));
            FileInputStream in = null;
            final byte buffer[] = new byte[(int) file.length()];
            try {
                in = new FileInputStream(file);
                in.read(buffer);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return buffer;
        }
        throw new FileNotFoundException(E_OBJECT_WAS_NULL);
    }

    /**
     * Read size of a whole directory
     *
     * @param directory _
     * @param fileSizeType the file size type (e.g KB, MB, GB etc)
     * @return size
     * @throws IllegalArgumentException
     */
    public static long readDirectoryContentSize(final File directory, final FileSizeType fileSizeType)
            throws IllegalArgumentException {
        if (verifyNotNull(directory)) {
            if (!directory.exists()) {
                throw new IllegalArgumentException(String.format("%s does not exist", directory));
            }
            if (!directory.isDirectory()) {
                throw new IllegalArgumentException(String.format("%s is not a directory", directory));
            }
            long size = 0;
            List<File> files = Arrays.asList(directory.listFiles());
            if (files == null || files.size() == size) return 0;
            for (File file : files) {
                if (file.isDirectory()) {
                    size += readDirectoryContentSize(file, null);
                } else {
                    size += file.length();
                }
            }
            size = Long.getLong(getHumanReadableFileSize(size, fileSizeType, HIDE_SIZE_TYPE));
            return size;
        }
        throw new IllegalArgumentException(E_OBJECT_WAS_NULL);
    }

    /**
     * Compare a file to a date (is newer than)
     *
     * @param file _
     * @param date the date
     * @return true/false
     * @throws IllegalArgumentException _
     */
    public static boolean isFileNewerThan(final File file, final Date date)
            throws IllegalArgumentException {
        if (verifyNotNull(file)) {
            if (date != null) {
                return isFileNewerThan(file, date.getTime());
            }
            throw new IllegalArgumentException("Date is null or fucked up.");
        }
        throw new IllegalArgumentException(E_OBJECT_WAS_NULL);
    }

    /**
     * Compare a file to a long (is newer than)
     * @param file _
     * @param time timeInLong
     * @return true/false
     */
    public static boolean isFileNewerThan(final File file, final Long time) {
        if (verifyNotNull(file)) {
            if (time != null && time > 0) {
                return file.lastModified() > time;
            }
            throw new IllegalArgumentException("Date is null or fucked up.");
        }
        throw new IllegalArgumentException(E_OBJECT_WAS_NULL);
    }

    /**
     * Create a folder on a location
     *
     * @param folder the new folder
     * @return true/false
     * @throws Exception _
     */
    public static boolean createFolder(final File folder) throws Exception {
        if(verifyNotNull(folder)) {
            if(folder.exists() && !folder.isDirectory()) {
                throw new Exception(String.format("File %s exists.", folder.getName()));
            }else{
                return folder.mkdir();
            }
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    /**
     * Perform a file-delete to a file, if exists
     *
     * @param file the file to delete
     * @return true/false
     * @throws NullPointerException _
     */
    public static boolean deleteIfFileExists(final File file)
            throws NullPointerException {
        if(verifyNotNull(file)) {
            return file.exists() && file.delete();
        }
        throw new NullPointerException(E_OBJECT_WAS_NULL);
    }

    private static String generateFileName(boolean omitFileExtension, String fileName) {
        if(omitFileExtension) {
            if(fileName.contains(".")) {
                if(fileName.lastIndexOf(".")!=fileName.length()) {
                    fileName = (fileName.substring(fileName.lastIndexOf("."), (fileName.length()-1)));
                }
                return fileName;
            }else{
                return "Your File Does Not Contain Any Extensions. \n" +
                        "Please set omitFileExtensions-flag to false. \n" +
                        "FileName: " + fileName;
            }
        }else{
            return fileName;
        }
    }

    /**
     * FILE-SIZE-TYPES
     */
    public enum FileSizeType {
        BYTE, KILOBYTE, MEGABYTE, GIGABYTE, TERABYTE
    }

    /**
     * File-Encodings
     */
    public enum FileEncoding {
        DEFAULT("utf-8"),
        UTF_8("utf-8"),
        ISO_8859_1("iso-8859-1"),
        WINDOWS_1252("windows-1252");

        private String fileEncoding;

        FileEncoding(String encoding) {
            this.fileEncoding = encoding;
        }

        @Override
        public String toString() {
            return fileEncoding;
        }
    }

}
