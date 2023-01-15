import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class Commands {
    String command;
    String attribute;
    String privateMessage;

    Selector selector;
    SocketChannel sc;
    
    Set<String> usernames;
    Map<String, List<Clients>> channels;

    public String executeCommand(SelectionKey key) {
        Clients client = (Clients)key.attachment();

        switch (command) {
            case "nick":
                break;
            case "join":
                break;
            case "leave":
                break;
            case "bye":
                break;
            default:
                break;
        }

        return "placeholder";
    }
}
