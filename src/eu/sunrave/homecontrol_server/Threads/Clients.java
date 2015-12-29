package eu.sunrave.homecontrol_server.Threads;

import eu.sunrave.homecontrol_server.Libs.Functions;
import eu.sunrave.homecontrol_server.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Admin on 29.12.2015.
 */
public class Clients implements Runnable {
    public String name;
    public int id;

    public Clients(int id) {
        this.id = id;
        Functions.SendMessage("Welcome to the server", Main.clientSockets.get(id));
    }

    @Override
    public void run() {
        while (true) {
            try {
                InputStreamReader IR = new InputStreamReader(Main.clientSockets.get(id).getInputStream());
                BufferedReader BR = new BufferedReader(IR);

                String message = BR.readLine();
                if (message != null) {
                    Main.logger.con(message);
                    String[] split = message.split(" ");
                    name = split[0];
                }
            } catch (Exception e) {

            }
        }
    }
}