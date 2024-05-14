package com.nin0dev.nyaboom.nyapp;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class Client extends WebSocketClient {

    public Client(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public Client(URI serverURI) {
        super(serverURI);
    }

    public Client(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Yapper.debug("WS connection opened");
    }

    @Override
    public void onMessage(String message) {
        JSONObject jsonMessage = new JSONObject(message);
        JSONObject data = jsonMessage.getJSONObject("d");
        int opCode = jsonMessage.getInt("op");
        if(opCode == 10) {
            // Hello event
            Yapper.info(String.format("Discord said hi, heartbeat interval is %s ms%n", jsonMessage.getJSONObject("d").getInt("heartbeat_interval")));
            new Timer().schedule(new TimerTask() {
                public void run()  {
                    Yapper.debug("Sending heartbeat");
                    send("{\"op\":1,\"d\":null}");
                }
            }, 0, data.getInt("heartbeat_interval"));
            // Identify
            send(String.format("""
                    {
                        "op": 2,
                        "d": {
                            "token": "%s",
                            "properties": {
                                "os": "JVM",
                                "browser": "Java",
                                "device": "Java"
                            },
                            "intents": 33280
                        }
                    }
                    """, Statics.token
            ));
        }
        if(opCode == 11) {
            // Heartbeat ack event
            Yapper.debug("Heartbeat was acknowledged");
        }
        if(opCode == 1) {
            // Heartbeat request
            Yapper.debug("Sending heartbeat (requested)");
            send("{\"op\":1,\"d\":null}");
        }
        if(opCode == 0) {
            if(Objects.equals(jsonMessage.getString("t"), "READY")) {
                Yapper.info(String.format("Logged in as %s#%s!", data.getJSONObject("user").getString("username"), data.getJSONObject("user").getString("discriminator")));
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }

    public static void main(String[] args) throws URISyntaxException {
        Yapper.info("Hi!!!");
        Client c = new Client(new URI(
                "wss://gateway.discord.gg/?v=10&encoding=json"));
        c.connect();
    }
}