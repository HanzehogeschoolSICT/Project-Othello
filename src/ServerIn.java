import sun.net.ConnectionResetException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by georg on 24-Mar-17.
 */
public class ServerIn implements Runnable {

    InputStreamReader inReader;
    boolean stop = false;

    ServerIn(InputStream in) throws IOException {
        inReader = new InputStreamReader(in);
    }

    @Override
    public void run() {
        while(!stop){
            try {
                System.out.print((char) inReader.read());
            } catch (ConnectionResetException e) {
                stop = true;
                System.out.println("Closing listening connection");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect(){
        stop = true;
    }
}
