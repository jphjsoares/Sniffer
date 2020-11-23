package com.packetsniffer;

import org.pcap4j.core.*;
import org.pcap4j.util.NifSelector;
import java.io.IOException;
import com.sun.jna.Platform;


public class Sniffer {

    public Sniffer() {}

    private static final String COUNT_KEY = Sniffer.class.getName() + ".count";
    private static final int COUNT = Integer.getInteger(COUNT_KEY, 5);

    private static final String READ_TIMEOUT_KEY = Sniffer.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT = Integer.getInteger(READ_TIMEOUT_KEY, 10); // [ms]

    private static final String SNAPLEN_KEY = Sniffer.class.getName() + ".snaplen";
    private static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]




    //How many packets to capture
    public static void finalStats(PcapHandle handle) throws PcapNativeException, NotOpenException {
        PcapStat ps = handle.getStats();

        System.out.println("ps_recv: " + ps.getNumPacketsCaptured());
        System.out.println("ps_drop: " + ps.getNumPacketsDropped());
        System.out.println("ps_ifdrop: " + ps.getNumPacketsDroppedByIf());
        if (Platform.isWindows()) {
            System.out.println("bs_capt: " + ps.getNumPacketsCaptured());
        }
    }

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
        System.out.println("Received " + totalLength);

    }

    public static void main(String[] args) throws PcapNativeException, NotOpenException {


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

    }
}