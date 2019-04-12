package com.nedap.university.utilities;

/**
 * Class containing all possible expected flag and command combinations.
 * Used for easier organisation of values.
 * @author kester.meurink
 *
 */
public class FlagBytes {
	//Possible list command flag combinations:
	public final static byte SYNLIST = (byte) 33;
	public final static byte SYNLISTACK = (byte) 35;
	public final static byte SYNLISTACKFIN = (byte) 39;
	public final static byte LISTACK = (byte) 34;
	
	//Possible Pause command flag combinations:
	public final static byte PAUSYN = (byte) 65;
	public final static byte PAUSYNACK = (byte) 67;
	public final static byte PAUACK = (byte) 66;
	
	//Possible download command flag combinations:
	public final static byte SYNDOWN = (byte) 17;
	public final static byte SYNDOWNACK = (byte) 19;
	public final static byte ACKDOWN = (byte) 18;
	public final static byte DOWN = (byte) 16;
	public final static byte FINDOWN = (byte) 20;
	public final static byte FINDOWNACK = (byte) 22;
	
	//Possible upload command flag combinations:
	public final static byte SYNUP = (byte) 9;
	public final static byte SYNUPACK = (byte) 11;
	public final static byte UP = (byte) 8;
	public final static byte UPACK = (byte) 10;
	public final static byte FINUP = (byte) 12;
	public final static byte FINUPACK = (byte) 14;
	
	//Possible stop command flag combinations:
	public final static byte STOPSYN = (byte) -127;
	public final static byte STOPACK = (byte) -126;
}
