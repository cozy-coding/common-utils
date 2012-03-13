package com.suiveg.utils.ftp;

import java.io.IOException;

public class FunctionDisconnect {
    private final FTP FTP;

    public FunctionDisconnect(FTP FTP) {
        this.FTP = FTP;
    }

    public synchronized void disconnect() throws IOException {
        try {
            FTP.getDataConnection().close();
            FTP.getSender().send(Consts.QUIT);
            while (FTP.getReader().hasMoreLines()) {
                FTP.setServerResponse(FTP.getReader().readLine());
                FTP.debug(FTP.getServerResponse());
            }
            if (FTP.getClient().isConnected()) {
                FTP.getClient().close();
            }
        } catch (IOException e) {
            throw new IOException("Failure while disconnecting from server\n" + e);
        }
    }
}