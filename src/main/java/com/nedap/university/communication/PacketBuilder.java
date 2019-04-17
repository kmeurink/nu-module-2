package com.nedap.university.communication;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;

/**
 * Class to build the packets from the given header information and data block.
 * @author kester.meurink
 *
 */
//TODO use clone in get functions to prevent linked array issues? Currently implemented see how it turns out
public class PacketBuilder { //TODO Should have methods to change the header, add data to the end and provide current settings.
	//Named Constants:
	private int fileNumberStartIndex = 0;
	private int seqStartIndex = 2;
	private int ackStartIndex = 6;
	private int flagStartIndex = 10;
	private int commandsStartIndex = 11;
	private int windowStartIndex = 12;
	private int checksumStartIndex = 14;
	private int endIndex = 22;
	
	
	//Bitwise operator constants:
	private int bitwiseShift24 = 24;
	private int bitwiseShift16 = 16;
	private int bitwiseShift8 = 8;
	private int bitMaskShift = 0xff;
	
	//Packet arrays;
	private byte[] packetHeaderArray;
	private byte[] packetDataArray;
	private byte[] packetArrayTotal;
	private int headerSize = 22;
	private int packetSize = 1024;
	private int checkSumSize = 8;
	
	//Constructors:
	public PacketBuilder(int headSize, int packetSize) {
		this.packetSize = packetSize;
		this.packetHeaderArray = new byte[headSize];
		this.packetDataArray = new byte[packetSize - headSize];
		this.packetArrayTotal = new byte[packetSize];
	}
	
	//Queries:
	
	/**
	 * Returns the fileNumber as an array of bytes.
	 * @return
	 */
	public short getFileNumber() {
		short fileNum = 0;
		byte[] fileNumberArray = new byte[2];
		for (int i = fileNumberStartIndex; i < seqStartIndex; i++) {
			fileNumberArray[i - fileNumberStartIndex] = this.packetArrayTotal.clone()[i];
		}
		ByteBuffer buffer = ByteBuffer.wrap(fileNumberArray);
		fileNum = buffer.getShort();
		return fileNum;
	}
	
	/**
	 * Returns the seqNumber as an array of bytes.
	 * @param i
	 * @return
	 */
	public int getSeqNumber() {
		int seqNum = 0;
		byte[] seqNumberArray = new byte[4];
		for (int i = seqStartIndex; i < ackStartIndex; i++) {
			seqNumberArray[i - seqStartIndex] = this.packetArrayTotal.clone()[i];
		}
		ByteBuffer buffer = ByteBuffer.wrap(seqNumberArray);
		seqNum = buffer.getInt();
		return seqNum;
	}
	
	/**
	 * Returns the ackNumber as an array of bytes.
	 * @return
	 */
	public int getAckNumber() {
		int ackNum = 0;
		byte[] ackNumberArray = new byte[4];
		for (int i = ackStartIndex; i < flagStartIndex; i++) {
			ackNumberArray[i - ackStartIndex] = this.packetArrayTotal.clone()[i];
		}
		ByteBuffer buffer = ByteBuffer.wrap(ackNumberArray);
		ackNum = buffer.getInt();
		return ackNum;		
	}
	
	/**
	 * Returns the flags set in the packet header.
	 * @return
	 */
	public byte getFlags() {
		return packetArrayTotal.clone()[flagStartIndex];
	}
	
	/**
	 * Returns the commands set in the packet header.
	 * @return
	 */
	public byte getCommands() {
		return packetArrayTotal.clone()[commandsStartIndex];
	}
	
	/**
	 * Returns the window size as an array of bytes;
	 * @return
	 */
	public byte[] getWindowSize() {
		byte[] windowSizeArray = new byte[2];
		for (int i = windowStartIndex; i < checksumStartIndex; i++) {
			windowSizeArray[i - windowStartIndex] = this.packetArrayTotal.clone()[i];
		}
		return windowSizeArray;
	}
	
	/**
	 * Returns the checksum set in the header, as an array of bytes.
	 * @return
	 */
	public byte[] getCheckSum() {
		byte[] checksumArray = new byte[8];
		for (int i = checksumStartIndex; i < endIndex; i++) {
			checksumArray[i - checksumStartIndex] = this.packetArrayTotal.clone()[i];
		}
		return checksumArray;		
	}
	
	/**
	 * Returns the full header.
	 * @return
	 */
	public byte[] getHeader() {
		this.packetHeaderArray = Arrays.copyOfRange(this.packetArrayTotal.clone(),0,this.headerSize);
		return this.packetHeaderArray;
	}
	
	/**
	 * Returns the data block behind the header.
	 * @return
	 */
	public byte[] getData() {
		this.packetDataArray = Arrays.copyOfRange(this.packetArrayTotal.clone(),this.headerSize,this.packetSize);
		return this.packetDataArray;
	}
	
	/**
	 * Returns the total packet as a byte array.
	 * @return
	 */
	public byte[] getPacket() {
		return this.packetArrayTotal.clone();
	}
	
	/**
	 * Returns the total packet excluding the crc itself.
	 * Used for checking if a packet is not corrupted.
	 * @return
	 */
	public byte[] getCRCFile() {
		byte[] CRCFilearray = new byte[this.packetSize - checkSumSize];
		//Read the header uptill the crc.
		for (int i = 0; i < (this.headerSize - checkSumSize); i++) {
			CRCFilearray[i] = this.packetArrayTotal.clone()[i];
		}
		//Read the data.
		for (int i = this.headerSize; i < this.packetSize; i++) {
			CRCFilearray[i - checkSumSize] = this.packetArrayTotal.clone()[i];
		}
		return CRCFilearray;
	}
	
	//Commands:
	
	/**
	 * Sets the file identification number
	 * @param number
	 */
	public void setFileNumber(short number) {
		int temp = number & bitMaskShift;
		packetArrayTotal[fileNumberStartIndex+1] = (byte) temp;
		temp = number >> bitwiseShift8 & bitMaskShift;
		packetArrayTotal[fileNumberStartIndex] = (byte) temp;
		}
	
	/**
	 * Sets a new sequence number in the header.
	 * @param sequenceNumber
	 */
	public void setSeqNumber(int sequenceNumber) {
		int temp = sequenceNumber & bitMaskShift;
		packetArrayTotal[this.seqStartIndex + 3] = (byte) temp;
		temp = sequenceNumber >> bitwiseShift8 & bitMaskShift;
		packetArrayTotal[this.seqStartIndex + 2] = (byte) temp;
		temp = sequenceNumber >> bitwiseShift16 & bitMaskShift;
		packetArrayTotal[this.seqStartIndex + 1] = (byte) temp;
		temp = sequenceNumber >> bitwiseShift24 & bitMaskShift;
		packetArrayTotal[this.seqStartIndex] = (byte) temp; 
	}
	
	/**
	 * Sets a new acknowledgment number in the header.
	 * @param ackNumber
	 */
	public void setAckNumber(int ackNumber) {
		int temp = ackNumber & bitMaskShift;
		packetArrayTotal[this.ackStartIndex + 3] = (byte) temp;
		temp = ackNumber >> bitwiseShift8 & bitMaskShift;
		packetArrayTotal[this.ackStartIndex + 2] = (byte) temp;
		temp = ackNumber >> bitwiseShift16 & bitMaskShift;
		packetArrayTotal[this.ackStartIndex + 1] = (byte) temp;
		temp = ackNumber >> bitwiseShift24 & bitMaskShift;
		packetArrayTotal[this.ackStartIndex] = (byte) temp;
	}
	
	/**
	 * Sets the flag bits in the header.
	 * @param flags
	 */
	public void setFlags(byte flags) {
		packetArrayTotal[this.flagStartIndex] = flags;
	}
	
	/**
	 * Sets the command bits in the header.
	 * @param commands
	 */
	public void setCommands(byte commands) {
		packetArrayTotal[this.commandsStartIndex] = commands;
	}

	/**
	 * Sets the window size in the header of the packet.
	 * @param window
	 */
	public void setWindowSize(int window) {//TODO might also become data size, i dont know if window size needs to be changed.
		int temp = window & bitMaskShift;
		packetArrayTotal[this.windowStartIndex + 1] = (byte) temp;
		temp = window >> bitwiseShift8 & bitMaskShift;
		packetArrayTotal[this.windowStartIndex] = (byte) temp;
	}
	
	/**
	 * Sets the checksum in the header.
	 * @param checkSum
	 */
	public void setCheckSum(byte[] checkSum) {
		for(int i = this.checksumStartIndex; i < this.endIndex; i++) {
			packetArrayTotal[i] = checkSum[i-this.checksumStartIndex];
		}
	}
	
	/**
	 * Calculates the checksum of a packet.
	 * @param file
	 * @return
	 */
	public byte[] calculateCheckSum(byte[] file) { //TODO handle checking the header and the data somehow, while skipping the crc.
		CRC32 checkSum = new CRC32();
		checkSum.update(file);
		long checkSumValue = checkSum.getValue();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeLong(checkSumValue);
			dos.flush();
		} catch (IOException e) {
			// TODO handle error
			e.printStackTrace();
		}
		// etc.
		byte[] data = bos.toByteArray();
		return data;
	}
	
	/**
	 * Replaces the total header with a new one given as an argument. 
	 * @param header
	 */
	public void setHeader(byte[] header) {
		for(int i = 0; i < header.length; i++) {
			packetArrayTotal[i] = header[i];
		}
	}
	
	/**
	 * Set the data behind the header.
	 * @param data
	 */
	public void setData(byte[] data) {
		for (int i = this.headerSize; i < (data.length + this.headerSize); i++) {
			packetArrayTotal[i] = data[i - this.headerSize];
		}
	}
	
	/**
	 * Set the contents of the whole packet.
	 * @param data
	 */
	public void setPacket(byte[] total) {
		for (int i = 0; i < total.length; i++) {
			packetArrayTotal[i] = total[i];
		}
	}
	
	/**
	 * Clear the contents of the header.
	 */
	public void clearHeader() { //TODO check if this works
		for(int i = 0; i < this.packetHeaderArray.length; i++) {
			packetArrayTotal[i] = 0;
		}		
	}
	
	/**
	 * Clear the contents of the data.
	 */
	public void clearData() { //TODO check if this works
		for (int i = this.packetHeaderArray.length; i < this.packetArrayTotal.length; i++) {
			packetArrayTotal[i] = 0;
		}
	}
	
}
