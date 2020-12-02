package com.packetgui;

import javax.swing.*;
import javax.swing.border.Border;

import com.packetsniffer.*;
import org.pcap4j.core.PcapNetworkInterface;

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

    private static JFrame networkSniffInterface = new JFrame("Sniffing!");

    private static void sniffNetworkInterface(int networkInterfaceIndex) {
        System.out.println("Sniffing the network interface...");

        JLabel testPlaceholder = new JLabel();
        testPlaceholder.setText("Sniffing!!!!");
        testPlaceholder.setBounds(10,10,130,100);

        networkSniffInterface.setSize(500,500);
        networkSniffInterface.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        networkSniffInterface.setLocationRelativeTo(null);
        networkSniffInterface.setLayout(null);
        networkSniffInterface.setVisible(true);
    }

    private static boolean checkInput(String input) {
        boolean isInputValid = false;
        //Check if its a number

        //Check if its a number larger than the network interfaces available




        return isInputValid;
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

                boolean inputChecks = checkInput(interfaceInput.getText());

                if (inputChecks) {
                    //Input is valid
                    chooseInterfaceFrame.setVisible(false);
                    chooseInterfaceFrame.dispose();

                    networkInterfaceListerFrame.setVisible(false);

                    sniffNetworkInterface(Integer.parseInt(interfaceInput.getText()));
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
                List<PcapNetworkInterface> netsInterfaces;

                try {
                    netsInterfaces = Sniffer.checkInterfacesManual();

                    if (numOfClicks >0) netInterfaces.setText(""); //Clears text after every click

                    int interfaceID = 1;

                    //Prints out a new line for every interface available
                    for( PcapNetworkInterface netInterface :  netsInterfaces){
                        netInterfaces.append('\n' + "[" + Integer.toString(interfaceID) + "] " + netInterface.getDescription() + "\nAddress -> " + netInterface.getAddresses().get(0).getAddress() + "\n" );
                        interfaceID++;
                    }

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

        JScrollPane scroll = new JScrollPane (netInterfacesTextList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //Adds a scroll to textArea

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
