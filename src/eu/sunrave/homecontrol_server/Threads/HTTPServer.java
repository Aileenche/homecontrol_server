package eu.sunrave.homecontrol_server.Threads;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import eu.sunrave.homecontrol_server.Main;
import eu.sunrave.homecontrol_server.Resources;

import java.io.*;
import java.net.InetSocketAddress;

/**
 * Created by Admin on 29.12.2015.
 */
public class HTTPServer {
    public static HttpServer server;

    public static void HTTPServer() throws Exception {
    }

    public static void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(Resources.WebServerPort), 0);
            server.createContext("/info", new InfoHandler());
            server.createContext("/", new MainHandler());
            server.createContext("/get", new GetHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (Exception e) {
            Main.logger.error("Webserver konnte nicht gestartet werden! ");
            Main.logger.error("" + e);
        }
    }

    public static void stop() {
        try {
            server.stop(0);
        } catch (Exception e) {
        }
    }

    public static class InfoHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            Main.logger.debug("" + t.getProtocol());
            Main.logger.debug("" + t.getRequestMethod());
            String response = "<html><head></head><body>TEST</body></html>";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static class MainHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String response = "Use /get to download a PDF";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static class GetHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {

            // add the required response header for a PDF file
            Headers h = t.getResponseHeaders();
            h.add("Content-Type", "application/pdf");

            // a PDF (you provide your own!)
            File file = new File("c:/temp/doc.pdf");
            byte[] bytearray = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(bytearray, 0, bytearray.length);

            // ok, we are ready to send the response.
            t.sendResponseHeaders(200, file.length());
            OutputStream os = t.getResponseBody();
            os.write(bytearray, 0, bytearray.length);
            os.close();
        }
    }
}