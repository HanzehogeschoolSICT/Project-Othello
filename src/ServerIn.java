import sun.net.ConnectionResetException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by georg on 24-Mar-17.
 */
public class ServerIn implements Runnable {

    BufferedReader inReader;
    boolean stop = false;
    private boolean Connected = false;
    private String message = "";
    private String move = "";

    ServerIn(InputStream in) throws IOException {
        inReader = new BufferedReader(new InputStreamReader(in));
    }

    private void parse(String line) throws IOException {
        if (line.equals("OK")) {
            System.out.println("Commando ontvangen/succesvol");
        }

        // SVR commando met value
        if (line.matches("^SVR.*")) {
            System.out.println("Value ontvangen");
            SVRparser(line);
        }
        if (line.contains("SVR GAME MATCH")) {
            Connected = true;
        }
        if (line.contains("PLAYERTOMOVE")) {
            message = line;
        }
        if (line.contains("SVR GAME MOVE")) {
            System.out.print("");
            move = line;
        }
    }

    /**
     * Parses an response with a value in it
     *
     * @param line
     */
    private void SVRparser(String line) throws IOException {
        // Gamelist return
        if (line.matches("^SVR GAMELIST.*")) {
            System.out.println(parseArray(line));
            System.out.println("playerlist done");
        }

        // Playerlist return
        if (line.matches("^SVR PLAYERLIST.*")) {
            parseArray(line);
        }
        if (line.contains("SVR GAME MATCH")) {
            Connected = true;
        }
    }

    /**
     * Parses the array vale from the Input string
     * Example: SVR GAMELIST {"HELLO", "WORLD"}
     * Returns arraylist with HELLO and WORLD
     * TODO prevent wrong parsing with strings containing " character. Example: SVR gamelist {"not", "work" ing"}
     *
     * @param line
     * @return
     */
    private ArrayList<String> parseArray(String line) {
        Pattern p = Pattern.compile("\"(.*?)\"");
        Matcher matcher = p.matcher(line);
        ArrayList<String> acc = new ArrayList<>();

        System.out.println(matcher.groupCount());
//        for (int i = 0; i < matcher.groupCount(); i++){
//            System.out.println("loop");
//            System.out.println(matcher.group(i));
//            acc.add(matcher.group(i));
//        }

        return acc;
    }

    /**
     * Parse input string with key value pairs.
     */
//    private Map<String, String> parseMap(String line){
//
//    }
    @Override
    public void run() {
        while (!stop) {
            try {
                String inLine = inReader.readLine();
                System.out.println(inLine);
                this.parse(inLine);
            } catch (ConnectionResetException e) {
                stop = true;
                System.out.println("Closing listening connection");
            } catch (IOException e) {
                stop = true;
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        stop = true;
    }

    public boolean getConnected() {
        return Connected;
    }

    public String getMsg() {
        return message;
    }
    public String getMove() {
        System.out.print("");
        return move;
    }
}