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
	private List<byte[]> fileNamesList;
	private int headerSize = 16; //TODO make changeable eventually
	private int packetSize = 1024;//TODO make changeable eventually
	private byte[] replyPacket; 
	private short nonDownloadFileNumber = 0;
	//Constructors:
	public InputCommands() {
		this.headerConstructor = new PacketBuilder(headerSize, packetSize);
		
	}
	
	
	//Queries:
	
	
	
	//Commands:
	
	//List function commands: TODO change structure to create list and then be able to get parts of the list instead of the whole one.
	/**
	 * Reacts to the list function sent by the user, by compiling the list of available files.
	 * @return
	 */
	public void listRequest() { //TODO improve structure and test
		fileNamesList = new ArrayList<byte[]>();
		int seqNum = 1;
        File file = new File(directory);//TODO change directory for pi
    	//Add all file names together in a byte array.
        byte[] nameByte;
        String allFiles = "";
        String concat = ",";
        String[] fileNames = file.list();
        for(String f: fileNames){
            //System.out.println(f);
            allFiles += f;
            allFiles += concat;
        }
        nameByte = allFiles.getBytes();
        
        //Split byte array into multiple packet arrays with the correct packet size.
        List<byte[]> nameListByte = new ArrayList<byte[]>();
        nameListByte.clear();
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
        	headerConstructor.setFileNumber(nonDownloadFileNumber);
        	headerConstructor.setAckNumber(seqNum);
        	seqNum++;
        	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));//TODO this seems a bit ridiculous, replace by a simpler version?
        	fileNamesList.add(headerConstructor.getPacket());
        }
	}
	
	public byte[] getListPart() {
		byte[] fileName = null;
		if(!this.fileNamesList.isEmpty()) {
			fileName = this.fileNamesList.get(0);
			this.fileNamesList.remove(0);
		}
		return fileName;
	}
	
	public byte[] listAcknowledgement() { 
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.LISTACK);
    	System.out.println("Received acknowledgement of the list arrival");
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));
        replyPacket = headerConstructor.getPacket();
        return replyPacket;
        }
	
	public byte[] listFinalAcknowledgement() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.LISTACK);
    	System.out.println("Received final acknowledgement of the list request");
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));
        replyPacket = headerConstructor.getPacket();
        return replyPacket;
        }
	
	public byte[] listReceivedAcknowledgement() {
    	//TODO only internal, does not send info back. Probably something with cancelling a timeout to retransmit.
		return null;
	}
	//Pause function commands:
	
	public byte[] pauseSynchronization() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.PAUSYNACK);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));
		return null;
	}
	
	public byte[] pauseSynchronizationAcknowledgement() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.PAUACK);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));

		return null;
	}
	
	public byte[] pauseAcknowledgement() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	//TODO do something internal, actually pausing for example.
		return null;
	}
	
	//Download function commands:
	
	public byte[] downloadSynchronization() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.SYNDOWNACK);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));

		return null;
	}
	
	public byte[] downloadSynchronizationAcknowledgement() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.ACKDOWN);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));

		return null;
	}
	
	public byte[] downloadAcknowledgement() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	//TODO which flags depends on whether it is the final piece. !!server sends this.
    	headerConstructor.setFlags(FlagBytes.DOWN);
    	headerConstructor.setFlags(FlagBytes.FINDOWN);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));

		return null;
	}

	public byte[] download() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.ACKDOWN);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));

		return null;
	}
	
	public byte[] downloadFinish() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.FINDOWNACK);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));

		return null;
	}
	
	public byte[] downloadFinishAcknowledgment() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	//TODO something happens internally to finish download. !!happens serverside.
		return null;
	}
	//Upload function commands:
	
	public byte[] uploadSynchronization() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.SYNUPACK);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));

		return null;
	}
	
	public byte[] uploadSynchronizationAcknowledgement() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.UP);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));

		return null;
	}
	
	public byte[] uploadAcknowledgement() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	//TODO which flag depends on if it is the last packet. !! client does this
    	headerConstructor.setFlags(FlagBytes.UP);
    	headerConstructor.setFlags(FlagBytes.FINUP);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));

		return null;
	}

	public byte[] upload() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.UPACK);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));

		return null;
	}
	
	public byte[] uploadFinish() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.FINUPACK);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));

		return null;
	}
	
	public byte[] uploadFinishAcknowledgement() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	//TODO do something internal to finish upload. !!happens clientside
    	return null;
	}
	//Stop function commands:
	public byte[] stopSynchronization() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.STOPACK);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));

		return null;
	}
	
	public byte[] stopAcknowledgement() {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	//TODO do something to stop the system.
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
