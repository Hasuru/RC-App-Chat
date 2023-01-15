import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ChatClient {

    // GUI interface variables
    JFrame frame = new JFrame("Chat Client");
    private JTextField chatBox = new JTextField();
    private JTextArea chatArea = new JTextArea();
    // --- Fim das variáveis relacionadas coma interface gráfica

    // ChatClient Variables
    private String server;
    private int port;
    SocketChannel sc;
    ByteBuffer buffer;
    
    // Constructor
    public ChatClient(String server, int port) throws IOException {

        // GUI configuration settings
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(chatBox);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.SOUTH);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.setSize(500, 300);
        frame.setVisible(true);
        chatArea.setEditable(false);
        chatBox.setEditable(true);
        chatBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    newMessage(chatBox.getText());
                } catch (IOException ex) {
                } finally {
                    chatBox.setText("");
                }
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                chatBox.requestFocusInWindow();
            }
        });

        this.server = server;
        this.port = port;

        // allocate same buffer size as the server
        this.buffer = ByteBuffer.allocate(16384);
    }


    // User prints message onto the text box
    // SocketChannel then sends message to the server
    public void newMessage(String message) throws IOException {
        message = message + '\n';

        // put message on buffer and switch buffer to write mode
        buffer.put(message.getBytes());
        buffer.flip();
        sc.write(buffer);

        // clear buffer to make it ready to read new messages
        buffer.clear();
        buffer.rewind();
    }

    
    public void run() throws IOException {
        // Get hostAddress and bind a socketChannel to it
        InetSocketAddress hostAddress = new InetSocketAddress(this.server, this.port);
        sc = SocketChannel.open(hostAddress);
        
        // Parsing messages and outputing to the chatArea 
        ClientMessages cm = new ClientMessages(sc, chatArea);
        cm.run();

    }
    

    // Instancia o ChatClient e arranca-o invocando o seu método run()
    // * NÃO MODIFICAR *
    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient(args[0], Integer.parseInt(args[1]));
        client.run();
    }
}