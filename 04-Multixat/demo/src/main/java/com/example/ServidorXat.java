package com.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {
    public final static int PORT = 9999;
    public final static String HOST = "localhost";
    public final static String MSG_SORTIR = "sortir";
    
    private Hashtable<String, GestorClients> clients;
    private boolean sortir;
    private ServerSocket serverSocket;
    
    public ServidorXat() {
        this.clients = new Hashtable<>();
        this.sortir = false;
    }
    
    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        } catch (Exception e) {
            System.out.println("Error iniciant el servidor: " + e.getMessage());
        }
    }
    
    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Servidor tancat");
            }
        } catch (Exception e) {
            System.out.println("Error tancant el servidor: " + e.getMessage());
        }
    }
    
    public void finalitzarXat() {
        enviarMissatgeGrup(MSG_SORTIR);
        System.out.println("Tancant tots els clients.");
        System.out.println("DEBUG: multicast sortir");
        clients.clear();
        sortir = true;
        pararServidor();
        System.exit(0);
    }
    
    public void afegirClient(GestorClients gestorClient) {
        String nomClient = gestorClient.getNom();
        clients.put(nomClient, gestorClient);
        System.out.println(nomClient + " connectat.");
        enviarMissatgeGrup("Entra: " + nomClient);
    }
    
    public void eliminarClient(String nomClient) {
        if (nomClient != null && clients.containsKey(nomClient)) {
            clients.remove(nomClient);
            System.out.println(nomClient + " desconnectat.");
        }
    }
    
    public void enviarMissatgeGrup(String missatge) {
        System.out.println("DEBUG: multicast " + missatge);
        String missatgeFormat = Missatge.getMissatgeGrup(missatge);
        for (GestorClients client : clients.values()) {
            client.enviarMissatge("", missatgeFormat);
        }
    }
    
    public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        if (clients.containsKey(destinatari)) {
            String missatgeFormat = Missatge.getMissatgePersonal(remitent, missatge);
            clients.get(destinatari).enviarMissatge(remitent, missatgeFormat);
            System.out.println("Missatge personal per (" + destinatari + ") de (" + remitent + "): " + missatge);
        } else {
            System.out.println("El destinatari " + destinatari + " no existeix.");
        }
    }
    
    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
        
        while (!servidor.sortir) {
            try {
                Socket clientSocket = servidor.serverSocket.accept();
                System.out.println("Client connectat: " + clientSocket.getInetAddress());
                GestorClients gestorClient = new GestorClients(clientSocket, servidor);
                gestorClient.start();
            } catch (Exception e) {
                if (!servidor.sortir) {
                    System.out.println("Error acceptant connexió: " + e.getMessage());
                }
            }
        }
        
        servidor.pararServidor();
    }
}