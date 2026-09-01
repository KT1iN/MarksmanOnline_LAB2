package com.marksman.network;

public class ServerMessage {
    private ServerMessageType type;
    private String text;

    public ServerMessage(ServerMessageType type, String text) {
        this.type = type;
        this.text = text;
    }

    public ServerMessageType getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}