package com.upc.EasyProduction.Communication;

import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class manages the Dashboard connection.
 * @author Enric Lamarca Ferrés
 */
public class DashBoardConnection {

    /**
     * Robot IP.
     */
    private final String hostname; // robot IP

    /**
     * Port of Dashboard interface.
     */
    private final int portNumber = 29999; // DashBoard Connection

    // https://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoClient.java
    /**
     * Socket that connects to robot.
     */
    private Socket socket;
    /**
     * To be able to send encoded strings.
     */
    private PrintWriter out; // to be able to send encoded strings

    /**
     * boolean that indicates if the connection is established or has been lost.
     */
    private boolean socket_connected = false;

    /**
     * Constructor.
     * @param robotIP IP of the robot.
     */
    public DashBoardConnection(String robotIP){
        hostname = robotIP;
    }

    /**
     * Connects via socket to robot.
     */
    public void connect(){
        try {
            socket = new Socket(hostname, portNumber);

            //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

            socket_connected = true;
        }
        catch (Exception e){
            e.printStackTrace();
            socket_connected = false;
        }
    }

    /**
     * Closes socket connection.
     */
    public void close(){
        try {
            if (socket != null) socket.close();
            if(out != null) out.close();
        }
        catch (Exception e){
            e.printStackTrace();
            socket_connected = false;
        }
    }

    /**
     * Tells whether the socket connection is established or has been lost.
     * @return boolean that indicates if the connection is established or has been lost.
     */
    public boolean isSocketConnected(){
        return socket_connected;
    }

    // in theory println adds \n in the end of the string

    /**
     * Executes play Dashboard command.
     */
    public void play(){
        out.println("play"); // does not rise an exception
    }

    /**
     * Executes pause Dashboard command.
     */
    public void pause(){
        out.println("pause");
    }

    /**
     * Executes stop Dashboard command.
     */
    public void stop(){
        out.println("stop");
    }

    /**
     * Executes popup Dashboard command with the text passed as parameter.
     * @param text text of the popup.
     */
    public void popup(String text){
        out.println("popup " + text);
    }
}
