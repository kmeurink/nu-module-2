import java.io.*;
import java.net.*;
import java.util.*;
 
/**
 * This program demonstrates how to implement a UDP server program.
 *
 *
 * @author www.codejava.net
 */
public class QuoteServer {
    private DatagramSocket socket;
    private List<String> listQuotes = new ArrayList<String>();
    private Random random;
 
    public QuoteServer(int port) throws SocketException {
        socket = new DatagramSocket(port);
        random = new Random();
    }
 
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Syntax: QuoteServer <file> <port>");
            return;
        }
 
        String quoteFile = args[0];
        int port = Integer.parseInt(args[1]);
 
        try {
            QuoteServer server = new QuoteServer(port);
            server.loadQuotesFromFile(quoteFile);
            server.service();
        } catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
 
    private void service() throws IOException {
        while (true) {
        	//Receive packet from client.
            DatagramPacket request = new DatagramPacket(new byte[1], 1);
            socket.receive(request);
 
            //Take random quote from the file and transform into bytes.
            String quote = getRandomQuote();
            byte[] buffer = quote.getBytes();
 
            //Get address and port of client from the request.
            InetAddress clientAddress = request.getAddress();
            int clientPort = request.getPort();
 
            //Send packet with chosen quote to the client.
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            socket.send(response);
        }
    }
 
    //Load the quotes from a file into a string array.
    private void loadQuotesFromFile(String quoteFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(quoteFile));
        String aQuote;
 
        while ((aQuote = reader.readLine()) != null) {
            listQuotes.add(aQuote);
        }
 
        reader.close();
    }
 
    //Select random quote from the array.
    private String getRandomQuote() {
        int randomIndex = random.nextInt(listQuotes.size());
        String randomQuote = listQuotes.get(randomIndex);
        return randomQuote;
    }
}

