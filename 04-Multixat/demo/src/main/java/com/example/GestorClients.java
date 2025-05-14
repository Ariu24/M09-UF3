package com.example;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class GestorClients extends Thread {
    private Socket client;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServidorXat servidorXat;
    private String nom;
    private boolean sortir;
    
    public GestorClients(Socket client, ServidorXat servidorXat) {
        this.client = client;
        this.servidorXat = servidorXat;
        this.sortir = false;
        
        try {
            this.oos = new ObjectOutputStream(client.getOutputStream());
            this.ois = new ObjectInputStream(client.getInputStream());
        } catch (Exception e) {
            System.out.println("Error inicialitzant streams: " + e.getMessage());
        }
    }
    
    public String getNom() {
        return nom;
    }
    
    @Override
    public void run() {
        try {
            String missatge;
            while (!sortir) {
                missatge = (String) ois.readObject();
                processaMissatge(missatge);
            }
        } catch (Exception e) {
            System.out.println("Error en la comunicació amb el client: " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (Exception e) {
                System.out.println("Error tancant el socket: " + e.getMessage());
            }
        }
    }
    
    public void enviarMissatge(String remitent, String missatge) {
        try {
            oos.writeObject(missatge);
            oos.flush();
        } catch (Exception e) {
            System.out.println("Error enviant missatge a " + nom + ": " + e.getMessage());
        }
    }
    
    public void processaMissatge(String missatgeCru) {
        String codi = Missatge.getCodiMissatge(missatgeCru);
        String[] parts = Missatge.getPartsMissatge(missatgeCru);
        
        if (codi == null || parts == null) {
            return;
        }
        
        switch (codi) {
            case Missatge.CODI_CONECTAR:
                if (parts.length > 1) {
                    this.nom = parts[1];
                    servidorXat.afegirClient(this);
                }
                break;
                
            case Missatge.CODI_SORTIR_CLIENT:
                servidorXat.eliminarClient(nom);
                sortir = true;
                break;
                
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidorXat.finalitzarXat();
                break;
                
            case Missatge.CODI_MSG_PERSONAL:
                if (parts.length > 2) {
                    String destinatari = parts[1];
                    String missatge = parts[2];
                    servidorXat.enviarMissatgePersonal(destinatari, nom, missatge);
                }
                break;
                
            case Missatge.CODI_MSG_GRUP:
                if (parts.length > 1) {
                    servidorXat.enviarMissatgeGrup(parts[1]);
                }
                break;
                
            default:
                System.out.println("Codi d'operació desconegut: " + codi);
                break;
        }
    }
}