package com.packetgui;


import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SniffGUI {

    private static  final JFrame networkSniffInterfaceFrame = new JFrame("Sniffing!");
    private static final JPanel networkLogPanel = new JPanel(new BorderLayout());
    private static final JTextArea networkSnifferLog = new JTextArea();
    private static final JPanel statsPanel = new JPanel(new BorderLayout());
    private static final JLabel totalLength = new JLabel();
    private static final JLabel droppedPackets = new JLabel();
    private static final JLabel capturedPackets = new JLabel();

    private static final JToolBar toolbar = new JToolBar();


    private static JToolBar createToolbar(Thread thread, SniffingThread runnable, PcapHandle handler) {
        toolbar.setRollover(true);
        JButton pauseSniff = new JButton("Pause");
        JButton startSniff = new JButton("Start sniffing");
        JButton endSniff = new JButton("End sniffing");
        final long[] totalLenght = {0};
        final long[] packetsCaptured = {0};
        final long[] packetsDropped = {0};


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
        //TODO: Add functionality (kill thread that is running) and show the final stats
        endSniff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statsPanel.setVisible(true);
                totalLength.setText(String.valueOf("Total size of packets: " + totalLenght[0] + " bytes\n"));
                droppedPackets.setText(String.valueOf("Packets dropped: " + packetsDropped[0] + "\n"));
                capturedPackets.setText(String.valueOf("Packets captured: " + packetsCaptured[0] + "\n"));

            }
        });

        pauseSniff.addActionListener(new ActionListener() {
            boolean isSetToResume = false;
            int timesPaused = 0;
            PcapHandle handle = handler;
            SniffingThread threadOfSniff = new SniffingThread(handle, networkSnifferLog);
            long[] statsHandler;

            @Override
            public void actionPerformed(ActionEvent e) {

                //Button was pressed to pause
                if(!isSetToResume) {
                    isSetToResume = true;
                    pauseSniff.setText("Resume");
                    if (timesPaused > 0) {
                        try {
                            statsHandler = threadOfSniff.killThread();
                            packetsCaptured[0] += statsHandler[0];
                            packetsDropped[0] += statsHandler[1];
                            totalLenght[0] += statsHandler[2];
                        } catch (PcapNativeException pcapNativeException) {
                            pcapNativeException.printStackTrace();
                        } catch (NotOpenException notOpenException) {
                            notOpenException.printStackTrace();
                        }
                    } else {
                        try {
                            statsHandler = runnable.killThread(); // Works the first time pause is pressed
                            packetsCaptured[0] += statsHandler[0];
                            packetsDropped[0] += statsHandler[1];
                            totalLenght[0] += statsHandler[2];

                        } catch (PcapNativeException pcapNativeException) {
                            pcapNativeException.printStackTrace();
                        } catch (NotOpenException notOpenException) {
                            notOpenException.printStackTrace();
                        }
                    }
                    timesPaused++;

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
        statsPanel.add(totalLength);
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
