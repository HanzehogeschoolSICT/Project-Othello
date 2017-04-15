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

    private BufferedReader inReader;
    private boolean stop = false;
    private volatile boolean Connected = false;
    private volatile String message = "";
    private volatile String move = "";
    private volatile String oppName = "";
    private volatile String ownName = "";
    private volatile boolean endOfGame = false;
    private volatile boolean challenge = false;
    private volatile String turn = "";
    private ObservableList<String> options = FXCollections.observableArrayList();

    ServerIn(InputStream in) throws IOException {
        inReader = new BufferedReader(new InputStreamReader(in));
    }

    private void parse(String line) throws IOException {
        if (line.equals("OK")) {
            //System.out.println("Commando ontvangen/succesvol");
        }

        // SVR commando met value
        if (line.matches("^SVR.*")) {
            //System.out.println("Value ontvangen");
            SVRparser(line);
        }
        if (line.contains("SVR GAME MATCH")) {
            Connected = true;
        }
        if (line.contains("PLAYERTOMOVE")) {
            message = line;
        }
        if (line.contains("SVR GAME MOVE")) {
            move = line;
        }
        if (line.contains("YOURTURN")) {
            turn = line;
        }
        if (line.contains("PLAYERTOMOVE")) {
            message = line;
        }
        if (line.contains("SVR GAME CHALLENGE")) {
            challenge = true;
            message = line;
        }
        
        if (oppName == "" && line.contains("OPPONENT")) {
        	oppName = line.substring(line.indexOf("OPPONENT") + 11, line.length() - 2);
        }
        if(line.contains("SVR GAME LOSS") || line.contains("SVR GAME DRAW") || line.contains("SVR GAME WIN")){
            endOfGame=true;
        }

    }
    
    public void setOwnName(String name){
    	ownName = name;
    }
    
    public String getOppName(){
    	return oppName;
    }

    /**
     * Parses an response with a value in it
     *
     * @param line
     */
    private void SVRparser(String line) throws IOException {
        // Gamelist return
        if (line.matches("^SVR GAMELIST.*")) {
            //
            // System.out.println(parseArray(line));
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
        return Connected;
    }

    public boolean getChallenge(){
        return challenge;
    }

    public  String getMsg() {
        return message;
    }

    public boolean endOfGame(){
        return endOfGame;
    }
    public String getMove() {
        return move;
    }

    public  String getTurn(){
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
        endOfGame = false;
        move = "";
        turn = "";
        Connected = false;
    }
}