package com.nedap.university.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.nedap.university.communication.PacketReceiver;

//TODO determine what distinguishes server and client, otherwise they should be combined.
public class FileServer_ClientSide {
	//Named variables:
	private static int clientPort = 8090;//TODO add way for client to set own port.
	private static int clientPortThreaded = 8099;//TODO add way for client to set own port.
	private static int serverPort = 8080;
    private DatagramSocket socket;
    //private PacketReceiver packetReceiver;
	
	//Constructors:
    public FileServer_ClientSide(int port) throws SocketException {
    	socket = new DatagramSocket(port);
    }
    
    public static void main(String[] args) {

    	FileServer_ClientSide testClient;
		try {
			testClient = new FileServer_ClientSide(clientPortThreaded);//create new instance of class and initialize
	    	Thread receivingThread = new Thread(new PacketReceiver(testClient.getSocket()));//Create and start receiver thread giving the socket as argument
	    	receivingThread.start();//TODO perhaps move this thread to filehandler
		} catch (SocketException e1) { //TODO handle error
			e1.printStackTrace();
		}


        try {//Setup connection with server
			testClient = new FileServer_ClientSide(clientPort);
            testClient.broadcastConnect();
            testClient.service();
        } catch (SocketException e) { //TODO needs handling
			e.printStackTrace();
		} catch (IOException ex) {
            System.out.println("Client error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    //Queries:
    
    /**
     * Returns the socket created for the client.
     * @return
     */
    public DatagramSocket getSocket() {
    	return this.socket;
    }
    
    //Commands:
    /**
     * Broadcasts a packet into the network to find its file server.
     * @param socket
     */
    public void broadcastConnect() { //TODO testing method.
    	//Status: broadcast is visible on wireshark
    	
    	//Test packet with only basic header
    	byte[] testPacket = new byte[13];
    	//Seq. number:
    	testPacket[0] = 0;
    	testPacket[1] = 0;
    	testPacket[2] = 0;
    	testPacket[3] = 1;
    	//Ack. number:
    	testPacket[4] = 0;
    	testPacket[5] = 0;
    	testPacket[6] = 0;
    	testPacket[7] = 10;
    	//Flags and commands:
    	testPacket[8] = 0;
    	//Window size:
    	testPacket[9] = 0;
    	testPacket[10] = 0;
    	//Checksum:
    	testPacket[11] = 0;
    	testPacket[12] = 0;
    	
    	//Broadcast packet to find server.
    	InetAddress broadcast;
		try {
			broadcast = InetAddress.getByName("localhost");//TODO testing using localhost "255.255.255.255");
			DatagramPacket request = new DatagramPacket(testPacket, 13, broadcast, serverPort);
				socket.send(request);        
		} catch (UnknownHostException e) {//TODO handle better
			e.printStackTrace();
		} catch (IOException e) {//TODO handle better
			e.printStackTrace();
		}

    }
    
    /**
     * Class to receive and print all packets during its connection.
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

        }
    }
    
}
