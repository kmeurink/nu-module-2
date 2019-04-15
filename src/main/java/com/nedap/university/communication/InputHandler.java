package com.nedap.university.communication;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.swing.JList;

import com.nedap.university.utilities.FlagBytes;
import com.nedap.university.utilities.InputCommands;

/**
 * Deals with the packet header to identify the purpose and contents of a packet.
 * @author kester.meurink
 *
 */
public class InputHandler {//TODO perhaps better as a method, but is used by both server and client.
	//Named Constants:
	private PacketBuilder inputPacket;
	private PacketBuilder outputPacket;
	private int headerSize = 22; //TODO make changeable eventually
	private int packetSize = 1024;//TODO make changeable eventually
    private InetAddress BROADCAST;
    private InetAddress server;
    private InputCommands commands;
    private PacketSender packetSender;
    private DatagramSocket socket;
    private FileListCompiler fileNameHandler;
    private TransferProtocol sender;
    private String[] currentAvailableFiles;
    private int seqNum = 0;
    private int ackNum = 0;
	private static int clientPort = 8090;//TODO add way for client to set own port.
	private static int serverPort = 8080;
    
	//Constructors:
	public InputHandler(DatagramSocket socket, InetAddress server, int serverPort, TransferProtocol sender) throws UnknownHostException {
		this.socket = socket;
		this.server = server;
		this.serverPort = serverPort;
		this.sender = sender;
		inputPacket = new PacketBuilder(headerSize, packetSize);
		outputPacket= new PacketBuilder(headerSize, packetSize);
		this.fileNameHandler = new FileListCompiler();
		this.BROADCAST = InetAddress.getByName("255.255.255.255");
		commands = new InputCommands();
		packetSender = new PacketSender(this.socket, this.server, this.serverPort);
		packetSender.start();
	}
	
	//Queries:
	
	
	
	//Commands:
	
	
	//userInput commands:
	/**
	 * Sends list request to server.
	 */
	public void getList() {//TODO perhaps move to other class?
		this.outputPacket.clearData();
		this.outputPacket.clearHeader();
		this.outputPacket.setFlags(FlagBytes.SYNLIST);
		this.outputPacket.setFileNumber((short) 0); //TODO assign magic variable.
		this.outputPacket.setAckNumber(0); //TODO assign magic variable.
		this.outputPacket.setSeqNumber(0); //TODO assign magic variable.
		this.outputPacket.setCheckSum(outputPacket.calculateCheckSum(outputPacket.getCRCFile()));
		byte[] outputData = this.outputPacket.getPacket();
		sender.addToSendingQueue(outputData);

	}
	
	/**
	 * Inner class to handle the composition of the file names list.
	 * @author kester.meurink
	 *
	 */
	private class FileListCompiler {
       private List<byte[]> nameListByte = new ArrayList<byte[]>();
       
       public void addToList(byte[] fileNames) {
    	   System.out.println("Adding files to list. " +  Thread.currentThread()); //TODO for testing.
    	   this.nameListByte.add(fileNames);
       }
       
       public void compileList() {
    	   System.out.println("Compiling list. " +  Thread.currentThread()); //TODO for testing.
           	String concat =",";
	        //This is assuming all bytes have been received, so all bytes must be collected first before translating back to string. listFinalAcknowledgement method
	        int byteLengthNames = 0;
	        for (byte[] i: nameListByte) {
	        	byteLengthNames += i.length;
	        }
	        byte[] reconvertedNamesList = new byte[byteLengthNames];
	        int pointerIndex = 0;
	        for (byte[] i: nameListByte) {
	        	for (int j = 0; j < i.length; j++) {
	        		reconvertedNamesList[pointerIndex] = i[j];
	        		pointerIndex++;
	        	}
	        }

	        //Now convert back from byte array to the strings using the known concatenation symbol. listFinalAcknowledgement method
	        String receivedNames= "";
	        String[] allNamesReceived;
	        receivedNames = new String(reconvertedNamesList);
	        allNamesReceived =receivedNames.split(concat);
	        currentAvailableFiles = allNamesReceived;
	        for (String i : allNamesReceived) {
	            System.out.println(i);
	        }
	        nameListByte.clear();
       }
	}
	
	/**
	 * Sends download request to server, first showing the available files and querying the user for the filename.
	 */
	public void downloadFile(Scanner userIn) { //TODO not very optimized, needs simplification.
		System.out.println("Please type the name of the file you would like to download: ");
		getList();
		String fileName = userIn.nextLine();
		byte[] fileNameByteVersion = fileName.getBytes();
		int fileNameLength = fileName.length();
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(fileNameLength);
		byte[] fileNameLengthBytes = b.array();
		this.outputPacket.setData(concat(fileNameLengthBytes, fileNameByteVersion));
		this.outputPacket.setFlags(FlagBytes.SYNDOWN);
		this.outputPacket.setCheckSum(outputPacket.calculateCheckSum(outputPacket.getCRCFile()));
		byte[] outputData = this.outputPacket.getPacket();
		packetSender.addToQueue(outputData);
	}
	
	/**
	 * Sends upload request to server, first querying user to select the file to upload.
	 */
	public void uploadFile() {
		
	}

	
	private byte[] concat(byte[]...arrays)
	{
	    // Determine the length of the result array
	    int totalLength = 0;
	    for (int i = 0; i < arrays.length; i++)
	    {
	        totalLength += arrays[i].length;
	    }

	    // create the result array
	    byte[] result = new byte[totalLength];

	    // copy the source arrays into the result array
	    int currentIndex = 0;
	    for (int i = 0; i < arrays.length; i++)
	    {
	        System.arraycopy(arrays[i], 0, result, currentIndex, arrays[i].length);
	        currentIndex += arrays[i].length;
	    }

	    return result;
	}
}
