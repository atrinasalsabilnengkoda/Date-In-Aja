/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 *
 * @author atrin
 */
public class SendingFileThread extends Thread {

    String sender, receiver;
    String filePath;
    Socket socketOfSender;
    BufferedWriter bufferedWriter;
    BufferedReader bufferedReader;
    JProgressBar progressBar;
    SendFileFrame frameToDisplayDialog;

    private final int BUFFER_SIZE = 1024;
    
    public SendingFileThread(String sender, String receiver, String filePath, Socket socket, SendFileFrame frameToDisplayDialog, JProgressBar progressBar) {
        this.sender = sender;
        this.receiver = receiver;
        this.filePath = filePath;
        this.socketOfSender = socket;
        this.frameToDisplayDialog = frameToDisplayDialog;
        this.progressBar = progressBar;
        
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socketOfSender.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socketOfSender.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(SendingFileThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void sendToServer(String line) {
        try {
            this.bufferedWriter.write(line);
            this.bufferedWriter.newLine();   // NewLine must be available to use the readLine function
            this.bufferedWriter.flush();
        } catch (java.net.SocketException e) {
            JOptionPane.showMessageDialog(null, "Server is close, can't send message!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @Override
    public void run() {
        FileInputStream fis = null;
        BufferedOutputStream bos = null;
        try {
            File file = new File(filePath); 
            //we need to send this file to server, and then server will deliver this file to the receiver
            int leng = (int) file.length();
            
            this.sendToServer("CMD_SENDFILETOSERVER|"+sender+"|"+receiver+"|"+file.getName()+"|"+leng);
            
            System.out.println("[SendingFileThread.java] CMD_SENDFILETOSERVER|"+sender+"|"+receiver+"|"+file.getName()+"|"+leng);   //dòng này thì có hiển thị ra
            
            fis = new FileInputStream(file);
            bos = new BufferedOutputStream(socketOfSender.getOutputStream());
            
            byte []buffer = new byte[BUFFER_SIZE];
            int count=0, percent=0;
            while((count = fis.read(buffer)) > 0) {
                bos.write(buffer, 0, count);    // continuously send each part of the file to the server
            }
            
        } catch (IOException ex) {
            Logger.getLogger(SendingFileThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(fis != null) fis.close();
                if(bos != null) bos.close();
                socketOfSender.close();
            } catch (IOException ex) {
                Logger.getLogger(SendingFileThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        JOptionPane.showMessageDialog(frameToDisplayDialog, "File successfully sent!", "Sucess", JOptionPane.INFORMATION_MESSAGE);  
    }
    
}
