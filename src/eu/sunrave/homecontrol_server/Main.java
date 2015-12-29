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
    public static Thread socketServer;

    //Client
    public static Webserver webserver;
    public static SocketClient socketClient;
    public static Socket clientSocket;

    //All
    public static varshandler varshandler;
    public static Logger logger;
    public static CommandHandler Commands;


    //Globals
    public static boolean isStopped = false;
    public static boolean debugMode = true;
    public static boolean isserver = false;


    public static void main(String[] args) {

        logger = new Logger();
        varshandler = new varshandler();
        logger.init();
        Commands = new CommandHandler();

        try {
            if (args[0].indexOf("-server") != -1) {
                isserver = true;
            }
        } catch (Exception e) {

        }

        if (isserver) {
            socketServer = new Thread(new SocketServer());
            socketServer.start();

        } else {

            webserver = new Webserver("Webserver");
            webserver.start();

            try {
                clientSocket = new Socket(Resources.socketServerIP, Resources.SocketServerPort);
            } catch (Exception e) {
                logger.debug("ERROR: couldn't connect to server");
            }

            socketClient = new SocketClient();
            Thread t = new Thread(socketClient);
            t.start();

            Functions.SendMessage("Aileen HelloWorld", clientSocket);
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

        Main.logger.notice("Shutdown Server...");
        webserver.stop();
        System.exit(0);
    }
}