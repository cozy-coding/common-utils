package com.suiveg.utils.ftp;

import com.suiveg.utils.ftp.model.DataConnection;
import com.suiveg.utils.ftp.model.FTPConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionPassive {

    synchronized DataConnection passive(String transferMode, FTPConnection ftpConnection) throws IOException {
        ftpConnection.getSender().send(Consts.PASV);
        while (ftpConnection.getReader().hasMoreLines()) {
            ftpConnection.setServerResponse(ftpConnection.getReader().readLine());
            FTP.debug(ftpConnection.getServerResponse());
        }
        int[] pasvResponse = new int[7];
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(ftpConnection.getServerResponse());
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
        DataConnection dc = new DataConnection();
        dc.setDataConnection(SocketChannel.open());
        dc.getDataConnection().connect(new InetSocketAddress(ftpConnection.getAddress(), dPort));
        if ("get".equals(transferMode)) {
            dc.setDataReader(new Reader(dc.getDataConnection()));
        } else if ("put".equals(transferMode)) {
            dc.setDataSender(new Sender(dc.getDataConnection()));
        }

        return dc;
    }
}