/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author atrin
 */
public class ServerThread  extends Thread {
    Socket socketOfServer;
    BufferedWriter bufferedWriter;
    BufferedReader bufferedReader;
    String clientName, clientPassword, clientRoom;
    public static Hashtable<String, ServerThread> userList = new Hashtable<>();
    
    public static final String NICKNAME_EXIST = "This nickname is already login in another place! Please using another nickname";
    public static final String NICKNAME_VALID = "This nickname is OK";
    public static final String NICKNAME_INVALID = "Nickname or password is incorrect";
    public static final String SIGNUP_SUCCESS = "Sign up successful!";
    public static final String ACCOUNT_EXIST = "This nickname has been used! Please use another nickname!";
    
    public JTextArea taServer;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    
    StringTokenizer tokenizer;
    private final int BUFFER_SIZE = 1024;
    
    String senderName, receiverName;   
    static Socket senderSocket, receiverSocket;     
    
    UserDatabase userDB;
    
    static boolean isBusy = false;    
    
    public ServerThread(Socket socketOfServer) {
        this.socketOfServer = socketOfServer;
        this.bufferedWriter = null;
        this.bufferedReader = null;
        
        clientName = "";
        clientPassword = "";
        clientRoom = "";
        
        userDB = new UserDatabase();
        userDB.connect();
    }
    
    public void appendMessage(String message) {
        taServer.append(message);
        taServer.setCaretPosition(taServer.getText().length() - 1);   
    }
    
    public String recieveFromClient() {
        try {
            return bufferedReader.readLine();
        } catch (IOException ex) {
            System.out.println(clientName+" is disconnected!");
        }
        return null;
    }
    
    public void sendToClient(String response) {     
        try {
            bufferedWriter.write(response);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException ex) {
            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendToSpecificClient(ServerThread socketOfClient, String response) {     
        try {
            BufferedWriter writer = socketOfClient.bufferedWriter;
            writer.write(response);
            writer.newLine();
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendToSpecificClient(Socket socket, String response) {     
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(response);
            writer.newLine();
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void notifyToAllUsers(String message) {
        
        Enumeration<ServerThread> clients = userList.elements();
        ServerThread st;
        BufferedWriter writer;
        
        while(clients.hasMoreElements()) {
            st = clients.nextElement();
            writer = st.bufferedWriter;

            try {
                writer.write(message);
                writer.newLine();
                writer.flush();
            } catch (IOException ex) {
                Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void notifyToUsersInRoom(String message) {
        Enumeration<ServerThread> clients = userList.elements();
        ServerThread st;
        BufferedWriter writer;
        
        while(clients.hasMoreElements()) {
            st = clients.nextElement();
            if(st.clientRoom.equals(this.clientRoom)) {     
                writer = st.bufferedWriter;

                try {
                    writer.write(message);
                    writer.newLine();
                    writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void notifyToUsersInRoom(String room, String message) {      
        Enumeration<ServerThread> clients = userList.elements();
        ServerThread st;
        BufferedWriter writer;
        
        while(clients.hasMoreElements()) {
            st = clients.nextElement();
            if(st.clientRoom.equals(room)) {
                writer = st.bufferedWriter;

                try {
                    writer.write(message);
                    writer.newLine();
                    writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void closeServerThread() {
        try {
            bufferedReader.close();
            bufferedWriter.close();
            socketOfServer.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getAllUsers() {
        StringBuffer kq = new StringBuffer();
        String temp = null;
        
        Enumeration<String> keys = userList.keys();
        if(keys.hasMoreElements()) {
            String str = keys.nextElement();
            kq.append(str);
        }
        
        while(keys.hasMoreElements()) {
            temp = keys.nextElement();
            kq.append("|").append(temp);
        }
        
        return kq.toString();
    }
    
    public String getUsersThisRoom() {
        StringBuffer kq = new StringBuffer();
        String temp = null;
        ServerThread st;
        Enumeration<String> keys = userList.keys();
        
        while(keys.hasMoreElements()) {
            temp = keys.nextElement();
            st = userList.get(temp);
            if(st.clientRoom.equals(this.clientRoom))  kq.append("|").append(temp);
        }
        
        if(kq.equals("")) return "|";
        return kq.toString();  
    }
    
    public String getUsersAtRoom(String room) {
        StringBuffer kq = new StringBuffer();
        String temp = null;
        ServerThread st;
        Enumeration<String> keys = userList.keys();
        
        while(keys.hasMoreElements()) {
            temp = keys.nextElement();
            st = userList.get(temp);
            if(st.clientRoom.equals(room))  kq.append("|").append(temp);
        }
        
        if(kq.equals("")) return "|";
        return kq.toString();   
    }
    
    public void clientQuit() {
        // When sending files, we will create a new socket to send files, and when sending the file, that socket will automatically close
        if(clientName != null) {
            
            this.appendMessage("\n["+sdf.format(new Date())+"] Client \""+clientName+"\" is disconnected!");
            userList.remove(clientName);
            if(userList.isEmpty()) this.appendMessage("\n["+sdf.format(new Date())+"] Now there's no one is connecting to server\n");
            notifyToAllUsers("CMD_ONLINE_USERS|"+getAllUsers());
            notifyToUsersInRoom("CMD_ONLINE_THIS_ROOM"+getUsersThisRoom());
            notifyToUsersInRoom(clientName+" has quitted");
        }
    }
    
    // update room for the object of this class in userList
    public void changeUserRoom() {      
        ServerThread st = userList.get(this.clientName);
        st.clientRoom = this.clientRoom;
        userList.put(this.clientName, st);    
        // st is the serverThread object associated with the client requesting to change room
    }
    
    public void removeUserRoom() {
        ServerThread st = userList.get(this.clientName);
        st.clientRoom = this.clientRoom;
        userList.put(this.clientName, st);
    }
    
    @Override
    public void run() {
        try {
            // create streams in and out of client sockets
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            
            boolean isUserExist = true;
            String message, sender, receiver, fileName;
            StringBuffer str;
            String cmd, icon;
            while(true) {   // Just wait for the client to send messages and respond
                try {
                    message = recieveFromClient();
                    tokenizer = new StringTokenizer(message, "|");
                    cmd = tokenizer.nextToken();
                    
                    switch (cmd) {
                        case "CMD_CHAT":
                            str = new StringBuffer(message);
                            str = str.delete(0, 9);
                            notifyToUsersInRoom("CMD_CHAT|" + this.clientName+"|"+str.toString());    //this.clientName = client name to send message to
                            break;
                            
                        case "CMD_PRIVATECHAT":
                            String privateSender = tokenizer.nextToken();
                            String privateReceiver = tokenizer.nextToken();
                            String messageContent = message.substring(cmd.length()+privateSender.length()+privateReceiver.length()+3, message.length());

                            ServerThread st_receiver = userList.get(privateReceiver);
                            sendToSpecificClient(st_receiver, "CMD_PRIVATECHAT|" + privateSender + "|" + messageContent);
                            
                            System.out.println("[ServerThread] message = "+messageContent);
                            break;
                            
                        case "CMD_ROOM":
                            clientRoom = tokenizer.nextToken();
                            changeUserRoom();
                            notifyToAllUsers("CMD_ONLINE_USERS|"+getAllUsers());
                            notifyToUsersInRoom("CMD_ONLINE_THIS_ROOM"+getUsersThisRoom());
                            notifyToUsersInRoom(clientName+" has just entered!");
                            break;
                            
                        case "CMD_LEAVE_ROOM":
                            String room = clientRoom;
                            clientRoom = "";    // if for clientRoom = null then error!
                            removeUserRoom();
                            notifyToUsersInRoom(room, "CMD_ONLINE_THIS_ROOM"+getUsersAtRoom(room));
                            notifyToUsersInRoom(room, clientName+" has just leaved this room!");
                            
                            break;
                            
                        case "CMD_CHECK_NAME":
                            clientName = tokenizer.nextToken();
                            clientPassword = tokenizer.nextToken();
                            isUserExist = userList.containsKey(clientName);
                            
                            if(isUserExist) {  // nickname is exist, which means that someone else is logged in with that nick
                                sendToClient(NICKNAME_EXIST);
                            }
                            else {  
                                int kq = userDB.checkUser(clientName, clientPassword);
                                if(kq == 1) {
                                    sendToClient(NICKNAME_VALID);
                                    // nickname has not been logged in yet
                                    this.appendMessage("\n["+sdf.format(new Date())+"] Client \""+clientName+"\" is connecting to server");
                                    userList.put(clientName, this);     // add the name of this object and add this object to the userList
                                } else sendToClient(NICKNAME_INVALID);
                            }
                            break;
                            
                        case "CMD_SIGN_UP":
                            String name = tokenizer.nextToken();
                            String pass = tokenizer.nextToken();
                            System.out.println("name, pass = "+name+" - "+pass);
                            isUserExist = userList.containsKey(name);
                            
                            if(isUserExist) {
                                sendToClient(NICKNAME_EXIST);
                            } else {
                                int kq = userDB.insertUser(new User(name, pass));
                                if(kq > 0) {
                                    sendToClient(SIGNUP_SUCCESS);
                                } else sendToClient(ACCOUNT_EXIST);
                            }
                            break;
                            
                        case "CMD_ONLINE_USERS":
                            sendToClient("CMD_ONLINE_USERS|"+getAllUsers());
                            notifyToUsersInRoom("CMD_ONLINE_THIS_ROOM"+getUsersThisRoom());
                            break;
                        
                        case "CMD_SENDFILETOSERVER":    //the sender sends a file to server:
                            sender = tokenizer.nextToken();
                            receiver = tokenizer.nextToken();
                            fileName = tokenizer.nextToken();
                            int len = Integer.valueOf(tokenizer.nextToken());
                            
                            String path = System.getProperty("user.dir") + "\\sendfile\\" +fileName;
                            

                            BufferedInputStream bis = new BufferedInputStream(socketOfServer.getInputStream());   // get the incoming stream from the sender
                            FileOutputStream fos = new FileOutputStream(path);  // The output stream is to the file that will be stored on the server's hard drive
                            
                            byte[] buffer = new byte[BUFFER_SIZE];
                            int count = -1;
                            while((count = bis.read(buffer)) > 0) {  // is read how many words sender will temporarily store in the buffer array
                                fos.write(buffer, 0, count);         // and then os gets a buffer and sends it to the receiver
                            }

                            Thread.sleep(300);
                            bis.close();
                            fos.close();
                            socketOfServer.close();
                            /// inform the sender and receiver that the file has just been sent, then they want to download it, it's their job:
                            ServerThread stSender = userList.get(sender); // Note that stSender is not socketOfServer above.
                            // because socketOfServer is a socket that connects to a sender's temporary socket. That temporary socket is created 
                            // when the sender wants it send a file to the server, and after sending the file, that temporary socket disappears
                            ServerThread stReceiver = userList.get(receiver);
                            
                            sendToSpecificClient(stSender, "CMD_FILEAVAILABLE|"+fileName+"|"+receiver+"|"+sender);
                            sendToSpecificClient(stReceiver, "CMD_FILEAVAILABLE|"+fileName+"|"+sender+"|"+sender);
                            
                            isBusy = false;
                            break;
                            
                        case "CMD_DOWNLOADFILE":    //server sends file to someone who just pressed download file
                            fileName = tokenizer.nextToken();
                            path = System.getProperty("user.dir") + "\\sendfile\\" + fileName;
                            FileInputStream fis = new FileInputStream(path);
                            BufferedOutputStream bos = new BufferedOutputStream(socketOfServer.getOutputStream());
                            
                            byte []buffer2 = new byte[BUFFER_SIZE];
                            int count2=0;
                            
                            while((count2 = fis.read(buffer2)) > 0) {
                                bos.write(buffer2, 0, count2);    // continuously send each part of the file to the server
                            }

                            bos.close();
                            fis.close();
                            socketOfServer.close();
                            
                            break;
                            
                        case "CMD_ICON":
                            icon = tokenizer.nextToken();
                            notifyToUsersInRoom("CMD_ICON|"+icon+"|"+this.clientName);
                            break;
                            
                        default:
                            notifyToAllUsers(message);
                            break;
                    }
                    
                } catch (Exception e) {
                    clientQuit();
                    break;
                }
            }
        } catch (IOException ex) {
            clientQuit();
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        //this.closeServerThread();
    }
}