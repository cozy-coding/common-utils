package com.suiveg.utils.abs;

/**
 * Abstract class for all Utilities
 *
 * @author <a href="mailto:vegaasen@gmail.com">Vegard Aasen</a>
 * @author <a href="mailto:marius.kristensen@gmail.com">Marius Kristensen</a>
 * @version see system.properties
 * @since 0.1
 */
public abstract class AbstractUtil {

    protected static final String E_OBJECT_WAS_NULL = "Object was null";
    protected static final String E_FILE_NOT_EXIST = "File does not exist";
    protected static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
    protected static final String DEFAULT_STRING = "tempFile";
    
    protected static boolean verifyNotNull(final Object... object) {
        boolean objectWasNotNull = true;
        for(Object o : object) {
            if(o==null) {
                objectWasNotNull = false;
                break;
            }
        }
        return objectWasNotNull;
    }

    protected static boolean verifyNull(final Object object) {
        return (object==null);
    }

}
