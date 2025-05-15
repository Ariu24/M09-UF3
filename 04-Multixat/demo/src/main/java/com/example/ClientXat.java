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
    @Override
    public void run() {
        try {
            System.out.println("DEBUG: Iniciant rebuda de missatges...");
            ois = new ObjectInputStream(socket.getInputStream());
            
            while (!sortir) {
                String missatgeCru = (String) ois.readObject();
                String codi = Missatge.getCodiMissatge(missatgeCru);
                String[] parts = Missatge.getPartsMissatge(missatgeCru);
                
                if (codi == null || parts == null) {
                    continue;
                }
                
                switch (codi) {
                    case Missatge.CODI_SORTIR_TOTS:
                        sortir = true;
                        break;
                        
                    case Missatge.CODI_MSG_PERSONAL:
                        if (parts.length > 2) {
                            String remitent = parts[1];
                            String missatge = parts[2];
                            System.out.println("Missatge de (" + remitent + "): " + missatge);
                        }
                        break;
                        
                    case Missatge.CODI_MSG_GRUP:
                        if (parts.length > 1) {
                            System.out.println("Missatge de grup: " + parts[1]);
                        }
                        break;
                        
                    default:
                        System.out.println("Codi d'operació desconegut: " + codi);
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error rebent missatge. Sortint...");
        }
    }
    
    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }
    
    public String getLinea(Scanner scanner, String missatge, boolean obligatori) {
        System.out.print(missatge);
        String linea = scanner.nextLine().trim();
        
        while (obligatori && linea.isEmpty()) {
            System.out.println("El camp és obligatori.");
            System.out.print(missatge);
            linea = scanner.nextLine().trim();
        }
        
        return linea;
    }
    
    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta();
        
        client.start();
        
        client.ajuda();
        
        Scanner scanner = new Scanner(System.in);
        String opcio;
        
        while (!client.sortir) {
            opcio = scanner.nextLine().trim();
            
            if (opcio.isEmpty()) {
                client.sortir = true;
                break;
            }
            
            switch (opcio) {
                case "1":
                    String nom = client.getLinea(scanner, "Introdueix el nom: ", true);
                    String missatgeConnectar = Missatge.getMissatgeConectar(nom);
                    client.enviarMissatge(missatgeConnectar);
                    break;
                    
                case "2":
                    String destinatari = client.getLinea(scanner, "Destinatari:: ", true);
                    String missatgePersonal = client.getLinea(scanner, "Missatge a enviar: ", true);
                    String missatgePerEnviar = Missatge.getMissatgePersonal(destinatari, missatgePersonal);
                    client.enviarMissatge(missatgePerEnviar);
                    break;
                    
                case "3":
                    String missatgeGrup = client.getLinea(scanner, "Missatge a enviar: ", true);
                    String missatgeGrupPerEnviar = Missatge.getMissatgeGrup(missatgeGrup);
                    client.enviarMissatge(missatgeGrupPerEnviar);
                    break;
                    
                case "4":
                    String missatgeSortir = Missatge.getMissatgeSortirClient("Adéu");
                    client.enviarMissatge(missatgeSortir);
                    client.sortir = true;
                    break;
                    
                case "5":
                    String missatgeSortirTots = Missatge.getMissatgeSortirTots("Adéu");
                    client.enviarMissatge(missatgeSortirTots);
                    client.sortir = true;
                    break;
                    
                default:
                    System.out.println("Opció no vàlida");
                    break;
            }
            
            if (!client.sortir) {
                client.ajuda();
            }
        }
        scanner.close();
        client.tancarClient();
        System.exit(0);
    }
}