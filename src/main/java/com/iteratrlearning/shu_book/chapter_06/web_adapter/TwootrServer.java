package com.iteratrlearning.shu_book.chapter_06.web_adapter;

import com.iteratrlearning.shu_book.chapter_06.database.DatabaseTwootRepository;
import com.iteratrlearning.shu_book.chapter_06.database.DatabaseUserRepository;
import com.iteratrlearning.shu_book.chapter_06.TwootRepository;
import com.iteratrlearning.shu_book.chapter_06.Twootr;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

// {"cmd": "logon", "userName": "Joe", "password": "ahc5ez2aiV" }
// {"cmd": "logon", "userName": "John", "password": "ahc5ez2aiV" }
// {"cmd": "follow", "userName": "John" }
// {"cmd": "sendTwoot", "content": "Hello World" }

public class TwootrServer extends WebSocketServer {
    public static final int STATIC_PORT = 8000;
    public static final int WEBSOCKET_PORT = 9000;

    private static final String USER_NAME = "Joe";
    private static final String PASSWORD = "ahc5ez2aiV";
    private static final String OTHER_USER_NAME = "John";

    public static void main(String[] args) throws Exception {

        var websocketAddress = new InetSocketAddress("localhost", WEBSOCKET_PORT);
        var twootrServer = new TwootrServer(websocketAddress);
        twootrServer.start();

        System.setProperty("org.eclipse.jetty.LEVEL", "INFO");

        var context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setResourceBase(System.getProperty("user.dir") + "/src/main/webapp");
        context.setContextPath("/");

        ServletHolder staticContentServlet = new ServletHolder(
            "staticContentServlet", DefaultServlet.class);
        staticContentServlet.setInitParameter("dirAllowed", "true");
        context.addServlet(staticContentServlet, "/");

        var jettyServer = new Server(STATIC_PORT);
        jettyServer.setHandler(context);
        jettyServer.start();
        jettyServer.dumpStdErr();
        jettyServer.join();
    }

    private final TwootRepository twootRepository = new DatabaseTwootRepository();
    private final Twootr twootr = new Twootr(new DatabaseUserRepository(), twootRepository);
    private final Map<WebSocket, WebSocketEndPoint> socketToEndPoint = new HashMap<>();

    public TwootrServer(final InetSocketAddress address) {
        super(address, 1);

        twootr.onRegisterUser(USER_NAME, PASSWORD);
        twootr.onRegisterUser(OTHER_USER_NAME, PASSWORD);
    }

    @Override
    public void onOpen(final WebSocket webSocket, final ClientHandshake clientHandshake) {
        socketToEndPoint.put(webSocket, new WebSocketEndPoint(twootr, webSocket));
    }

    @Override
    public void onClose(final WebSocket webSocket, final int i, final String s, final boolean b) {
        socketToEndPoint.remove(webSocket);
    }

    @Override
    public void onMessage(final WebSocket webSocket, final String message) {
        var endPoint = socketToEndPoint.get(webSocket);
        try {
            endPoint.onMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(final WebSocket webSocket, final Exception e) {
        e.printStackTrace();
    }
}
