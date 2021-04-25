/**
 * Server class which send and receive the message from the client.
 * Team 4
 * @version 04/20/2020
 * @author JiaJia Chen, Shuying Chen, Fei Lin
*/
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;


public class Server extends JFrame implements ActionListener{

   //Attribute 
   Vector<ServerThread> players = new Vector<>();
   private int playerNum = 0;
   private static PrintWriter opw;
   private ServerSocket gameServerSoc;
   private Socket gameSocket;

   
   //Attribute for Gui
   private JButton exit;
   private JButton start;
   private JButton jbRefresh;
   private JLabel totalPlayer;
   private JTextArea jtaChat;

   //Attribute for ipaddress
   private InetAddress host = null;
   private String ip_address = null;
   
   /**
       This is a main method to call constructor to run the server GUI
   */
   public static void main(String[] args) {
      new Server();
   }
   
   
   
   /**
    *  This is a constructor method to create the GUI.
    *  display a list of current clients logged on
    *  matintains information about the game such that it can validation all moves
    */
    
   public Server() {
      try {
         //get ip addreserverChat
         host = InetAddress.getLocalHost();
         ip_address = host.getHostAddress();
      } 
      catch(Exception e) {
      }
      
      //create GUI
      JFrame jfServer = new JFrame("Server");
      jfServer.setLayout(new BorderLayout());
      
      //gui for chat
      jtaChat = new JTextArea("", 20, 30);
      jtaChat.setEnabled(false);
      JScrollPane sp = new JScrollPane(jtaChat); 
      jfServer.add(sp,BorderLayout.CENTER);
   
      //button panel
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(5, 1));
      jfServer.add(panel, BorderLayout.SOUTH);
      
      //ipAddreserverChat
      JLabel ipLabel = new JLabel("Server' IP Address: " + ip_address);
      panel.add(ipLabel);
   
      //player number
      totalPlayer = new JLabel("Total Players online: "+ players.size() );
      panel.add(totalPlayer);
      
      //button
      jbRefresh = new JButton("Refresh/Current players list");
      start = new JButton("Start"); 
      exit = new JButton("Exit"); 
   
      panel.add(jbRefresh); 
      panel.add(exit);
      panel.add(start);
   
      //add action listener
      exit.addActionListener( this );
      start.addActionListener( this );
      jbRefresh.addActionListener( this );
      
      //when exit the gui by red x
      jfServer.addWindowListener(
         new WindowAdapter(){
            public void windowClosing(WindowEvent we){
               JOptionPane.showMessageDialog(null, "Thank you, have good day!");
               exit();
            }
         }); 
   
      //fundamental setting for server gui
      jfServer.pack();
      jfServer.setVisible(true); 
      jfServer.setLocationRelativeTo(null);
      jfServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
      // accepts mulitple connections from clients
      try {
         //create socket for game
         gameServerSoc = new ServerSocket(16789);
         gameSocket = null;
         
         while(true) {
            //track players
            playerNum++;
            gameSocket = gameServerSoc.accept();
            ServerThread ts = new ServerThread(gameSocket, this, playerNum);
            
            //print write
            opw = new PrintWriter( new OutputStreamWriter(gameSocket.getOutputStream()));
            opw.println("WELCOME, You are Player "+(playerNum));
            opw.flush();
            
            //add thread to the server
            ts.start();
            players.add(ts);
            
            //display current players are log in
            jtaChat.append("Player" + playerNum +" connect \n");
            System.out.println("Player" + playerNum +" connect \n");
            
         }//end of while loop
         
      } catch(IOException ioe) {
         ioe.printStackTrace();
         System.out.println("Error occurred when preloading objects");
      }
   
   }//constructor
   
   
   
   
   /**
    * This is a method that handler for any click events that are made
    * @param ActionEvent ae: button that been click.
    * @Override
    */
   @Override
   public void actionPerformed( ActionEvent ae ){
      Object choice = ae.getSource();
      
      // if user click refresh current record 
      if( choice == jbRefresh ){
         refresh();
      }
      
      // if user click exit
      else if( choice == exit ){
         exit();
      }
      
      // if user click start, it start the game
      else if( choice == start ){
         start();
      }
   }//end performance
   
   
   
   /**
    *  this method display total players are connected
    */
   public void refresh() {  
      try {
         totalPlayer.setText("Total Players online: "+ players.size());
         
         players.toString();
         jtaChat.append("\n");
         jtaChat.append("Current thread connection information \n");
        // Displaying all the connection
      
         for (ServerThread player: players) { 
            jtaChat.append("       " + player +"\n"); 
         } 
      } //try
      catch(Exception e) {
         e.printStackTrace();
         System.out.println("Error occurred when preloading objects");
      }
   }
   
   
   
   /**
    * exit method, close all the gui
    */
   public void exit() {
      //if one exit the game, all the program are closed
      if (gameSocket != null){
         sentToClient("SERVER DOWN");
         
      }
      System.exit(0);
      
   }// end of exit()
   
   
   
   /**
    * start method, when user click start, sent message to clients, and dispaly the game borad
    */
   public void start() {
      //only allow 6 players in total
      if( players.size()>6){
         sentToClient("@!!!!!!! TOO MUCH PLAYERS");
      }
      else{
         refresh();
         sentToClient("TOTAL PLAYER " + players.size()); 
         sentToClient("GAME START");//sent message to clients
         sentToClient("Player #: NOW IS THE TURN FOR PLAYER 1");//player1 go first
      }
   }
   
   
   
   /**
    *  This is a method the send messages to the all client
    * @param: message - sent the string to all the clients
    */
   public void sentToClient(String message) {
      for (int i = 0; i < players.size(); i++) {
         ServerThread ts = players.get(i);
         ts.ipw.println(message); // store
         ts.ipw.flush();  //write 
      } 
      jtaChat.append(message + "\n");
   }//sentToClient

}//class