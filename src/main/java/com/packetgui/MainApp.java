package com.packetgui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;

import com.packetsniffer.*;
import org.pcap4j.core.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class MainApp {

    private static JFrame networkInterfaceListerFrame = new JFrame("Network Interface Lister");
    private static JPanel networkListerPanel = new JPanel();
    private static JTextArea netInterfacesTextList = new JTextArea(10,30);

    private static JFrame chooseInterfaceFrame = new JFrame("Choose interface");
    private static JPanel chooseInterfacePanel = new JPanel();

    private static JFrame networkSniffInterfaceFrame = new JFrame("Sniffing!");
    private static JPanel networkLogPanel = new JPanel(new FlowLayout());
    private static JTextArea networkSnifferLog = new JTextArea(10, 40);

    private static List<PcapNetworkInterface> netsInterfaces;

    private static void sniffNetworkInterface(int networkInterfaceIndex, PcapHandle handle) {
        boolean keepSniffing = true;
        JScrollPane scrollSniffPanel = new JScrollPane(networkSnifferLog);

        networkSnifferLog.setEditable(false);
        networkSnifferLog.setLineWrap(true);

        //Scroll to the bottom on every update
        DefaultCaret caret = (DefaultCaret)networkSnifferLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        networkLogPanel.add(scrollSniffPanel);

        networkSniffInterfaceFrame.setContentPane(networkLogPanel);
        networkSniffInterfaceFrame.setLocationRelativeTo(null);
        networkSniffInterfaceFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        networkSniffInterfaceFrame.pack();
        networkSniffInterfaceFrame.setVisible(true);
        networkSniffInterfaceFrame.setResizable(false);

        //Get packets
        new Thread(new Runnable() {
            public void run() {

                while(keepSniffing) {

                    PcapPacket newPacket = null;

                    try {
                        newPacket = Sniffer.getAPacket(handle);
                    } catch (NotOpenException e) {
                        e.printStackTrace();
                    }

                    if (newPacket != null) {
                        networkSnifferLog.append(String.valueOf(newPacket));
                    }

                    try {
                        java.lang.Thread.sleep(1);
                    } catch(Exception e) { }

                }
            }
        }).start();

    }


    private static boolean checkInput(String input) throws IOException {

        int inputAsNumber;

        //Check if its not null
        if (input == null) {
            return false;
        }

        // Check if its an integer
        try {
            inputAsNumber= Integer.parseInt(input);
        } catch (NumberFormatException nfe) {
            return false;
        }

        //Check if its a number larger than the network interfaces available
        if(inputAsNumber <= Sniffer.checkInterfacesManual().size() && inputAsNumber > 0 ) return true;

        return false;
    }

    private static void submitInterfaceToSniff() {
        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(100,100,140,40);

        JLabel infoSubmitLabel = new JLabel();
        infoSubmitLabel.setText("Choose network interface");
        infoSubmitLabel.setBounds(10,10,130,100);

        JTextField interfaceInput = new JTextField();
        interfaceInput.setBounds(145,50,130,30);

        chooseInterfaceFrame.add(submitButton);
        chooseInterfaceFrame.add(infoSubmitLabel);
        chooseInterfaceFrame.add(interfaceInput);

        chooseInterfaceFrame.setSize(300,200);
        chooseInterfaceFrame.setLocationRelativeTo(networkInterfaceListerFrame);
        chooseInterfaceFrame.setResizable(false);
        chooseInterfaceFrame.setLayout(null);
        chooseInterfaceFrame.setVisible(true);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Submit button was pressed, going to listen on: " + interfaceInput.getText());

                boolean inputChecks = false;

                try {
                    inputChecks = checkInput(interfaceInput.getText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                if (inputChecks) {
                    //Input is valid
                    chooseInterfaceFrame.setVisible(false);
                    chooseInterfaceFrame.dispose();

                    networkInterfaceListerFrame.setVisible(false);

                    //I need to implement this
                    //nifs.get(nifIdx) --> this will get the nif to sniff to and use in the handler
                    PcapNetworkInterface networkInterfaceToUseInHandler  = netsInterfaces.get(Integer.parseInt(interfaceInput.getText())-1);
                    System.out.println("\n\n" + networkInterfaceToUseInHandler);
                    final PcapHandle handle;
                    try {
                        handle = networkInterfaceToUseInHandler.openLive(Sniffer.SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, Sniffer.READ_TIMEOUT);
                        sniffNetworkInterface(Integer.parseInt(interfaceInput.getText()), handle);
                    } catch (PcapNativeException pcapNativeException) {
                        pcapNativeException.printStackTrace();
                    }

                } else {
                    //Crete a Error message
                    JOptionPane.showMessageDialog(chooseInterfaceFrame,
                            "Wow! Something went wrong... Check your input",
                            "Input error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });

    }

    private static JButton addCheckButton(JTextArea netInterfaces, JFrame frame) {

        JButton checkInterfaces = new JButton("Check interfaces");

        checkInterfaces.addActionListener(new ActionListener() {
            int numOfClicks = 0;

            @Override
            public void actionPerformed(ActionEvent e) {

                //Resize all windows after button click
                netInterfaces.setBounds(50,10,500,250);
                //checkInterfaces.setBounds(250,300,200,40);


                try {
                    netsInterfaces = Sniffer.checkInterfacesManual();

                    if (numOfClicks >0) netInterfaces.setText(""); //Clears text after every click

                    int interfaceID = 1;

                    //Prints out a new line for every interface available
                    for( PcapNetworkInterface netInterface :  netsInterfaces){
                        netInterfaces.append('\n' + "[" + Integer.toString(interfaceID) + "] " + netInterface.getDescription() + "\nAddress -> " + netInterface.getAddresses().get(0).getAddress() + "\n" );
                        interfaceID++;
                    }
                    System.out.println(netsInterfaces.size());

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                numOfClicks++;

                checkInterfaces.setVisible(false);
                networkListerPanel.remove(checkInterfaces);

                submitInterfaceToSniff();
            }


        });
        return checkInterfaces;
    }

    private static void createAndShowNetInterfaceLister() {
        //Components initialize

        JScrollPane scroll = new JScrollPane (netInterfacesTextList); //Adds a scroll to textArea

        //Text area edits
        netInterfacesTextList.setEditable(false);
        netInterfacesTextList.setLineWrap(true);

        //Add components to main panel
        networkListerPanel.add(scroll, BorderLayout.EAST);
        networkListerPanel.add(addCheckButton(netInterfacesTextList, networkInterfaceListerFrame));


        networkInterfaceListerFrame.add(networkListerPanel); //Adds main panel to frame


        //MainFrame tweaks
        networkInterfaceListerFrame.setResizable(false); //Doesnt let resize window
        networkInterfaceListerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        networkInterfaceListerFrame.pack();
        networkInterfaceListerFrame.setLocationRelativeTo(null);
        networkInterfaceListerFrame.setLayout(null);
        networkInterfaceListerFrame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        createAndShowNetInterfaceLister();
    }
}
