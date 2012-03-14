package com.suiveg.utils.ftp;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import static com.suiveg.utils.file.FileUtils.deleteIfFileExists;

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
public class Reader {

    private int totalResponseLines = 0;
    private int currentResponseLine = 0;
    private boolean hasMoreLines = true;
    private boolean nextTimeReturn = true;
    private int firstRun = 0;
    private String[] response;
    private SocketChannel client;


    protected Reader(SocketChannel client) {
        this.client = client;
    }

    protected synchronized String[] read() {
        StringBuilder response = new StringBuilder();
        ByteBuffer buf = ByteBuffer.allocate(Consts.bufferSize);
        int bytesRead = 0;
        try {
            bytesRead = client.read(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bytesRead > 0) {
            buf.flip();
            // create a new byte array that is the size of the buffer
            byte[] b = new byte[buf.remaining()];
            // fill b with the bytes from ByteBuffer
            buf.get(b,0,b.length);

            for (byte aB : b) {
                // Append all chars to StringBuilder
                response.append((char) aB);
            }
        } else {
            return null;
        }
        return response.toString().split("\n");
    }

    protected synchronized void getFile(String fileName, File localDirectory) throws IOException, NullPointerException {

        int bytesRead = 1;
        boolean append = true;
        ByteBuffer buf = ByteBuffer.allocate(Consts.bufferSize);
        File file = new File(localDirectory,fileName);
        deleteIfFileExists(file);
        FileChannel writeChannel = new FileOutputStream(file, append).getChannel();
        while (bytesRead > 0) {
            try {
                bytesRead = client.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bytesRead != -1) {
                buf.flip();
                // Write buffer content to file
                writeChannel.write(buf);
                // Make sure we have written everything from buffer
                if (buf.hasRemaining()) {
                    buf.compact();
                } else {
                    buf.clear();
                }
            }
        }
        writeChannel.close();
    }


    protected synchronized String readLine() {

        if (response == null) {
            response = read();
            if (response == null) {
                totalResponseLines = 0;
                currentResponseLine = 0;
                hasMoreLines = false;
                firstRun = 0;
                return "Nothing to read...";
            }
            totalResponseLines = response.length;
            currentResponseLine = 0;
            firstRun = 0;
        }
        String line = response[currentResponseLine];
        currentResponseLine++;
        hasMoreLines = true;
        if (currentResponseLine >= totalResponseLines) {
            hasMoreLines = false;
            firstRun = 0;
            currentResponseLine = 0;
            nextTimeReturn = false;
            response = null;
        }
        return line;
    }

    protected synchronized boolean hasMoreLines() {
        if (firstRun == 0 && nextTimeReturn) {
            firstRun++;
            return true;
        } else if(firstRun == 0 && !nextTimeReturn) {
            nextTimeReturn = true;
            return false;
        } else {
            firstRun++;
            return hasMoreLines;
        }
    }

    protected synchronized void closeConnection() throws IOException {
        this.client.close();
    }
    protected synchronized boolean isConnected() throws IOException {
        return this.client.isConnected();
    }

}
