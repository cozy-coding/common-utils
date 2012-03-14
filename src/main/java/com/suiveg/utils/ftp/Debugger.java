package com.suiveg.utils.ftp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Debugger {
    protected boolean debug = false;
    private String info = "INFO----:";
    private String warn = "WARN----:";
    private String err  = "ERROR---:";

    /**
     * debug
     * Method used while debugging. Prints out all server responses to System.out
     *
     * @param textToPrint the text to write to System.out
     */
    protected void debug(String textToPrint) {
        // Set to false when you don't want debugging in console
        if (debug) {
            Date d = new Date();
            String date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(d);
            System.out.println(info+date+" -- "+textToPrint);
        }
    }

    protected boolean isDebug() {
        return debug;
    }

    protected void setDebug(boolean debug) {
        this.debug = debug;
    }
}