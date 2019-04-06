package com.nedap.university.communication;

import java.util.Arrays;

/**
 * Class to build the packets from the given header information and data block.
 * @author kester.meurink
 *
 */
public class PacketBuilder { //TODO Should have methods to change the header, add data to the end and provide current settings.
	//Named Constants:
	//Bitwise operator constants:
	private int bitwiseShift24 = 24;
	private int bitwiseShift16 = 16;
	private int bitwiseShift8 = 8;
	private int bitMaskShift = 0xff;
	
	//Packet arrays;
	private byte[] packetHeaderArray;
	private byte[] packetDataArray;
	private byte[] packetArrayTotal;
	private int headerSize = 13;
	private int dataSize = 499; //TODO remove eventually
	private int packetSize = 512;//TODO remove eventually
	
	//Constructors:
	public PacketBuilder(int dataSize, int packetSize) {
		this.dataSize = dataSize;
		this.packetSize = packetSize;
		this.packetHeaderArray = new byte[headerSize];
		this.packetDataArray = new byte[dataSize];
		this.packetArrayTotal = new byte[packetSize];
	}
	
	//Queries:
	/**
	 * Returns the seqNumber as an array of bytes.
	 * @param i
	 * @return
	 */
	public byte[] getSeqNumber() {
		byte[] seqNumberArray = new byte[4];
		for (int i = 0; i < 4; i++) {
			seqNumberArray[i] = this.packetArrayTotal[i];
		}
		
		return seqNumberArray;
	}
	
	/**
	 * Returns the ackNumber as an array of bytes.
	 * @return
	 */
	public byte[] getAckNumber() {
		byte[] ackNumberArray = new byte[4];
		for (int i = 4; i < 8; i++) {
			ackNumberArray[i] = this.packetArrayTotal[i];
		}
		
		return ackNumberArray;		
	}
	
	/**
	 * Returns the flags set in the packet header.
	 * @return
	 */
	public byte getFlags() {
		return packetArrayTotal[8];
	}
	
	/**
	 * Returns the window size as an array of bytes;
	 * @return
	 */
	public byte[] getWindowSize() {
		byte[] windowSizeArray = new byte[2];
		
		return windowSizeArray;
	}
	
	/**
	 * Returns the checksum set in the header, as an array of bytes.
	 * @return
	 */
	public byte[] getCheckSum() {
		byte[] checksumArray = new byte[2];
		
		return checksumArray;		
	}
	
	/**
	 * Returns the full header.
	 * @return
	 */
	public byte[] getHeader() {
		this.packetHeaderArray = Arrays.copyOfRange(this.packetArrayTotal,0,this.headerSize);
		return this.packetHeaderArray;
	}
	
	/**
	 * Returns the data block behind the header.
	 * @return
	 */
	public byte[] getData() {
		this.packetDataArray = Arrays.copyOfRange(this.packetArrayTotal,this.headerSize,this.packetSize);
		return this.packetDataArray;
	}
	
	/**
	 * Returns the total packet as a byte array.
	 * @return
	 */
	public byte[] getPacket() {
		return this.packetArrayTotal;
	}
	
	//Commands:
	/**
	 * Sets a new sequence number in the header.
	 * @param sequenceNumber
	 */
	public void setSeqNumber(int sequenceNumber) {
		int temp = sequenceNumber & bitMaskShift;
		packetArrayTotal[0] = (byte) temp;
		temp = sequenceNumber >> bitwiseShift8 & bitMaskShift;
		packetArrayTotal[1] = (byte) temp;
		temp = sequenceNumber >> bitwiseShift16 & bitMaskShift;
		packetArrayTotal[2] = (byte) temp;
		temp = sequenceNumber >> bitwiseShift24 & bitMaskShift;
		packetArrayTotal[3] = (byte) temp; 
	}
	
	/**
	 * Sets a new acknowledgment number in the header.
	 * @param ackNumber
	 */
	public void setAckNumber(int ackNumber) {
		int temp = ackNumber & bitMaskShift;
		packetArrayTotal[4] = (byte) temp;
		temp = ackNumber >> bitwiseShift8 & bitMaskShift;
		packetArrayTotal[5] = (byte) temp;
		temp = ackNumber >> bitwiseShift16 & bitMaskShift;
		packetArrayTotal[6] = (byte) temp;
		temp = ackNumber >> bitwiseShift24 & bitMaskShift;
		packetArrayTotal[7] = (byte) temp;
	}
	
	/**
	 * Sets the flag bits in the header.
	 * @param flags
	 */
	public void setFlags(byte flags) {
		packetArrayTotal[8] = flags;
	}

	/**
	 * Sets the window size in the header of the packet.
	 * @param window
	 */
	public void setWindowSize(int window) {//TODO might also become data size, i dont know if window size needs to be changed.
		int temp = window & bitMaskShift;
		packetArrayTotal[9] = (byte) temp;
		temp = window >> bitwiseShift8 & bitMaskShift;
		packetArrayTotal[10] = (byte) temp;
	}
	
	/**
	 * Sets the checksum in the header.
	 * @param checkSum
	 */
	public void setCheckSum(int checkSum) {
		int temp = checkSum & bitMaskShift;
		packetArrayTotal[11] = (byte) temp;
		temp = checkSum >> bitwiseShift8 & bitMaskShift;
		packetArrayTotal[12] = (byte) temp;
	}
	
	/**
	 * Replaces the total header with a new one given as an argument. 
	 * @param header
	 */
	public void setHeader(byte[] header) {
		for(int i = 0; i < this.packetHeaderArray.length; i++) {
			packetArrayTotal[i] = header[i];
		}
	}
	
	/**
	 * Set the data behind the header.
	 * @param data
	 */
	public void setData(byte[] data) {
		for (int i = this.packetHeaderArray.length; i < this.packetArrayTotal.length; i++) {
			packetArrayTotal[i] = data[i];
		}
	}
	
	/**
	 * Set the contents of the whole packet.
	 * @param data
	 */
	public void setPacket(byte[] total) {
		for (int i = 0; i < this.packetArrayTotal.length; i++) {
			packetArrayTotal[i] = total[i];
		}
	}
	
}
