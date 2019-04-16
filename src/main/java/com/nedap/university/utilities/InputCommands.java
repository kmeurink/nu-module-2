package com.nedap.university.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.nedap.university.communication.PacketBuilder;
import com.nedap.university.files.DownUploader;

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
	private short downloadStartAck = 1;
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
        	headerConstructor.setSeqNumber(seqNum);
        	seqNum++;
        	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));//TODO this seems a bit ridiculous, replace by a simpler version?
        	fileNamesList.add(headerConstructor.getPacket());
        }
	}
	
	/**
	 * Returns a part of the list which is null if all parts have already been retrieved.
	 * @return byte[] fileName
	 */
	public byte[] getListPart() {
		byte[] fileName = null;
		if(!this.fileNamesList.isEmpty()) {
			fileName = this.fileNamesList.get(0);
			this.fileNamesList.remove(0);
		}
		return fileName;
	}
	
	public byte[] listAcknowledgement(int seq) { 
		System.out.println("Received acknowledgement of the list arrival");
		headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.LISTACK);
    	headerConstructor.setAckNumber(seq);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));
        replyPacket = headerConstructor.getPacket();
        return replyPacket;
        }
	
	public byte[] listFinalAcknowledgement(int seq) {
    	System.out.println("Received final acknowledgement of the list request");
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.LISTACK);
    	headerConstructor.setAckNumber(seq);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));
        replyPacket = headerConstructor.getPacket();
        return replyPacket;
        }
	
	public void listReceivedAcknowledgement() {
    	//TODO only internal, does not send info back. Probably something with cancelling a timeout to retransmit.
		System.out.println("Server: List received acknowledgement received.");
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
	
	public byte[] downloadSynchronization(short fileNumber, byte[] data) {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.SYNDOWNACK);
    	headerConstructor.setAckNumber(0);
    	headerConstructor.setSeqNumber(0);
    	headerConstructor.setFileNumber(fileNumber);
    	headerConstructor.setData(data);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));
        replyPacket = headerConstructor.getPacket();
        return replyPacket;
	}
	
	public byte[] downloadSynchronizationAcknowledgement(short file) {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setFlags(FlagBytes.ACKDOWN);
    	headerConstructor.setFileNumber(file);
    	headerConstructor.setAckNumber(downloadStartAck);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));
        replyPacket = headerConstructor.getPacket();
        return replyPacket;	}
	
	public byte[] downloadAcknowledgement(int ack, DownUploader load) {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	headerConstructor.setSeqNumber(ack);
    	headerConstructor.setFileNumber(load.getFileNumber());
    	if (load.checkIfLastPart()) {//TODO check if this is the last piece of the file.
    		headerConstructor.setFlags(FlagBytes.FINDOWN);
    	} else {
    		headerConstructor.setFlags(FlagBytes.DOWN);
    	}
    	
    	byte[] data = load.readOutFilePart();
    	headerConstructor.setData(data);
    	//TODO which flags depends on whether it is the final piece. !!server sends this.
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));
        replyPacket = headerConstructor.getPacket();
        return replyPacket;	
        }

	public byte[] download(int seq, byte[] data, DownUploader load) {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	load.writeFilePart(data);
    	headerConstructor.setFlags(FlagBytes.ACKDOWN);
    	headerConstructor.setAckNumber(seq + 1);
    	headerConstructor.setFileNumber(load.getFileNumber());
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));
        replyPacket = headerConstructor.getPacket();
        return replyPacket;
        }
	
	public byte[] downloadFinish(int seq, byte[] data, DownUploader load) {
    	headerConstructor.clearData();
    	headerConstructor.clearHeader();
    	load.writeFilePart(data);
    	if (load.calculateFileChecksum() == load.getCRC()) {
    		
    	}
    	headerConstructor.setFlags(FlagBytes.FINDOWNACK);
    	headerConstructor.setFileNumber(load.getFileNumber());
    	headerConstructor.setAckNumber(seq + 1);
    	headerConstructor.setCheckSum(headerConstructor.calculateCheckSum(headerConstructor.getCRCFile()));
        replyPacket = headerConstructor.getPacket();
        return replyPacket;	}
	
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
	 * Method to easily turn byte[] into an integer.
	 * @param b
	 * @return
	 */
	public static int byteArrayToInt(byte[] b) 
	{
	    int integerValue = 0;
	    for (int i = 0; i < 4; i++) {
	        int shift = (4 - 1 - i) * 8;
	        integerValue += (b[i] & 0x000000FF) << shift;
	    }
	    return integerValue;
	}
	
	/**
	 * Method to easily turn byte[] into a long.
	 * @param b
	 * @return
	 */
	public static int byteArrayToLong(byte[] b) 
	{
	    int integerValue = 0;
	    for (int i = 0; i < 8; i++) {
	        int shift = (8 - 1 - i) * 8;
	        integerValue += (b[i] & 0x000000FF) << shift;
	    }
	    return integerValue;
	}
	
	/**
	 * Internal method to easily convert lists with bytes to a byte array.
	 * @param list
	 * @return
	 */
	public static byte[] listToByteArray(List<Byte> list) {
		byte[] listArray = new byte[list.size()];
		for (int i = 0; i < list.size(); i++) {
			listArray[i] = list.get(i);
		}
		return listArray; 
	}
	
	public static byte[] concat(byte[]...arrays)
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
	
	public static byte[] longToBytes(long l) {
	    byte[] result = new byte[8];
	    for (int i = 7; i >= 0; i--) {
	        result[i] = (byte)(l & 0xFF);
	        l >>= 8;
	    }
	    return result;
	}
	
	public static byte[] intToBytes(int data) {
	    return new byte[] {
	        (byte)((data >> 24) & 0xff),
	        (byte)((data >> 16) & 0xff),
	        (byte)((data >> 8) & 0xff),
	        (byte)((data >> 0) & 0xff),
	    };
	}

}
