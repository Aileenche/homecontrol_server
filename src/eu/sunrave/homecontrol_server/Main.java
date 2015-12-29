package eu.sunrave.homecontrol_server;

import eu.sunrave.homecontrol_server.Handler.CommandHandler;
import eu.sunrave.homecontrol_server.Handler.varshandler;
import eu.sunrave.homecontrol_server.Libs.Functions;
import eu.sunrave.homecontrol_server.Libs.Logger;
import eu.sunrave.homecontrol_server.Threads.Clients;
import eu.sunrave.homecontrol_server.Threads.SocketClient;
import eu.sunrave.homecontrol_server.Threads.SocketServer;
import eu.sunrave.homecontrol_server.Threads.Webserver;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    //Server
    public static ArrayList<Clients> clients;
    public static ArrayList<Socket> clientSockets;
    public static Thread serverThread;

    //Client
    public static Webserver webserver;
    public static SocketClient socketClient;
    public static Socket mainServerSocket;
    public static Thread clientThread;

    //All
    public static varshandler varshandler;
    public static Logger logger;
    public static CommandHandler Commands;


    //Globals
    public static boolean isStopped = false;
    public static boolean debugMode = true;
    public static boolean isserver = false;
    public static String identifier = "";


    public static void main(String[] args) {

        logger = new Logger();
        varshandler = new varshandler();
        logger.init();
        Commands = new CommandHandler();

        for (int i = 0; i < args.length; i++) {
            if (args[i].substring(0, 12).equals("-identifier=")) {
                identifier = args[i].substring(12, args[i].length());
            }
        }
        if (identifier.equals("")) {
            logger.critical("No Unique Identifier found! Please append [-identifier=YOURNAME] to the startup");
        } else if (identifier.equals("~server~")) {
            isserver = true;
            logger.notice("ServerMode engaged!");
        } else {
            logger.notice("Hello, i'm " + identifier);
        }

        if (isserver) {
            serverThread = new Thread(new SocketServer());
            serverThread.start();

        } else {

            webserver = new Webserver("Webserver");
            webserver.start();

            try {
                mainServerSocket = new Socket(Resources.socketServerIP, Resources.SocketServerPort);
            } catch (Exception e) {
                logger.debug("ERROR: couldn't connect to server");
            }

            socketClient = new SocketClient();
            clientThread = new Thread(socketClient);
            clientThread.start();

            Functions.SendMessage("HelloWorld", mainServerSocket);
        }


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
        shutdown();
    }

    public static void shutdown() {
        if (isserver) {
            serverThread.interrupt();
        } else {
            clientThread.interrupt();
        }
        Main.logger.notice("Shutdown Server...");
        if (!isserver) {
            webserver.stop();
        }
        System.exit(0);
    }
}