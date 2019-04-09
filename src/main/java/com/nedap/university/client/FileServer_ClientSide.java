package com.nedap.university.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.nedap.university.communication.PacketReceiver;

//TODO determine what distinguishes server and client, otherwise they should be combined.
public class FileServer_ClientSide {
	//Named variables:
	private static int clientPort = 8090;//TODO add way for client to set own port.
	private static int clientPortThreaded = 8099;//TODO add way for client to set own port.
	private static int serverPort = 8080;
    private DatagramSocket socket;
    //private PacketReceiver packetReceiver;
	
	//Constructors:
    public FileServer_ClientSide(int port) throws SocketException {
    	socket = new DatagramSocket(port);
    }
    
    public static void main(String[] args) {

    	FileServer_ClientSide testClient;
        try {//Setup connection with server
			testClient = new FileServer_ClientSide(clientPort);
			Scanner scan = new Scanner(System.in);
            testClient.userInput(scan);
            testClient.serverInput();
        } catch (SocketException e) { //TODO needs handling
			e.printStackTrace();
		} catch (IOException ex) {
            System.out.println("Client error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    //Queries:
    
    /**
     * Returns the socket created for the client.
     * @return
     */
    public DatagramSocket getSocket() {
    	return this.socket;
    }
    
    //Commands:

    //TODO correctly implement
    //Example for handling serverinput:
    /**
	 * Command to continuously read out the server input.
	 * Allowing for simultaneous processing of input, output and internal commands.
	 */
	public void serverInput() {
		Thread tsiThread = new Thread() {
			public void run() {
				serverConnection();
			}
		};
		tsiThread.start();
	}

	/**
	 * Loop for listening to buffered reader in, reading out the server input
	 * And performing the required actions based on the input.
	 */
	public void serverConnection() {
		while (!finished) {
			try {
				String input = serverIn.readLine();
				checkCommands(input);
			} catch (IOException e) { //TODO be more specific with exception.
				if (!serverLost) {
					System.out.println("Sorry i can't reach the server.");
					this.shutdown();
					serverLost = true;
				}
			}
		}
	}
	//---------------------------------------------------------------------------------------------------------
    //Example for handling user input:
	/**
	 * Command to continuously read out the user input.
	 * Allowing for simultaneous processing of input, output and internal commands.
	 */
	public void userInput(Scanner scan) {
		Thread tuiThread = new Thread() {
			public void run() {
				listenLoop(scan);
			}
		};
		tuiThread.start();
	}

	/**
	 * Loop for listening to System.in, reading out the user input
	 * And performing the required actions based on the input.
	 */
	public void listenLoop(Scanner scan) {
		Scanner userIn = scan;
		while (!gameFinished) { 
			try {
				Thread.sleep(250);		
			} catch (InterruptedException e) {
				//Has slept.
			}
			boolean hasInput = false;
			try {
				hasInput = System.in.available() > 0;
			} catch (IOException e) {
				//Does not have input.
			}
			if (shouldAskInput() || hasInput) {
				showQuestion();
				if (userIn.hasNext()) {
					String input = userIn.nextLine();
					addOutputCommand(input);
				}
			}
		}
		userIn.close();
	}
	//---------------------------------------------------------------------------------------------------------
}
