package com.packetsniffer;

import org.pcap4j.core.*;
import org.pcap4j.util.NifSelector;

import java.io.EOFException;
import java.io.IOException;
import com.sun.jna.Platform;
import org.pcap4j.core.Pcaps;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class Sniffer {

    public Sniffer() {}

    public static final String COUNT_KEY = Sniffer.class.getName() + ".count";
    public static final int COUNT = Integer.getInteger(COUNT_KEY, 5);

    public static final String READ_TIMEOUT_KEY = Sniffer.class.getName() + ".readTimeout";
    public static final int READ_TIMEOUT = Integer.getInteger(READ_TIMEOUT_KEY, 10); // [ms]

    public static final String SNAPLEN_KEY = Sniffer.class.getName() + ".snaplen";
    public static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]




    //How many packets to capture
    public static long[] finalStats(PcapHandle handle) throws PcapNativeException, NotOpenException {
        PcapStat ps = handle.getStats();

        long[] stats = new long[3];

        stats[0] = ps.getNumPacketsCaptured();
        stats[1] = ps.getNumPacketsDropped();
        stats[2] = 0; //Will be added in the thread

        return stats;
    }

    public static List<PcapNetworkInterface> checkInterfacesManual() throws IOException {

        List<PcapNetworkInterface> allDevs = null;

        try {
            allDevs = Pcaps.findAllDevs();
        } catch (PcapNativeException e) {
            throw new IOException(e.getMessage());
        }

        if (allDevs == null || allDevs.isEmpty()) {
            throw new IOException("No NIF to capture.");
        }

        //Print out all the available network itnerfaces by line
        for(PcapNetworkInterface netInterface : allDevs) {
            System.out.println(netInterface);
        }

        return allDevs;
    }

    //Check for interface using nif
    public static PcapNetworkInterface checkForNetworkInterface() {
        PcapNetworkInterface nif = null;
        try {
            nif = new NifSelector().selectNetworkInterface();
            System.out.println(nif.getName() + "(" + nif.getDescription() + ")");
            return nif;
        } catch (IOException e) {
            e.printStackTrace();
            return nif;
        }
    }

    public static PcapPacket getAPacket(PcapHandle handle) throws NotOpenException {
        PcapPacket packet = handle.getNextPacket();
        return packet;
    }

    public static void handlePackets(PcapHandle handle) throws NotOpenException {
        long totalLength = 0;
        int num = 0;

        while (true) {

            PcapPacket packet = handle.getNextPacket();
            if (packet == null) {
                continue;
            } else {
                //Output received packet
                System.out.println(packet.length());
                totalLength += packet.length();
                num++;
                if (num >= COUNT) {
                    break;
                }
            }
        }
        System.out.println("Exitting sniffing.... Goodbye!");
    }

    public static void main(String[] args) throws PcapNativeException, NotOpenException, IOException {

        //Just testing out getting all devices manually
        checkInterfacesManual();


        /*
        String filter = args.length != 0 ? args[0] : "";

        //Choose interface to sniff
        PcapNetworkInterface nif = checkForNetworkInterface();

        //Create and use packet handler
        final PcapHandle handle = nif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
        handlePackets(handle);

        if (filter.length() != 0) handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);

        //Call for final stats
        finalStats(handle);

        //Close handler
        handle.close();

         */

    }
}