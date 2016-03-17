import java.awt.Color; //
import java.awt.Container;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
  
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
  
public class Test implements ActionListener {
    // 
    public static JFrame mainFrame = new JFrame();
    public static Label labelIP = new Label("Host IP");
    public static Label labelPortStart = new Label("StartPort:");
    public static Label labelPortEnd = new Label("EndPort:");
    public static Label labelThread = new Label("Thread:");
    public static Label labelResult = new Label("Result:");
    public static Label State = new Label("State:");
    public static Label Scanning = new Label("Ready");
    public static JTextField hostName = new JTextField("127.0.0.1");
    public static JTextField PortStart = new JTextField("0");
    public static JTextField PortEnd = new JTextField("100");
    public static JTextField ThreadNum = new JTextField("100");
    // 
    public static TextArea Result = new TextArea();
    public static Label DLGINFO = new Label("");
    public static JButton Start = new JButton("TCP Scan");
    public static JButton UDPStart = new JButton("UDP Scan");
    public static JButton Exit = new JButton("Exit");
    // 
    public static JDialog DLGError = new JDialog(mainFrame, "Error");
    public static JButton OK = new JButton("OK");
  
    public Test() {
  
        // set main window name
        mainFrame.setTitle("Multiple threads TCP port scan");
        // set window position and size
        mainFrame.setBounds(380, 300, 550, 300);
  
        // Action listener
        mainFrame.addWindowListener(new WindowAdapter() {
            /**
             * close the window
             * */
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
  
        });
  
        // set the error reminder dialog 
        Container dPanel = DLGError.getContentPane();
        dPanel.setLayout(null);
        dPanel.add(DLGINFO);
        dPanel.add(OK);
        OK.setActionCommand("ok");
        OK.addActionListener(this);
        // add components
        mainFrame.setLayout(null);
        mainFrame.setResizable(false);
        mainFrame.add(Start);
        mainFrame.add(Exit);
        mainFrame.add(labelIP);
        mainFrame.add(hostName);
        mainFrame.add(labelPortStart);
        mainFrame.add(labelPortEnd);
        mainFrame.add(PortStart);
        mainFrame.add(PortEnd);
        mainFrame.add(labelThread);
        mainFrame.add(ThreadNum);
        mainFrame.add(labelResult);
        mainFrame.add(Result);
        mainFrame.add(State);
        mainFrame.add(Scanning);
        mainFrame.add(UDPStart);
        
        // set scan and exit button
        Start.setBounds(375, 232, 90, 30);
        Start.setActionCommand("Start");
        Start.addActionListener(this);
        Exit.setBounds(475, 232, 60, 30);
        Exit.setActionCommand("Exit");
        Exit.addActionListener(this);
        
        UDPStart.setBounds(285, 232, 90, 30);
        UDPStart.setActionCommand("UDP");
        UDPStart.addActionListener(this);
        
        labelIP.setBounds(17, 13, 50, 20);
        hostName.setBounds(67, 10, 92, 25);
        hostName.setHorizontalAlignment(JTextField.CENTER);
  
        labelPortStart.setBounds(162, 13, 60, 20);
        PortStart.setBounds(227, 10, 45, 25);
        PortStart.setHorizontalAlignment(JTextField.CENTER);
  
        labelPortEnd.setBounds(292, 13, 60, 20);
        PortEnd.setBounds(357, 10, 45, 25);
        PortEnd.setHorizontalAlignment(JTextField.CENTER);
  
        labelThread.setBounds(422, 13, 50, 20);
        ThreadNum.setBounds(477, 10, 45, 25);
        ThreadNum.setHorizontalAlignment(JTextField.CENTER);
  
        labelResult.setBounds(1, 45, 55, 20);
        Result.setBounds(1, 65, 542, 160);
        Result.setEditable(false);
        //Result.setBackground(Color.GREY);// set color
        State.setBounds(17, 232, 60, 30);
        Scanning.setBounds(80, 232, 120, 30);
        mainFrame.setVisible(true);
    }
  
    /**
     * click event
     * */
    public void actionPerformed(ActionEvent e) {
  
        // get the command and handle event
        String cmd = e.getActionCommand();
  
        // start scanning
        if (cmd.equals("Start")) {
            try {
                Scan.hostAddress = InetAddress.getByName(Test.hostName
                        .getText());
                
            } catch (UnknownHostException e1) {
                DLGError.setBounds(300, 280, 160, 110);
                DLGINFO.setText("Wrong IP address o rhost address");
                DLGINFO.setBounds(25, 15, 100, 20);
                OK.setBounds(45, 40, 60, 30);
                DLGError.setVisible(true);
                return;
            }
            int minPort;
            int maxPort;
            int threadNum;
            // get the input data
            try {
                minPort = Integer.parseInt(PortStart.getText());
                maxPort = Integer.parseInt(PortEnd.getText());
                threadNum = Integer.parseInt(ThreadNum.getText());
            } catch (NumberFormatException e1) {
                DLGError.setBounds(300, 280, 299, 120);
                DLGINFO.setText("Wrong prot number or thread number. Must be integer!");
                DLGINFO.setBounds(10, 20, 280, 20);
                OK.setBounds(110, 50, 60, 30);
                DLGError.setVisible(true);
                return;
            }
            // error handle for input message
            if ((minPort < 0) || (maxPort > 65535) || (minPort > maxPort)) {
                DLGError.setBounds(300, 280, 295, 120);
                DLGINFO.setText("Minimum port must be 0-65535 and less than mamimum port");
                DLGINFO.setBounds(10, 20, 280, 20);
                OK.setBounds(120, 50, 60, 30);
                DLGError.setVisible(true);
                return;
            }
            if ((threadNum > 200) || (threadNum < 0)) {
                DLGError.setBounds(300, 280, 184, 120);
                DLGINFO.setText("Threads must be integer between 1 and 200");
                DLGINFO.setBounds(10, 20, 200, 20);
                OK.setBounds(55, 50, 60, 30);
                DLGError.setVisible(true);
                return;
            }
            Result.append("Scanning " + hostName.getText() + " Threads:" + threadNum
                    + "\n");
            Scanning.setText("Start scanning...");
            Result.append("Start port " + minPort + " end port " + maxPort + " \n");
            for (int i = minPort; i <= maxPort;) {
                if ((i + threadNum) <= maxPort) {
                    new Scan(i, i + threadNum).start();
                    i += threadNum;
                } else {
                    new Scan(i, maxPort).start();
                    i += threadNum;
                }
            }
            try {
                Thread.sleep(6000);// set the timeout
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            Result.append("Scan complete!\n");
            Scanning.setText("Scan complete");
        } 
        	else if(cmd.equals("UDP")){
        		//UDP scan
        		try {
        			UDPScan.hostAddress = InetAddress.getByName(Test.hostName.getText());
                } catch (UnknownHostException e1) {
                    DLGError.setBounds(300, 280, 160, 110);
                    DLGINFO.setText("Wrong IP address o rhost address");
                    DLGINFO.setBounds(25, 15, 100, 20);
                    OK.setBounds(45, 40, 60, 30);
                    DLGError.setVisible(true);
                    return;
                }
        		
        		int minPort;
                int maxPort;
                int threadNum;
                // get the input data
                try {
                    minPort = Integer.parseInt(PortStart.getText());
                    maxPort = Integer.parseInt(PortEnd.getText());
                    threadNum = Integer.parseInt(ThreadNum.getText());
                } catch (NumberFormatException e1) {
                    DLGError.setBounds(300, 280, 299, 120);
                    DLGINFO.setText("Wrong prot number or thread number. Must be integer!");
                    DLGINFO.setBounds(10, 20, 280, 20);
                    OK.setBounds(110, 50, 60, 30);
                    DLGError.setVisible(true);
                    return;
                }
                Result.append("Scanning " + hostName.getText() + " Threads:" + threadNum
                        + "\n");
                Scanning.setText("Start scanning...");
                Result.append("Start port " + minPort + " end port " + maxPort + " \n");
                for (int i = minPort; i <= maxPort;) {
                    if ((i + threadNum) <= maxPort) {
                        new UDPScan(i, i + threadNum).start();
                        i += threadNum;
                    } else {
                        new UDPScan(i, maxPort).start();
                        i += threadNum;
                    }
                }
                try {
                    Thread.sleep(6000);// set the timeout
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                //Result.append("Scan complete!\n");
                Scanning.setText("Scan complete");
        		
        		
        }
        	else if (cmd.equals("ok")) {
        		DLGError.dispose();
        	} else if (cmd.equals("Exit")) {
        		System.exit(1);
        	}
    }
  
    public static void main(String[] args) {
  
        new Test();
  
    }
  
}
  
class Scan extends Thread {
    int maxPort, minPort;
    public static InetAddress hostAddress;
  
    Scan(int minPort, int maxPort) {
        this.minPort = minPort;
        this.maxPort = maxPort;
    }
  
    public void run() {
        // scan the port
        for (int i = minPort; i < maxPort; i++) {
            Test.Scanning.setText("Scanning Port " + i );
            try {
                // set the socket address according to host name and port
                SocketAddress sockaddr = new InetSocketAddress(hostAddress, i);
                Socket scans = new Socket();
                int timeoutMs = 60;
                // connect socket to the server
                scans.connect(sockaddr, timeoutMs);
                
                scans.close();
  
                // show the result
                Test.Result.append("Host:" + Test.hostName.getText() + " TCP port:"
                        + i);
                switch (i) {
                case 20:
                    Test.Result.append("(FTP Data)");
                    break;
                case 21:
                    Test.Result.append("(FTP Control)");
                    break;
                case 23:
                    Test.Result.append("(TELNET)");
                    break;
                case 25:
                    Test.Result.append("(SMTP)");
                    break;
                case 38:
                    Test.Result.append("(RAP)");
                    break;
                case 53:
                    Test.Result.append("(DNS)");
                    break;
                case 80:
                    Test.Result.append("(HTTP)");
                    break;
                case 110:
                    Test.Result.append("(POP)");
                    break;
                case 161:
                    Test.Result.append("(SNMP)");
                    break;
                case 443:
                    Test.Result.append("(HTTPS)");
                    break;
                case 1433:
                    Test.Result.append("(SqlServer)");
                    break;
                case 3306:
                    Test.Result.append("(MySql)");
                    break;
                }
                Test.Result.append("  open\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class UDPScan extends Thread{
	int maxPort, minPort;
    public static InetAddress hostAddress;
  
    UDPScan(int minPort, int maxPort) {
        this.minPort = minPort;
        this.maxPort = maxPort;
    }
    public void run(){
    	for (int i = minPort; i < maxPort; i++) {
            Test.Scanning.setText("Scanning Port " + i);
            DatagramSocket ds = null;
            byte[] buff = new byte[128];
            try {
            	ds = new DatagramSocket();
            	DatagramPacket dp = new DatagramPacket(buff,buff.length);
            	ds.setSoTimeout(100);
            	ds.connect(hostAddress,i);
            	ds.send(dp);
            	ds.isConnected();
            	
            	dp = new DatagramPacket(buff,buff.length);
            	ds.receive(dp);
            	ds.close();
            	
            	Test.Result.append("Host:" + Test.hostName.getText() + " UDP port:" + i);
            	Test.Result.append("  open\n");
            } catch (IOException e){
            	e.printStackTrace();
            }           
    	}
    }
    
}