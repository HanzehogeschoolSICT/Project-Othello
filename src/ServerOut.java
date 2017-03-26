import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by georg on 24-Mar-17.
 */
public class ServerOut {

    /**
     * Connects with the game server
     * @param name To login as
     * @param hostname ip adress of game server
     * @param port port of game server
     * @throws IOException
     */
    ServerOut(String name, String hostname, int port) throws IOException {

        Socket socket = new Socket(hostname, port);
        ServerIn serverIn;

        if (socket.isConnected()) {
            System.out.println("connected");
        }

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        serverIn = new ServerIn(socket.getInputStream());
        new Thread(serverIn).start();


        // Small connection delay
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        out.println("login " + name);
    }

    public static void main( String[] args ) {
        try {
            new ServerOut("Georg", "localhost", 7789);
        } catch (IOException e) { 
            e.printStackTrace();
        }
    }
}
