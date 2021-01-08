package com.packetgui;

import com.packetsniffer.Sniffer;
import org.pcap4j.core.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class ShowAndSelectNetInterfaceGUI {

    private static final JFrame networkInterfaceListerFrame = new JFrame("Network Interface Lister");
    private static final JPanel networkListerPanel = new JPanel(new BorderLayout(10,10));

    public static final ImageIcon icon = new ImageIcon("src/main/resources/icon.png");

    private static List<PcapNetworkInterface> netsInterfaces;



    private static void interfaceSubmitHandler(int interfaceInput) {
        PcapNetworkInterface networkInterfaceToUseInHandler  = netsInterfaces.get(interfaceInput);
        System.out.println("\n\n" + networkInterfaceToUseInHandler);
        final PcapHandle handle;
        try {
            handle = networkInterfaceToUseInHandler.openLive(Sniffer.SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, Sniffer.READ_TIMEOUT);
            SniffGUI.sniffNetworkInterface(interfaceInput, handle);
        } catch (PcapNativeException pcapNativeException) {
            pcapNativeException.printStackTrace();
        }
    }

    private static JButton selAndShowInterfaces() {

        JButton checkInterfaces = new JButton("Check interfaces");

        checkInterfaces.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    netsInterfaces = Sniffer.checkInterfacesManual();

                    int interfaceID = 1;

                    final DefaultListModel interfacesList = new DefaultListModel();

                    for( PcapNetworkInterface netInt :  netsInterfaces){
                        //Having issues with a PopOS(linux) virtual machine
                        interfacesList.addElement("[" + interfaceID + "] " + netInt.getDescription() + "Address -> " + netInt.getAddresses().get(0).getAddress());
                        interfaceID++;
                    }

                    final JList interfaces = new JList(interfacesList);
                    interfaces.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    interfaces.setSelectedIndex(0);
                    interfaces.setVisibleRowCount(3);

                    JScrollPane interfacesScroller = new JScrollPane(interfaces);

                    networkListerPanel.add(interfacesScroller, BorderLayout.CENTER);
                    checkInterfaces.setVisible(false);

                    networkInterfaceListerFrame.pack();


                    interfaces.addMouseListener(new MouseAdapter() {
                        int selectedInterface;
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            super.mouseClicked(e);
                            JList theList = (JList) e.getSource();

                            if(e.getClickCount() == 2) {
                                selectedInterface = theList.locationToIndex(e.getPoint());
                                if(selectedInterface >= 0) {
                                    interfaceSubmitHandler(selectedInterface);
                                    networkInterfaceListerFrame.setVisible(false);
                                }
                            }
                        }
                    });

                }
                catch (IOException ioException) {
                        ioException.printStackTrace();
                }
            }
        });

        return checkInterfaces;
    }

    private static void createAndShowNetInterfaceLister() {

        networkListerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        networkListerPanel.add(selAndShowInterfaces(), BorderLayout.SOUTH); // Add button

        networkInterfaceListerFrame.add(networkListerPanel); //Adds main panel to frame

        networkInterfaceListerFrame.setIconImage(icon.getImage()); //Show icon of the app

        //MainFrame tweaks
        networkInterfaceListerFrame.setContentPane(networkListerPanel);
        networkInterfaceListerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        networkInterfaceListerFrame.pack();
        networkInterfaceListerFrame.setLocationRelativeTo(null);
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
