package com.example;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat extends Thread {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean sortir;
    

    
    public ClientXat() {
        this.sortir = false;
    }



    public void connecta() {
        try {
            socket = new Socket(ServidorXat.HOST, ServidorXat.PORT);
            System.out.println("Client connectat a " + ServidorXat.HOST + ":" + ServidorXat.PORT);
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (Exception e) {
            System.out.println("Error connectant al servidor: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void enviarMissatge(String missatge) {
        try {
            if (oos != null) {
                System.out.println("Enviant missatge: " + missatge);
                oos.writeObject(missatge);
                oos.flush();
            } else {
                System.out.println("oos null. Sortint...");
                sortir = true;
            }
        } catch (Exception e) {
            System.out.println("Error enviant missatge: " + e.getMessage());
            sortir = true;
        }
    }
    
    public void tancarClient() {
        System.out.println("Tancant client...");
        sortir = true;
        try {
            if (ois != null) {
                ois.close();
                System.out.println("Flux d'entrada tancat.");
            }
            if (oos != null) {
                oos.close();
                System.out.println("Flux de sortida tancat.");
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            System.out.println("Error tancant client: " + e.getMessage());
        }
    }
    
}