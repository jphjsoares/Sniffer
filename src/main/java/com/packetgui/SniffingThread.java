package com.packetgui;

import com.packetsniffer.Sniffer;
import org.apache.commons.lang3.StringUtils;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapPacket;

import javax.swing.*;

public class SniffingThread implements Runnable{
    private volatile boolean keepSniffing = true;
    PcapHandle globalPcapHandler;
    JTextArea networkSnifferLog;
    long totalLength = 0;


    SniffingThread(PcapHandle globalPcapHandler, JTextArea networkSnifferLog) {
        this.globalPcapHandler = globalPcapHandler;
        this.networkSnifferLog = networkSnifferLog;
    }

    public void run() {
        System.out.println("Starting a new thread.....");
        String[] blocksOfPacketInfo;


        while (keepSniffing) {

            //PcapPacket newPacket = null;
            PcapPacket newPacket = null;
            try {
                newPacket = Sniffer.getAPacket(globalPcapHandler); //returns a PcapPacket
            } catch (NotOpenException e) { e.printStackTrace(); }

            blocksOfPacketInfo = StringUtils.substringsBetween(String.valueOf(newPacket), "]", "[");

            if (newPacket != null) {
                //Index 1 shows the user the ethernet header
                totalLength += newPacket.length();
                networkSnifferLog.append(blocksOfPacketInfo[1] + "\n" + "#########################################\n");

            }

        }

        System.out.println("Exiting thread.....");
    }

    public long[] killThread() throws PcapNativeException, NotOpenException {
        long[] stats = Sniffer.finalStats(globalPcapHandler);
        stats[2] = totalLength;
        keepSniffing = false;
        return stats;
    }

    public void setKeepSniffing(boolean toSniff){
        keepSniffing = toSniff;
    }

}

