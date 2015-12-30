package eu.sunrave.homecontrol_server.Handler;

import eu.sunrave.homecontrol_server.Libs.Functions;
import eu.sunrave.homecontrol_server.Libs.Packet;
import eu.sunrave.homecontrol_server.Main;

import java.util.Set;

/**
 * Created by Admin on 28.12.2015.
 */
public class CommandHandler {
    public void make(String[] presplit) {
        switch (presplit[0].toLowerCase()) {
            case ("stop"):
                Main.isStopped = true;
                break;
            case ("debug"):
                switch (presplit[1].toLowerCase()) {
                    default:
                    case (" "):
                        Main.logger.con("Debug Mode: " + Main.debugMode);
                        break;
                    case ("true"):
                    case ("on"):
                        Main.debugMode = true;
                        Main.logger.con("Debug Mode set to true");
                        break;
                    case ("false"):
                    case ("off"):
                        Main.debugMode = false;
                        Main.logger.con("Debug Mode set to false");
                        break;
                }
                break;
            case ("webserv"):
                switch (presplit[1].toLowerCase()) {
                    default:
                    case (" "):
                        Main.logger.con("Webserver isRunning: " + Main.webserver.isRunning);
                        break;
                    case ("stop"):
                        Main.webserver.stop();
                        break;
                    case ("start"):
                        Main.webserver.start();
                        break;
                }
                break;
            case ("sockserv"):
                switch (presplit[1].toLowerCase()) {
                    default:
                    case (" "):
                        Main.logger.con("Socketserver isRunning: " + Main.varshandler.socketserverIsRunning);
                        break;
                    case ("stop"):
                        Main.serverThread.interrupt();
                        break;
                    case ("start"):
                        Main.serverThread.start();
                        break;
                }
                break;
            case ("msg"):
                String message = "";
                for (int i = 1; i < presplit.length; i++) {
                    message += " " + presplit[i];
                }
                if (Main.isserver) {
                    for (int i = 0; i < Main.clientSockets.size(); i++) {
                        Packet p = new Packet(Main.identifier, Packet.PacketType.command);
                        p.data = message;
                        Functions.SendPacket(p, Main.clientSockets.get(i));
                    }
                } else {
                    Packet p = new Packet(Main.identifier, Packet.PacketType.command);
                    p.data = message;
                    Functions.SendPacket(p, Main.mainServerSocket);
                }
                break;
            case ("rss"):
                if (!Main.isserver) {
                    Packet p = new Packet(Main.identifier, Packet.PacketType.command);
                    p.data = "rss";
                    Functions.SendPacket(p, Main.mainServerSocket);
                } else {
                    Main.logger.debug("WARNING: this command can only be executed on a client");
                }
                break;
            case ("cthreads"):
                Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
                for (int i = 0; i < threadArray.length; i++) {
                    Main.logger.debug(threadArray[i].getName());
                }
                Main.logger.debug(threadArray.length + "");
                break;
        }
    }
}
