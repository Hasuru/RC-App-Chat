import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class ChatServer {
    // list of usernames connected to server (no repeated names)
    static Set<String> usernames = new HashSet<String>();

    // buffer variable
    static private final ByteBuffer buffer = ByteBuffer.allocate( 16384 );

    // Message decoder variables
    static private final Charset charset = Charset.forName("UTF8");
    static private final CharsetDecoder decoder = charset.newDecoder();

    static Selector selector;
    static String username = "";

    static public void main(String args[]) throws Exception {
        int port = Integer.parseInt(args[0]);

        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();

            ssc.configureBlocking(false);

            ServerSocket ss = ssc.socket();
            InetSocketAddress isa = new InetSocketAddress(port);
            ss.bind(isa);

            selector = Selector.open();

            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Listening on port " + port);

            while (true) {
                int num = selector.select();

                if (num == 0) {
                    continue;
                }

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();

                while (it.hasNext()) {
                    SelectionKey key = it.next();

                    if (key.isAcceptable()) {
                        Socket s = ss.accept();
                        System.out.println("Got connection from " + s);

                        SocketChannel sc = s.getChannel();
                        sc.configureBlocking(false);

                        // Create new client connection and give them an unique key
                        SelectionKey selectedKey = sc.register(selector, SelectionKey.OP_READ);
                        Clients client = new Clients(sc, selectedKey);
                        selectedKey.attach(client);

                    } else if (key.isReadable()) {
                        SocketChannel sc = null;

                        try {
                            sc = (SocketChannel) key.channel();
                            boolean ok = processInput(sc);

                            if (!ok) {
                                key.cancel();

                                Socket s = null;
                                
                                try {
                                    s = sc.socket();
                                    System.out.println("Closing connection to " + s);
                                    s.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            key.cancel();

                            try {
                                sc.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }

                            System.out.println("Closed " + sc);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static private boolean processInput(SocketChannel sc) throws IOException {
        buffer.clear();
        sc.read(buffer);
        buffer.flip();

        if (buffer.limit() == 0) {
            return false;
        }

        String message = decoder.decode(buffer).toString();
        System.out.println(message);

        return true;
    }
}
