package com.suiveg.utils.ftp;

import com.suiveg.utils.ftp.model.DataConnection;
import com.suiveg.utils.ftp.model.FTPConnection;

import java.io.IOException;
import java.util.ArrayList;

public class FunctionGet {

    public synchronized void getFile(String fileName, FTPConnection ftpConnection, DataConnection dataConnection) throws IOException {
      boolean fileFound = true;
        // Set passive mode to get a data connection
        //FTP.getFunctionPassive().passive("get");

        // Send retrieve command and read response from server
        ftpConnection.getSender().send(Consts.RETR + fileName);
        while (ftpConnection.getReader().hasMoreLines()) {
            ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
            FTP.debug(ftpConnection.getServerResponse());
            if (ftpConnection.getServerResponse().startsWith("550")) {
                fileFound = false;
            }
        }

        if (fileFound) {
            dataConnection.getDataReader().getFile(fileName, ftpConnection.getLocalDirectory());
        }
        while (ftpConnection.getReader().hasMoreLines()) {
            ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
            FTP.debug(ftpConnection.getServerResponse());
        }
    }
}