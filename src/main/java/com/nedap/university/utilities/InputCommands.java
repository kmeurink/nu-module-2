package com.nedap.university.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.nedap.university.communication.PacketBuilder;

public class InputCommands {
	
	//Named Constants:
	private PacketBuilder headerConstructor;
	private String directory = "C:/Users/kester.meurink/Nedap university/module 2/assignment/";
	private List<Byte> fileNamesList;
	private List<byte[]> packetsToSend;
	private int dataSize = 1011; //TODO make changeable eventually
	private int packetSize = 1024;//TODO make changeable eventually
	
	//Constructors:
	public InputCommands() {
		this.headerConstructor = new PacketBuilder(dataSize, packetSize);
		fileNamesList = new ArrayList<Byte>();
		packetsToSend = new ArrayList<byte[]>();
	}
	
	
	//Queries:
	
	
	
	//Commands:
	
	//List function commands:
	/**
	 * Reacts to the list function sent by the user, by compiling the list of available files.
	 * @return
	 */
	public List<byte[]> listRequest() { //TODO implement
        File file = new File(directory);//TODO change directory for pi
        File[] files = file.listFiles();
    	byte[] Data = new byte[ files.length];
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	/*
        for(File f: files){
        	String nameTemp = f.getName();
        	fileNamesList.add(Byte.valueOf(nameTemp));//TODO String of course has multiple bytes.
        }
        byte[] tempPacket;
        while(fileNamesList.size() >= this.dataSize) { //If multiple packets are needed, only the last one has the FIN flags.
            headerConstructor.setFlags(FlagBytes.SYNLISTACK);
            tempPacket = new byte[this.dataSize];
            for (int i = 0; i < this.dataSize; i++) {
            	tempPacket[i] = fileNamesList.get(i);
            }
            headerConstructor.setData(tempPacket); //TODO might cause a linked data problem.
            packetsToSend.add(headerConstructor.getPacket());
            fileNamesList.subList(0, (dataSize < fileNamesList.size()) ? dataSize : fileNamesList.size()).clear();;
        }
        */
        headerConstructor.setFlags(FlagBytes.SYNLISTACKFIN);
        //headerConstructor.setData(listToByteArray(fileNamesList));
        packetsToSend.add(headerConstructor.getPacket());
        return packetsToSend;
	}
	
	public byte[] listAcknowledgement() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.LISTACK);
    	System.out.println("Received acknowledgement of the list request");
		return null;
	}
	
	public byte[] listFinalAcknowledgement() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.LISTACK);
    	System.out.println("Received acknowledgement of the list request");
		return null;
	}
	
	public byte[] listReceivedAcknowledgement() {
    	//TODO only internal, does not send info back. Probably something with a timeout to retransmit.
		return null;
	}
	//Pause function commands:
	
	public byte[] pauseSynchronization() {
		
		return null;
	}
	
	public byte[] pauseSynchronizationAcknowledgement() {
		
		return null;
	}
	
	public byte[] pauseAcknowledgement() {
		
		return null;
	}
	
	//Download function commands:
	
	public byte[] downloadSynchronization() {
		
		return null;
	}
	
	public byte[] downloadSynchronizationAcknowledgement() {
		
		return null;
	}
	
	public byte[] downloadAcknowledgement() {
		
		return null;
	}

	public byte[] download() {
	
		return null;
	}
	
	public byte[] downloadFinish() {
		
		return null;
	}
	
	public byte[] downloadFinishAcknowledgment() {
		
		return null;
	}
	//Upload function commands:
	
	public byte[] uploadSynchronization() {
		
		return null;
	}
	
	public byte[] uploadSynchronizationAcknowledgement() {
		
		return null;
	}
	
	public byte[] uploadAcknowledgement() {
		
		return null;
	}

	public byte[] upload() {
	
		return null;
	}
	
	public byte[] uploadFinish() {
		
		return null;
	}
	
	public byte[] uploadFinishAcknowledgement() {
		
		return null;
	}
	//Stop function commands:
	public byte[] stopSynchronization() {
		
		return null;
	}
	
	public byte[] stopAcknowledgement() {
		
		return null;
	}
	
	
	/**
	 * Internal method to easily convert lists with bytes to a byte array.
	 * @param list
	 * @return
	 */
	private byte[] listToByteArray(List<Byte> list) {
		byte[] listArray = new byte[list.size()];
		for (int i = 0; i < list.size(); i++) {
			listArray[i] = list.get(i);
		}
		return listArray; 
	}
	

}
