package com.nedap.university.communication;

import java.io.File;
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
    private FileListCompiler fileNameHandler;
    private TransferProtocol sender;
    private String[] currentAvailableFiles;
    private int seqNum = 0;
    private int ackNum = 0;
	private static int clientPort = 8090;//TODO add way for client to set own port.
	private static int serverPort = 8080;
    
	//Constructors:
	public InputHandler(TransferProtocol sender) throws UnknownHostException {
		//this.server = server;
		//this.serverPort = serverPort;
		this.sender = sender;
		//inputPacket = new PacketBuilder(headerSize, packetSize);
		outputPacket= new PacketBuilder(headerSize, packetSize);
		this.fileNameHandler = new FileListCompiler();
		//this.BROADCAST = InetAddress.getByName("255.255.255.255");
		//commands = new InputCommands(directory);
		//packetSender = new PacketSender(this.socket, this.server, this.serverPort);
		//packetSender.start();
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
	        int count = 0;
	        for (String i : allNamesReceived) {
	            System.out.println(count + " : " + i);
	            count++;
	        }
	        nameListByte.clear();
       }
	}
	
	/**
	 * Sends download request to server, first showing the available files and querying the user for the filename.
	 */
	public void downloadFile(Scanner userIn) { //TODO add check to make sure filename is correct.
		System.out.println("Please type the name of the file you would like to download: ");
		getList();
		String fileName = userIn.nextLine();
		byte[] fileNameByteVersion = fileName.getBytes();
		byte[] fileNameLengthBytes = InputCommands.intToBytes(fileName.length());
		this.outputPacket.clearData();
		this.outputPacket.clearHeader();
		this.outputPacket.setData(InputCommands.concat(fileNameLengthBytes, fileNameByteVersion));
		this.outputPacket.setAckNumber(0);
		this.outputPacket.setSeqNumber(0);
		this.outputPacket.setFileNumber((short) sender.getAvailableFileNumber()); //TODO select available file number.
		this.outputPacket.setFlags(FlagBytes.SYNDOWN);
		this.outputPacket.setCheckSum(outputPacket.calculateCheckSum(outputPacket.getCRCFile()));
		byte[] outputData = this.outputPacket.getPacket();
		sender.addToSendingQueue(outputData);
	}
	
	/**
	 * Sends upload request to server, first querying the user to select the file to upload.
	 */
	public void uploadFile(Scanner userIn) {
		System.out.println("Please type the name of the file you would like to upload: ");
		System.out.println("Make sure it is present in the file directory, such that the application can find it.");
		this.outputPacket.clearData();
		this.outputPacket.clearHeader();
		String fileName = userIn.nextLine();
		byte[] fileNameByteVersion = fileName.getBytes();
		byte[] fileNameLengthBytes = InputCommands.intToBytes(fileName.length());
		this.outputPacket.setFileNumber((short) sender.getAvailableFileNumber()); //TODO select available file number.
		this.sender.getFilelist().createDownload(fileName, this.outputPacket.getFileNumber(), false, false);
		long crc = this.sender.getFilelist().getDownUploads().get(this.outputPacket.getFileNumber()).calculateFileChecksum();
		int size = this.sender.getFilelist().getDownUploads().get(this.outputPacket.getFileNumber()).getSize();
		this.outputPacket.setData(InputCommands.concat(fileNameLengthBytes, fileNameByteVersion, InputCommands.intToBytes(size), InputCommands.longToBytes(crc)));
		this.outputPacket.setAckNumber(0);
		this.outputPacket.setSeqNumber(0);
		this.outputPacket.setFlags(FlagBytes.SYNUP);		
		this.outputPacket.setCheckSum(outputPacket.calculateCheckSum(outputPacket.getCRCFile()));
		byte[] outputData = this.outputPacket.getPacket();
		sender.addToSendingQueue(outputData);
	}

}
