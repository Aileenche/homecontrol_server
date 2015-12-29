package eu.sunrave.homecontrol_server.Threads;

import eu.sunrave.homecontrol_server.Main;
import eu.sunrave.homecontrol_server.Resources;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Admin on 29.12.2015.
 */
public class SocketServer implements Runnable {
    public ServerSocket serverSocket;
    public ArrayList<Thread> clients;

    @Override
    public void run() {
        clients = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(Resources.SocketServerPort);
            Main.varshandler.socketserverIsRunning = true;
        } catch (Exception e) {
            Main.logger.error("SocketServer konnte nicht gestartet werden! ");
            Main.logger.error("" + e);
        }

        try {
            while (Main.varshandler.socketserverIsRunning) {
                Socket socket = serverSocket.accept();
                Thread t = new Thread(new Clients(socket));
                clients.add(t);
                t.start();
            }
        } catch (Exception e) {
            Main.logger.error("SocketServer konnte nicht gestartet werden! ");
            Main.logger.error("" + e);
        }
        Main.varshandler.socketserverIsRunning = false;
    }
}
