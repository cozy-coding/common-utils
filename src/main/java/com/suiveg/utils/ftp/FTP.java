package com.suiveg.utils.ftp;


import com.suiveg.utils.file.FileUtils;
import com.suiveg.utils.ftp.model.DataConnection;
import com.suiveg.utils.ftp.model.FTPConnection;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FTP - Does FTP stuff.
 *
 * Todo: more info
 *
 * @author <a href="mailto:vegaasen@gmail.com">Vegard Aasen</a>
 * @author <a href="mailto:marius.kristensen@gmail.com">Marius Kristensen</a>
 * @version see system.properties
 * @since 0.1
 */

public class FTP extends FileUtils {

    private FTPConnection ftpConnection;
    private ConnectionManager connectionManager;
    protected static Debugger debugger;
    /**
     * Constructor
     * @param address server address
     * @param port server port
     * @param username the username
     * @param password the password
     */
    public FTP(String address, int port, String username, String password) {
        this.ftpConnection = new FTPConnection(address,port,username,password);
        debugger = new Debugger();
    }

    /**
     * connect
     * This opens a connection to the ftp server, using the parameters given in the constructor
     * @return true if connection is successful
     * @throws java.io.IOException IO Exception
     */
    public synchronized boolean connect() throws IOException {
        connectionManager = new ConnectionManager();
        return connectionManager.connect(this.ftpConnection);
    }

    /**
     * getFile
     * Gets a single file from the FTP server.
     * @param fileName is the name of the file to download
     * @throws java.io.IOException IO Exception
     */
    public synchronized void getFile(String fileName) throws IOException {
        FunctionGet get = new FunctionGet();
        DataConnection dataConnection = passive("get");
        get.getFile(fileName, this.ftpConnection, dataConnection);
    }
    /**
     * getFiles
     * This method gets all files in a folder
     * @throws java.io.IOException IO Exception
     */
    public synchronized void getFiles() throws IOException {
        ArrayList<String> fileList = getFileList();
        for (String file : fileList) {
            getFile(file);
        }
    }

    /**
     * putFile
     * This method uploads a single file to the ftp server
     * @param fileName name of the file to be uploaded
     * @throws java.io.IOException IO Exception
     */
    public synchronized void putFile(String fileName) throws IOException {
        FunctionPut put = new FunctionPut();
        DataConnection dataConnection = passive("put");
        put.putFile(fileName, this.ftpConnection, dataConnection);
    }

    /**
     * passive
     * This method is used to open a data connection to the server. Needed before many of the other
     * functions, like receiving and getting files.
     * @param transferMode put or get, this handles which way the data transfer is going
     * @throws java.io.IOException IO Exception
     */
    private synchronized DataConnection passive(String transferMode) throws IOException {
        FunctionPassive functionPassive = new FunctionPassive();
        DataConnection dataConnection = functionPassive.passive(transferMode, this.ftpConnection);
        return dataConnection;
    }

    protected synchronized ArrayList<String> list() throws IOException {
        ArrayList<String> listResponse = new ArrayList<String>();
        DataConnection dc = passive("get");
        ftpConnection.getSender().send(Consts.LIST);
        while (ftpConnection.getReader().hasMoreLines()) {
            ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
            debugger.debug(ftpConnection.getServerResponse());
        }

        while (dc.getDataReader().hasMoreLines()) {
            //serverResponse = dc.getDataReader().readLine();
            ftpConnection.setServerResponse(dc.getDataReader().readLine());
            listResponse.add(ftpConnection.getServerResponse());
            debugger.debug(ftpConnection.getServerResponse());
        }
        while (ftpConnection.getReader().hasMoreLines()) {
            ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
            debugger.debug(ftpConnection.getServerResponse());
        }
        return listResponse;
    }
    
    public synchronized ArrayList<String> getFileList() throws IOException {
        ArrayList<String> listResponse = list();
        ArrayList<String> fileList = new ArrayList<String>();
        Pattern filePattern = Pattern.compile("^(-).*([a-zA-Z0-9_.-]+)+?$");  //lists all files
        Pattern fileNamePattern = Pattern.compile("([a-zA-Z0-9_.-]+)+?$"); // Gets only filename
        
        for (String line: listResponse) {
            Matcher m = filePattern.matcher(line);
            while (m.find()) {
                Matcher fileNameMatcher = fileNamePattern.matcher(m.group());
                fileNameMatcher.find();
                //System.out.println(fileNameMatcher.group());
                fileList.add(fileNameMatcher.group());
            }
        }
        return fileList;
    }

    public synchronized ArrayList<String> getDirectoryList() throws IOException {
        ArrayList<String> listResponse = list();
        ArrayList<String> directoryList = new ArrayList<String>();
        // This pattern should find all directories (lines starting with d)
        Pattern directoryPattern = Pattern.compile("^(d).*([a-zA-Z0-9_.-]+)+?$");
        // This pattern gets only the directory name
        Pattern dirNamePattern = Pattern.compile("([a-zA-Z0-9_.-]+)+?$");
        
        for (String line: listResponse) {
            Matcher m = directoryPattern.matcher(line);
            // find directories
            while (m.find()) {
                // if directory, get directory name
                Matcher dirNameMatcher = dirNamePattern.matcher(m.group());
                if(dirNameMatcher.find()) directoryList.add(dirNameMatcher.group());
            }
        }
        return directoryList;
    }

    public synchronized boolean changeRemoteDirectory(String directory) throws IOException {
        boolean operationStatus = true;
        ftpConnection.getSender().send(Consts.CWD + directory);
        while (ftpConnection.getReader().hasMoreLines()) {
                ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
                if(ftpConnection.getServerResponse().startsWith("550")) {
                    operationStatus = false;
                }
            debugger.debug(ftpConnection.getServerResponse());
        }
        return operationStatus;
    }

    public synchronized String printWorkingDirectory() throws IOException {
        String pwd = "";
        ftpConnection.getSender().send(Consts.PWD);
        while (ftpConnection.getReader().hasMoreLines()) {
            ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
            debugger.debug(ftpConnection.getServerResponse());
                if (ftpConnection.getServerResponse().startsWith("257")) {
                    Pattern filePathPattern = Pattern.compile("\"([a-zA-Z0-9_.-/]+)+?\"");
                    Matcher filePathMatcher = filePathPattern.matcher(ftpConnection.getServerResponse());
                    if (filePathMatcher.find()) {
                        pwd = filePathMatcher.group().replaceAll("\"","");
                    }
                }
        }
        return pwd;
    }
    
    public synchronized void setLocalDirectory(File directory) throws IOException {
        if (directory.exists() && directory.isDirectory()) {
            this.ftpConnection.setLocalDirectory(directory);
        } else {
            throw new IOException(directory+" don't exist, or is not a directory");
        }
    }

    /**
     * disconnect
     * This method closes the connection to the ftp server
     * @throws java.io.IOException IO Exception
     */
    public synchronized void disconnect() throws IOException {
        connectionManager.disconnect(this.ftpConnection);
    }

    /**
     * isConnected
     * @return True if connected. False if there is no connection
     */
    public synchronized boolean isConnected() {
        return ftpConnection.getClient().isConnected();
    }

    /**
     * debug
     * Method used while debugging. Prints out all server responses to System.out
     * @param textToPrint the text to write to System.out
     */
    protected static void debug(String textToPrint) {
        debugger.debug(textToPrint);
    }

    /**
     * setDebuggingMode
     * @param debugMode true if you want to output debug-info
     */
    protected void setDebuggingMode(boolean debugMode) {
        debugger.setDebug(debugMode);
    }

}
