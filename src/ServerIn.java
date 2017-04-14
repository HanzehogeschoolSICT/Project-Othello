import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sun.net.ConnectionResetException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Observable;
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
    private boolean eog = false;
    private boolean challenge = false;
    private String turn = "";
    private ObservableList<String> options = FXCollections.observableArrayList();

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
        if (line.contains("YOURTURN")) {
            turn = line;
        }
        if(line.contains("SVR GAME LOSS") || line.contains("SVR GAME DRAW") || line.contains("SVR GAME WIN")){
            eog=true;
            message = line;

        }
        if (line.contains("PLAYERTOMOVE")) {
            message = line;
        }
        if (line.contains("SVR GAME CHALLENGE")) {
            System.out.print("");
            challenge = true;
            message = line;
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
            //System.out.println(parseArray(line));
            System.out.println("playerlist done");
        }

        // Playerlist return
        if (line.matches("^SVR PLAYERLIST.*")) {
            createPlayerList(line);
        }
    }


    private void createPlayerList(String line) {
        Pattern p = Pattern.compile("\"(.*?)\"");
        Matcher matcher = p.matcher(line);
        ArrayList<String> acc = new ArrayList<>();
        while(matcher.find()){
            String s = matcher.group();
            options.add(s);
        }
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
        System.out.print("");
        return Connected;
    }

    public boolean getChallenge(){
        System.out.print("");
        return challenge;
    }

    public String getMsg() {
        return message;
    }

    public boolean eogMsg(){
        return eog;
    }
    public String getMove() {
        System.out.print("");
        return move;
    }

    public String getTurn(){
        return turn;
    }
    public ObservableList<String> returnOptions(){
        return options;
    }

    public void resetTurn(){
        turn = "";
    }

    public void Reset(){
        message = "";
        eog = false;
        move = "";
        turn = "";
        Connected = false;
        challenge = false;
    }
}