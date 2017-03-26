import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by georg on 24-Mar-17.
 */
public class ServerOut {

    private ServerIn serverIn;
    private PrintWriter out;

    /**
     * Connects with the game server
     * @param name To login as
     * @param hostname ip adress of game server
     * @param port port of game server
     * @throws IOException
     */
    ServerOut(String name, String hostname, int port) throws IOException {

        Socket socket = new Socket(hostname, port);

        if (socket.isConnected()) {
            System.out.println("Connected on: " + hostname + ":" + port);
        }

        out = new PrintWriter(socket.getOutputStream(), true);

        serverIn = new ServerIn(socket.getInputStream());
        new Thread(serverIn).start();

        // Small connection delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        out.println("login " + name);
    }

    /**
     * Execute a command fromt System.in
     */
    public void readFromConsole(){
        Scanner reader = new Scanner(System.in);
        String input = reader.nextLine();

        // Stop listening thread
        if (input.equals("quit")) {
            System.out.println("Disconnecting server");
            serverIn.disconnect();
            out.println("quit");
            return;
        }

        out.println(input);
        readFromConsole();
    }

    public static void main( String[] args ) {
        try {
            ServerOut server1 = new ServerOut("Groep 7", "localhost", 7789);
            server1.readFromConsole();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
