
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author atrin
 */
public class ClientFemale extends javax.swing.JFrame {
    String nameUser;
    String Addr = "localhost";
    
    ArrayList<String> Users = new ArrayList();
    int Port = 1000;
    Boolean isConnected = false;
    
    Socket Socket;
    BufferedReader Reader;
    PrintWriter Writer;
    
    public static String text; 
    
    // creating threads 
    public void ListenThread() 
    {
         Thread IncomingReader = new Thread(new ClientFemale.IncomingReader());
         IncomingReader.start();
    }
    
    
    public void userAdd(String data) 
    {
         Users.add(data);
    }
    
    
    public void userRemove(String data) 
    {
         Chat.append(data + " is now offline.\n");
    }
    
    
    public void writeUsers() 
    {
         String[] tempList = new String[(Users.size())];
         Users.toArray(tempList);
         for (String token:tempList) 
         {
             //users.append(token + "\n");
         }
    }
    
    
    public void sendDisconnect() 
    {
        String bye = (nameUser + ": :D");
        try
        {
            Writer.println(bye); 
            Writer.flush(); 
        } catch (Exception e) 
        {
            //Chat.append("Could not send Disconnect message.\n");
        }
    }
    public ClientFemale() {
        initComponents();
    }

    public class IncomingReader implements Runnable
    {
        @Override
        public void run() 
        {
            String[] data;
            String stream;
            String done = "Done";
            String connect = "Login";
            String disconnect = "LogOff";
            String chat = "t";

            try 
            {
                while ((stream = Reader.readLine()) != null) 
                {
                     data = stream.split(":");

                     if (data[2].equals(chat)) 
                     {
                        Chat.append(data[0] + ": " + data[1] + "\n");
                        Chat.setCaretPosition(Chat.getDocument().getLength());
                     } 
                     else if (data[2].equals(connect))
                     {
                        Chat.removeAll();
                        userAdd(data[0]);
                     } 
                     else if (data[2].equals(disconnect)) 
                     {
                         userRemove(data[0]);
                     } 
                     else if (data[2].equals(done)) 
                     {
                        //users.setText("");
                        writeUsers();
                        Users.clear();
                     }
                }
           }catch(Exception ex) { }
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        Chat = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        btnStart = new javax.swing.JButton();
        btnEnd = new javax.swing.JButton();
        inputType = new javax.swing.JTextField();
        btnSend = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 204, 204));
        setMaximumSize(new java.awt.Dimension(550, 355));
        setName(""); // NOI18N

        Chat.setColumns(20);
        Chat.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        Chat.setRows(5);
        jScrollPane1.setViewportView(Chat);

        jLabel1.setFont(new java.awt.Font("Rockwell", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("DateInAja");

        btnStart.setBackground(new java.awt.Color(255, 255, 255));
        btnStart.setFont(new java.awt.Font("Rockwell", 0, 18)); // NOI18N
        btnStart.setForeground(new java.awt.Color(51, 51, 51));
        btnStart.setText("Start");
        btnStart.setMaximumSize(new java.awt.Dimension(145, 30));
        btnStart.setMinimumSize(new java.awt.Dimension(145, 30));
        btnStart.setPreferredSize(new java.awt.Dimension(145, 30));
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        btnEnd.setBackground(new java.awt.Color(255, 255, 255));
        btnEnd.setFont(new java.awt.Font("Rockwell", 0, 18)); // NOI18N
        btnEnd.setForeground(new java.awt.Color(51, 51, 51));
        btnEnd.setText("Logoff");
        btnEnd.setMaximumSize(new java.awt.Dimension(145, 30));
        btnEnd.setMinimumSize(new java.awt.Dimension(145, 30));
        btnEnd.setPreferredSize(new java.awt.Dimension(145, 30));
        btnEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEndActionPerformed(evt);
            }
        });

        inputType.setFont(new java.awt.Font("Rockwell", 0, 12)); // NOI18N
        inputType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputTypeActionPerformed(evt);
            }
        });

        btnSend.setFont(new java.awt.Font("Rockwell", 0, 18)); // NOI18N
        btnSend.setForeground(new java.awt.Color(51, 51, 51));
        btnSend.setText("Send");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                        .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(btnEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(inputType)
                        .addGap(18, 18, 18)
                        .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputType, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSend))
                .addGap(23, 23, 23))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void inputTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputTypeActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        String nothing = "";
        if ((inputType.getText()).equals(nothing)) {
            inputType.setText("");
            inputType.requestFocus();
        } else {
            try {
               Writer.println(nameUser + ":" + inputType.getText() + ":" + "t");
               Writer.flush(); // flushes the buffer
            } catch (Exception ex) {
                Chat.append("            Could not send Messages.\n");
            }
            inputType.setText("");
            inputType.requestFocus();
        }
        
        inputType.setText("");
        inputType.requestFocus();
    }//GEN-LAST:event_btnSendActionPerformed

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        nameUser = text;
   
        if(isConnected==false)
        try {
            Socket = new Socket(Addr, Port);
            InputStreamReader streamreader = new InputStreamReader(Socket.getInputStream());
            Reader = new BufferedReader(streamreader);
            Writer = new PrintWriter(Socket.getOutputStream());
            Writer.println(nameUser + ":Has Connected :C");
            Writer.flush(); 
            isConnected = true;
        }catch (Exception ex) {
            Chat.append("            Fail to Connect Server!\n");
        }   
        else{
            Chat.append("             You already Started! \n");
        }
        ListenThread();
    }//GEN-LAST:event_btnStartActionPerformed

    private void btnEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndActionPerformed
        sendDisconnect();
        this.setVisible(false);
    }//GEN-LAST:event_btnEndActionPerformed

    
    public static void main(String args[]) 
    {     
        text = args[0];
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                new ClientFemale().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea Chat;
    private javax.swing.JButton btnEnd;
    private javax.swing.JButton btnSend;
    private javax.swing.JButton btnStart;
    private javax.swing.JTextField inputType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
