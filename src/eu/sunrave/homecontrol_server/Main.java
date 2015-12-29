package eu.sunrave.homecontrol_server;

import eu.sunrave.homecontrol_server.Handler.CommandHandler;
import eu.sunrave.homecontrol_server.Handler.varshandler;
import eu.sunrave.homecontrol_server.Libs.Logger;
import eu.sunrave.homecontrol_server.Threads.SocketServer;
import eu.sunrave.homecontrol_server.Threads.Webserver;

import java.util.Scanner;

public class Main {
    public static Logger logger;
    public static varshandler varshandler;
    public static CommandHandler Commands;
    public static Webserver webserver;
    public static Thread socketServer;
    public static boolean isStopped = false;
    public static boolean debugMode = true;

    public static void main(String[] args) {

        logger = new Logger();
        varshandler = new varshandler();
        logger.init();
        Commands = new CommandHandler();
/////////////////////////////////////////////////////
        webserver = new Webserver("Webserver");
        webserver.start();

        socketServer = new Thread(new SocketServer());
        socketServer.start();


        Scanner scanner = new Scanner(System.in);

        while (isStopped == false) {
            String command = scanner.nextLine();
            String[] splitted = command.split(" ");
            if (!command.contains(" ")) {
                splitted = new String[2];
                splitted[0] = command;
                splitted[1] = "0";
            }
            Commands.make(splitted);
            logger.debug("Typed Command is [" + splitted[0] + "]");
        }
        Main.logger.notice("Shutdown Server...");
        webserver.stop();
        System.exit(0);
    }
}