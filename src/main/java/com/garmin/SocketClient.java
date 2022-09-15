package com.garmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class SocketClient {

    public static void main(String[] args) {
        String address = "localhost";
//        String address = "10.66.173.86";
        int port = 31415;
        String teamName = "Probleme la Mansarda";
        MyClient client = new MyClient(address, port);
        new Thread(client).start();
        client.register(teamName);
    }
}

