package com.packetgui;

import javax.swing.*;
import com.packetsniffer.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainApp {

    static Sniffer sniff = new Sniffer();

    //private static void dealWithCheckButton() {
        /*
        * Create text with possible options
        * Create Checkbox for each option available
        * Delete checkInterfacesButton
        * */
        //JLabel optionsText = new JLabel();

    //}


    private static JButton addCheckButton() {
        TextField displayNetworkInterfaces = new TextField();
        displayNetworkInterfaces.setBounds(50,100,300,300);
        JButton checkInterfaces = new JButton("Check interfaces");
        checkInterfaces.setBounds(130,400,200,40);
        checkInterfaces.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    displayNetworkInterfaces.setText(String.valueOf(sniff.checkInterfacesManual()));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        return checkInterfaces;
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("PacketSniff");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(addCheckButton());
        frame.setSize(500,500);
        frame.setLayout(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        createAndShowGUI();
    }
}
