package com.example.widok;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;


public class HelloController {
    public TextField MOD;
    public Button keyGenerator;
    public TextField keyG;
    public TextField keyH;
    public TextField keyA;
    private byte [] dane = new byte[0];


    public String getKeyG() {
        return keyG.getText();
    }

    public String getKeyH() {
        return keyH.getText();
    }

    public String getKeyA() {
        return keyA.getText();
    }

    public String getMOD() {
        return MOD.getText();
    }

    public String converter(byte [] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
    public void zapis_do_pliku() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapis do pliku");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*"));
        File fileToSave = fileChooser.showSaveDialog(Scene.getStage());
        if (fileToSave != null) {
            try {
                OutputStream fos = new FileOutputStream(fileToSave);
                fos.write(dane);
                fos.close();
                System.out.println("Zapisano pomyślnie!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Zapis został przerwany");
        }
    }
    public byte[] odczyt_z_pliku() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Odczyt z pliku");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Wszystkie plik", "*.*"));
        File loadFile = fileChooser.showOpenDialog(Scene.getStage());
        if (loadFile != null) {
            try {
                FileInputStream fis = new FileInputStream(loadFile);
                int size = fis.available();
                dane = new byte [size];
                int bytes_read = fis.read(dane);
                if(bytes_read == 0) {
                    System.out.println("Plik jest pusty!");
                }
                else {
                    System.out.println("Odczytano pomyślnie!");
                }
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Odczyt został przerwany");
        }
        return dane;
    }
    public void saveKey() {
        dane = HexFormat.of().parseHex(getKeyG() + getKeyH() + getKeyH());
        System.out.println(Arrays.toString(dane));
        System.out.print("87FC7EC047157F6D7AE682CF82B68CA9");
        zapis_do_pliku();
    }
//    public void saveTextJ() {
//        if(dane.length != 0)
//            dane = HexFormat.of().parseHex(getTekst_jawny());
//        else
//            dane =  HexFormat.of().parseHex(converter(getTekst_jawny().getBytes()));
//        zapis_do_pliku();
//        dane = new byte[0];
//    }
//    public void saveTextZ() {
//        dane = HexFormat.of().parseHex(getTekst_zaszyfrowany());
//        zapis_do_pliku();
//    }
    public void loadKey() {
        byte[] klucze = odczyt_z_pliku();
        ByteBuffer bb = ByteBuffer.wrap(klucze);

        byte[] kluczG = new byte[16];
        byte[] kluczH = new byte[16];
        byte[] kluczA = new byte[16];
        bb.get(kluczG, 0, kluczG.length);
        bb.get(kluczH, 0, kluczH.length);
        bb.get(kluczA, 0, kluczA.length);
        keyG.setText(converter(kluczG));
        keyH.setText(converter(kluczH));
        keyA.setText(converter(kluczA));
    }
//    public void loadTextJ() {
//        tekst_jawny.setText(converter(odczyt_z_pliku()));
//    }
//    public void loadTextZ() {
//        tekst_zaszyfrowany.setText(converter(odczyt_z_pliku()));
//    }
}