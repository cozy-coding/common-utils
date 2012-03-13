package com.suiveg.utils.ftp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionPassive {
    private final FTP FTP;

    public FunctionPassive(FTP FTP) {
        this.FTP = FTP;
    }

    synchronized void passive(String transferMode) throws IOException {
        FTP.getSender().send(Consts.PASV);
        while (FTP.getReader().hasMoreLines()) {
            FTP.setServerResponse(FTP.getReader().readLine());
            FTP.debug(FTP.getServerResponse());
        }
        int[] pasvResponse = new int[7];
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(FTP.getServerResponse());
        int i = 0;
        while (m.find()) {
            String s = m.group();
            pasvResponse[i] = Integer.parseInt(s);
            i++;
        }
        // Calculate what data connection port from response
        int dPort = (pasvResponse[5] * 256) + pasvResponse[6];
        // Open data connection
        FTP.debug("Opening dataconnection on port " + dPort);
        FTP.setDataConnection(SocketChannel.open());
        FTP.getDataConnection().connect(new InetSocketAddress(FTP.getAddress(), dPort));
        if ("get".equals(transferMode)) {
            FTP.setDataReader(new Reader(FTP.getDataConnection()));
        } else if ("put".equals(transferMode)) {
            FTP.setDataSender(new Sender(FTP.getDataConnection()));
        }
    }
}