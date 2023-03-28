package com.example.widok;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.example.krypto.ElGamal;
import org.example.krypto.Keys;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;


public class HelloController {
    public TextField MOD;
    public Button keyGenerator;
    public TextField keyG;
    public TextField keyH;
    public TextField keyA;
    public TextArea tekst_jawny;
    public TextArea tekst_zaszyfrowany;

    private byte [] dane = new byte[0];
    BigInteger kluczA;
    BigInteger kluczG;
    BigInteger  kluczH;
    BigInteger kluczN;
    BigInteger kluczNn1;
    BigInteger[] szyfr;


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
        dane = HexFormat.of().parseHex(getKeyG() + getKeyH() + getKeyA()+getMOD());
        System.out.println(Arrays.toString(dane));
        System.out.print("87FC7EC047157F6D7AE682CF82B68CA9");
        zapis_do_pliku();
    }
    public void loadKey() {
        byte[] klucze = odczyt_z_pliku();
        ByteBuffer bb = ByteBuffer.wrap(klucze);
        bb.get(kluczG.toByteArray(), 0, kluczG.toByteArray().length);
        bb.get(kluczH.toByteArray(), 0, kluczH.toByteArray().length);
        bb.get(kluczA.toByteArray(), 0, kluczA.toByteArray().length);
        bb.get(kluczN.toByteArray(), 0, kluczN.toByteArray().length);
        keyG.setText(converter(kluczG.toByteArray()));
        keyH.setText(converter(kluczH.toByteArray()));
        keyA.setText(converter(kluczA.toByteArray()));
        MOD.setText(converter(kluczN.toByteArray()));
    }

    public void generateKeys() throws NoSuchAlgorithmException {
        Keys keys= new Keys();
        keys.generateKeys();
        kluczG = keys.getG();
        kluczH= keys.getH();
        kluczA = keys.getA();
        kluczN = keys.getN();
        kluczNn1 = keys.getNn1();
        keyG.setText(converter(kluczG.toByteArray()));
        keyH.setText(converter(kluczH.toByteArray()));
        keyA.setText(converter(kluczA.toByteArray()));
        MOD.setText(converter(kluczN.toByteArray()));


    }
    public void saveTextJ() {
        if(dane.length != 0)
            dane = HexFormat.of().parseHex(getTekst_jawny());
        else
            dane =  HexFormat.of().parseHex(converter(getTekst_jawny().getBytes()));
        System.out.println(dane);
        zapis_do_pliku();
        dane = new byte[0];
    }

    private String getTekst_jawny() {
        return tekst_jawny.getText();
    }

    public void saveTextZ() {
        dane = HexFormat.of().parseHex(getTekst_zaszyfrowany());
        zapis_do_pliku();
    }

    private String getTekst_zaszyfrowany() {
        return tekst_zaszyfrowany.getText();
    }

    public void loadTextJ() {
        tekst_jawny.setText(converter(odczyt_z_pliku()));
    }
    public void loadTextZ() {
        tekst_zaszyfrowany.setText(converter(odczyt_z_pliku()));
    }

    public void podpisz() throws NoSuchAlgorithmException {
        ElGamal gamal = new ElGamal();
        szyfr =  gamal.sign(getTekst_jawny().getBytes(),kluczNn1,kluczG,kluczA,kluczN);
        StringBuilder sb = new StringBuilder();
        for (BigInteger number : szyfr) {
            sb.append(number.toString());
        }
        String result = sb.toString();
        tekst_zaszyfrowany.setText( result);
    }

    public void zweryfikuj() throws NoSuchAlgorithmException {
        ElGamal gamal = new ElGamal();
        // pomysl jak zmienic Stringa na BIgInteger,bo tak przekazuje szyfr z bufora
        boolean flag = gamal.verify(szyfr,getTekst_zaszyfrowany().getBytes(),kluczN,kluczG,kluczH);
        if (flag) {
            Alert alert = new Alert(Alert.AlertType.NONE, "Podpis OK", ButtonType.OK);
            alert.setResizable(false);
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.NONE, "Sfałszowano!", ButtonType.OK);
            alert.setResizable(false);
            alert.showAndWait();
        }
    }
}