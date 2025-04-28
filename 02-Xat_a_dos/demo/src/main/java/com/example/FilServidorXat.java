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

public class FilServidorXat extends Thread {
    private Socket clientSocket;
    private BufferedReader entrada;
    private String ultimMissatge = "";
    private final String MSG_SORTIR = "sortir";
    
    public FilServidorXat(Socket socket) {
        try {
            this.clientSocket = socket;
            this.entrada = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            System.out.println("Error en crear el fil: " + e.getMessage());
        }
    }
    
    public String getUltimMissatge() {
        return ultimMissatge;
    }
    
    @Override
    public void run() {
        try {
            String missatge;
            
            while ((missatge = entrada.readLine()) != null) {
                ultimMissatge = missatge;
                // Mostrar el mensaje recibido inmediatamente
                System.out.println("Rebut: " + missatge);
                
                if (missatge.equalsIgnoreCase(MSG_SORTIR)) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error en el fil: " + e.getMessage());
        }
    }
}