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


    private static JButton addCheckButton(JTextArea netInterfaces, JFrame frame) {

        JButton checkInterfaces = new JButton("Check interfaces");

        checkInterfaces.addActionListener(new ActionListener() {
            int numOfClicks = 0;

            @Override
            public void actionPerformed(ActionEvent e) {

                //Resize all windows after button click
                netInterfaces.setBounds(50,10,500,250);
                //checkInterfaces.setBounds(250,300,200,40);
                List<PcapNetworkInterface> netsInterfaces;

                try {
                    netsInterfaces = Sniffer.checkInterfacesManual();

                    if (numOfClicks >0) netInterfaces.setText("");

                    //Prints out a new line for every interface available
                    for( PcapNetworkInterface netInterface :  netsInterfaces){
                        netInterfaces.append('\n' + netInterface.getDescription() + "\nAddress -> " + netInterface.getAddresses().get(0).getAddress() + "\n" );
                    }

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                numOfClicks++;

            }
        });
        return checkInterfaces;
    }


    public static void main(String[] args) {
        //Components initialize
        JFrame mainFrame = new JFrame("SniffSniff");
        JPanel mainPanel = new JPanel();
        JTextArea netInterfaces = new JTextArea(20,30);
        JScrollPane scroll = new JScrollPane (netInterfaces,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); //Adds a scroll to textArea


        //Text area edits
        netInterfaces.setEditable(false);
        netInterfaces.setLineWrap(true);

        //Add components to main frame
        mainPanel.add(scroll); //Adds scroll to main panel

        mainFrame.add(mainPanel, BorderLayout.CENTER); //Adds main panel to frame
        mainFrame.add(addCheckButton(netInterfaces, mainFrame), BorderLayout.SOUTH); //Adds check button


        //MainFrame tweaks
        mainFrame.setResizable(false); //Doesnt let resize window
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(null);
        mainFrame.setVisible(true);
    }
}
