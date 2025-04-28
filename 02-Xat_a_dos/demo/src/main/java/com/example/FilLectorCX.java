package com.example;

import java.io.*;

public class FilLectorCX extends Thread {
    private BufferedReader entrada;
    
    public FilLectorCX(InputStream inputStream) {
        try {
            this.entrada = new BufferedReader(new InputStreamReader(inputStream));
        } catch (Exception e) {
            System.out.println("Error en crear el fil lector: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            String missatge;
            
            while ((missatge = entrada.readLine()) != null) {
                System.out.println("Rebut: " + missatge);
            }
        } catch (Exception e) {
        }
    }
}