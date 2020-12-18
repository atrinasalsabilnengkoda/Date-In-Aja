/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author atrin
 */
public class ServerFrame extends JFrame implements Runnable {    
    
    JButton btStart, btStop;
    JTextArea taInfo;
    ServerSocket serverSocket;
    
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    ServerThread serverThread;
    
    public ServerFrame() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new java.awt.Color(102,102,255));
        
        JLabel lbStateServer = new JLabel("     Server's status\n");
        lbStateServer.setFont(new java.awt.Font("Rockwell", 1, 36));
        lbStateServer.setForeground(new java.awt.Color(255, 255, 255)); 
        
        taInfo = new JTextArea();
        taInfo.setEditable(false);
        taInfo.setFont(new java.awt.Font("Serif", 0, 16));
        taInfo.setBackground(new java.awt.Color(255, 255, 255));  
        taInfo.setForeground(new java.awt.Color(0, 0, 0));  
        
        JScrollPane scroll = new JScrollPane();
        scroll.setBackground(new java.awt.Color(102,102,255));
        scroll.setViewportView(taInfo);
        scroll.setPreferredSize(new Dimension(400, 400));
        
        btStart = new JButton("Start server");
        btStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                btStartEvent(ae);
            }
        });
        
        btStop = new JButton("Stop server");
        btStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                btStopEvent(ae);
            }
        });
        btStart.setEnabled(true);
        btStop.setEnabled(false);
        
        JPanel panelBtn = new JPanel();
        panelBtn.setBackground(new java.awt.Color(102,102,255));
        panelBtn.add(btStart);
        panelBtn.add(btStop);
        
        JPanel p1 = new JPanel();
        p1.setPreferredSize(new Dimension(30, 30));
        p1.setBackground(new java.awt.Color(102,102,255));
        JPanel p2 = new JPanel();
        p2.setPreferredSize(new Dimension(30, 30));
        p2.setBackground(new java.awt.Color(102,102,255));
        
        panel.add(lbStateServer, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(panelBtn, BorderLayout.SOUTH);
        panel.add(p1, BorderLayout.WEST);
        panel.add(p2, BorderLayout.EAST);
        
        
        this.add(panel);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void appendMessage(String message) {
        taInfo.append(message);
        taInfo.setCaretPosition(taInfo.getText().length() - 1);
    }
    
    private void startServer() {
        try {
            serverSocket = new ServerSocket(9999); // only has a single serverSocket object, this object will create each socket to 
            // communicate with the client socket
            appendMessage("["+sdf.format(new Date())+"] Server is running and ready to server any client...");
            appendMessage("\n["+sdf.format(new Date())+"] Now there's no one is connecting to server\n");
            
            while(true) {
                Socket socketOfServer = serverSocket.accept();      
                // For every client connected, this command will be executed, then a new thread is created to handle that client, 
                // which means there is a new socket on the server side to connect to the client's socket.
                serverThread = new ServerThread(socketOfServer);
                serverThread.taServer = this.taInfo;
                serverThread.start();
            }
            
        } catch (java.net.SocketException e) {
            System.out.println("Server is closed");
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error occurred, may be: This port is occupied by a server!");
            System.out.println("Or the server was closed");
            JOptionPane.showMessageDialog(this, "This port is occupied by the server", "Error", JOptionPane.ERROR_MESSAGE);
            this.setVisible(false);
            System.exit(0);
        }
    }

    private void btStartEvent(ActionEvent ae) {
        Connection conn = new UserDatabase().connect();
        if(conn == null) {
            JOptionPane.showMessageDialog(this, "Please open MySQL first", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new Thread(this).start();
        this.btStart.setEnabled(false);
        this.btStop.setEnabled(true);
    }
    
    private void btStopEvent(ActionEvent ae) {
        int kq = JOptionPane.showConfirmDialog(this, "Are you sure to close server?", "Notice", JOptionPane.YES_NO_OPTION);
        if(kq == JOptionPane.YES_OPTION) {
            try {
                //notify to all clients that server is closed:
                //code here
                serverThread.notifyToAllUsers("Warnning: Server has been closed!");

                //then close server:
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        ServerFrame serverFrame = new ServerFrame();
        serverFrame.setVisible(true);
        //serverFrame.startServer();
        
    }

    @Override
    public void run() {
        this.startServer();
    }
}