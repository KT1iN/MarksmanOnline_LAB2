package com.marksman.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    private static final int PORT = 5000;
    private ServerSocket serverSocket;
    private Map<String, ClientHandler> players = new ConcurrentHashMap<>(); //список игроков

    public GameServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public void start() throws IOException {
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Подключился новый клиент");

            ClientHandler handler = new ClientHandler(clientSocket, this);
            Thread thread = new Thread(handler);
            thread.start();
        }
    }

    public synchronized boolean addPlayer(String username, ClientHandler handler) {
        if (players.containsKey(username)) {
            return false;
        }
        players.put(username, handler);
        return true;
    }

    public void removePlayer(String username) {
        players.remove(username);
    }
}

/**
 * GameServer — открывает "дверь" (ServerSocket) на фиксированном порту
 * и в бесконечном цикле ждёт новых подключений (accept()).
 * Как только кто-то подключился — не общается с ним сам, а передаёт
 * сокет в ClientHandler и запускает его в отдельном потоке (Thread),
 * чтобы сразу же вернуться к ожиданию следующего клиента.
 *
 * players — общий список подключённых игроков (имя -> его обработчик),
 * ConcurrentHashMap используется вместо обычного HashMap, потому что
 * к нему одновременно обращаются разные потоки (разные ClientHandler),
 * и обычная коллекция могла бы повредиться при параллельном доступе.
 *
 * addPlayer помечен synchronized — чтобы проверка "имя свободно"
 * и последующее добавление выполнялись как единое целое, без риска,
 * что два клиента с одинаковым именем "проскочат" проверку одновременно.
 */