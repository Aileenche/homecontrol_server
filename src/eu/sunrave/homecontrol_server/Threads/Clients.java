package eu.sunrave.homecontrol_server.Threads;

import eu.sunrave.homecontrol_server.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by Admin on 29.12.2015.
 */
public class Clients implements Runnable {
    Thread reader;
    Thread writer;
    private Socket socket;

    public Clients(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        reader = new Thread(new ReadClientData(socket));
        writer = new Thread(new WriteClientData(socket));
        reader.start();
        writer.start();
    }
}

class ReadClientData implements Runnable {
    Socket socket;

    public ReadClientData(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                InputStreamReader IR = new InputStreamReader(this.socket.getInputStream());
                BufferedReader BR = new BufferedReader(IR);

                String message = BR.readLine();
                if (message != null) {
                    Main.logger.con(message);
                }
            } catch (Exception e) {

            }
        }
    }
}

class WriteClientData implements Runnable {

    Socket socket;

    public WriteClientData(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            PrintStream printStream = new PrintStream(this.socket.getOutputStream());
            //printStream.println(message);
        } catch (Exception e) {

        }
    }
}