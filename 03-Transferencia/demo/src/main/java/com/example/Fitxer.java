package com.example;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Fitxer {
    private String nom;
    private byte[] contingut;

    public Fitxer(String nom) {
        this.nom = nom;
        this.contingut = null;
    }

    public byte[] getContingut() {
        try {
            File fitxer = new File(nom);
            if (!fitxer.exists()) {
                System.out.println("El fitxer no existeix: " + nom);
                return null;
            }
            Path path = Paths.get(nom);
            contingut = Files.readAllBytes(path);
            return contingut;
        } catch (Exception e) {
            System.out.println("Error en llegir el fitxer: " + e.getMessage());
            return null;
        }
    }

    public String getNom() {
        return nom;
    }
}
