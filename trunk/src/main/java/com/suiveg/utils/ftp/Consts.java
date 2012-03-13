package com.suiveg.utils.ftp;

/**
 * Created by Marius Haugli Kristensen (t716852)
 * Date: 10.02.12
 * Time: 15:04
 */
public final class Consts {

    /**
     * List of RAW FTP commands. The space in some of them means it takes an argument
     */
    protected final static String ABOR      = "ABOR";   // abort a file transfer
    protected final static String CWD       = "CWD ";   // change working directory
    protected final static String DELE      = "DELE";   // delete a remote file
    protected final static String LIST      = "LIST";   // list remote files
    protected final static String MDTM      = "MDTM";   // return the modification time of a file
    protected final static String MKD       = "MKD";    // make a remote directory
    protected final static String NLST      = "NLST";   // name list of remote directory
    protected final static String PASS      = "PASS ";  // send password
    protected final static String PASV      = "PASV";   // enter passive mode
    protected final static String PORT      = "PORT";   // open a data port
    protected final static String PWD       = "PWD";    // print working directory
    protected final static String QUIT      = "QUIT";   // terminate the connection
    protected final static String RETR      = "RETR ";  // retrieve a remote file
    protected final static String RMD       = "RMD";    // remove a remote directory
    protected final static String RNFR      = "RNFR";   // rename from
    protected final static String RNTO      = "RNTO";   // rename to
    protected final static String SITE      = "SITE";   // site-specific commands
    protected final static String SIZE      = "SIZE";   // return the size of a file
    protected final static String STOR      = "STOR ";  // store a file on the remote host
    protected final static String TYPE      = "TYPE ";   // set transfer type
    protected final static String TYPE_I    = "TYPE I";   // set transfer type binary
    protected final static String USER      = "USER ";  // send username

    protected final static int bufferSize   = 4 * 1024; // buffer size.

    private Consts() {
        throw new AssertionError();
    }
}
