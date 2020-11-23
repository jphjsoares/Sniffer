package com.packetgui;

import javax.swing.*;
import com.packetsniffer.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        JButton checkInterfaces = new JButton("Check interfaces");
        checkInterfaces.setBounds(130,400,200,40);
        checkInterfaces.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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
