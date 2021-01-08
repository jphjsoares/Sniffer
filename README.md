# Packet Manager (SNIFFER)

This small program was made with the intention to deepen my following skills:

  - Java core langauge
  - Java swing GUI
  - Packet and network sniffing
  - Java concurrency

I will add new features once in a while. Although this wasn't created to be used in any kind of real world scenario. 
Just an educational project to learn more about all that I mentioned above.

# How to use
This program is actually really simple to use. Once I release the executables you will need:
* openjdk-15.0.1

And for the dependencies you need:
For linux:
* apt-get install libpcap-dev 
For CentOS
* yum install libpcap-devel
For Mac 
* brew install libpcap
For Windows
* choco install winpcap

After running the executable, just double click the interface you want to listen to. Then, a new window will open and you need to click -start sniffing-
Once sniffing begins, you can pause, resume or end the sniff (which will show you some statistics about the sniff).
You can also run the app in the background and use it as a bandwith monitor.
When you select to run in the background the app moves to system tray (if available). In the system tray there will be all the buttons available in the main frame, so that way you can interact directly with the app.

# Hope you enjoy it :D

