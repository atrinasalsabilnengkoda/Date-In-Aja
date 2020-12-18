/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import view.ClientPanel;
import view.LoginPanel;
import view.PrivateChatPanel;
import view.RoomChatPanel;
import view.SignUpPanel;
import view.WelcomePanel;

/**
 *
 * @author atrin
 */
public class ClientFrame extends JFrame implements Runnable {
    String serverHost;
    public static final String NICKNAME_EXIST = "This nickname is already login in another place! Please using another nickname";
    public static final String NICKNAME_VALID = "This nickname is OK";
    public static final String NICKNAME_INVALID = "Nickname or password is incorrect";
    public static final String SIGNUP_SUCCESS = "Sign up successful!";
    public static final String ACCOUNT_EXIST = "This nickname has been used! Please use another nickname!";
    
    String name;
    String room;
    Socket socketOfClient;
    BufferedWriter bufferedWriter;
    BufferedReader bufferedReader;
    
    JPanel main;
    LoginPanel login;
    ClientPanel client;
    WelcomePanel welcome;
    SignUpPanel signUp;
    RoomChatPanel roomChat;
    
    Thread clientThread;
    boolean isRunning;
    
    JMenuBar menuBar;
    JMenu menuShareFile;
    JMenuItem itemSendFile;
    JMenu menuAccount;
    JMenuItem itemLeaveRoom, itemLogout, itemChangePass;
    
    SendFileFrame sendFileFrame;
    
    StringTokenizer tokenizer;
    String myDownloadFolder;
    
    Socket socketOfSender, socketOfReceiver;
    
    DefaultListModel<String> listModel, listModelThisRoom, listModel_rp;
        
    boolean isConnectToServer;
    
    int timeClicked = 0;    /// this variable to check if the user just clicked or double-clicked jList.
    
    Hashtable<String, PrivateChatPanel> listReceiver;
    
    public ClientFrame(String name) {
        this.name = name;
        socketOfClient = null;
        bufferedWriter = null;
        bufferedReader = null;
        isRunning = true;
        listModel = new DefaultListModel<>();
        listModelThisRoom = new DefaultListModel<>();
        listModel_rp = new DefaultListModel<>();
        isConnectToServer = false;
        listReceiver = new Hashtable<>();
        
        main = new JPanel();
        login = new LoginPanel();
        client = new ClientPanel();
        welcome = new WelcomePanel();
        signUp = new SignUpPanel();
        roomChat = new RoomChatPanel();
        
        //mainPanel.setSize(570, 450);
        welcome.setVisible(true);
        signUp.setVisible(false);
        login.setVisible(false);
        roomChat.setVisible(false);
        client.setVisible(false);
        
        main.add(welcome);
        main.add(signUp);
        main.add(login);
        main.add(roomChat);
        main.add(client);
        
        addEventsForWelcomePanel();
        addEventsForSignUpPanel();
        addEventsForLoginPanel();
        addEventsForClientPanel();
        addEventsForRoomPanel();
        
        menuBar = new JMenuBar();   //menuBar
        menuShareFile = new JMenu();    //menu, This is the menuBar item
        menuAccount = new JMenu();
        itemLeaveRoom = new JMenuItem();
        itemLogout = new JMenuItem();
        itemSendFile = new JMenuItem();     //menuItem: This is the menu item
        
        menuAccount.setText("Account");
        itemLogout.setText("Logout");
        itemLeaveRoom.setText("Leave room");
        menuAccount.add(itemLeaveRoom);
        menuAccount.add(itemLogout);
        
        menuShareFile.setText("File sharing");
        itemSendFile.setText("Send a file");
        menuShareFile.add(itemSendFile);
        
        menuBar.add(menuAccount);
        menuBar.add(menuShareFile);
        
        itemLeaveRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int kq = JOptionPane.showConfirmDialog(ClientFrame.this, "Are you sure to leave this room?", "Notice", JOptionPane.YES_NO_OPTION);
                if(kq == JOptionPane.YES_OPTION) {
                    leaveRoom();
                }
            }
        });
        itemLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int kq = JOptionPane.showConfirmDialog(ClientFrame.this, "Are you sure to logout?", "Notice", JOptionPane.YES_NO_OPTION);
                if(kq == JOptionPane.YES_OPTION) {
                    try {
                        isConnectToServer = false;
                        socketOfClient.close();
                        ClientFrame.this.setVisible(false);
                    } catch (IOException ex) {
                        Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    new ClientFrame(null).setVisible(true);
                }
            }
        });
        itemSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                //openSendFileFrame();
                JOptionPane.showMessageDialog(ClientFrame.this, "This function has changed! Go to private chat to send a file", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menuBar.setVisible(false);
        
        setJMenuBar(menuBar);
        pack();
        
        add(main);
        setSize(570, 520);
        setLocation(400, 100);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(name);
    }
    
    
    private void addEventsForWelcomePanel() {
        
        welcome.getBtLogin_welcome().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                welcome.setVisible(false);
                signUp.setVisible(false);
                login.setVisible(true);
                client.setVisible(false);
                roomChat.setVisible(false);
            }
        });
        welcome.getBtSignUp_welcome().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                welcome.setVisible(false);
                signUp.setVisible(true);
                login.setVisible(false);
                client.setVisible(false);
                roomChat.setVisible(false);
            }
        });
        
    }

    private void addEventsForSignUpPanel() {
        signUp.getLbBack_signup().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                welcome.setVisible(true);
                signUp.setVisible(false);
                login.setVisible(false);
                client.setVisible(false);
                roomChat.setVisible(false);
            }
        });
        signUp.getBtSignUp().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                btSignUpEvent();
            }
        });
    }

    private void addEventsForLoginPanel() {
        login.getTfNickname().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {
                if(ke.getKeyCode() == KeyEvent.VK_ENTER) btOkEvent();
            }
            
        });
        login.getTfPass().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {
                if(ke.getKeyCode() == KeyEvent.VK_ENTER) btOkEvent();
            }
            
        });
        login.getBtOK().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                btOkEvent();
            }
        });
        login.getLbBack_login().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                welcome.setVisible(true);
                signUp.setVisible(false);
                login.setVisible(false);
                client.setVisible(false);
                roomChat.setVisible(false);
            }
        });
    }

    private void addEventsForClientPanel() {
        client.getBtSend().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                btSendEvent();
            }
        });
        client.getBtExit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                btExitEvent();
            }
        });
        client.getTaInput().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {
                if(ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    btSendEvent();
                    btClearEvent();
                }
            }
        });
        //events for emotion icons:
        client.getLbLike().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                sendToServer("CMD_ICON|LIKE");
            }
        });
        client.getLbDislike().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                sendToServer("CMD_ICON|DISLIKE");
            }
        });
        client.getLbPacMan().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                sendToServer("CMD_ICON|PAC_MAN");
            }
        });
        client.getLbCry().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                sendToServer("CMD_ICON|CRY");
            }
        });
        client.getLbGrin().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                sendToServer("CMD_ICON|GRIN");
            }
        });
        client.getLbSmile().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                sendToServer("CMD_ICON|SMILE");
            }
        });
        
        client.getOnlineList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                openPrivateChatInsideRoom();
            }
        });
    }
    
    private void addEventsForRoomPanel() {
        roomChat.getLbRoom1().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                ClientFrame.this.room = roomChat.getLbRoom1().getText();
                labelRoomEvent();
            }
        });
        roomChat.getLbRoom2().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                ClientFrame.this.room = roomChat.getLbRoom2().getText();
                labelRoomEvent();
            }
        });
        roomChat.getLbRoom3().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                ClientFrame.this.room = roomChat.getLbRoom3().getText();
                labelRoomEvent();
            }
        });
        roomChat.getLbRoom4().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                ClientFrame.this.room = roomChat.getLbRoom4().getText();
                labelRoomEvent();
            }
        });
        roomChat.getLbRoom5().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                ClientFrame.this.room = roomChat.getLbRoom5().getText();
                labelRoomEvent();
            }
        });
        roomChat.getLbRoom6().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                ClientFrame.this.room = roomChat.getLbRoom6().getText();
                labelRoomEvent();
            }
        });
        roomChat.getLbRoom7().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                ClientFrame.this.room = roomChat.getLbRoom7().getText();
                labelRoomEvent();
            }
        });
        roomChat.getLbRoom8().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                ClientFrame.this.room = roomChat.getLbRoom8().getText();
                labelRoomEvent();
            }
        });
        
        roomChat.getOnlineList_rp().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                openPrivateChatOutsideRoom();
            }
        });
    }
    
    private void openPrivateChatInsideRoom() {
        timeClicked++;
        if(timeClicked == 1) {
            Thread countingTo500ms = new Thread(counting);
            countingTo500ms.start();
        }

        if(timeClicked == 2) {  // if countingTo500ms hasn't done yet, that means timeClicked still = 2:
            String nameClicked = client.getOnlineList().getSelectedValue();
            if(nameClicked.equals(ClientFrame.this.name)) {
                JOptionPane.showMessageDialog(ClientFrame.this, "Can't send a message to yourself!", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if(!listReceiver.containsKey(nameClicked)) { // If this is the first time texting the person that we just double-clicked   
                PrivateChatPanel pc = new PrivateChatPanel(name, nameClicked, serverHost, bufferedWriter, bufferedReader);

                pc.getLbReceiver().setText("Private chat with \""+pc.receiver+"\"");
                pc.setTitle(pc.receiver);
                pc.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                pc.setVisible(true);

                listReceiver.put(nameClicked, pc);
            } else {
                PrivateChatPanel pc = listReceiver.get(nameClicked);
                pc.setVisible(true);
            }
        }
    }
            
    private void openPrivateChatOutsideRoom() {
        timeClicked++;
        if(timeClicked == 1) {
            Thread countingTo500ms = new Thread(counting);
            countingTo500ms.start();
        }

        if(timeClicked == 2) {  // if countingTo500ms hasn't done yet, that means timeClicked still = 2:
            String privateReceiver = roomChat.getOnlineList_rp().getSelectedValue();
            PrivateChatPanel pc = listReceiver.get(privateReceiver);
            if(pc == null) {    // If this is the first time texting the person that we just double-clicked
                pc = new PrivateChatPanel(name, privateReceiver, serverHost, bufferedWriter, bufferedReader);
                
                pc.getLbReceiver().setText("Private chat with \""+pc.receiver+"\"");
                pc.setTitle(pc.receiver);
                pc.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                pc.setVisible(true);

                listReceiver.put(privateReceiver, pc);
            } else {
                pc.setVisible(true);
            }
        }
    }
    
    Runnable counting = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            timeClicked = 0;
        }
    };
    
    private void labelRoomEvent() {
        this.client.getTpMessage().setText("");
        this.sendToServer("CMD_ROOM|"+this.room);
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.roomChat.setVisible(false);
        this.client.setVisible(true);
        this.setTitle("\""+this.name+"\" - "+this.room);
        client.getLbRoom().setText(this.room);
    }
    
    private void leaveRoom() {
        this.sendToServer("CMD_LEAVE_ROOM|"+this.room);
        try {
            Thread.sleep(200);    
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.roomChat.setVisible(true);
        this.client.setVisible(false);
        client.getTpMessage().setText("");
        this.setTitle("\""+this.name+"\"");
    }
    
    private void btOkEvent() {
        String hostname = login.getTfHost().getText().trim();
        String nickname = login.getTfNickname().getText().trim();
        String pass = login.getTfPass().getText().trim();
        
        this.serverHost = hostname;
        this.name = nickname;
        
        if(hostname.equals("") || nickname.equals("") || pass.equals("")) {
            JOptionPane.showMessageDialog(this, "Please fill up all fields", "Notice!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(!isConnectToServer) {
            isConnectToServer = true;  // means that when running this ClientFrame.java file, only connect to the server 
            // only once,then don't connect anymore because there is already a connection to the server. If you keep 
            // connecting many times (due to wrong account entry) will create many sockets connecting to the server each 
            //time you click OK, then the socket will automatically close leading to an error!
            this.connectToServer(hostname); // create a socket to connect to the server
        }    
        this.sendToServer("CMD_CHECK_NAME|" +this.name+"|"+pass); // then send the name to request login = that name
        
        // server responds that the name entered is valid or not:
        String response = this.recieveFromServer();
        if(response != null) {
            if (response.equals(NICKNAME_EXIST) || response.equals(NICKNAME_INVALID)) {
                JOptionPane.showMessageDialog(this, response, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Valid name, enter the chat room:
                login.setVisible(false);
                roomChat.setVisible(true);
                client.setVisible(false);
                this.setTitle("\""+name+"\"");

                menuBar.setVisible(true);

                // A client thread is created to communicate with the server, note that this thread's task is only waiting 
                // for the server to send message.and print kq on the textarea, while sending the message to the server 
                // using the btSend event
                clientThread = new Thread(this);
                clientThread.start();
                this.sendToServer("CMD_ROOM|"+this.room); // Request ds users who are online to be able to chat privately

                System.out.println("this is \""+name+"\"");
            }
        } else System.out.println("[btOkEvent()] Server is not open yet, or already closed!");
    }
    
    private void btSignUpEvent() {
        String pass = this.signUp.getTfPass().getText();
        String pass2 = this.signUp.getTfPass2().getText();
        if(!pass.equals(pass2)) {
            JOptionPane.showMessageDialog(this, "Passwords don't match", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            String nickname = signUp.getTfID().getText().trim();
            String hostName = signUp.getTfHost().getText().trim();
            if(hostName.equals("") || nickname.equals("") || pass.equals("") || pass2.equals("")) {
                JOptionPane.showMessageDialog(this, "Please fill up all fields", "Notice!", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(!isConnectToServer) {
                isConnectToServer = true;  
                this.connectToServer(hostName);
            }    
            this.sendToServer("CMD_SIGN_UP|" +nickname+"|"+pass); // then send the name to request login = measure name
        
            String response = this.recieveFromServer();
            if(response != null) {
                if(response.equals(NICKNAME_EXIST) || response.equals(ACCOUNT_EXIST)) {
                    JOptionPane.showMessageDialog(this, response, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, response+"\nYou can now go back and login to join chat room", "Success!", JOptionPane.INFORMATION_MESSAGE);
                    signUp.clearTf();
                }
            }
        }
        
    }
            
    private void btSendEvent() {
        String message = client.getTaInput().getText().trim();
        if(message.equals("")) client.getTaInput().setText("");
        else {
            this.sendToServer("CMD_CHAT|" + message);
            this.btClearEvent();
        }
    }

    private void btClearEvent() {
        client.getTaInput().setText("");
    }

    private void btExitEvent() {
        try {
            isRunning = false;
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void connectToServer(String hostAddress) { // Each time you connect to the server, you reset the socketOfClient property
        try {
            socketOfClient = new Socket(hostAddress, 9999);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
            
        } catch (java.net.UnknownHostException e) {
            JOptionPane.showMessageDialog(this, "Host IP is not correct.\nPlease try again!", "Failed to connect to server", JOptionPane.ERROR_MESSAGE);
        } catch (java.net.ConnectException e) {
            JOptionPane.showMessageDialog(this, "Server is unreachable, maybe server is not open yet, or can't find this host.\nPlease try again!", "Failed to connect to server", JOptionPane.ERROR_MESSAGE);
        } catch(java.net.NoRouteToHostException e) {
            JOptionPane.showMessageDialog(this, "Can't find this host!\nPlease try again!", "Failed to connect to server", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }
    
    public void sendToServer(String line) {
        try {
            this.bufferedWriter.write(line);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        } catch (java.net.SocketException e) {
            JOptionPane.showMessageDialog(this, "Server is closed, can't send message!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (java.lang.NullPointerException e) {
            System.out.println("[sendToServer()] Server is not open yet, or already closed!");
        } catch (IOException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String recieveFromServer() {
        try {
            return this.bufferedReader.readLine();  // Note that only one line is received from the sending server, if the server sends multiple lines, the following lines are not read
        } catch (java.lang.NullPointerException e) {
            System.out.println("[recieveFromServer()] Server is not open yet, or already closed!");
        } catch (IOException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void disconnect() {
        System.out.println("disconnect()");
        try {
            if(bufferedReader!=null) this.bufferedReader.close();
            if(bufferedWriter!=null) this.bufferedWriter.close();
            if(socketOfClient!=null) this.socketOfClient.close();
            System.out.println("trong khoi try catch");
        } catch (IOException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        ClientFrame client = new ClientFrame(null);
        client.setVisible(true);
    }

    @Override
    public void run() {
        String response;
        String sender, receiver, fileName, thePersonIamChattingWith, thePersonSendFile;
        String msg;
        String cmd, icon;
        PrivateChatPanel pc;
        
        while(isRunning) {
            response = this.recieveFromServer();   // Receive response from server after sending data above
            tokenizer = new StringTokenizer(response, "|");
            cmd = tokenizer.nextToken();
            switch (cmd) {
                case "CMD_CHAT": 
                    sender = tokenizer.nextToken();
                    msg = response.substring(cmd.length()+sender.length()+2, response.length());
                    
                    if(sender.equals(this.name)) this.client.appendMessage(sender+": ", msg, Color.BLACK, new Color(0, 102, 204));
                    else this.client.appendMessage(sender+": ", msg, Color.MAGENTA, new Color(56, 224, 0));
                    break;
                    
                case "CMD_PRIVATECHAT":   
                    sender = tokenizer.nextToken();
                    msg = response.substring(cmd.length()+sender.length()+2, response.length());
                    
                    pc = listReceiver.get(sender);
                    
                    if(pc == null) {
                        pc = new PrivateChatPanel(name, sender, serverHost, bufferedWriter, bufferedReader);
                        pc.sender = name;
                        pc.receiver = sender;
                        pc.serverHost = this.serverHost;
                        pc.bw = ClientFrame.this.bufferedWriter;
                        pc.br = ClientFrame.this.bufferedReader;

                        pc.getLbReceiver().setText("Private chat with \""+pc.receiver+"\"");
                        pc.setTitle(pc.receiver);
                        pc.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        pc.setVisible(true);

                        listReceiver.put(sender, pc);
                    } else {
                        pc.setVisible(true);
                    }
                    pc.appendMessage_Left(sender+": ", msg);
                    break;
                    
                case "CMD_ONLINE_USERS":
                    listModel.clear();
                    listModel_rp.clear();
                    while(tokenizer.hasMoreTokens()) {
                        cmd = tokenizer.nextToken();
                        listModel.addElement(cmd);
                        listModel_rp.addElement(cmd);
                    }
                    client.getOnlineList().setModel(listModel);
                    
                    listModel_rp.removeElement(this.name);
                    roomChat.getOnlineList_rp().setModel(listModel_rp);
                    break;
                    
                case "CMD_ONLINE_THIS_ROOM":
                    listModelThisRoom.clear();
                    while(tokenizer.hasMoreTokens()) {
                        cmd = tokenizer.nextToken();
                        listModelThisRoom.addElement(cmd);
                    }
                    client.getOnlineListThisRoom().setModel(listModelThisRoom);
                    break;
                    
                case "CMD_FILEAVAILABLE":
                    System.out.println("file available");
                    fileName = tokenizer.nextToken();
                    thePersonIamChattingWith = tokenizer.nextToken();
                    thePersonSendFile = tokenizer.nextToken();
                    
                    pc = listReceiver.get(thePersonIamChattingWith);
                    
                    if(pc == null) {
                        sender = this.name;
                        receiver = thePersonIamChattingWith;
                        pc = new PrivateChatPanel(sender, receiver, serverHost, bufferedWriter, bufferedReader);
                        
                        pc.getLbReceiver().setText("Private chat with \""+pc.receiver+"\"");
                        pc.setTitle(pc.receiver);
                        pc.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        
                        listReceiver.put(receiver, pc);
                    }
                    
                    pc.setVisible(true);
                    pc.insertButton(thePersonSendFile, fileName);
                    break;
                    
                case "CMD_ICON":
                    icon = tokenizer.nextToken();
                    cmd = tokenizer.nextToken();  
                    
                    if(cmd.equals(this.name)) this.client.appendMessage(cmd+": ", "\n  ", Color.BLACK, Color.BLACK);
                    else this.client.appendMessage(cmd+": ", "\n   ", Color.MAGENTA, Color.MAGENTA);
                    
                    switch (icon) {
                        case "LIKE":
                            this.client.getTpMessage().insertIcon(new ImageIcon(getClass().getResource("/images/like2.png")));
                            break;
                            
                        case "DISLIKE":
                            this.client.getTpMessage().insertIcon(new ImageIcon(getClass().getResource("/images/dislike.png")));
                            break;
                            
                        case "PAC_MAN":
                            this.client.getTpMessage().insertIcon(new ImageIcon(getClass().getResource("/images/pacman.png")));
                            break;
                            
                        case "SMILE":
                            this.client.getTpMessage().insertIcon(new ImageIcon(getClass().getResource("/images/smile.png")));
                            break;
                            
                        case "GRIN":
                            this.client.getTpMessage().insertIcon(new ImageIcon(getClass().getResource("/images/grin.png")));
                            break;
                            
                        case "CRY":
                            this.client.getTpMessage().insertIcon(new ImageIcon(getClass().getResource("/images/cry.png")));
                            break;
                            
                        default:
                            throw new AssertionError("The icon is invalid, or can't find icon!");
                    }
                    
                    break;
                    
                default:
                    if(!response.startsWith("CMD_")) {      // case the response is just a regular message
                        if(response.equals("Warnning: Server has been closed!")) {
                            this.client.appendMessage(response, Color.RED);
                        }
                        else this.client.appendMessage(response, new Color(153, 153, 153));
                    }
            }
        }
        System.out.println("Disconnected to server!");
    }


}