package com.nedap.university.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileServer_ServerSide {

	//Named constants:
    private DatagramSocket socket;
    private List<String> listQuotes = new ArrayList<String>();
    private Random random;
	private static int serverPort = 8080;

	
	//Constructors:	
    public FileServer_ServerSide(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }
 
    public static void main(String[] args) {
    	//Requires specific file with quotes, and port to listen on.
    	//TODO specific file is not necessary for file server, only folder where files can be found.
 
 
        try {
        	FileServer_ServerSide server = new FileServer_ServerSide(serverPort);
            server.service();
        } catch (SocketException ex) {//TODO handle better
            System.out.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {//TODO handle better
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
    

	
	
	//Queries:
	
	
	
	//Commands:
     /**
     * Class to run all required actions during its connection.
     * @throws IOException
     */
    private void service() throws IOException { //TODO rename
        while (true) {
        	//Receive packet from client.
            DatagramPacket request = new DatagramPacket(new byte[13], 13);
            socket.receive(request);
            byte[] tempData = request.getData();
            for (byte i : tempData) {
                System.out.println(Byte.toString(i)); //TODO for testing.

            }
            
            //Get address and port of client from the request.
            InetAddress clientAddress = request.getAddress();
            int clientPort = request.getPort();
            
          //Test packet with only basic header
        	byte[] testPacket = new byte[13];
        	//Seq. number:
        	testPacket[0] = 0;
        	testPacket[1] = 0;
        	testPacket[2] = 0;
        	testPacket[3] = 6;
        	//Ack. number:
        	testPacket[4] = 0;
        	testPacket[5] = 0;
        	testPacket[6] = 0;
        	testPacket[7] = 20;
        	//Flags and commands:
        	testPacket[8] = 0;
        	//Window size:
        	testPacket[9] = 0;
        	testPacket[10] = 0;
        	//Checksum:
        	testPacket[11] = 0;
        	testPacket[12] = 0;
        	
            //Send packet with chosen data to the client.
            DatagramPacket response = new DatagramPacket(testPacket, testPacket.length, clientAddress, clientPort);
            socket.send(response);
            System.out.println("response sent."); //TODO for testing.
        }
    }
 
    //Load the quotes from a file into a string array.
    //TODO replace by loading a folder.
    private void loadQuotesFromFile(String quoteFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(quoteFile));
        String aQuote;
 
        while ((aQuote = reader.readLine()) != null) {
            listQuotes.add(aQuote);
        }
 
        reader.close();
    }
 


}
