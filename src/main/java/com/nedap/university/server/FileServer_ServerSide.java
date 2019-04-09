package com.nedap.university.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.nedap.university.communication.InputHandler;
import com.nedap.university.communication.PacketReceiver;

public class FileServer_ServerSide {

	//Named constants:
    private DatagramSocket socket;
    private List<String> listQuotes = new ArrayList<String>();
    private Random random;
	private static int serverPort = 8080;
	private boolean finished = false;
    private PacketReceiver packetReceiver;
	private InputHandler inputHandler;
	
	//Constructors:	
    public FileServer_ServerSide(int port) throws SocketException {
        socket = new DatagramSocket(port);
		packetReceiver = new PacketReceiver(socket);
		inputHandler = new InputHandler();
    }
 
    public static void main(String[] args) {
    	//Requires specific file with quotes, and port to listen on.
    	//TODO specific file is not necessary for file server, only folder where files can be found.
 
 
        try {
        	FileServer_ServerSide server = new FileServer_ServerSide(serverPort);
        	server.clientInput();
        } catch (SocketException ex) {//TODO handle better
            System.out.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {//TODO handle better
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
    

	
	
	//Queries:
	
	
	
	//Commands:

    //Load the quotes from a file into a string array.
    //TODO replace by loading a folder.
    private void loadQuotesFromFile(String quoteFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(quoteFile));
        String aQuote;
 
        while ((aQuote = reader.readLine()) != null) {
            listQuotes.add(aQuote);
        }
 
        reader.close();
    }
    //TODO correctly implement
    //Example for handling serverinput:
    /**
	 * Command to continuously read out the server input.
	 * Allowing for simultaneous processing of input, output and internal commands.
	 */
	public void clientInput() {
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
				byte[] handledPacket = this.packetReceiver.receivePacket();
				inputHandler.PacketInputHandler(handledPacket);
			} catch (IOException e) {//TODO handle error
				e.printStackTrace();
			}
		}
	}

}
