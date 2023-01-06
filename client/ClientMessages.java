import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.security.auth.Subject;
import javax.swing.*;

public class ClientMessages implements Runnable {
    JTextArea chatArea;
    ByteBuffer buffer;
    SocketChannel sc;

    ClientMessages(SocketChannel sc, JTextArea chatArea) {
        this.sc = sc;
        this.chatArea = chatArea;
        this.buffer = ByteBuffer.allocate(16384);
    }

    // test purposed
    ClientMessages() {
        this.buffer = ByteBuffer.allocate(16384);
    }

    /// parse message sent by the server and returns the message that should be displayed in the chat area
    public String parseServerMessage(String serverMessage) {
        String displayMessage = "";
        
        // token sent by server (e.g OK | ERROR | MESSAGE | BYE | etc...)
        String token;
        int indexSpace = serverMessage.indexOf(" ");
        if (indexSpace != -1) {
            token = serverMessage.substring(0, indexSpace);
        } else {
            token = serverMessage.substring(0); 
        }

        String rest = serverMessage.substring(indexSpace+1);

        switch (token) {
            case "OK":
                displayMessage = token;
                break;
            case "ERROR":
                displayMessage = token;
                break;
            case "MESSAGE":
                indexSpace = rest.indexOf(" ");
                
                String nick = rest.substring(0, indexSpace);
                String message = rest.substring(indexSpace+1);
                
                displayMessage = nick + ": " + message;
                break;
            case "NEWNICK":
                indexSpace = rest.indexOf(" ");

                String nick1 = rest.substring(0, indexSpace);
                String nick2 = rest.substring(indexSpace+1);

                displayMessage = nick1 + " mudou de nome para " + nick2; 
                break;
            case "JOINED":
                displayMessage = token + " " + rest;
                break;
            case "LEFT":
                displayMessage = token + " " + rest;
                break;
            case "BYE":
                displayMessage = token;
                break;
            case "PRIVATE":
                indexSpace = rest.indexOf(" ");
                    
                nick = rest.substring(0, indexSpace);
                message = rest.substring(indexSpace+1);
                
                displayMessage = "[PRIVADO] " + nick + ": " + message;
                break;
        }

        return displayMessage + "\n";
    }
    
    public void run() {
        String message = parseServerMessage("PRIVATE micas ola");
        System.out.println(message);
    }

    public static void main(String args[]) {
        ClientMessages cm = new ClientMessages();
        cm.run();
    }
}
