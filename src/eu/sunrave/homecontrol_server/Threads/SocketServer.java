package eu.sunrave.homecontrol_server.Threads;

import eu.sunrave.homecontrol_server.Main;
import eu.sunrave.homecontrol_server.Resources;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Admin on 29.12.2015.
 */
public class SocketServer implements Runnable {
    public ServerSocket serverSocket;

    @Override
    public void run() {
        Main.logger.con("SocketServer Started");
        //Main.clients = new ArrayList<>();
        //Main.clientSockets = new ArrayList<>();
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

                Main.waitlist.add(socket);
                Main.logger.debug("Received new Client, put him to Waitlist...");

                /*
                Main.clientSockets.add(socket);

                int clientID = -1;
                for (int i = 0; i < Main.clientSockets.size(); i++) {
                    if (Main.clientSockets.get(i) == socket) {
                        clientID = i;
                    }
                }

                Clients c = new Clients(clientID);
                Thread t = new Thread(c, "CliendID:" + clientID);
                t.start();
                Main.clients.add(c);
            */
            }
        } catch (Exception e) {
            Main.logger.error("error connecting client.");
            Main.logger.error("" + e);
        }
        Main.varshandler.socketserverIsRunning = false;
    }
}
