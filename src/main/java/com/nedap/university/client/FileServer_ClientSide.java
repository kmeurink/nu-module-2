package com.nedap.university.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.nedap.university.communication.InputHandler;
import com.nedap.university.communication.PacketReceiver;

//TODO determine what distinguishes server and client, otherwise they should be combined.
public class FileServer_ClientSide {
	//Named variables:
	private static int clientPort = 8090;//TODO add way for client to set own port.
	private static int clientPortThreaded = 8099;//TODO add way for client to set own port.
	private static int serverPort = 8080;
	private String broadcastString = "255.255.255.255";
	private InetAddress BROADCASTaddress = InetAddress.getByName(broadcastString);
	private byte[] broadcastPacket= broadcastString.getBytes();
	private byte[] broadcastAckPacket= new byte[15];
	private DatagramPacket broadcast = new DatagramPacket(broadcastPacket, broadcastPacket.length, BROADCASTaddress, serverPort);
	private DatagramPacket broadcastACK = new DatagramPacket(broadcastPacket, broadcastPacket.length, BROADCASTaddress, serverPort); 
    private DatagramSocket socket = new DatagramSocket(clientPort);
    
	private InetAddress serverAddress;
	private boolean finished = false;
	private boolean userFinished = false;
    private PacketReceiver packetReceiver;
	private InputHandler inputHandler;
    private boolean broadcasting = true;
    
	//Constructors:
    public FileServer_ClientSide() throws SocketException, UnknownHostException {
		socket.setSoTimeout(2000);
		packetReceiver = new PacketReceiver(socket);
		this.serverAddress = InetAddress.getByName("localhost");//TODO for testing
		inputHandler = new InputHandler(socket, serverAddress, serverPort); //TODO create method to set them, instead of on boot?
    }
    
    public static void main(String[] args) {
    	FileServer_ClientSide testClient;
        try {//Setup connection with server
			testClient = new FileServer_ClientSide();
			//TODO Send broadcast until server responds back.
			testClient.broadcast();

			
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
    
    public void broadcast() {
		while(broadcasting) {
			try {
				socket.send(broadcast);
				socket.receive(broadcastACK);
				System.out.print("server reply received");
				this.serverAddress = broadcastACK.getAddress();
				broadcastPacket = this.serverAddress.getHostAddress().getBytes();
				DatagramPacket broadcastReply= new DatagramPacket(broadcastPacket, broadcastPacket.length, serverAddress, serverPort);
				socket.send(broadcastReply);
				System.out.println("client reply sent.");
				broadcasting = false;
				inputHandler.bindAddress(serverAddress);
				socket.setSoTimeout(0);
			} catch (SocketTimeoutException e) {
				System.out.println("Failed to connect, retrying.");
			} catch (IOException e) { //TODO handle error
				e.printStackTrace();
			}
		}
    }

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
				byte[] handledPacket = this.packetReceiver.receivePacket();
				System.out.println("Packet received from server");
				inputHandler.PacketInputSort(handledPacket, this.packetReceiver.getReceiverAddress(), this.packetReceiver.getReceiverPort());
			} catch (IOException e) {//TODO handle error
				e.printStackTrace();
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
	 * @throws InterruptedException 
	 */
	public void listenLoop(Scanner scan) {
		Scanner userIn = scan;
		while (!userFinished) { 
			printTUI();
			handleInput(userIn.nextLine());
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		userIn.close();
	}
	
	/**
	 * Prints out the elements of the textual user interface.
	 */
	private void printTUI() {
		System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
		System.out.println("Welcome to this fileServer, please type a number corresponding to one of the oftions below");
		System.out.println("1 : Connect to server.");
		System.out.println("2 : List the available files on the server.");
		System.out.println("3 : Download a file from the server.");
		System.out.println("4 : Upload a file to the server.");
		System.out.println("5 : Show the statistics of the server connection.");
		System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
	}
	
	/**
	 * Handling of user input.
	 * @param input
	 */
	private void handleInput(String input) { //TODO actually handle the input, possibly better to do in another class?
		if (input.equals("1")) { //TODO make connection before starting everything. So automatic.
			System.out.println("Sorry this command has not yet been implemented.");
		} else if (input.equals("2")) {
			//System.out.println("Sorry this command has not yet been implemented.");
			this.inputHandler.getList();
		} else if (input.equals("3")) {
			System.out.println("Sorry this command has not yet been implemented.");
		} else if (input.equals("4")) {
			System.out.println("Sorry this command has not yet been implemented.");
		} else if (input.equals("5")) {
			System.out.println("Sorry this command has not yet been implemented.");
		}
	}
	
	//---------------------------------------------------------------------------------------------------------
}
