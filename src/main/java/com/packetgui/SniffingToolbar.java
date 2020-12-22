package com.packetgui;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;



public class SniffingToolbar {

    private static final JToolBar toolbar = new JToolBar();
    public static JToolBar createToolbar(Thread thread, SniffingThread runnable, PcapHandle handler) {
        JButton pauseSniff = new JButton("Pause");
        JButton startSniff = new JButton("Start sniffing");
        JButton endSniff = new JButton("End sniffing");
        JButton runInBackground = new JButton("Run in the background");
        final double[] stats = new double[3];
        final long[] timesPausedGlobalHandle = {0};


        SniffingThread threadOfSniff = new SniffingThread(handler, SniffGUI.networkSnifferLog);


        //Cant show a button to pause before the thread is being actually executed
        pauseSniff.setVisible(false);
        runInBackground.setVisible(false);
        endSniff.setVisible(false);


        toolbar.add(startSniff);
        toolbar.add(pauseSniff);
        toolbar.add(endSniff);
        toolbar.add(runInBackground);


        runInBackground.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SystemTray.isSupported()) {

                    MenuItem showFrame = new MenuItem("Show interface");
                    MenuItem pauseSysTray = new MenuItem("Pause");
                    MenuItem exitItem = new MenuItem("Exit");
                    final PopupMenu popup = new PopupMenu();

                    SniffGUI.networkSniffInterfaceFrame.setVisible(false);
                    TrayIcon trayIcon = new TrayIcon(ShowAndSelectNetInterfaceGUI.icon.getImage()); //Add here the icon of the app
                    trayIcon.setImageAutoSize(true);
                    trayIcon.setToolTip("Sniffing packets...");

                    popup.add(showFrame);
                    popup.add(pauseSysTray);
                    popup.add(exitItem);
                    trayIcon.setPopupMenu(popup);

                    showFrame.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SystemTray.getSystemTray().remove(trayIcon);
                            SniffGUI.networkSniffInterfaceFrame.setVisible(true);
                        }
                    });

                    pauseSysTray.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            pauseSniff.doClick();
                            if(pauseSysTray.getLabel().equals("Pause")) {
                                pauseSysTray.setLabel("Resume");
                            } else {
                                pauseSysTray.setLabel("Pause");
                            }
                        }
                    });

                    exitItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SystemTray.getSystemTray().remove(trayIcon);
                            SniffGUI.networkSniffInterfaceFrame.setVisible(true);
                            endSniff.doClick();
                        }
                    });

                    try {
                        SystemTray.getSystemTray().add(trayIcon);
                    } catch (Exception err) {
                        System.out.println(err);
                    }

                } else {
                    JOptionPane.showMessageDialog(SniffGUI.networkSnifferLog,
                            "Looks like your operating system does not have a system tray. Can't run app in background",
                            "System tray Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        //Start the whole process after the button to start is pressed
        // Start thread on button click
        startSniff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thread.start(); // Start thread on button click
                startSniff.setVisible(false);
                pauseSniff.setVisible(true);
                endSniff.setVisible(true);
                runInBackground.setVisible(true);
            }
        });

        //Button was pressed to end sniffing
        endSniff.addActionListener(new ActionListener() {
            double[] statsHandler;
            @Override
            public void actionPerformed(ActionEvent e) {
                SniffGUI.statsPanel.setVisible(true);
                pauseSniff.setVisible(false);
                endSniff.setVisible(false);
                runInBackground.setVisible(false);

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

                SniffGUI.networkSnifferLog.append("\n\n\nYour sniffing process finished\nCheck some statistics below!\n\t\tGoodbye :)");

                DecimalFormat df = new DecimalFormat("#####.##");

                //Bytes conversion into mbytes, kbytes and gbytes
                if (stats[2] < 1024) {
                    SniffGUI.totalLengthOfPackets.setText("Total size of packets: " + df.format(stats[2]) + " bytes\n");
                }
                if(stats[2] > 1024) {
                    SniffGUI.totalLengthOfPackets.setText("Total size of packets: " + df.format(stats[2] / 1024) + " Kbytes\n");
                }
                if (stats[2] > 1048576) {
                    SniffGUI.totalLengthOfPackets.setText("Total size of packets: " + df.format(stats[2] / 1048576) + " Mbytes\n");
                }
                if (stats[2] > 1073741824 ) {
                    SniffGUI.totalLengthOfPackets.setText("Total size of packets: " + df.format(stats[2] / 1073741824) + " Gbytes\n");
                }
                SniffGUI.droppedPackets.setText("Packets dropped: " + (long) stats[1] + "\n");
                SniffGUI.capturedPackets.setText("Packets captured: " + (long) stats[0] + "\n");

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
}
