package com.packetgui;

import com.packetsniffer.Sniffer;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapPacket;

import javax.swing.*;

/*
public class SniffingThread extends Thread{
    boolean keepSniffing = true;
    PcapHandle globalPcapHandler;
    JTextArea networkSnifferLog;


    SniffingThread(PcapHandle globalPcapHandler, JTextArea networkSnifferLog) {
        this.globalPcapHandler = globalPcapHandler;
        this.networkSnifferLog = networkSnifferLog;
    }

    public void run() {
        while(keepSniffing) {

            PcapPacket newPacket = null;

            try {
                newPacket = Sniffer.getAPacket(globalPcapHandler);
            } catch (NotOpenException e) {
                e.printStackTrace();
            }

            if (newPacket != null) {
                networkSnifferLog.append(String.valueOf(newPacket));
            }

            try {
                java.lang.Thread.sleep(1);
            } catch(Exception e) { }

        }
    }
    public void pauseTheSniff() {
        keepSniffing = false;
    }
}

*/

public class SniffingThread implements Runnable{
    private volatile boolean keepSniffing = true;
    PcapHandle globalPcapHandler;
    JTextArea networkSnifferLog;


    SniffingThread(PcapHandle globalPcapHandler, JTextArea networkSnifferLog) {
        this.globalPcapHandler = globalPcapHandler;
        this.networkSnifferLog = networkSnifferLog;
    }

    public void run() {

        while (keepSniffing) {

            PcapPacket newPacket = null;

            try {
                newPacket = Sniffer.getAPacket(globalPcapHandler);
            } catch (NotOpenException e) {
                e.printStackTrace();
            }

            if (newPacket != null) {
                networkSnifferLog.append(String.valueOf(newPacket));
            }

            try {
                java.lang.Thread.sleep(1);
            } catch (Exception e) {}
        }

    }

    public void continueTheSniff() {
        keepSniffing = true;
    }

    public void pauseTheSniff() {
        keepSniffing = false;
    }
}