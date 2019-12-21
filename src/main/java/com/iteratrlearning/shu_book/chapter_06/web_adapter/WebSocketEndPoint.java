package com.iteratrlearning.shu_book.chapter_06.web_adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteratrlearning.shu_book.chapter_06.*;
import org.java_websocket.WebSocket;

import java.io.IOException;

class WebSocketEndPoint implements ReceiverEndPoint {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String CMD = "cmd";

    private final Twootr twootr;
    private final WebSocket webSocket;

    private SenderEndPoint senderEndPoint;

    WebSocketEndPoint(final Twootr twootr, final WebSocket webSocket) {
        this.twootr = twootr;
        this.webSocket = webSocket;
    }

    void onMessage(final String message) throws IOException {
        var json = mapper.readTree(message);
        var cmd = json.get(CMD).asText();
        switch (cmd) {
            case "logon": {
                var userName = json.get("userName").asText();
                var password = json.get("password").asText();
                var endPoint = twootr.onLogon(userName, password, this);
                if (!endPoint.isPresent()) {
                    senderEndPoint = null;
                    webSocket.close();
                } else {
                    this.senderEndPoint = endPoint.get();
                }
                break;
            }

            case "follow": {
                var userName = json.get("userName").asText();
                sendStatusUpdate(senderEndPoint.onFollow(userName).toString());
                break;
            }

            case "sendTwoot": {
                var id = json.get("id").asText();
                var content = json.get("content").asText();
                sendPosition(senderEndPoint.onSendTwoot(id, content));
                break;
            }

            case "deleteTwoot": {
                var id = json.get("id").asText();
                sendStatusUpdate(senderEndPoint.onDeleteTwoot(id).toString());
            }
        }
    }

    @Override
    public void onTwoot(final Twoot twoot) {
        webSocket.send(String.format(
            "{\"cmd\":\"twoot\", \"user\":\"%s\", \"msg\":\"%s\"}",
            twoot.getSenderId(),
            twoot.getContent()));
    }

    private void sendPosition(final Position position) {
        webSocket.send(String.format(
            "{\"cmd\":\"sent\", \"position\":%s}",
            position.getValue()));
    }

    private void sendStatusUpdate(final String status) {
        webSocket.send(String.format(
            "{\"cmd\":\"statusUpdate\", \"status\":\"%s\"}",
            status));
    }
}
