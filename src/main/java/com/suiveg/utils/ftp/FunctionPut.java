package com.suiveg.utils.ftp;

import java.io.File;
import java.io.IOException;

public class FunctionPut {
    private final FTP FTP;

    public FunctionPut(FTP FTP) {
        this.FTP = FTP;
    }

    public synchronized void putFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (file.exists()) {
            FTP.getFunctionPassive().passive("put");
            FTP.getSender().send(Consts.STOR + fileName);
            while (FTP.getReader().hasMoreLines() && !FTP.getServerResponse().startsWith("226")) {
                FTP.setServerResponse(FTP.getReader().readLine());
                FTP.debug(FTP.getServerResponse());
            }

            if (FTP.getDataSender().putFile(file)) {
                while (FTP.getReader().hasMoreLines()) {
                    FTP.setServerResponse(FTP.getReader().readLine());
                    FTP.debug(FTP.getServerResponse());
                }
            } else {
                throw new IOException("Something went wrong while sending " + fileName + ".");
            }
        }
    }
}