package eu.sunrave.homecontrol_server.Threads;

import eu.sunrave.homecontrol_server.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by zeesh on 12/29/2015.
 */
public class SocketClient implements Runnable {

    @Override
    public void run() {
        while (true) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(Main.clientSocket.getInputStream()));
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