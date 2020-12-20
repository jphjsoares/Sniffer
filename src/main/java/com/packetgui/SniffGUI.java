package com.packetgui;


import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class SniffGUI {

    private static  final JFrame networkSniffInterfaceFrame = new JFrame("Sniffing!");
    private static final JPanel networkLogPanel = new JPanel(new BorderLayout());
    private static final JTextArea networkSnifferLog = new JTextArea();
    private static final JPanel statsPanel = new JPanel(new BorderLayout());
    private static final JLabel totalLengthOfPackets = new JLabel();
    private static final JLabel droppedPackets = new JLabel();
    private static final JLabel capturedPackets = new JLabel();
    private static final JToolBar toolbar = new JToolBar();


    private static JToolBar createToolbar(Thread thread, SniffingThread runnable, PcapHandle handler) {
        JButton pauseSniff = new JButton("Pause");
        JButton startSniff = new JButton("Start sniffing");
        JButton endSniff = new JButton("End sniffing");
        final double[] stats = new double[3];
        final long[] timesPausedGlobalHandle = {0};


        SniffingThread threadOfSniff = new SniffingThread(handler, networkSnifferLog);


        //Cant show a button to pause before the thread is being actually executed
        pauseSniff.setVisible(false);
        endSniff.setVisible(false);

        toolbar.add(startSniff);
        toolbar.add(pauseSniff);
        toolbar.add(endSniff);

        //Start the whole process after the button to start is pressed
        startSniff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thread.start(); // Start thread on button click
                startSniff.setVisible(false);
                pauseSniff.setVisible(true);
                endSniff.setVisible(true);
            }
        });


        //Button was pressed to end sniffing
        endSniff.addActionListener(new ActionListener() {
            double[] statsHandler;
            @Override
            public void actionPerformed(ActionEvent e) {
                statsPanel.setVisible(true);
                pauseSniff.setVisible(false);
                endSniff.setVisible(false);

                //This makes updates the stats if no pause is given during the sniff before pressing end sniff
                if(timesPausedGlobalHandle[0] == 0) {
                    try {
                        statsHandler = runnable.killThread();
                        stats[0] += statsHandler[0];
                        stats[1] += statsHandler[1];
                        stats[2] += statsHandler[2];
                    } catch (PcapNativeException | NotOpenException pcapNativeException) { pcapNativeException.printStackTrace(); }
                } else {
                    try {
                        statsHandler = threadOfSniff.killThread();
                        stats[0] += statsHandler[0];
                        stats[1] += statsHandler[1];
                        stats[2] += statsHandler[2];
                    } catch (PcapNativeException | NotOpenException pcapNativeException) { pcapNativeException.printStackTrace(); }
                }

                networkSnifferLog.append("\n\n\nYour sniffing process finished\nCheck some statistics below!\n\t\tGoodbye :)");

                DecimalFormat df = new DecimalFormat("#####.###");

                //Bytes conversion into mbytes, kbytes and gbytes
                if (stats[2] < 1024) {
                    totalLengthOfPackets.setText(String.valueOf("Total size of packets: " + df.format(stats[2]) + " bytes\n"));
                }
                if(stats[2] > 1024) {
                    totalLengthOfPackets.setText(String.valueOf("Total size of packets: " + df.format(stats[2] / 1024) + " Kbytes\n"));
                }
                if (stats[2] > 1048576) {
                    totalLengthOfPackets.setText(String.valueOf("Total size of packets: " + df.format(stats[2] / 1048576) + " Mbytes\n"));
                }
                if (stats[2] > 1073741824 ) {
                    totalLengthOfPackets.setText(String.valueOf("Total size of packets: " + df.format(stats[2] / 1073741824) + " Gbytes\n"));
                }
                droppedPackets.setText(String.valueOf("Packets dropped: " + (long)stats[1] + "\n"));
                capturedPackets.setText(String.valueOf("Packets captured: " + (long)stats[0] + "\n"));

            }
        });

        pauseSniff.addActionListener(new ActionListener() {
            boolean isSetToResume = false;
            int timesPaused = 0;
            double[] statsHandler;

            @Override
            public void actionPerformed(ActionEvent e) {

                //Button was pressed to pause
                if(!isSetToResume) {
                    isSetToResume = true;
                    pauseSniff.setText("Resume");

                    if (timesPaused > 0) {

                        try {
                            statsHandler = threadOfSniff.killThread();
                        }
                        catch (PcapNativeException | NotOpenException pcapNativeException) { pcapNativeException.printStackTrace(); }

                        //Add the acquired stats to the final stats array (stats are only being given at the end ATM)
                        stats[0] += statsHandler[0];
                        stats[1] += statsHandler[1];
                        stats[2] += statsHandler[2];

                    } else {

                        try {
                            statsHandler = runnable.killThread(); // Works the first time pause is pressed
                        }
                        catch (PcapNativeException | NotOpenException pcapNativeException) { pcapNativeException.printStackTrace(); }

                        //Add the acquired stats to the final stats array (stats are only being given at the end ATM)
                        stats[0] += statsHandler[0];
                        stats[1] += statsHandler[1];
                        stats[2] += statsHandler[2];

                    }
                    timesPaused++;
                    timesPausedGlobalHandle[0] = timesPaused;
                }

                //Button was pressed to resume
                else if (isSetToResume) {
                    isSetToResume = false;
                    pauseSniff.setText("Pause");

                    //Start a new thread
                    new Thread(threadOfSniff).start();
                    threadOfSniff.setKeepSniffing(true);

                }
            }
        });
        return toolbar;
    }

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
        networkLogPanel.add(createToolbar(thread, threadOfSniff, handle),BorderLayout.NORTH);
        networkLogPanel.add(scrollSniffPanel, BorderLayout.CENTER);


        networkSniffInterfaceFrame.setContentPane(networkLogPanel);
        networkSniffInterfaceFrame.setLocationRelativeTo(null);
        networkSniffInterfaceFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        networkSniffInterfaceFrame.setSize(500,300);
        networkSniffInterfaceFrame.setVisible(true);
        networkSniffInterfaceFrame.setResizable(true);
    }
}
