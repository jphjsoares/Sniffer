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
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setSize(800,600);
                netInterfaces.setBounds(50,10,700,500);
                checkInterfaces.setBounds(250,400,200,40);
                try {
                    List<PcapNetworkInterface> netsInterfaces = sniff.checkInterfacesManual();

                    //Prints out a new line for every interface available
                    for( PcapNetworkInterface netInterface :  netsInterfaces){

                        //          Add text to the previous text area     Get the current interface name                    Get current interface local address
                        netInterfaces.setText(netInterfaces.getText()+'\n'+netInterface.getDescription() + "- address -> " + netInterface.getAddresses().get(0).getAddress());
                    }


                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

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
