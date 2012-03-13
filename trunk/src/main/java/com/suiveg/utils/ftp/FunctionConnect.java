package com.suiveg.utils.ftp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;

public class FunctionConnect {
    private final FTP FTP;

    public FunctionConnect(FTP FTP) {
        this.FTP = FTP;
    }

    public synchronized boolean connect() throws IOException {

        // Let's try to open a connection to to the server
        try {
            FTP.setClient(SocketChannel.open());
            FTP.getClient().connect(new InetSocketAddress(FTP.getAddress(), FTP.getPort()));
        } catch (UnresolvedAddressException e) {
            throw new IOException("Unable to connect to server");
        }

        // Create a reader and a sender for the command connection
        FTP.setReader(new Reader(FTP.getClient()));
        FTP.setSender(new Sender(FTP.getClient()));

        while (FTP.getReader().hasMoreLines()) {
            FTP.setServerResponse(FTP.getReader().readLine());
            FTP.debug(FTP.getServerResponse());
            if (!FTP.getServerResponse().startsWith("220")) {
                throw new IOException("Unknown response from server\n " + FTP.getServerResponse());
            }

        }

        // Send username to server and read response
        FTP.getSender().send(Consts.USER + FTP.getUsername());
        while (FTP.getReader().hasMoreLines()) {
            FTP.setServerResponse(FTP.getReader().readLine());
            FTP.debug(FTP.getServerResponse());
            if (!FTP.getServerResponse().startsWith("331")) {
                throw new IOException("Unknown response from server\n " + FTP.getServerResponse());
            }
        }

        // Send password to server and read response
        FTP.getSender().send(Consts.PASS + FTP.getPassword());
        while (FTP.getReader().hasMoreLines()) {
            FTP.setServerResponse(FTP.getReader().readLine());
            FTP.debug(FTP.getServerResponse());
            if (!FTP.getServerResponse().startsWith("230")) {
                throw new IOException("Unknown response from server\n " + FTP.getServerResponse());
            }
        }

        // Get current directory on server
        FTP.getSender().send(Consts.PWD);
        while (FTP.getReader().hasMoreLines()) {
            FTP.setServerResponse(FTP.getReader().readLine());
            FTP.debug(FTP.getServerResponse());
        }
        // Set binary mode
        FTP.getSender().send(Consts.TYPE_I);
        while (FTP.getReader().hasMoreLines()) {
            FTP.setServerResponse(FTP.getReader().readLine());
            FTP.debug(FTP.getServerResponse());
        }
        return true;
    }
}