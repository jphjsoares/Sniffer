package com.packetgui;

import com.packetsniffer.Sniffer;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapPacket;

import javax.swing.*;

/*
public class SniffingThread extends Thread{
    private volatile boolean keepSniffing = true;
    PcapHandle globalPcapHandler;
    JTextArea networkSnifferLog;


    SniffingThread(PcapHandle globalPcapHandler, JTextArea networkSnifferLog) {
        this.globalPcapHandler = globalPcapHandler;
        this.networkSnifferLog = networkSnifferLog;
    }

    public void run() {

        System.out.println("Starting a new thread.....");

        while (keepSniffing && !Thread.interrupted()) {

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

        System.out.println("Exiting thread");
    }

    public void killThread() {
        keepSniffing = false;
    }
}*/





public class SniffingThread implements Runnable{
    private volatile boolean keepSniffing = true;
    PcapHandle globalPcapHandler;
    JTextArea networkSnifferLog;


    SniffingThread(PcapHandle globalPcapHandler, JTextArea networkSnifferLog) {
        this.globalPcapHandler = globalPcapHandler;
        this.networkSnifferLog = networkSnifferLog;
    }

    public void run() {

        System.out.println("Starting a new thread.....");

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

        System.out.println("Exiting thread");
    }

    public void killThread() {
        keepSniffing = false;
    }

    public void setKeepSniffing(boolean toSniff){
        keepSniffing = toSniff;
    }

}

