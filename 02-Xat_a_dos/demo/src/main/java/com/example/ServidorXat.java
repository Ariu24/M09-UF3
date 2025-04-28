package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorXat {
    public final static int PORT = 9999;
    public final static String HOST = "localhost";
    public final String MSG_SORTIR = "sortir";
    private ServerSocket srvSocket = null;
    private Socket clientSocket = null;

    public void iniciarServidor() {
        try {
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
            srvSocket = new ServerSocket(PORT);
            clientSocket = srvSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String getNom() {
        String nom = "";
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            nom = entrada.readLine();
            System.out.println("Nom rebut: " + nom);
        } catch (Exception e) {
            System.out.println("Error obtenint nom: " + e.getMessage());
        }
        return nom;
    }

    public void pararServidor() {
        try {
            if (clientSocket != null) clientSocket.close();
            if (srvSocket != null) srvSocket.close();
            System.out.println("Servidor aturat.");
        } catch (Exception e) {
            System.out.println("Error tancant servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.iniciarServidor();
        String nomClient = servidor.getNom();
        try {
            PrintWriter sortida = new PrintWriter(servidor.clientSocket.getOutputStream(), true);
            FilServidorXat filServidor = new FilServidorXat(servidor.clientSocket);
            System.out.println("Fil de xat creat.");
            filServidor.start();
            System.out.println("Fil de " + nomClient + " iniciat");
            BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
            String missatge;
            while (true) {
                System.out.println("Missatge ('" + servidor.MSG_SORTIR + "' per tancar): ");
                missatge = consola.readLine();
                sortida.println(missatge);
                if (missatge.equalsIgnoreCase(servidor.MSG_SORTIR)) {
                    System.out.println("sortir");
                    break;
                }
            }
            System.out.println("Fil de xat finalitzat.");
            filServidor.join();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            servidor.pararServidor();
        }
    }
}