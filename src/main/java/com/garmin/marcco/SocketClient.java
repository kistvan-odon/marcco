package com.garmin.marcco;

import java.io.IOException;

public final class SocketClient {

    public static void main(String[] args) throws IOException {
        String address = "localhost";
//        String address = "10.66.173.86";
        int port = 31415;
        String teamName = "Probleme la Mansarda";
        MyClient client = new MyClient(address, port);
        new Thread(client).start();
        client.register(teamName);
    }
}

