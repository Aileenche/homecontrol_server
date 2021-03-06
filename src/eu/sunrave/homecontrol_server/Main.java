package eu.sunrave.homecontrol_server;

import eu.sunrave.homecontrol_server.Handler.*;
import eu.sunrave.homecontrol_server.Libs.ClientType;
import eu.sunrave.homecontrol_server.Libs.Logger;
import eu.sunrave.homecontrol_server.Threads.*;

import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {
    //Server
    public static Thread serverThread;
    public static ServerPacketHandler serverPacketHandler;
    public static ArrayList<Clients> clientHarv;
    public static ArrayList<Socket> waitlist;
    public static Thread waitlistHandler;
    public static Thread connectionHeatbeat;
    public static Socket waitingSocket;
    public static Thread autoFileUpdate;

    //Client
    public static Webserver webserver;
    public static SocketClient socketClient;
    public static Socket mainServerSocket;
    public static Thread clientThread;
    public static ClientPacketHandler clientPacketHandler;
    public static ClientType clientType;
    public static Thread mediaplayer;

    //All
    public static varshandler varshandler;
    public static Logger logger;
    public static CommandHandler Commands;

    //Globals
    public static boolean isStopped = false;
    public static boolean debugMode = true;
    public static boolean isserver = false;
    public static String identifier = "";
    public static boolean forceConnect = false;
    public static boolean testC = false;
    public static Path rundir;


    public static void main(String[] args) {
        rundir = Paths.get("/root/");//Paths.get(".").toAbsolutePath();
        logger = new Logger();
        varshandler = new varshandler();
        logger.init();
        Commands = new CommandHandler();

        for (int i = 0; i < args.length; i++) {
            if (args[i] != null) {
                if (args[i].equals("-force")) {
                    forceConnect = true;
                    args[i] = null;
                } else if (args[i].equals("-pi")) {
                    clientType = ClientType.pi;
                    args[i] = null;
                } else if (args[i].equals("-php")) {
                    clientType = ClientType.php;
                    args[i] = null;
                } else if (args[i].equals("-testc")) {
                    testC = true;
                    args[i] = null;
                } else if (args[i].substring(0, 12).equals("-identifier=")) {
                    identifier = args[i].substring(12, args[i].length());
                }
            }
        }
        if (testC) {
            Random rnd = new Random();
            int multi = rnd.nextInt(Integer.MAX_VALUE);
            identifier = identifier + "" + multi;
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
            serverThread = new Thread(new SocketServer(), "Connectionhandler");
            serverThread.start();
            serverPacketHandler = new ServerPacketHandler();
            clientHarv = new ArrayList<>();
            for (int i = 0; i < 500; i++) {
                clientHarv.add(i, null);
            }
            waitlist = new ArrayList<>();
            waitlistHandler = new Thread(new WaitListHandler(), "WaitListHandler");
            waitlistHandler.start();
            connectionHeatbeat = new Thread(new ConnectionHeatbeat(), "connectionHeatbeat");
            connectionHeatbeat.start();
            autoFileUpdate = new Thread(new AutoFileUpdate(), "autoFileUpdate");
            autoFileUpdate.start();
        } else {
            //Create client packet handler & webserver
            clientPacketHandler = new ClientPacketHandler();
            //webserver = new Webserver("Webserver");
            //webserver.start();
            //mediaplayer = new Thread(new MediaPlayer(),"MediaPlayer");
            //mediaplayer.start();

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
            String[] splitted = "bc SERVER IS RESTARTING!".split(" ");
            Commands.make(splitted);
            serverThread.interrupt();
        } else {
            clientThread.interrupt();
        }
        Main.logger.notice("Shutdown Server...");
        if (!isserver && webserver.isRunning) {
            webserver.stop();
        }
        System.exit(0);
    }
}