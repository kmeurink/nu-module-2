package com.nedap.university.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nedap.university.communication.InputHandler;
import com.nedap.university.communication.PacketReceiver;
import com.nedap.university.communication.TransferProtocol;

public class FileServer_ServerSide {

	//Named constants:
    private DatagramSocket socket;
    private List<String> listQuotes = new ArrayList<String>();
	private static int serverPort = 8080;
	private boolean finished = false;
	private boolean listening = true;
	private InetAddress clientAddress;
	private int clientPort = 8090;
	private String BROADCAST = "255.255.255.255";
	private InetAddress BROADCASTaddress = InetAddress.getByName("255.255.255.255");
    private PacketReceiver packetReceiver;
	private InputHandler inputHandler;
	private byte[] broadcastAckPacket= new byte[15];
	private DatagramPacket broadcastACK = new DatagramPacket(broadcastAckPacket, broadcastAckPacket.length, BROADCASTaddress, serverPort);// 
    private TransferProtocol reliableSender;

	//Constructors:	
    public FileServer_ServerSide(int port) throws SocketException, UnknownHostException {
        socket = new DatagramSocket(port);
		packetReceiver = new PacketReceiver(socket);
    	this.reliableSender = new TransferProtocol(socket);
		this.clientAddress = InetAddress.getByName("localhost");//TODO for testing
		inputHandler = new InputHandler(socket, clientAddress, clientPort);
    }
 
    public static void main(String[] args) {
    	//Requires specific file with quotes, and port to listen on.
    	//TODO specific file is not necessary for file server, only folder where files can be found.
 
 
        try {
        	FileServer_ServerSide server = new FileServer_ServerSide(serverPort);
        	//moved to standard receiver connection setup server.connectionSetup();
        	
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
	public void serverConnection() { //TODO figure out how to handle reliable data transfer.
		while (!finished) {
			try {
				byte[] handledPacket = this.packetReceiver.receivePacket();
				System.out.println("Packet received from client");
	    		if(Arrays.equals(handledPacket, this.BROADCAST.getBytes())) {
	    			System.out.println("Sending broadcast acknowledgement"); //TODO not reached, something does not match
	        		byte[] broadcastPacket= new byte[0];
	        		DatagramPacket broadcast = new DatagramPacket(broadcastPacket, 0, this.packetReceiver.getReceiverAddress(), this.packetReceiver.getReceiverPort());
	        		socket.send(broadcast);
	    		} else {
					this.reliableSender.setAddress(this.packetReceiver.getReceiverAddress());
					this.reliableSender.setPort(clientPort);
	    			inputHandler.PacketInputSort(handledPacket, this.packetReceiver.getReceiverAddress(), clientPort);//TODO add to receive queue of transfer protocol
	    		}
			} catch (IOException e) {//TODO handle error
				e.printStackTrace();
			}
		}
	}
	
	
    /**
     * @deprecated
     * Wait on a reply from the client, before initiating.
     */
    public void connectionSetup() {
    	while(listening) {
    		try {
				socket.receive(this.broadcastACK);
				byte[] test1 = this.BROADCAST.getBytes();
				byte[] test2 = this.broadcastACK.getData();
	    		if(Arrays.equals(this.broadcastACK.getData(), this.BROADCAST.getBytes())) {
	    			System.out.println("Sending broadcast acknowledgement"); //TODO not reached, something does not match
	        		byte[] broadcastPacket= new byte[0];
	        		DatagramPacket broadcast = new DatagramPacket(broadcastPacket, 0, this.broadcastACK.getAddress(), this.broadcastACK.getPort());
	        		socket.send(broadcast);
	    		} else {
	    			listening = false;
	    		}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}
    }
    

}
