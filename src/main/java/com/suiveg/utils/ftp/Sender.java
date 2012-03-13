package com.suiveg.utils.ftp;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 *
 *
 * Todo: more info
 *
 * @author <a href="mailto:vegaasen@gmail.com">Vegard Aasen</a>
 * @author <a href="mailto:marius.kristensen@gmail.com">Marius Kristensen</a>
 * @version see system.properties
 * @since 0.1
 */
public class Sender {
    private SocketChannel client;


    protected Sender(SocketChannel client) {
        this.client = client;
    }

    protected synchronized boolean putFile(File file) {

        ByteBuffer buf = ByteBuffer.allocate(Consts.bufferSize);
        try {
            FileInputStream fis = new FileInputStream(file);
            long position = 0;
            while (position < file.length()) {
                fis.getChannel().read(buf, position);
                buf.flip();
                client.write(buf);
                position += (Consts.bufferSize);
                buf.clear();
            }
            client.close();
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * send
     * @param lineToSend The command to send to the FTP-server
     * @throws IOException When there is some trouble with the connection.
     */
    protected synchronized void send(String lineToSend) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(Consts.bufferSize);
        byte[] b = (lineToSend+"\n").getBytes();
        buf.put(b);
        buf.flip();
        client.write(buf);
        //System.out.println(lineToSend);
    }
}
