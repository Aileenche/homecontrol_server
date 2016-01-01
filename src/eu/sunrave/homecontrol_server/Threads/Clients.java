package eu.sunrave.homecontrol_server.Threads;

import eu.sunrave.homecontrol_server.Libs.ClientType;
import eu.sunrave.homecontrol_server.Libs.Functions;
import eu.sunrave.homecontrol_server.Libs.Packet;
import eu.sunrave.homecontrol_server.Main;
import eu.sunrave.homecontrol_server.Resources;

import java.io.InputStream;
import java.net.Socket;

/**
 * Created by Admin on 29.12.2015.
 */
public class Clients implements Runnable {
    public String identifier;
    public String ip;
    public boolean isRegistered;
    public Socket socket;
    public ClientType clientType;

    public Clients(Socket socket, String identifier) {
        this.socket = socket;
        this.identifier = identifier;
    }

    //TODO When client sends a message server recives them here
    @Override
    public void run() {
        isRegistered = false;
        while (true) {
            try {

                InputStream IR = socket.getInputStream();
                byte[] data = new byte[Resources.MaxPacketSize];
                IR.read(data);
                Packet p = (Packet) Functions.deserialize(data);
                identifier = p.identifier;
                ip = socket.getRemoteSocketAddress().toString();
                Main.serverPacketHandler.handle(p, this);
            } catch (Exception e) {
                Main.logger.debug("Unable to get correct responce from client " + identifier);
                try {
                    socket.close();
                } catch (Exception ex) {
                    Main.logger.debug("Couldn't remove connected user " + identifier);
                }
                Main.clientHarv.remove(this);
                break;
            }
        }
    }
}