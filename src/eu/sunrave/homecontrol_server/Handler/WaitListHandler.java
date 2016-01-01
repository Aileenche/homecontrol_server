package eu.sunrave.homecontrol_server.Handler;

import eu.sunrave.homecontrol_server.Libs.Functions;
import eu.sunrave.homecontrol_server.Libs.Packet;
import eu.sunrave.homecontrol_server.Main;
import eu.sunrave.homecontrol_server.Resources;
import eu.sunrave.homecontrol_server.Threads.Clients;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by Admin on 31.12.2015.
 */
public class WaitListHandler implements Runnable {
    public boolean isHandling = false;

    @Override
    public void run() {
        Main.logger.con("WaitlistHandler Started");
        Main.varshandler.waitlistHandlerIsRunning = true;
        while (Main.varshandler.waitlistHandlerIsRunning) {
            if (!isHandling) {
                if (!Main.waitlist.isEmpty()) {
                    Socket socket = Main.waitlist.get(0);
                    Packet p = new Packet(Main.identifier, Packet.PacketType.registration);
                    p.data = "whoareyou";
                    Functions.SendPacket(p, socket);
                    Main.waitingSocket = socket;
                    Main.waitlist.remove(0);
                    Main.waitlist.trimToSize();
                    isHandling = true;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    Main.waitingSocket.setSoTimeout(5000);
                    InputStream IR = Main.waitingSocket.getInputStream();
                    byte[] data = new byte[Resources.MaxPacketSize];
                    IR.read(data);
                    Packet p = (Packet) Functions.deserialize(data);
                    Main.waitingSocket.setSoTimeout(0);
                    Clients c = new Clients(Main.waitingSocket, p.identifier);
                    c.isRegistered = true;
                    Main.logger.debug("p.data=" + p.data);
                    if (Functions.checkDoubleIdentifier(p.identifier)) {
                        if (p.data == null) {
                            p.data = "";
                        }
                        if (p.data.equals("force")) {
                            Main.logger.debug("THE FORCE IS WITH ME!!!!!!!!!!!!!!!");
                            Functions.disconnectAndCleanUpClient(p.identifier);
                        } else {
                            Packet packet = new Packet(Main.identifier, Packet.PacketType.message);
                            packet.data = "You are allready Connected!";
                            Functions.SendPacket(packet, Main.waitingSocket);
                            kill();
                        }
                    }
                    int i = Functions.getFreeClientSlot();
                    while (i == -1) {
                        i = Functions.getFreeClientSlot();
                        Thread.sleep(100);
                    }
                    Thread t = new Thread(c, "identifier-" + c.identifier);
                    t.start();
                    Packet packet = new Packet(Main.identifier, Packet.PacketType.message);
                    packet.data = "Welcome to the Server " + c.identifier;
                    Functions.SendPacket(packet, Main.waitingSocket);
                    Main.clientHarv.set(i, c);
                    Main.waitingSocket = null;
                    isHandling = false;
                } catch (Exception e) {
                    Main.logger.warning("Client " + Main.waitingSocket.getInetAddress().getHostAddress() + " Didnt answer within Timelimit... Lets Kick him.");
                    kill();
                }
            }
        }
        Main.varshandler.waitlistHandlerIsRunning = false;
    }

    public void kill() {
        Main.logger.debug("Killed!");
        try {
            Main.waitingSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Main.waitingSocket = null;
        isHandling = false;
        return;
    }
}
