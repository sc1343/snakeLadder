/**
 * ServerThread class which dealing with multiple client and message. 
 * Process connection between client, server and board game.
 * Team 4
 * @version 04/20/2020
 * @author JiaJia Chen, Shuying Chen, Fei Lin
*/
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 
/**
 *  This is a Class that extend thread for multi client
 */
public class ServerThread extends Thread {
   //attribute
   Server server;
   Socket socket = null;
   PrintWriter ipw;
   BufferedReader ibr;
   ObjectInputStream objectInput;
   int playerNum;
   


   /**
    * This is a constructor method to create the client.
    * @param _socket client it create 
    * @param _server server it connect
    * @param playerNum give the player name
   */
   public ServerThread(Socket _socket, Server _server,int playerNum) {
      socket = _socket;
      server = _server;
      setName("Player " + playerNum);
   }
   


   /**
    * This is the run method that will receive the command from the client.
    */
   public void run() {
      try {
         String text = null;
         
         // use for connect read and write
            ipw = new PrintWriter(socket.getOutputStream());
            ibr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         // keep recieve the message from the client
         while(socket != null){ 
            if ((text = ibr.readLine()) != null) { 
                  System.out.println("Received: " + text); // print the recieve message
                  server.sentToClient(getName() + ": " +text); // send back to the client
                    
                  // if string is exit, server exit, including the client
                  if(text.equals("EXIT")){
                     socket.close();
                     System.exit(0);
                  }
                  
                  // announce the winner if recieve message is 100.
                  if (text.length() == 25 ){
                     if(text.substring(0,5).equals("DICE:")){
                        if (text.substring(22).equals("100")){
                           server.sentToClient("Winner are " + getName()); // send back the winner.
                        }
                     }// if (text.substring)
                  }
                                        
            }
                      
         }//while loop
      } // try
      
      catch (Exception e) {
         System.out.println("Error occurred when preloading objects");
      }//catch
       
   }//run()

   
} //class
