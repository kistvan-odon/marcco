package com.garmin.marcco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garmin.marcco.model.Dumpster;
import com.garmin.marcco.model.MarccoMessage;
import com.garmin.marcco.model.MessageType;
import com.garmin.marcco.model.Trash;

import static com.garmin.marcco.Utils.changeMappingToList;
import static com.garmin.marcco.Utils.getAllDumpstersFromMatrix;
import static com.garmin.marcco.Utils.makeAction;

class MyClient implements Runnable {

    private final ObjectMapper objectMapper = new ObjectMapper();
    public static String botId;
    public static int maxVol;
    private final Robot robot = new Robot();
    char[][]board;
    List<Dumpster> dumpsterList;
    private final Socket connection;
    private boolean connected = true;
    private final BufferedReader buffReader;
    private final OutputStream writer;

    public MyClient(String address, int port) {
        this.connection = initConnection(address, port);
        this.buffReader = initReader();
        this.writer = initWriter();
    }

    @Override
    public void run() {
        try {
            read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(String teamName) {
        String registerMsg = "register: " + teamName;
        registerMsg = "{ \"get_team_id_for\" :\"" + teamName + "\"}";

        try {
            this.sendMessage(registerMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) throws IOException {
        System.out.println("Sending message: " + message);
        int msgLen = message.length();
        String hex = String.format("%04X", msgLen);
        String fullMsg = hex + message + "\0";

        byte[] byteArray = fullMsg.getBytes(StandardCharsets.UTF_8);
        writer.write(byteArray);
    }

    private void read() throws IOException {
        char c;
        while (true) {
            StringBuilder message = new StringBuilder();
            do {
                int r = this.buffReader.read();
                if (r < 0 || r > 65535) {
                    throw new IllegalArgumentException("Invalid Char code: " + r);
                }
                c = (char) r;
                //println("char: " + c)
                message.append(c);

            } while (c != (char) 0);
            String stringMessage = message.toString();
            System.out.println("Message received: " + stringMessage);

            String json = stringMessage.substring(stringMessage.indexOf("{"), stringMessage.lastIndexOf("}") + 1);
            if (botId == null) {
                Map<String, String> messageMap = objectMapper.readValue(json, new TypeReference<Map<String, String>>() {
                });
                botId = messageMap.get("bot_id");
            } else {
                if (json.contains("\"err\"")) {
                    System.err.println("ERROR: " + json);
                } else {
                    MarccoMessage marccoMessage = objectMapper.readValue(json, MarccoMessage.class);
                    System.out.println("marccoMessage: " + marccoMessage);
                    if (marccoMessage.gameBoard != null) {
                        marccoMessage.messageType = MessageType.GAME_BOARD;
                        maxVol = marccoMessage.maxVol;
                        this.board=marccoMessage.gameBoard;
                        dumpsterList=getAllDumpstersFromMatrix(this.board);
                    } else {
                        marccoMessage.messageType = MessageType.OBJECTS;
                    }
                    robot.updateLocation(marccoMessage.row,marccoMessage.col);
                    List<Trash> trashes=changeMappingToList(marccoMessage.objects);
                    String resp=makeAction(robot,this.board,trashes,botId,this.dumpsterList);
                    if (resp == null) {
                        continue;
                    }
                    this.sendMessage(resp);

                    //TODO: do smth with message
                }
            }
        }
    }

    public void close() throws IOException {
        this.connected = false;
        this.connection.close();
    }

    private Socket initConnection(String address, int port) {
        try {
            System.out.println("Connected to server at " + address + " on port " + port);
            return new Socket(address, port);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private OutputStream initWriter() {
        try {
            return this.connection.getOutputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    private BufferedReader initReader() {
        try {
            return new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
        } catch (IOException ex) {
            return null;
        }
    }
}
