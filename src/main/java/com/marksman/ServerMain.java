package com.marksman;

import com.marksman.server.GameServer;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        try {
            GameServer server = new GameServer();
            server.start();
        } catch (IOException e) {
            System.out.println("Ошибка запуска сервера: " + e.getMessage());
        }
    }
}