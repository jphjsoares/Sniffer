package com.packetgui;


import org.pcap4j.core.PcapHandle;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class SniffingGui {

    public static  final JFrame networkSniffInterfaceFrame = new JFrame("Sniffing!");
    private static final JPanel networkLogPanel = new JPanel(new BorderLayout());
    public static final JTextArea networkSnifferLog = new JTextArea();
    public static final JPanel statsPanel = new JPanel(new BorderLayout());
    public static final JLabel totalLengthOfPackets = new JLabel();
    public static final JLabel droppedPackets = new JLabel();
    public static final JLabel capturedPackets = new JLabel();





    public static void sniffNetworkInterface(int networkInterfaceIndex, PcapHandle handle) {

        SniffingThread threadOfSniff = new SniffingThread(handle, networkSnifferLog);
        Thread thread = new Thread(threadOfSniff);

        JScrollPane scrollSniffPanel = new JScrollPane(networkSnifferLog);

        statsPanel.setLayout(new FlowLayout());
        statsPanel.add(totalLengthOfPackets);
        statsPanel.add(droppedPackets);
        statsPanel.add(capturedPackets);
        statsPanel.setVisible(false);

        networkSnifferLog.setEditable(false);
        networkSnifferLog.setLineWrap(true);

        //Scroll to the bottom on every update
        DefaultCaret caret = (DefaultCaret)networkSnifferLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        //Add a border
        networkLogPanel.setBorder(BorderFactory.createEmptyBorder(0, 10,10,10));


        networkLogPanel.add(statsPanel, BorderLayout.SOUTH);
        networkLogPanel.add(SniffingToolbar.createToolbar(thread, threadOfSniff, handle),BorderLayout.NORTH);
        networkLogPanel.add(scrollSniffPanel, BorderLayout.CENTER);

        networkSniffInterfaceFrame.setIconImage(SniffingInterfaces.icon.getImage());
        networkSniffInterfaceFrame.setContentPane(networkLogPanel);
        networkSniffInterfaceFrame.setLocationRelativeTo(null);
        networkSniffInterfaceFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        networkSniffInterfaceFrame.setSize(500,300);
        networkSniffInterfaceFrame.setVisible(true);
        networkSniffInterfaceFrame.setResizable(true);
    }
}
