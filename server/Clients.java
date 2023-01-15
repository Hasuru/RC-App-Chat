import java.nio.*;
import java.nio.channels.*;

public class Clients {
    ByteBuffer buffer;
    SocketChannel sc;
    SelectionKey key;

    // Client's username
    String username;
    // Client's state : init, outside, inside
    String state;
    // Client's current room (if he is in one)
    String room;
    
    Clients(SocketChannel sc, SelectionKey key) throws Exception {
        this.sc = sc;
        this.key = key;
        this.state = "init";
        this.buffer = ByteBuffer.allocate( 16384 );
    }

    public void leave() {
        try {
            if (key != null) {
                key.cancel();
            }
            if (sc != null) {
                sc.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
