package eu.sunrave.homecontrol_server;

import eu.sunrave.homecontrol_server.Handler.ClientPacketHandler;
import eu.sunrave.homecontrol_server.Handler.CommandHandler;
import eu.sunrave.homecontrol_server.Handler.ServerPacketHandler;
import eu.sunrave.homecontrol_server.Handler.varshandler;
import eu.sunrave.homecontrol_server.Libs.Functions;
import eu.sunrave.homecontrol_server.Libs.Logger;
import eu.sunrave.homecontrol_server.Libs.Packet;
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
    public static ServerPacketHandler serverPacketHandler;

    //Client
    public static Webserver webserver;
    public static SocketClient socketClient;
    public static Socket mainServerSocket;
    public static Thread clientThread;
    public static ClientPacketHandler clientPacketHandler;

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
            //Create a new thread and handle all of the server connections in there
            serverThread = new Thread(new SocketServer());
            serverThread.start();
            serverPacketHandler = new ServerPacketHandler();
        } else {
            //Create client packet handler & webserver
            clientPacketHandler = new ClientPacketHandler();
            webserver = new Webserver("Webserver");
            webserver.start();

            //Attempt to connect to the server
            try {
                mainServerSocket = new Socket(Resources.socketServerIP, Resources.SocketServerPort);
            } catch (Exception e) {
                logger.warning("couldn't connect to server");
            }
            //Creat a new thread and handle connection to the server in there
            socketClient = new SocketClient();
            clientThread = new Thread(socketClient);
            clientThread.start();

            //Send Registration To Server
            Packet p = new Packet(identifier, Packet.PacketType.registration);
            Functions.SendPacket(p, mainServerSocket);
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