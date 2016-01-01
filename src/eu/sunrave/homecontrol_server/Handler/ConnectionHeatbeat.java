package eu.sunrave.homecontrol_server.Handler;

import eu.sunrave.homecontrol_server.Libs.Functions;
import eu.sunrave.homecontrol_server.Libs.Packet;
import eu.sunrave.homecontrol_server.Main;
import eu.sunrave.homecontrol_server.Threads.Clients;

/**
 * Created by Admin on 01.01.2016.
 */
public class ConnectionHeatbeat implements Runnable {

    @Override
    public void run() {
        Main.logger.con("ConnectionHeatbeat Started");
        Main.varshandler.connectionHeatbeatIsRunning = true;
        while (Main.varshandler.connectionHeatbeatIsRunning) {
            Packet p = new Packet(Main.identifier, Packet.PacketType.ping);
            for (int i = 0; i < Main.clientHarv.size(); i++) {
                boolean returned = false;
                if (Main.clientHarv.get(i) != null) {
                    Clients c = Main.clientHarv.get(i);
                    Functions.SendPacket(p, c.socket);
                    Main.logger.debug("Sending Ping to " + c.identifier);
                    try {
                        Thread.sleep(1000);
                        int counter = 0;
                        while (counter <= 10 && !returned) {
                            if (Main.varshandler.pong != null) {
                                returned = true;
                            }
                            if (counter >= 10) {
                                Exception exception = new Exception("Client timed Out");
                                throw exception;
                            }
                            counter++;
                            Thread.sleep(1000);
                        }
                        Main.logger.debug("Received Pong from " + Main.varshandler.pong.identifier);
                        Main.varshandler.pong = null;
                        returned = false;
                    } catch (Exception e) {
                        try {
                            Main.logger.notice("Heatbeat to " + c.identifier + " Failed, Cleaning up after him...");
                            c.socket.close();
                            Main.clientHarv.set(i, null);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Main.varshandler.waitlistHandlerIsRunning = false;
    }
}