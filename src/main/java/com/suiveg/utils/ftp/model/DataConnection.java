package com.suiveg.utils.ftp.model;

import com.suiveg.utils.ftp.Reader;
import com.suiveg.utils.ftp.Sender;

import java.nio.channels.SocketChannel;

/**
 * Created by IntelliJ IDEA.
 * User: marhaukri
 * Date: 14.03.12
 * Time: 12:10
 * To change this template use File | Settings | File Templates.
 */
public class DataConnection {
    public Sender dataSender;
    private SocketChannel dataConnection;
    private Reader dataReader;

    /**
     * Constructor
     */
    public void DataConnection() {

    }

    public Sender getDataSender() {
        return dataSender;
    }

    public void setDataSender(Sender dataSender) {
        this.dataSender = dataSender;
    }

    public SocketChannel getDataConnection() {
        return dataConnection;
    }

    public void setDataConnection(SocketChannel dataConnection) {
        this.dataConnection = dataConnection;
    }

    public Reader getDataReader() {
        return dataReader;
    }

    public void setDataReader(Reader dataReader) {
        this.dataReader = dataReader;
    }
}
