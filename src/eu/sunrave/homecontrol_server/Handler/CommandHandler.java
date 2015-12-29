package eu.sunrave.homecontrol_server.Handler;

import eu.sunrave.homecontrol_server.Main;

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
                        Main.socketServer.interrupt();
                        break;
                    case ("start"):
                        Main.socketServer.start();
                        break;
                }
                break;
        }
    }
}
