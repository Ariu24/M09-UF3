package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
    public int port = Servidor.PORT;
    public String host = Servidor.HOST;
    private Socket socket;
    private PrintWriter out;

    public void conecta() {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connectat al servidor: " + host + ":" + port);
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }

    public void enviaMensage(String msg) {
        if (out != null) {
            System.out.println("Enviat al servidor: " + msg);
            out.println(msg);
        }
    }

    public void tanca() {
        try {
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Client tancat");
        } catch (Exception e) {
            System.err.println("Error tancant client: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client cliente = new Client();
        cliente.conecta();
        cliente.enviaMensage("Prova d'enviament 1");
        cliente.enviaMensage("Prova d'enviament 2");
        cliente.enviaMensage("Adéu!");
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Prem Enter per tancar el client...");
            bf.readLine();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        cliente.tanca();
    }
}