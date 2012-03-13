package com.suiveg.utils.ftp;


import com.suiveg.utils.file.FileUtils;

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
    private SocketChannel client;
    private SocketChannel dataConnection;
    private String serverResponse;
    private Sender sender;
    private Sender dataSender;
    private Reader reader;
    private Reader dataReader;
    private String address;
    private int port;
    private String username;
    private String password;
    protected boolean debug = false;
    private File localDirectory;
    private final FunctionConnect functionConnect = new FunctionConnect(this);
    private final FunctionPassive functionPassive = new FunctionPassive(this);
    private final FunctionDisconnect functionDisconnect = new FunctionDisconnect(this);
    private final FunctionGet functionGet = new FunctionGet(this);
    private final FunctionPut functionPut = new FunctionPut(this);

    /**
     * Constructor
     * @param address server address
     * @param port server port
     * @param username the username
     * @param password the password
     */
    public FTP(String address, int port, String username, String password) {
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * connect
     * This opens a connection to the ftp server, using the parameters given in the constructor
     * @return true if connection is successful
     * @throws java.io.IOException IO Exception
     */
    public synchronized boolean connect() throws IOException {
        return functionConnect.connect();
    }

    /**
     * getFile
     * Gets a single file from the FTP server.
     * @param fileName is the name of the file to download
     * @throws java.io.IOException IO Exception
     */
    public synchronized void getFile(String fileName) throws IOException {
        functionGet.getFile(fileName);
    }
    /**
     * getFiles
     * This method gets all files in a folder
     * @throws java.io.IOException IO Exception
     */
    public synchronized void getFiles() throws IOException {
        functionGet.getFiles();
    }

    /**
     * putFile
     * This method uploads a single file to the ftp server
     * @param fileName name of the file to be uploaded
     * @throws java.io.IOException IO Exception
     */
    public synchronized void putFile(String fileName) throws IOException {
        functionPut.putFile(fileName);
    }

    /**
     * passive
     * This method is used to open a data connection to the server. Needed before many of the other
     * functions, like receiving and getting files.
     * @param transferMode put or get, this handles which way the data transfer is going
     * @throws java.io.IOException IO Exception
     */
    private synchronized void passive(String transferMode) throws IOException {
        functionPassive.passive(transferMode);
    }

    protected synchronized ArrayList<String> list() throws IOException {
        ArrayList<String> listResponse = new ArrayList<String>();
        functionPassive.passive("get");
        sender.send(Consts.LIST);
        while (reader.hasMoreLines()) {
            serverResponse = reader.readLine();
            debug(serverResponse);
        }

        while (dataReader.hasMoreLines()) {            
            serverResponse = dataReader.readLine();
            listResponse.add(serverResponse);
            debug(serverResponse);
        }
        while (reader.hasMoreLines()) {
            serverResponse = reader.readLine();
            debug(serverResponse);
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
        sender.send(Consts.CWD+directory);
        while (reader.hasMoreLines()) {
                serverResponse = reader.readLine();
                if(serverResponse.startsWith("550")) {
                    operationStatus = false;
                }
                debug(serverResponse);
        }
        return operationStatus;
    }

    public synchronized String printWorkingDirectory() throws IOException {
        String pwd = "";
        sender.send(Consts.PWD);
        while (reader.hasMoreLines()) {
                serverResponse = reader.readLine();
                debug(serverResponse);
                if (serverResponse.startsWith("257")) {
                    Pattern filePathPattern = Pattern.compile("\"([a-zA-Z0-9_.-/]+)+?\"");
                    Matcher filePathMatcher = filePathPattern.matcher(serverResponse);
                    if (filePathMatcher.find()) {
                        pwd = filePathMatcher.group().replaceAll("\"","");
                    }
                }
        }
        return pwd;
    }
    
    public synchronized void setLocalDirectory(File directory) throws IOException {
        if (directory.exists() && directory.isDirectory()) {
            this.localDirectory = directory;
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
             functionDisconnect.disconnect();
    }

    /**
     * isConnected
     * @return True if connected. False if there is no connection
     */
    public synchronized boolean isConnected() {
        return client.isConnected();
    }

    /**
     * debug
     * Method used while debugging. Prints out all server responses to System.out
     * @param textToPrint the text to write to System.out
     */
    protected void debug(String textToPrint) {
        // Set to false when you don't want debugging in console
        if (debug) {
            System.out.println(textToPrint);
        }
    }

    /**
     * setDebuggingMode
     * @param debugMode true if you want to output debug-info
     */
    protected void setDebuggingMode(boolean debugMode) {
        this.debug  = debugMode;
    }

    /*
     *
     * Getters and setters
     *
     */

    protected String getPassword() {
        return password;
    }

    public File getLocalDirectory() {
        return localDirectory;
    }

    protected String getServerResponse() {
        return serverResponse;
    }

    protected Reader getReader() {
        return reader;
    }

    protected SocketChannel getClient() {
        return client;
    }

    protected int getPort() {
        return port;
    }

    protected Sender getSender() {
        return sender;
    }

    protected String getAddress() {
        return address;
    }

    protected String getUsername() {
        return username;
    }

    protected void setServerResponse(String serverResponse) {
        this.serverResponse = serverResponse;
    }

    protected void setReader(Reader reader) {
        this.reader = reader;
    }

    protected void setClient(SocketChannel client) {
        this.client = client;
    }

    protected void setSender(Sender sender) {
        this.sender = sender;
    }

    protected Sender getDataSender() {
        return dataSender;
    }

    protected Reader getDataReader() {
        return dataReader;
    }

    protected SocketChannel getDataConnection() {
        return dataConnection;
    }

    protected void setDataSender(Sender dataSender) {
        this.dataSender = dataSender;
    }

    protected void setDataReader(Reader dataReader) {
        this.dataReader = dataReader;
    }

    protected void setDataConnection(SocketChannel dataConnection) {
        this.dataConnection = dataConnection;
    }

    protected FunctionPassive getFunctionPassive() {
        return functionPassive;
    }
}
