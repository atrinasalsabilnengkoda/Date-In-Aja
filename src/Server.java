
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.util.*;


public class Server extends javax.swing.JPanel {
    

   ArrayList<String> Users;
   ArrayList ClientOutputStreams;
   
   public class clientManager implements Runnable	
   {
       BufferedReader reader;
       Socket Socket;
       PrintWriter Client;

       public clientManager(Socket clientSocket, PrintWriter user) 
       {
            Client = user;
            try 
            {
                Socket = clientSocket;
                InputStreamReader isReader = new InputStreamReader(Socket.getInputStream());
                reader = new BufferedReader(isReader);
            }
            catch (Exception ex) 
            {
                chat.append("Error\n");
            }

       }

       @Override
       public void run() 
       {
            String message;
            String connect = "C";
            String disconnect = "D";
            String chat = "t" ;
            String[] data;

            try 
            {
                while ((message = reader.readLine()) != null) 
                {
                  
                    data = message.split(":");
                    
                    for (String token:data) 
                    {
                        Server.this.chat.append(token + "\n");
                    }

                    if (data[2].equals(connect)) 
                    {
                        Spread((data[0] + ":" + data[1] + ":" + chat));
                        userAdd(data[0]);
                    } 
                    else if (data[2].equals(disconnect)) 
                    {
                        Spread((data[0] + ": disconnected" + ":" + chat));
                        userRemove(data[0]);
                    } 
                    else if (data[2].equals(chat)) 
                    {
                        Spread(message);
                    } 
                    else 
                    {
                     //  chat.append("Disconnected\n");
                    }
                } 
             } 
             catch (Exception ex) 
             {
                Server.this.chat.append("disconnected \n");
                ex.printStackTrace();
                ClientOutputStreams.remove(Client);
             }}}


   
    public Server() {
        initComponents();
    }
    
    public static void main(String args[]) 
    {
           java.awt.EventQueue.invokeLater(() -> {
            new Server().setVisible(true);
        });
        
    }
    
    public class Connection implements Runnable 
    {
        @Override
        public void run() 
        {
            ClientOutputStreams = new ArrayList();
            Users = new ArrayList(); 
            //establish connection
            try 
            {
                // creating server socket 
                ServerSocket serverSock = new ServerSocket(1000);
                boolean flag = true;
                while (flag) 
                {                
                                // creting client socket 
				Socket clientSock = serverSock.accept();
				PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
                                // adding clients in to an array
				ClientOutputStreams.add(writer);
                                // creating threds to listen to every client 
				Thread listener = new Thread(new clientManager(clientSock, writer));
				listener.start();
				chat.append("Connection successful \n");
                }
            }
            catch (Exception ex)
            {
                chat.append("ERROR \n");
            }
        }
    }
    
    
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        chat = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        btnStart = new javax.swing.JButton();
        btnEnd = new javax.swing.JButton();
        btnOnlineUser = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();

        setBackground(new java.awt.Color(102, 102, 255));
        setMaximumSize(new java.awt.Dimension(550, 355));
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(579, 412));

        chat.setColumns(20);
        chat.setRows(5);
        jScrollPane1.setViewportView(chat);

        jLabel1.setFont(new java.awt.Font("Rockwell", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("DateInAja");

        btnStart.setBackground(new java.awt.Color(255, 255, 255));
        btnStart.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        btnStart.setForeground(new java.awt.Color(102, 102, 255));
        btnStart.setText("START");
        btnStart.setMaximumSize(new java.awt.Dimension(145, 31));
        btnStart.setMinimumSize(new java.awt.Dimension(145, 31));
        btnStart.setPreferredSize(new java.awt.Dimension(145, 31));
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        btnEnd.setBackground(new java.awt.Color(255, 255, 255));
        btnEnd.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        btnEnd.setForeground(new java.awt.Color(102, 102, 255));
        btnEnd.setText("END");
        btnEnd.setMaximumSize(new java.awt.Dimension(145, 31));
        btnEnd.setMinimumSize(new java.awt.Dimension(145, 31));
        btnEnd.setPreferredSize(new java.awt.Dimension(145, 31));
        btnEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEndActionPerformed(evt);
            }
        });

        btnOnlineUser.setBackground(new java.awt.Color(255, 255, 255));
        btnOnlineUser.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        btnOnlineUser.setForeground(new java.awt.Color(102, 102, 255));
        btnOnlineUser.setText("Online Users");
        btnOnlineUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOnlineUserActionPerformed(evt);
            }
        });

        btnClear.setBackground(new java.awt.Color(255, 255, 255));
        btnClear.setFont(new java.awt.Font("Rockwell", 1, 18)); // NOI18N
        btnClear.setForeground(new java.awt.Color(102, 102, 255));
        btnClear.setText("Clear");
        btnClear.setMaximumSize(new java.awt.Dimension(145, 31));
        btnClear.setMinimumSize(new java.awt.Dimension(145, 31));
        btnClear.setPreferredSize(new java.awt.Dimension(145, 31));
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnOnlineUser))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnOnlineUser))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
       
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnOnlineUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOnlineUserActionPerformed
      
    }//GEN-LAST:event_btnOnlineUserActionPerformed

    private void btnEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndActionPerformed
       
    }//GEN-LAST:event_btnEndActionPerformed

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        Thread starter = new Thread(new Connection());
        starter.start();

        chat.append("Booting\n");
    }//GEN-LAST:event_btnStartActionPerformed

    public void Clear_ButtonAction() {
        ActionEvent event;
        long when;
        when  = System.currentTimeMillis();
        event = new ActionEvent(this.btnClear, ActionEvent.ACTION_PERFORMED, "Anything", when, 0);
        btnClearActionPerformed(event);
    }
    
    public void userAdd (String data) 
    {
        String message;
        String add = ": :C";
       
        String name = data;
        Users.add(name);
        chat.append(name + " added. \n");
        String[] tempList = new String[(Users.size())];
        Users.toArray(tempList);

        for (String token:tempList) 
        {
            message = (token + add);
   
        }
    
    }
    
     public void userRemove (String data) 
    {
        String message;
        String add = ": :C";
        
        String name = data;
        Users.remove(name);
        chat.append(name + " removed.\n");
        String[] tempList = new String[(Users.size())];
        Users.toArray(tempList);

        for (String token:tempList) 
        {
            message = (token + add);
         
        }
       
    }
     
     public void Spread(String message) 
    {
	Iterator it = ClientOutputStreams.iterator();
        while (it.hasNext()) 
        {
            try 
            {
                PrintWriter writer = (PrintWriter) it.next();
		writer.println(message);
		//c_chat.append("Sending: " + message + "\n");
                writer.flush();
                chat.setCaretPosition(chat.getDocument().getLength());
            } 
            catch (Exception ex) 
            {
		chat.append("Error \n");
            }
        } 
    }
    
    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnEnd;
    private javax.swing.JButton btnOnlineUser;
    private javax.swing.JButton btnStart;
    private javax.swing.JTextArea chat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
