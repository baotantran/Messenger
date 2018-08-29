import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private ObjectInputStream input;
    private ObjectOutputStream output;

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
                    closeStuff();
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
        input = new ObjectInputStream(connection.getInputStream());
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        showMessage("\n Streams are now setup!");
    }

    // During the chat conversation
    private void whileChatting() throws IOException {
        String message = " You are now connected! ";
        sendMessage(message);
        ableToType(true);
        do {
            try {
                message = (String) input.readObject();
                showMessage("\n" + message);
            } catch(ClassNotFoundException e) {
                showMessage("\n Error" + e.getMessage());
            }
        } while(!message.equals("CLIENT - END"));
    }
}
