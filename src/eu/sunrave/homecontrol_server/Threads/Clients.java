package eu.sunrave.homecontrol_server.Threads;

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
    //public int id;
    public String ip;
    public boolean isRegistered;
    public Socket socket;

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
//                if (!isRegistered) {
//                    if (!TryRegister(p)) {
//                        break;
//                    }
//                }
                Main.serverPacketHandler.handle(p, this);
            } catch (Exception e) {
                Main.logger.debug("Unable to get correct responce from client " + identifier);
                //Functions.printStacktoDebug(e);
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

    //Make sure the user is allowed to register to the server
//    private boolean TryRegister(Packet p) {
//        if (p.pakettype == Packet.PacketType.registration) {
//            isRegistered = true;
//        } else {
//            Main.logger.debug("No register packet was sent from " + identifier);
//            try {
//                Main.clientSockets.get(id).close();
//            } catch (Exception ex) {
//
//            }
//            Main.clientSockets.remove(id);
//            Main.clients.remove(id);
//            for (int i = id; i < Main.clientSockets.size(); i++) {
//                Main.clients.get(i).id = i;
//            }
//            return false;
//        }
//
//        int prevClientID = -1;
//        int counter = 0;
//        for (int i = 0; i < Main.clients.size(); i++) {
//            if (p.identifier.equals(Main.clients.get(i).identifier)) {
//                if (Main.clients.get(id) != Main.clients.get(i)) {
//                    prevClientID = i;
//                }
//                counter++;
//            }
//        }
//
//        if (counter > 1) {
//            String pdata = "";
//            try {
//                if (p.data != null)
//                    pdata = (String) p.data;
//                else
//                    pdata = "";
//            } catch (Exception ex) {
//                Main.logger.debug("User didn't send data in correct format");
//            }
//
//            if (pdata.equals("force")) {
//
//                Main.logger.debug("Removing previous user with same identifier " + identifier);
//                try {
//                    Main.clientSockets.get(prevClientID).close();
//                } catch (Exception ex) {
//                    Main.logger.debug("Couldn't remove previously connected user " + identifier);
//                }
//
//            } else {
//
//                Main.logger.debug("User already connected with that identifier " + identifier);
//                try {
//                    Main.clientSockets.get(id).close();
//                } catch (Exception ex) {
//                    Main.logger.debug("Couldn't remove connected user " + identifier);
//                }
//                Main.clientSockets.remove(id);
//                Main.clients.remove(id);
//                for (int i = id; i < Main.clientSockets.size(); i++) {
//                    Main.clients.get(i).id = i;
//                }
//                return false;
//
//            }
//        }
//
//        return true;
//    }
}