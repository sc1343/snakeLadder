/**
 * Client class which contain client chat and show game board.
 * Team 4
 * @version 04/20/2020
 * @author JiaJia Chen, Shuying Chen, Fei Lin
*/

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class Client extends Thread implements ActionListener{
   
   //attributes
   private JTextArea jtaChat;
   private JTextField jtfIpAddress;
   private JTextField jtfSendMessage;
   
   private JButton jbSent;
   private JButton jbAddress;
   
   private Socket serverGame = null;
   private PrintWriter gamePrintOut;
   private BufferedReader gameBr;
   
   private int playerNumber;
   private int numberClients;
   private int totalMove = 1;
   private int step;
   private int currentTurn = 1;
   
   private JButton restart;
   private JButton tossDice;
   private JButton die;
   private JButton gameBoard[][]=new JButton[10][10];
   
   private int path;
   private String str;
   
   // board game
   private int game[][]={
                  {91,92,93,94,95,96,97,98,99,100},
                  {81,82,83,84,85,86,87,88,89,90},
   	            {71,72,73,74,75,76,77,78,79,80},
   	            {61,62,63,64,65,66,67,68,69,70},
   	            {51,52,53,54,55,56,57,58,59,60},
   	            {41,42,43,44,45,46,47,48,49,50},
   	            {31,32,33,34,35,36,37,38,39,40},
   	            {21,22,23,24,25,26,27,28,29,30},
   	            {11,12,13,14,15,16,17,18,19,20},
   	            { 1, 2, 3, 4, 5, 6, 7, 8, 9,10},
                 };
                 
   private Icon gameBoardIcon[][]= new Icon[10][10];
   private Icon dieIcon;

   /**
       This is a main method to call constructor
   */
   public static void main(String args[]){
      Client cl = new Client();
   } 
   
   /**
       This is a constructor method to call the chat GUI 
       for connection.
   */
   public Client(){
      chat();
   } 
   
   
   
   /**
    * This is the method to create the GUI for game which contains the board game by
    * using the JButton, players images, toss dice button, restart button, and help menu bar  
    * that contain the about message.
    */
   public void game(){
      JFrame f= new JFrame();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setTitle("Snakes and Ladders");
            
      // Add menu
      JMenuBar mBar = new JMenuBar();
      JMenu fileMenu = new JMenu("File");
      JMenu helpMenu = new JMenu("Help");
       
      JMenuItem exit = new JMenuItem("Exit");
      exit.addActionListener(this);
      JMenuItem about = new JMenuItem("About");
      about.addActionListener(this);
      
      fileMenu.add(exit);
      helpMenu.add(about);
      
      mBar.add(fileMenu);
      mBar.add(helpMenu);
      f.setJMenuBar(mBar);
      
      //add the title in the screen 
      JPanel jpNorth = new JPanel(new GridLayout(2,1));
      
      JLabel title1 = new JLabel("Snakes and Ladders",  JLabel.CENTER);
      Font fontTitle = new Font("Arial", Font.BOLD, 20 );
      title1.setFont( fontTitle );
      
      
      jpNorth.add( title1);
         
      f.add( jpNorth, BorderLayout.NORTH );
      
      
      // add the game board by using JButton
      JPanel jpGame = new JPanel( new GridLayout(10,10) );  
      for(int i=0;i<10;i++){
         for(int j=0;j<10;j++){
            gameBoard[i][j]= new JButton();      
            path = game[i][j];
            str = Integer.toString(path);
            gameBoardIcon[i][j] = new ImageIcon(getClass().getResource("imgs/background/"+str+".png"));
            gameBoard[i][j].setIcon(gameBoardIcon[i][j]);
            jpGame.add(gameBoard[i][j]);
         }
      }
      
      f.add( jpGame, BorderLayout.CENTER );
   
      // add the players image
      JPanel jpPlayer = new JPanel(new GridLayout(numberClients,1));
      for(int j= 1; j <= numberClients; j++){
         JLabel playerTitle = new JLabel("Player " + j +": ",  JLabel.CENTER);
         JLabel player= new JLabel(); 
         str = Integer.toString(j);
         Icon playerIcon = new ImageIcon(getClass().getResource("imgs/players/player"+str+".png"));
         player.setIcon(playerIcon);
         jpPlayer.add(playerTitle);
         jpPlayer.add(player);
      }
         
      f.add( jpPlayer, BorderLayout.EAST ); 
      
      // add the control Buttons- restart, tossDice
      JPanel jpControls = new JPanel(new FlowLayout());
      restart = new JButton("Restart");
      restart.addActionListener(this);
      die = new JButton();
      tossDice = new JButton("Toss Dice");
      tossDice.addActionListener(this);
   
      dieIcon= new ImageIcon(getClass().getResource("imgs/dice/dice1.png"));
      die.setIcon(dieIcon);
               
      jpControls.add( restart );
      jpControls.add( die );
      jpControls.add( tossDice );
      
      f.add( jpControls, BorderLayout.SOUTH );
   
     
      //when exit the gui
      f.addWindowListener(
         new WindowAdapter(){
            public void windowClosing(WindowEvent we){
               JOptionPane.showMessageDialog(null, "Thank you, have good day!");
               exit();
            }
         }); 
      
      // GUI display controls 
      f.setResizable(false);
      f.setSize(760,820);
      f.setVisible(true);
   
   }//game
   
   /**
    * This is the method that will pop up the winner message window
    * @param message the message that want to pop up 
    */
   public void winner(String message){
      JOptionPane.showMessageDialog(null,message);
   }
   
   /**
    * This is the method that will move the other player to their position by 
    * replace board game image to player image 
    * @param move the position of the player move to
    * @param player which player to move
    */
   public void player_move(int move, int player){
      //player images
      Icon playerNum= new ImageIcon(getClass().getResource("imgs/players/player"+player+".png"));
      
      //if player positon are between 1 - 10
      if (move <= 10 && move > 0){
         gameBoard[9][move-1].setIcon(playerNum);
        
      }
      //if player positon are between 11 - 20
      else if (move <= 20 && move > 10){ 
         gameBoard[8][move-11].setIcon(playerNum);
      
      }
      
      //if player positon are between 21 - 30
      else if (move <= 30 && move > 20){
         gameBoard[7][move-21].setIcon(playerNum);
      
      }
      
      //if player positon are between 31 - 40
      else if (move <= 40 && move > 30){
         gameBoard[6][move-31].setIcon(playerNum);
      }
      //if player positon are between 41 - 50
      else if (move <= 50 && move > 40){
         gameBoard[5][move-41].setIcon(playerNum);
      
      }
      //if player positon are between 51 - 60
      else if (move <= 60 && move > 50){
         gameBoard[4][move-51].setIcon(playerNum);
      }
      //if player positon are between 61 - 70
      else if (move <= 70 && move > 60){
         gameBoard[3][move-61].setIcon(playerNum);
      }
      //if player positon are between 71 - 80
      else if (move <= 80 && move > 70){
         gameBoard[2][move-71].setIcon(playerNum);
      }
      //if player positon are between 81 - 90
      else if (move <= 90 && move > 80){
         gameBoard[1][move-81].setIcon(playerNum);   
      }
      
      //if player positon are between 91 - 100
      else if (move <= 100 && move > 90){
         gameBoard[0][move-91].setIcon(playerNum);
      
      }
      
   
   }
   
   /**
    * This is the method that will move the player itself to their position 
    * by replacing the board game image to the player image and calculate the 
    * final position.
    * @param move the position of the player move to
    * @param player which player to move
    */
   public void own_move(int move, int player){
      Icon playerNum= new ImageIcon(getClass().getResource("imgs/players/player"+player+".png"));
      //if player positon are between 1 - 10
      if (move <= 10 && move > 0){
         gameBoard[9][move-1].setIcon(playerNum);
         //ladder in position 5
         if (move == 5){
            afterMove(5);
            gameBoard[6][1].setIcon(playerNum);
            totalMove = 32;
         }
      
      }
      //if player positon are between 1 - 10
      else if (move <= 20 && move > 10){ 
         gameBoard[8][move-11].setIcon(playerNum);
      
      }
      //if player positon are between 21 - 30
      else if (move <= 30 && move > 20){
         gameBoard[7][move-21].setIcon(playerNum);
      
      }
      //if player positon are between 31 - 40
      else if (move <= 40 && move > 30){
         gameBoard[6][move-31].setIcon(playerNum);
         //snack in position 36
         if (move == 36){
            afterMove(36);
            gameBoard[7][4].setIcon(playerNum);
            totalMove = 25;
         }
         //snack in position 40
         else if (move == 40){
            afterMove(40);
            gameBoard[8][8].setIcon(playerNum);
            totalMove = 19;
         }
      }
      //if player positon are between 41 - 50
      else if (move <= 50 && move > 40){
         gameBoard[5][move-41].setIcon(playerNum);
      
      }
      //if player positon are between 51 - 60
      else if (move <= 60 && move > 50){
         gameBoard[4][move-51].setIcon(playerNum);
         //ladder in position 59
         if (move == 59){
            afterMove(59);
            gameBoard[0][8].setIcon(playerNum);
            totalMove = 99;
         }
      }
      //if player positon are between 61 - 70
      else if (move <= 70 && move > 60){
         gameBoard[3][move-61].setIcon(playerNum);
      }
      //if player positon are between 71 - 80
      else if (move <= 80 && move > 70){
         gameBoard[2][move-71].setIcon(playerNum);
         //ladder in position 75
         if (move == 75){
            afterMove(75);
            gameBoard[0][6].setIcon(playerNum);
            totalMove = 97;
         }
         //snack in position 78
         else if (move == 78){
            afterMove(78);
            gameBoard[4][4].setIcon(playerNum);
            totalMove = 55;
         }
      }
      //if player positon are between 81 - 90
      else if (move <= 90 && move > 80){
         gameBoard[1][move-81].setIcon(playerNum);   
      }
      //if player positon are between 91 - 100
      else if (move <= 100 && move > 90){
         gameBoard[0][move-91].setIcon(playerNum);
         //snack in position 91
         if (move == 91){
            afterMove(91);
            gameBoard[4][3].setIcon(playerNum);
            totalMove = 54;
         }
      }
      
   
   }

   /**
    * This is the method that will move This is the method that will change 
    * back to background image after the player moves.
    * @param move the position of the player that move before
    */
   public void afterMove(int move){
      Icon boardIcon= new ImageIcon(getClass().getResource("imgs/background/"+move+".png"));
      //if player positon are between 1 - 10
      if (move <= 10 && move > 0){
         gameBoard[9][move-1].setIcon(boardIcon); 
      }
      //if player positon are between 11 - 20
      else if (move <= 20 && move > 10){
         gameBoard[8][move-11].setIcon(boardIcon);
      
      }
      //if player positon are between 21 - 30
      else if (move <= 30 && move > 20){
         gameBoard[7][move-21].setIcon(boardIcon);
      
      }
      //if player positon are between 31 - 40
      else if (move <= 40 && move > 30){
         gameBoard[6][move-31].setIcon(boardIcon);
      
      }
      //if player positon are between 41 - 50
      else if (move <= 50 && move > 40){
         gameBoard[5][move-41].setIcon(boardIcon);
      
      }
      //if player positon are between 51 - 60
      else if (move <= 60 && move > 50){
         gameBoard[4][move-51].setIcon(boardIcon);
      
      }
      //if player positon are between 61 - 70
      else if (move <= 70 && move > 60){
         gameBoard[3][move-61].setIcon(boardIcon);
      
      }
      //if player positon are between 71 - 80
      else if (move <= 80 && move > 70){
         gameBoard[2][move-71].setIcon(boardIcon);
      
      }
      //if player positon are between 81 - 90
      else if (move <= 90 && move > 80){
         gameBoard[1][move-81].setIcon(boardIcon);
      
      }
      //if player positon are between 91 - 100
      else if (move <= 100 && move > 90){
         gameBoard[0][move-91].setIcon(boardIcon);
      }   
   }
   
   /**
    * This is the method that will toss dice for the player to move.
    * @return the number for player to move
    */
   public int dice(){
      int i= (int)(Math.random()*6+1);
      dieIcon= new ImageIcon(getClass().getResource("imgs/dice/dice"+i+".png"));
      die.setIcon(dieIcon);
      return i;
   }
   

   /**
    * This is the method that changes the dice image.
    * @param step the number of dice want to change
    */
   public void diceImage(int step){
      dieIcon= new ImageIcon(getClass().getResource("imgs/dice/dice"+step+".png"));
      die.setIcon(dieIcon);
   }
   

   

  /**
    * This is the method to exit the program.
    */
   public void exit(){
      String buttonExit = "EXIT"; 
      try{
         if (serverGame != null){
            gamePrintOut.println(buttonExit); 
            gamePrintOut.flush();
         } 
      }
      catch(Exception e){
      }  
      System.exit(0);
   
   }
   
   /**
    * This is the method to restart the game.
    */
   public void restart(){
      for(int i=1;i<=100;i++){
         afterMove(i);
      }
      totalMove = 1;
      
   }
   

   /**
    * This is the method to create the GUI for chat which contains a send button,
    * connect button, and menu bar that contain the help message.
    */
   public void chat(){
      JFrame jfClient = new JFrame("Let's Chat");
      jfClient.setLayout(new BorderLayout());
         
         
       //menu
      JMenuBar jmBar = new JMenuBar();
      jfClient.setJMenuBar(jmBar);
      JMenu jmMenu = new JMenu("Menu");
      jmBar.add(jmMenu);
         
      
      JMenuItem help = new JMenuItem("Help");
      help.addActionListener(this);
      
      jmMenu.add(help);
      
      
     
      
      //Chat 
      jtaChat = new JTextArea("", 20, 20);
      jtaChat.setEnabled(false);
      JScrollPane sp = new JScrollPane(jtaChat); 
         
   
   
      jfClient.add(sp,BorderLayout.CENTER);
        
      //button panel
      JPanel jpEnter = new JPanel();
      jpEnter.setLayout(new GridLayout(2,1));
      jfClient.add(jpEnter,BorderLayout.SOUTH);
         
      //address button
      jpEnter.add( new JLabel( "Ip Address: ", JLabel.RIGHT));
      jtfIpAddress = new JTextField("");
      jpEnter.add(jtfIpAddress);
      jbAddress = new JButton("Connect server");
      jpEnter.add(jbAddress);
         
      //send button
      jpEnter.add( new JLabel( "text box: ", JLabel.RIGHT));
      jtfSendMessage = new JTextField("");
      jpEnter.add(jtfSendMessage);
      jbSent = new JButton("Send Message");
      jbSent.setEnabled(false);
      jpEnter.add(jbSent);
         
         
      // add action 
      jbAddress.addActionListener( this );
      jbSent.addActionListener( this );
   
      jfClient.pack();
      jfClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jfClient.setVisible(true); 
      jfClient.setLocation(760,0);  
      
      //when kill gui
      jfClient.addWindowListener(
         new WindowAdapter(){
            public void windowClosing(WindowEvent we){
               JOptionPane.showMessageDialog(null, "Thank you, have good day!");
               exit();
            }
         }); 
   
   }
   
   /**
    * This is the method connect to server, use to received and send message
    */
   public void connect(){
      try{
         //if IP Address user input is empty
         if(jtfIpAddress.getText().equals("")){
            JOptionPane.showMessageDialog(null, "IP address can not be empty"); 
         }
         else{
            serverGame = new Socket(jtfIpAddress.getText(), 16789);
            gamePrintOut = new PrintWriter(serverGame.getOutputStream());
            gameBr = new BufferedReader(new InputStreamReader(serverGame.getInputStream()));
         
            start();
            System.out.println("connect");
            jtaChat.append("connect\n");
         }
         
      }catch( UnknownHostException uhe ) {
         System.out.println("Cannot connect to server");
         JOptionPane.showMessageDialog(null, "Cannot connect to server");
      }
      catch( ConnectException ce ){
         System.out.println("No server running on specified computer.");
         JOptionPane.showMessageDialog(null, "No server running on specified computer");
      }
      catch( IOException ioe ){
         System.out.println("Socket connection error");
      }
   
   }
   

   /**
    * This is the method to send the message to server
    * @param message the message that want to send to the server
    */
   public void sendGame(String message){
      try{
         gamePrintOut.println(message);
         gamePrintOut.flush();
      }
      catch( Exception ioe ){
         System.out.println("Socket connection error");
         ioe.printStackTrace();
      }
   }
   
   
   
   /**
    * This is an method that handler for any click events that are made
    * @param button that been click.
    */
   @Override
   public void actionPerformed( ActionEvent ae ){
      Object choice = ae.getSource();
      
      String command = ae.getActionCommand();
      // if user click about in the menu in game gui
      if(command.equals("About")){
      
         String line = null;
         try
         {
         
            InputStream dictionary = Client.class.getResourceAsStream("info.txt");
            BufferedReader word = new BufferedReader(new InputStreamReader(dictionary));
         
            String message ="";
            while((line = word.readLine()) != null)
            {
               message = message+"\n" +line; 
            
            }
            JOptionPane.showMessageDialog(null, message);
            
         //always close the file after use
            word.close();
         
         //loadFile(word);
         }
         catch(IOException ex)
         {
            System.out.println("Error reading file named 'info.txt'");
         }
      }
      // if user click exit in the menu in game gui
      else if(command.equals("Exit")){
         exit();
         System.exit(0);
      }
      // if user click help in the menu in chat gui
      else if(command.equals("Help")){
         //loadFile("server.txt");
         String line = null;
         try
         {
         
            InputStream dictionary = Client.class.getResourceAsStream("server.txt");
            BufferedReader word = new BufferedReader(new InputStreamReader(dictionary));
         
            String message ="";
            while((line = word.readLine()) != null)
            {
               message = message+"\n" +line; 
            
            }
            JOptionPane.showMessageDialog(null, message);
            
         //always close the file after use
            word.close();
         
         //loadFile(word);
         }
         catch(IOException ex)
         {
            System.out.println("Error reading file named 'server.txt'");
         }
      
      
      }
      
      
      // if user click connect in chat gui
      else if( choice == jbAddress ){
         connect();
      }
      // if user click count in chat gui
      else if( choice == jbSent ){
         sendGame(jtfSendMessage.getText());
      }
      
      
      // if user click restart in game gui
      else if( choice == restart ){
         sendGame("RESTART");
         restart();      
      }
      // if user click tossDice in game gui calcuate the position and send to client
      else if( choice == tossDice ){
         afterMove(totalMove);
         step = dice();
         totalMove += step; 
         
         // if position less or equal to 100
         if (totalMove <= 100){
            own_move(totalMove,playerNumber);
            String info = ("DICE: " + step + " AND POSITION: " + totalMove);
            sendGame(info);//send to server
            
            if (totalMove < 100){
               //use to determine the turn of player to toss the dice
               if(currentTurn == numberClients){
                  currentTurn = 1;
                  sendGame("NOW IS THE TURN FOR PLAYER "+ currentTurn);
               }
               else{
                  currentTurn += 1;
                  sendGame("NOW IS THE TURN FOR PLAYER "+ currentTurn);
               }
            }
         }
         else{
            totalMove -= step;
            own_move(totalMove,playerNumber);
            String info = ("DICE: " + step + " AND POSITION: " + totalMove);
            sendGame(info);//send to server
            
            if (totalMove < 100){
               //use to determine the turn of player to toss the dice
               if(currentTurn == numberClients){
                  currentTurn = 1;
                  sendGame("NOW IS THE TURN FOR PLAYER "+ currentTurn);
               }
               else{
                  currentTurn += 1;
                  sendGame("NOW IS THE TURN FOR PLAYER "+ currentTurn);
               }
            }
         }
      }
   } // end actionPerformed
   
   
   /**
    * This will pop up the about and help message from the text file 
    * @param fileName the name of the text file
    */
   public void loadFile(String fileName) {
      String fname = fileName;    
      //this will reference only one line at a time 
      String line = null;
      try
      {
            //FileReader reads text files in the default encoding 
         FileReader fileReader = new FileReader(fname);
            
            //always wrap the FileReader in BufferedReader
         BufferedReader bufferedReader = new BufferedReader(fileReader);
         String message ="";
         while((line = bufferedReader.readLine()) != null)
         {
            message = message+"\n" +line; 
         
         }
         JOptionPane.showMessageDialog(null, message);
            
         //always close the file after use
         bufferedReader.close();
      }
      catch(IOException ex)
      {
         System.out.println("Error reading file named '" + fname + "'");
      }		
   }

   
   
   /**
    * This run method will receive the message from the server and display in the 
    * chat board and if the message is about the game message of the player, move 
    * that player to its position and display the dice that player tosses.
   */
   public void run(){
      try{ 
         String text = null;
         int step;
         int before1= 1,before2= 1,before3= 1,before4= 1,before5= 1,before6 = 1;
         int current1,current2,current3,current4,current5,current6;
         
         while (true){
            if ((text = gameBr.readLine()) != null) {
               System.out.println("Receive: " +text);
               jtaChat.append(text + "\n");
               if (text.length() == 11){
                  //if message is the about the server down
                  if (text.equals("SERVER DOWN")){
                     serverGame.close();
                     gamePrintOut.close();
                     gameBr.close();
                     exit();
                  }
               }
               else if (text.length() == 19){
                  //if message is the about the winner of the game
                  if (text.substring(0,6).equals("Winner")){
                     winner(text);
                     restart.setEnabled(true);
                     tossDice.setEnabled(false);
                  }
               }
               else if (text.length() == 25){
                  //if message is the welcome that will determine the player number
                  if (text.substring(0,7).equals("WELCOME")){
                     playerNumber = Integer.parseInt(text.substring(24));
                     System.out.println("playerNumber:" + playerNumber);
                     jbAddress.setEnabled(false);
                     jtfIpAddress.setEnabled(false);
                     jbSent.setEnabled(false);
                  }
                  //if message is the about too much player
                  else if(text.equals("@!!!!!!! TOO MUCH PLAYERS")){
                     JOptionPane.showMessageDialog(null, "You have more than 6 players, you can chat but can't play.\nIf you want to play, please 'EXIT' and restart the Server");
                     jbSent.setEnabled(true);
                  
                  }
               }
               
               else if (text.length() == 14){
                  //if message is the about total player in the game
                  if (text.substring(0,5).equals("TOTAL")){
                     System.out.println("test" + numberClients);
                     numberClients = Integer.parseInt(text.substring(13));
                     System.out.println("numberClients" + numberClients);
                     game();
                     tossDice.setEnabled(false);
                     jbSent.setEnabled(true);
                     restart.setEnabled(false);
                     
                  }
                  
                  //if message is the about the some player exit the game
                  else if (text.substring(10,14).equals("EXIT")){
                     serverGame.close();
                     gamePrintOut.close();
                     gameBr.close();
                     exit();
                  }
               }
               else if (text.length() == 17){
                  //if message is the about the restart of the game
                  if (text.substring(10).equals("RESTART")){
                     restart();
                     sendGame("NOW IS THE TURN FOR PLAYER 1");
                     restart.setEnabled(false);
                  }
               
               }
               
               else if (text.length() > 32 && text.length() < 36){
                  //if message is the about the game message of player 1
                  if (text.substring(0,14).equals("Player 1: DICE")){
                     afterMove(before1);
                     current1 = Integer.parseInt(text.substring(32));
                     step = Integer.parseInt(text.substring(16,17));
                     diceImage(step);
                     player_move(current1,1);
                     before1 = current1;
                  
                  }
                  //if message is the about the game message of player 2
                  else if (text.substring(0,14).equals("Player 2: DICE")){
                     afterMove(before2);
                     current2 = Integer.parseInt(text.substring(32));
                     step = Integer.parseInt(text.substring(16,17));
                     diceImage(step);
                     player_move(current2,2);
                     before2 = current2;
                  
                  }
                  //if message is the about the game message of player 3
                  else if (text.substring(0,14).equals("Player 3: DICE")){
                     afterMove(before3);
                     current3 = Integer.parseInt(text.substring(32));
                     step = Integer.parseInt(text.substring(16,17));
                     diceImage(step);
                     player_move(current3,3); 
                     before3 = current3;
                  
                  }
                  
                  //if message is the about the game message of player 4
                  else if (text.substring(0,14).equals("Player 4: DICE")){
                     afterMove(before4);
                     current4 = Integer.parseInt(text.substring(32));
                     step = Integer.parseInt(text.substring(16,17));
                     diceImage(step);
                     player_move(current4,4);
                     before4 = current4;
                  }
                  
                  //if message is the about the game message of player 5
                  else if (text.substring(0,14).equals("Player 5: DICE")){
                     afterMove(before5);
                     current5 = Integer.parseInt(text.substring(32));
                     step = Integer.parseInt(text.substring(16,17));
                     diceImage(step);
                     player_move(current5,5);
                     before5 = current5;
                  
                  }
                  
                  //if message is the about the game message of player 6
                  else if (text.substring(0,14).equals("Player 6: DICE")){
                     afterMove(before6);
                     current6 = Integer.parseInt(text.substring(32));
                     step = Integer.parseInt(text.substring(16,17));
                     diceImage(step);
                     player_move(current6,6);
                     before6 = current6;
                  }
                   
               }//length
               else if (text.length() == 38){
                  //if message is the about the game message of the toss dice turn from server
                  if (text.substring(0,13).equals("Player #: NOW")){
                     currentTurn = Integer.parseInt(text.substring(37,38));
                     if (currentTurn == playerNumber) {
                        tossDice.setEnabled(true);
                     } 
                     else{
                        tossDice.setEnabled(false);
                     }
                  }
                  //if message is the about the game message of the toss dice turn from player 1
                  else if (text.substring(0,13).equals("Player 1: NOW")){
                     currentTurn = Integer.parseInt(text.substring(37,38));
                     if (currentTurn == playerNumber) {
                        tossDice.setEnabled(true);
                     } 
                     else{
                        tossDice.setEnabled(false);
                     }
                  }
                  //if message is the about the game message of the toss dice turn from player 2
                  else if (text.substring(0,13).equals("Player 2: NOW")){
                     currentTurn = Integer.parseInt(text.substring(37,38));
                     if (currentTurn == playerNumber ) {
                        tossDice.setEnabled(true);
                           
                     } 
                     else{
                        tossDice.setEnabled(false);
                     }
                  }
                  //if message is the about the game message of the toss dice turn from player 3
                  else if (text.substring(0,13).equals("Player 3: NOW")){
                     currentTurn = Integer.parseInt(text.substring(37,38));
                     if (currentTurn == playerNumber) {
                        tossDice.setEnabled(true);
                           
                     } 
                     else{
                        tossDice.setEnabled(false);
                     }
                  }
                  //if message is the about the game message of the toss dice turn from player 4
                  else if (text.substring(0,13).equals("Player 4: NOW")){
                     currentTurn = Integer.parseInt(text.substring(37,38));
                     if (currentTurn == playerNumber) {
                        tossDice.setEnabled(true);
                           
                     } 
                     else{
                        tossDice.setEnabled(false);
                     }
                  }
                  //if message is the about the game message of the toss dice turn from player 5
                  else if (text.substring(0,13).equals("Player 5: NOW")){
                     currentTurn = Integer.parseInt(text.substring(37,38));
                     if (currentTurn == playerNumber) {
                        tossDice.setEnabled(true);
                           
                     } 
                     else{
                        tossDice.setEnabled(false);
                     }
                  }
                  //if message is the about the game message of the toss dice turn from player 6
                  else if (text.substring(0,13).equals("Player 6: NOW")){
                     currentTurn = Integer.parseInt(text.substring(37,38));
                     if (currentTurn == playerNumber) {
                        tossDice.setEnabled(true);
                           
                     } 
                     else{
                        tossDice.setEnabled(false);
                     }
                  }
               }
            }//if chattext
         }//while
      }catch(IOException ioe){
         System.out.println("IO error");
         ioe.printStackTrace();
      }catch(NullPointerException npe){
         System.out.println("NullPointerException error");
         npe.printStackTrace();
      }
   }
   
}//class