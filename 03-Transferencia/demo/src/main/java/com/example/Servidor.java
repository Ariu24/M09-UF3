package com.example;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public final static int PORT = 9999;
    public final static String HOST = "localhost";
    private ServerSocket srvSocket = null;
    private Socket clientSocket = null;
    private boolean end = false;

    public Socket conecta() {
        try {
            System.out.println("Acceptant connexions en -> " + HOST + " : " + PORT);
            srvSocket = new ServerSocket(PORT);
            clientSocket = srvSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());
            return clientSocket;
        } catch (Exception e) {
            System.out.println("Error en connexió: " + e.getMessage());
            return null;
        }
    }

    public void enviaFitxers() {
        try (
            ObjectInputStream entrada = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream sortida = new ObjectOutputStream(clientSocket.getOutputStream())
        ) {
            while (!end) {
                String nomFitxer = (String) entrada.readObject();
                System.out.println("Petició rebuda per al fitxer: " + nomFitxer);
                if (nomFitxer.equals("sortir")) {
                    end = true;
                    continue;
                }
                Fitxer fitxer = new Fitxer(nomFitxer);
                byte[] contingut = fitxer.getContingut();
                if (contingut == null || contingut.length == 0) {
                    System.out.println("El fitxer no existeix o està buit: " + nomFitxer);
                    sortida.writeObject(new byte[0]);
                } else {
                    sortida.writeObject(contingut);
                    System.out.println("Fitxer enviat: " + nomFitxer);
                }
                sortida.flush();
            }
        } catch (Exception e) {
            //System.out.println("Error: " + e.getMessage());
            //e.printStackTrace();
        }
    }

    public void tanca(Socket socket) {
        try {
            socket.close();
            clientSocket.close();
            srvSocket.close();
            System.out.println("Servidor tancat");
        } catch (Exception e) {
            System.out.println("Error tancant: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor server = new Servidor();
        Socket socket = server.conecta();
        if (socket != null) {
            server.enviaFitxers();
            server.tanca(socket);
        }
    }
}
