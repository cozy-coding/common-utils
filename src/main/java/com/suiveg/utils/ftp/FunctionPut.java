package com.suiveg.utils.ftp;

import com.suiveg.utils.ftp.model.DataConnection;
import com.suiveg.utils.ftp.model.FTPConnection;

import java.io.File;
import java.io.IOException;

public class FunctionPut {

    public synchronized void putFile(String fileName, FTPConnection ftpConnection, DataConnection dataConnection) throws IOException {
      File file = new File(ftpConnection.getLocalDirectory(),fileName);
        if (file.exists()) {
            ftpConnection.getSender().send(Consts.STOR + fileName);
            while (ftpConnection.getReader().hasMoreLines() && !ftpConnection.getServerResponse().startsWith("226")) {
                ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
                FTP.debug(ftpConnection.getServerResponse());
            }

            if (dataConnection.getDataSender().putFile(file)) {
                while (ftpConnection.getReader().hasMoreLines()) {
                    ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
                    FTP.debug(ftpConnection.getServerResponse());
                }
            } else {
                throw new IOException("Something went wrong while sending " + fileName + ".");
            }
        } else {
            throw new IOException("File don't exist");
        }
    }
}