package server;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class ServerOut {

    private ServerIn sIn;
    private boolean connected = false;
    private PrintWriter out;
    private Socket socket;

    public void connectToServer(String name, String adress) throws IOException{
        ServerOut(name, adress, 7789);
        connected = true;
    }


    public Socket returnSocket() throws IOException{
        return socket;
    }


    public void ServerOut(String name, String hostname, int port) throws IOException {

        socket = new Socket(hostname, port);

        if (socket.isConnected()) {
            System.out.println("Connected on: " + hostname + ":" + port + "\n ");
        }

        // Create a printwriter, autoflush is enabled
        out = new PrintWriter(socket.getOutputStream(), true);


        // Small connection delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Execute the login command
        out.println("login " + name);
    }

    /**
     * Execute a command from System.in.
     * FIXME: Momenteel buiten gebruik
     */
    public void readFromConsole(){
        Scanner reader = new Scanner(System.in);
        String input = reader.nextLine();

        // Stop listening thread
        if (input.equals("quit")) {
            System.out.println("Disconnecting server");
            sIn.disconnect();
            out.println("quit");
            return;
        }

        out.println(input);
        readFromConsole();
    }
    /**
     * Execute a command from System.in,
     */
    public void sendToServer(String input){
        // Stop listening thread
        if (input.equals("quit")) {
            System.out.println("Disconnecting server");
            sIn.disconnect();
            out.println("quit");
            return;
        }

        out.println(input);
    }
}
