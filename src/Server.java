import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {
    private ServerSocket server;
    private Socket  connection;
    private JTextArea chatWindow;
    private JTextField userText;
    private DataInputStream input;
    private DataOutputStream output;

    public Server() {
        super("Bao's Messenger");

        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendMessage(e.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(300,150);
        setVisible(true);

    }

    // Setup and run the server
    public void startRunning() {
        try {
            server = new ServerSocket(1234,10);
            while(true) {
                try {
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                } catch (EOFException e) {
                    showMessage("\n Server ended the connection");
                } finally {
                    closeConnection();
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // wait for connection
    private void waitForConnection() throws IOException {
        showMessage("Waiting for connection... \n");
        connection = server.accept();
        showMessage("Connected to " + connection.getInetAddress().getHostName());
    }

    // get stream to send and receive
    private void setupStreams() throws IOException{
        input = new DataInputStream(connection.getInputStream());
        output = new DataOutputStream(connection.getOutputStream());
        //output.flush();
        showMessage("\n Streams are now setup!");
    }

    // During the chat conversation
    private void whileChatting() throws IOException {
        String message = " You are now connected! ";
        sendMessage(message);
        ableToType(true);
        do {
                message = input.readUTF();
                showMessage("\n" + message);
        } while(!message.equals("CLIENT - END"));
    }
    // Close stream and socket
    private void closeConnection() {
        showMessage("\n Closing connections.. \n");
        ableToType(false);
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
            System.out.println(e.getMessage());
        }
    }

    // send message to a client
    private void sendMessage(String message) {
        try {
            output.writeUTF("SERVER - " + message);
            output.flush();
            showMessage("\nSERVER - " + message);
        } catch(IOException e) {
            chatWindow.append("\n ERROR: CAN'T SEND THE MESSAGE");
        }
    }

    // show message in the chat window
    private void showMessage(final String message){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(message);
                    }
                }
        );
    }

    private void ableToType(final boolean state){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        userText.setEditable(state);
                    }
                }
        );
    }
}
