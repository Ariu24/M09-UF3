package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private PrintWriter sortida;
    private BufferedReader entrada;
    private final String MSG_SORTIR = "sortir";
    
    public void connecta(String host, int port) {
        try {
            socket = new Socket(host, port);
            System.out.println("Client connectat a " + host + ":" + port);
            sortida = new PrintWriter(socket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Flux d'entrada i sortida creat.");
            
        } catch (Exception e) {
            System.out.println("Error connectant: " + e.getMessage());
        }
    }
    
    public void enviarMissatge(String missatge) {
        sortida.println(missatge);
    }
    
    public void tancarClient() {
        try {
            System.out.println("Tancant client...");
            if (sortida != null) sortida.close();
            if (entrada != null) entrada.close();
            if (socket != null) socket.close();
            System.out.println("Client tancat.");
        } catch (Exception e) {
            System.out.println("Error tancant client: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta("localhost", 9999);
        try {
            System.out.println("Rebut: Escriu el teu nom:");
            Scanner scanner = new Scanner(System.in);
            String nom = scanner.nextLine();
            client.enviarMissatge(nom);
            System.out.println("Enviant missatge: " + nom);
            FilLectorCX filLector = new FilLectorCX(client.socket.getInputStream());
            System.out.println("Fil de lectura iniciat");
            filLector.start();
            String missatge;
            while (true) {
                System.out.println("Missatge ('" + client.MSG_SORTIR + "' per tancar): ");
                missatge = scanner.nextLine();
                client.enviarMissatge(missatge);
                System.out.println("Enviant missatge: " + missatge);
                if (missatge.equalsIgnoreCase(client.MSG_SORTIR)) {
                    System.out.println("sortir");
                    break;
                }
            }
            scanner.close();
            client.tancarClient();
            System.out.println("El servidor ha tancat la connexió.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}