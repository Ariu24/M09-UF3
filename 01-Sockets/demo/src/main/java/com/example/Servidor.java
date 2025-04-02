package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public final static int PORT = 7777;
    public final static String HOST = "localhost";
    private ServerSocket srvSocket = null;
    private Socket clientSocket = null;

    private boolean end = false;

    public void Conecta() {
        try {
            System.out.println("Servidor en marxa a " + HOST + " : " + PORT);
            System.out.println("Esperant connexions a " + HOST + " : " + PORT);
            srvSocket = new ServerSocket(PORT);
            clientSocket = srvSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void repDades() {
        try {
            BufferedReader missatge = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String missatgeC;
            while ((missatgeC = missatge.readLine()) != null) {
                System.out.println("Rebut: " + missatgeC);
            }
            missatge.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void tanca() {
        try {
            clientSocket.close();
            srvSocket.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    public static void main(String[] args) {
        Servidor server = new Servidor();
        server.Conecta();
        server.repDades();
        server.tanca();
    }

}
