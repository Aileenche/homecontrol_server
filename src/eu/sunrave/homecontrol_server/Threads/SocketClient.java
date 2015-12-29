package eu.sunrave.homecontrol_server.Threads;

import eu.sunrave.homecontrol_server.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by zeesh on 12/29/2015.
 */
public class SocketClient implements Runnable {

    //TODO When server sends a message client recives them here
    @Override
    public void run() {
        while (true) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(Main.mainServerSocket.getInputStream()));
                String message = in.readLine();
                if (message != null) {
                    Main.logger.debug(message);
                }
            } catch (Exception e) {
                Main.logger.debug("Error: reading from server");
            }
        }
    }
}