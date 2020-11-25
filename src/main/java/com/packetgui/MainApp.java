package com.packetgui;

import javax.swing.*;
import com.packetsniffer.*;
import org.pcap4j.core.PcapNetworkInterface;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class MainApp {

    static Sniffer sniff = new Sniffer();


    private static JButton addCheckButton(JTextArea netInterfaces, JFrame frame) {

        JButton checkInterfaces = new JButton("Check interfaces");
        checkInterfaces.setBounds(50,100,200,40);

        checkInterfaces.addActionListener(new ActionListener() {
            int numOfClicks = 0;
            @Override
            public void actionPerformed(ActionEvent e) {

                //Resize all windows after button click
                frame.setSize(600,400);
                netInterfaces.setBounds(50,10,500,250);
                checkInterfaces.setBounds(250,300,200,40);

                try {
                    List<PcapNetworkInterface> netsInterfaces = sniff.checkInterfacesManual();
                    if (numOfClicks >0) {
                        netInterfaces.setText("");
                    }
                    //Prints out a new line for every interface available
                    for( PcapNetworkInterface netInterface :  netsInterfaces){
                        netInterfaces.append('\n' + netInterface.getDescription() + "- address -> " + netInterface.getAddresses().get(0).getAddress());
                    }

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                numOfClicks++;

            }
        });
        return checkInterfaces;
    }

    private static void createAndShowMainGUI() {
        JFrame mainFrame = new JFrame("PacketSniff");
        JTextArea netInterfaces = new JTextArea();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(netInterfaces);
        mainFrame.setSize(300,200);
        mainFrame.setLayout(null);
        mainFrame.setVisible(true);
        mainFrame.add(addCheckButton(netInterfaces, mainFrame));
    }

    public static void main(String[] args) {
        createAndShowMainGUI();
    }
}
