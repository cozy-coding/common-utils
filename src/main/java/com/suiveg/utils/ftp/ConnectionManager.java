package com.suiveg.utils.ftp;

import com.suiveg.utils.ftp.model.FTPConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;

public class ConnectionManager {


    public synchronized boolean connect(FTPConnection ftpConnection) throws IOException {
        if(openConnection(ftpConnection)) {
            createReaderAndSender(ftpConnection);
            login(ftpConnection);
            initialSetup(ftpConnection);
            return true;
        } else {
            return false;
        }
    }

    public synchronized void disconnect(FTPConnection ftpConnection) throws IOException {
        try {
            ftpConnection.getSender().send(Consts.QUIT);
            while (ftpConnection.getReader().hasMoreLines()) {
                ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
                FTP.debug(ftpConnection.getServerResponse());
            }
            if (ftpConnection.getClient().isConnected()) {
                ftpConnection.getClient().close();
            }
        } catch (IOException e) {
            throw new IOException("Failure while disconnecting from server\n" + e);
        }
    }

    /**
     * openConnection
     * This opens the SocketChannel to the ftp server
     * @return returns true if connection succeeded
     * @throws IOException
     */
    private boolean openConnection(FTPConnection ftpConnection) throws IOException {
        // Let's try to open a connection to to the server
        try {
            ftpConnection.setClient(SocketChannel.open());
            ftpConnection.getClient().connect(new InetSocketAddress(ftpConnection.getAddress(), ftpConnection.getPort()));
            return true;
        } catch (UnresolvedAddressException e) {
            throw new IOException("Unable to connect to server");
        }
    }

    /**
     * createReaderAndSender
     * Sets up a Sender to send raw ftp commands to server
     * Sets up a Reader to read back answers from commands
     * @throws IOException -
     */
    private void createReaderAndSender(FTPConnection ftpConnection) throws IOException {
        // Create a reader and a sender for the command connection
        ftpConnection.setReader(new Reader(ftpConnection.getClient()));
        ftpConnection.setSender(new Sender(ftpConnection.getClient()));

        while (ftpConnection.getReader().hasMoreLines()) {
            ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
            FTP.debug(ftpConnection.getServerResponse());
            if (!ftpConnection.getServerResponse().startsWith("220")) {
                throw new IOException("Unknown response from server\n " + ftpConnection.getServerResponse());
            }

        }
    }
    /**
     * login
     * Sends username and password to server
     * @throws IOException if we get an unwanted response from server
     */
    private void login(FTPConnection ftpConnection) throws IOException {
        // Send username to server and read response
        ftpConnection.getSender().send(Consts.USER + ftpConnection.getUsername());
        while (ftpConnection.getReader().hasMoreLines()) {
            ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
            FTP.debug(ftpConnection.getServerResponse());
            if (!ftpConnection.getServerResponse().startsWith("331")) {
                throw new IOException("Unknown response from server\n " + ftpConnection.getServerResponse());
            }
        }

        // Send password to server and read response
        ftpConnection.getSender().send(Consts.PASS + ftpConnection.getPassword());
        while (ftpConnection.getReader().hasMoreLines()) {
            ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
            FTP.debug(ftpConnection.getServerResponse());
            if (!ftpConnection.getServerResponse().startsWith("230")) {
                throw new IOException("Unknown response from server\n " + ftpConnection.getServerResponse());
            }
        }
    }
    /**
     * run these commands when connected
     * @throws IOException -
     */
    private void initialSetup(FTPConnection ftpConnection) throws IOException {
        // Get current directory on server
        ftpConnection.getSender().send(Consts.PWD);
        while (ftpConnection.getReader().hasMoreLines()) {
            ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
            FTP.debug(ftpConnection.getServerResponse());
        }
        // Set binary mode
        ftpConnection.getSender().send(Consts.TYPE_I);
        while (ftpConnection.getReader().hasMoreLines()) {
            ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
            FTP.debug(ftpConnection.getServerResponse());
        }
    }
}