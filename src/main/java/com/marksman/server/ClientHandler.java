package com.marksman.server;

import com.google.gson.Gson;
import com.marksman.network.ClientCommand;
import com.marksman.network.CommandType;
import com.marksman.network.ServerMessage;
import com.marksman.network.ServerMessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//выполняется для каждого клиента в отдельном потоке
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private GameServer gameServer;
    private String username;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public ClientHandler(Socket socket, GameServer gameServer) {
        this.clientSocket = socket;
        this.gameServer = gameServer;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            System.out.println("Обработчик клиента запущен");

            String receivedJson = in.readLine();
            System.out.println("Получено от клиента: " + receivedJson);

            Gson gson = new Gson();
            ClientCommand command = gson.fromJson(receivedJson, ClientCommand.class);

            if (command.getType() == CommandType.CONNECT) {
                handleConnect(command.getUsername(), gson);
            }

        } catch (IOException e) {
            System.out.println("Ошибка обработки клиента: " + e.getMessage());
        }
    }

    private void handleConnect(String requestedUsername, Gson gson) {
        boolean added = gameServer.addPlayer(requestedUsername, this);

        if (added) {
            this.username = requestedUsername;
            ServerMessage response = new ServerMessage(ServerMessageType.WELCOME, "Добро пожаловать, " + requestedUsername + "!");
            out.println(gson.toJson(response));
            System.out.println("Игрок добавлен: " + requestedUsername);
        } else {
            ServerMessage response = new ServerMessage(ServerMessageType.ERROR, "Имя уже занято");
            out.println(gson.toJson(response));
            System.out.println("Отклонено: имя занято — " + requestedUsername);
        }
    }
}

/**
 * ClientHandler — обработчик ОДНОГО подключённого клиента.
 * Работает в своём отдельном потоке (implements Runnable, метод run()),
 * поэтому сервер может одновременно обслуживать нескольких игроков.
 *
 * BufferedReader in  — читает текстовые строки от клиента (readLine()).
 * PrintWriter out    — отправляет текстовые строки клиенту (println()),
 *   autoFlush=true гарантирует немедленную отправку без ручного flush().
 *
 * gameServer — ссылка на общий сервер, нужна чтобы регистрировать
 *   игрока в общем списке (addPlayer) и снимать регистрацию при отключении.
 * username — имя этого игрока, заполняется после успешного CONNECT.
 *
 * handleConnect — обрабатывает команду CONNECT: пытается добавить
 *   игрока в общий список сервера; если имя свободно — шлёт клиенту
 *   WELCOME, если занято — ERROR. Логика вынесена в отдельный метод,
 *   чтобы на следующих этапах так же аккуратно добавить обработку
 *   READY, SHOOT, PAUSE, не раздувая run().
 */