package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {
    public int port = Servidor.PORT;
    public String host = Servidor.HOST;
    private Socket socket;
    private final String DIR_ARRIBADA = "C:/Temp";

    public void conecta() {
        try {
            socket = new Socket(host, port);
            System.out.println("Connectat al servidor: " + host + ":" + port);
        } catch (Exception e) {
            System.err.println("Error de connexió: " + e.getMessage());
        }
    }

    public void rebreFitxers() {
        if (socket != null) {
            try (
                BufferedReader teclat = new BufferedReader(new InputStreamReader(System.in));
                ObjectOutputStream sortida = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())
            ) {
                System.out.println("Nom del fitxer a rebre (escriu 'sortir' per finalitzar): ");
                String missatgeUser = teclat.readLine();

                File dir = new File(DIR_ARRIBADA);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                while (!missatgeUser.equals("sortir") && !missatgeUser.isEmpty()) {
                    sortida.writeObject(missatgeUser);
                    sortida.flush();

                    byte[] contingut = (byte[]) entrada.readObject();

                    if (contingut != null && contingut.length > 0) {
                        String nombreSoloArchivo = new File(missatgeUser).getName();
                        File fitxerDesti = new File(DIR_ARRIBADA + File.separator + nombreSoloArchivo);
                        FileOutputStream fos = new FileOutputStream(fitxerDesti);
                        fos.write(contingut);
                        fos.close();
                        System.out.println("Fitxer rebut i guardat a: " + fitxerDesti.getAbsolutePath());
                        System.out.println("Nom del fitxer a rebre (escriu 'sortir' per finalitzar): ");
                        missatgeUser = teclat.readLine();
                    } else {
                        System.out.println("El fitxer no existeix al servidor. Finalitzant el client.");
                        return;
                    }
                }

            } catch (Exception e) {
                System.err.println("Error rebent fitxers: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No hi ha connexió amb el servidor");
        }
    }

    public void tanca() {
        try {
            if (socket != null) socket.close();
            System.out.println("Client tancat");
        } catch (Exception e) {
            System.err.println("Error tancant client: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client cliente = new Client();
        cliente.conecta();
        cliente.rebreFitxers();
        cliente.tanca();
    }
}
