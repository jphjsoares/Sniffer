package com.packetgui;

import org.pcap4j.core.PcapHandle;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SniffGUI {

    private static  final JFrame networkSniffInterfaceFrame = new JFrame("Sniffing!");
    private static final JPanel networkLogPanel = new JPanel(new BorderLayout());
    //private static final JTextArea networkSnifferLog = new JTextArea(10, 40);
    private static final JTextArea networkSnifferLog = new JTextArea();

    private static final JToolBar toolbar = new JToolBar();



    private static JToolBar createToolbar(Thread thread, SniffingThread runnable, PcapHandle handler) {
        toolbar.setRollover(true);
        JButton pauseSniff = new JButton("Pause");
        JButton startSniff = new JButton("Start sniffing");

        //Cant show a button to pause before the thread is being actually executed
        pauseSniff.setVisible(false);

        toolbar.add(startSniff);
        toolbar.add(pauseSniff);

        //Start the whole process after the button to start is pressed
        startSniff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thread.start();
                startSniff.setVisible(false);
                pauseSniff.setVisible(true);
            }
        });

        pauseSniff.addActionListener(new ActionListener() {
            boolean isSetToResume = false;
            int timesPaused = 0;
            PcapHandle handle = handler;
            SniffingThread threadOfSniff = new SniffingThread(handle, networkSnifferLog);

            @Override
            public void actionPerformed(ActionEvent e) {

                //Button was pressed to pause
                if(!isSetToResume) {

                    isSetToResume = true;
                    pauseSniff.setText("Resume");

                    if (timesPaused > 0) {
                        threadOfSniff.killThread();
                    } else {
                        runnable.killThread(); // Works the first time pause is pressed
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

        networkSnifferLog.setEditable(false);
        networkSnifferLog.setLineWrap(true);

        //Scroll to the bottom on every update
        DefaultCaret caret = (DefaultCaret)networkSnifferLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        networkLogPanel.add(createToolbar(thread, threadOfSniff, handle),BorderLayout.NORTH);
        networkLogPanel.add(scrollSniffPanel, BorderLayout.CENTER);


        networkSniffInterfaceFrame.setContentPane(networkLogPanel);
        networkSniffInterfaceFrame.setLocationRelativeTo(null);
        networkSniffInterfaceFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        networkSniffInterfaceFrame.setSize(500,300);
        //networkSniffInterfaceFrame.pack();
        networkSniffInterfaceFrame.setVisible(true);
        networkSniffInterfaceFrame.setResizable(true);
    }
}
