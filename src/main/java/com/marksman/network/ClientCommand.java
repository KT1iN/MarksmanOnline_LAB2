package com.marksman.network;

public class ClientCommand {
    private CommandType type;
    private String username;

    public ClientCommand(CommandType type, String username) {
        this.type = type;
        this.username = username;
    }

    public CommandType getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }


}