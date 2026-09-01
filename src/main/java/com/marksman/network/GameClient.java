package com.marksman.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * GameClient — отвечает за подключение к серверу и обмен текстовыми
 * строками (JSON) с ним. connect() открывает Socket по адресу и порту
 * сервера. sendLine() отправляет строку. receiveLine() блокирующе
 * ждёт и возвращает одну строку от сервера (аналогично readLine()
 * на сервере).
 */
public class GameClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Подключились к серверу " + host + ":" + port);
    }

    public void sendLine(String line) {
        out.println(line);
    }

    public String receiveLine() throws IOException {
        return in.readLine();
    }
}