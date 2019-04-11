package com.nedap.university.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.nedap.university.communication.PacketBuilder;

public class InputCommands {
	
	//Named Constants:
	private PacketBuilder headerConstructor;
	private String directory = "C:/Users/kester.meurink/Nedap university/module 2/assignment/";
	private List<byte[]> packetsToSend;
	private int headerSize = 16; //TODO make changeable eventually
	private int packetSize = 1024;//TODO make changeable eventually
	private byte[] replyPacket; 
	
	//Constructors:
	public InputCommands() {
		this.headerConstructor = new PacketBuilder(headerSize, packetSize);
		packetsToSend = new ArrayList<byte[]>();
	}
	
	
	//Queries:
	
	
	
	//Commands:
	
	//List function commands:
	/**
	 * Reacts to the list function sent by the user, by compiling the list of available files.
	 * @return
	 */
	public List<byte[]> listRequest() { //TODO improve structure and test
        File file = new File(directory);//TODO change directory for pi
    	//Add all file names together in a byte array.
        byte[] nameByte;
        String allFiles = "";
        String concat =",";
        String[] fileNames = file.list();
        for(String f: fileNames){
            //System.out.println(f);
            allFiles += f;
            allFiles += concat;
        }
        nameByte = allFiles.getBytes();
        
        //Split byte array into multiple packet arrays with the correct packet size.
        List<byte[]> nameListByte = new ArrayList<byte[]>();
        int pointer = 0;
        int maxPacket = packetSize - headerSize;
        byte[] tempPacket = new byte[maxPacket];
        for(int i = pointer; i < nameByte.length; i += maxPacket) {
        	int newSize = (maxPacket < nameByte.length - i) ? maxPacket : nameByte.length - i;
        	tempPacket = new byte[newSize];
        	for (int j = i; j < i + newSize; j++) {
        		tempPacket[j - i] = nameByte[j];
        	}
    		nameListByte.add(tempPacket.clone());
        }        
        //Add flags to the header, based on their order.
        for (int i = 0; i < nameListByte.size(); i++) {
        	headerConstructor.clearData();
        	headerConstructor.clearHeader();
            headerConstructor.setData(nameListByte.get(i)); //
        	if (i != nameListByte.size() - 1) {
                headerConstructor.setFlags(FlagBytes.SYNLISTACK);
        	} else {
        		headerConstructor.setFlags(FlagBytes.SYNLISTACKFIN);
        	}
            packetsToSend.add(headerConstructor.getPacket());
        }
        return packetsToSend;
	}
	
	public byte[] listAcknowledgement() { 
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.LISTACK);
    	System.out.println("Received acknowledgement of the list request");
        replyPacket = headerConstructor.getPacket();
        return replyPacket;
        }
	
	public byte[] listFinalAcknowledgement() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.LISTACK);
    	System.out.println("Received final acknowledgement of the list request");
        replyPacket = headerConstructor.getPacket();
        return replyPacket;
        }
	
	public byte[] listReceivedAcknowledgement() {
    	//TODO only internal, does not send info back. Probably something with cancelling a timeout to retransmit.
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
