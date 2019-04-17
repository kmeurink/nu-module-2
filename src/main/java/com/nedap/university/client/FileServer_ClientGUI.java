package com.nedap.university.client;
import javax.swing.*;
import java.awt.*;

/**
 * Class that was to be used as gui for controlling the file server, but was never implemented.
 * @author kester.meurink
 *
 */
public class FileServer_ClientGUI {
	public static void main(String args[]){
		//Creating the Frame
        JFrame frame = new JFrame("Chat Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        //Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("Connect");
        JMenu m2 = new JMenu("Disconnect");
        JMenu m3 = new JMenu("Help");
        mb.add(m1);
        mb.add(m2);
        mb.add(m3);


        //Creating the panel at bottom and adding components
        JPanel panelDownload = new JPanel(); // the panel is not visible in output
        JLabel labelDownload = new JLabel("Download");
        JTextField tfDOwnload = new JTextField(10); // accepts up to 10 characters
        JButton download = new JButton("Submit");
        JPanel panelUpload = new JPanel(); // the panel is not visible in output
        JLabel labelUpload = new JLabel("Upload");
        JTextField tfUpload = new JTextField(10); // accepts up to 10 characters
        JButton upload = new JButton("Submit");
        panelDownload.add(labelDownload); // Components Added using Flow Layout
        panelDownload.add(tfDOwnload); // Components Added using Flow Layout
        panelDownload.add(download); // Components Added using Flow Layout
        panelUpload.add(labelUpload); // Components Added using Flow Layout
        panelUpload.add(tfUpload); // Components Added using Flow Layout
        panelUpload.add(upload); // Components Added using Flow Layout


        //TODO show list of downloads and uploads under the download and upload segment

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.SOUTH, panelDownload);
        frame.getContentPane().add(BorderLayout.CENTER, panelUpload);
        frame.getContentPane().add(BorderLayout.NORTH, mb);
        frame.setVisible(true);
    }


}
