package com.packetgui;

import com.packetsniffer.Sniffer;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapPacket;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class SniffGUI {

    private static JFrame networkSniffInterfaceFrame = new JFrame("Sniffing!");
    private static JPanel networkLogPanel = new JPanel(new FlowLayout());
    private static JTextArea networkSnifferLog = new JTextArea(10, 40);

    public static void sniffNetworkInterface(int networkInterfaceIndex, PcapHandle handle) {
        boolean keepSniffing = true;
        JScrollPane scrollSniffPanel = new JScrollPane(networkSnifferLog);

        networkSnifferLog.setEditable(false);
        networkSnifferLog.setLineWrap(true);

        //Scroll to the bottom on every update
        DefaultCaret caret = (DefaultCaret)networkSnifferLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        networkLogPanel.add(scrollSniffPanel);

        networkSniffInterfaceFrame.setContentPane(networkLogPanel);
        networkSniffInterfaceFrame.setLocationRelativeTo(null);
        networkSniffInterfaceFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        networkSniffInterfaceFrame.pack();
        networkSniffInterfaceFrame.setVisible(true);
        networkSniffInterfaceFrame.setResizable(false);

        //Get packets
        new Thread(new Runnable() {
            public void run() {

                while(keepSniffing) {

                    PcapPacket newPacket = null;

                    try {
                        newPacket = Sniffer.getAPacket(handle);
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
        }).start();

    }
}
