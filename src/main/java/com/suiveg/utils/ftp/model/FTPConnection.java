package com.suiveg.utils.ftp.model;

import com.suiveg.utils.ftp.Reader;
import com.suiveg.utils.ftp.Sender;

import java.io.File;
import java.nio.channels.SocketChannel;

/**
 * Created by IntelliJ IDEA.
 * User: marhaukri
 * Date: 14.03.12
 * Time: 09:55
 * To change this template use File | Settings | File Templates.
 */
public class FTPConnection {
    private String address;
    private int port;
    private String username;
    private String password;
    private SocketChannel client;
    private Reader reader;
    private Sender sender;
    private String serverResponse;
    private File localDirectory;

    
    public FTPConnection(String address, int port, String username, String password) {
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SocketChannel getClient() {
        return client;
    }

    public void setClient(SocketChannel client) {
        this.client = client;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public String getServerResponse() {
        return serverResponse;
    }

    public void setServerResponse(String serverResponse) {
        this.serverResponse = serverResponse;
    }

    public File getLocalDirectory() {
        return localDirectory;
    }

    public void setLocalDirectory(File localDirectory) {
        this.localDirectory = localDirectory;
    }
}
