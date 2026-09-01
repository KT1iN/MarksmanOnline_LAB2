package com.marksman.network;

import com.google.gson.Gson;
import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) {
        GameClient client = new GameClient();
        Gson gson = new Gson();

        try {
            client.connect("localhost", 5000);

            ClientCommand connectCommand = new ClientCommand(CommandType.CONNECT, "Иван");
            String json = gson.toJson(connectCommand);
            client.sendLine(json);

            System.out.println("Отправили: " + json);

            String responseJson = client.receiveLine();
            System.out.println("Получено от сервера: " + responseJson);

            ServerMessage response = gson.fromJson(responseJson, ServerMessage.class);
            System.out.println("Тип ответа: " + response.getType());
            System.out.println("Текст: " + response.getText());

        } catch (IOException e) {
            System.out.println("Не удалось подключиться: " + e.getMessage());
        }
    }
}