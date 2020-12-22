package com.packetgui;

import com.packetsniffer.Sniffer;
import org.pcap4j.core.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class ShowAndSelectNetInterfaceGUI {

    private static final JFrame networkInterfaceListerFrame = new JFrame("Network Interface Lister");
    private static final JPanel networkListerPanel = new JPanel(new BorderLayout(10,10));
    private static final JTextArea netInterfacesTextList = new JTextArea(15,40);

    private static final JFrame chooseInterfaceFrame = new JFrame("Choose interface");
    public static final ImageIcon icon = new ImageIcon("src/main/resources/icon.png");

    private static List<PcapNetworkInterface> netsInterfaces;

    /////////////////////////////////////////////////////////////////////////////////////////////////

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
        return inputAsNumber <= Sniffer.checkInterfacesManual().size() && inputAsNumber > 0;
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
                        SniffGUI.sniffNetworkInterface(Integer.parseInt(interfaceInput.getText()), handle);
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

    private static JButton addCheckButton(JTextArea netInterfaces) {

        JButton checkInterfaces = new JButton("Check interfaces");

        checkInterfaces.addActionListener(new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    netsInterfaces = Sniffer.checkInterfacesManual();

                    int interfaceID = 1;

                    //Prints out a new line for every interface available
                    netInterfaces.setText("");
                    for( PcapNetworkInterface netInterface :  netsInterfaces){
                        netInterfaces.append("[" + interfaceID + "] " + netInterface.getDescription() + "\nAddress -> " + netInterface.getAddresses().get(0).getAddress() + "\n\n");
                        interfaceID++;
                    }

                    submitInterfaceToSniff();
                }

                catch (IOException ioException) {
                        ioException.printStackTrace();
                }
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

        networkListerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Add components to main panel
        networkListerPanel.add(scroll, BorderLayout.CENTER);
        networkListerPanel.add(addCheckButton(netInterfacesTextList), BorderLayout.SOUTH);

        networkInterfaceListerFrame.add(networkListerPanel); //Adds main panel to frame

        //MainFrame tweaks

        networkInterfaceListerFrame.setIconImage(icon.getImage());
        chooseInterfaceFrame.setIconImage(icon.getImage());
        networkInterfaceListerFrame.setResizable(false); //Doesnt let resize window
        networkInterfaceListerFrame.setContentPane(networkListerPanel);
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
