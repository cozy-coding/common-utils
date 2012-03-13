package com.suiveg.utils.ftp;

import java.io.IOException;
import java.util.ArrayList;

public class FunctionGet {
    private final FTP FTP;

    public FunctionGet(FTP FTP) {
        this.FTP = FTP;
    }

    public synchronized void getFile(String fileName) throws IOException {
        boolean fileFound = true;
        // Set passive mode to get a data connection
        FTP.getFunctionPassive().passive("get");
        // Send retrieve command and read response from server
        FTP.getSender().send(Consts.RETR + fileName);
        while (FTP.getReader().hasMoreLines()) {
            FTP.setServerResponse(FTP.getReader().readLine());
            FTP.debug(FTP.getServerResponse());
            if (FTP.getServerResponse().startsWith("550")) {
                fileFound = false;
            }
        }

        if (fileFound) {
            FTP.getDataReader().getFile(fileName, FTP.getLocalDirectory());
        }
        while (FTP.getReader().hasMoreLines()) {
            FTP.setServerResponse(FTP.getReader().readLine());
            FTP.debug(FTP.getServerResponse());
        }
    }

    /**
     * getFiles
     * This method gets all files in a folder
     */
    public synchronized void getFiles() throws IOException {
        if (FTP.isConnected()) {
            ArrayList<String> fileList = FTP.getFileList();
            for (String file : fileList) {
                getFile(file);
            }
        }
    }
}