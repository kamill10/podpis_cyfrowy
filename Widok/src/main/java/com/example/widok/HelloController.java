package com.example.widok;

import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.example.krypto.ElGamal;
import org.example.krypto.Keys;
import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;



public class HelloController {
    public TextField MOD;
    public Button keyGenerator;
    public TextField keyG;
    public TextField keyH;
    public TextField keyA;
    public TextArea tekst_jawny;
    public TextArea tekst_zaszyfrowany;
    private byte[] dane = new byte[0];
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

    private String getTekst_zaszyfrowany() {
        return tekst_zaszyfrowany.getText();
    }

    private String getTekst_jawny() {
        return tekst_jawny.getText();
    }
    public void generateKeys() {
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
        dane = HexFormat.of().parseHex(getKeyG() + getKeyH() + getKeyA()+ getMOD());
        zapis_do_pliku();
    }
    public void loadKey() {
        byte[] klucze = odczyt_z_pliku();
        ByteBuffer bb = ByteBuffer.wrap(klucze);
        byte[] kluczg = new byte[256];
        byte[] kluczh = new byte[257];
        byte[] klucza = new byte[256];
        byte[] kluczn = new byte[257];
        bb.get(kluczg, 0, kluczg.length);
        bb.get(kluczh, 0, kluczh.length);
        bb.get(klucza, 0, klucza.length);
        bb.get(kluczn, 0, kluczn.length);
        keyG.setText(converter(kluczg));
        keyH.setText(converter(kluczh));
        keyA.setText(converter(klucza));
        MOD.setText(converter(kluczn));
    }


    public void saveTextJ() {
        if (dane.length != 0)
            dane = HexFormat.of().parseHex(getTekst_jawny());
        else
            dane =  HexFormat.of().parseHex(converter(getTekst_jawny().getBytes()));
        zapis_do_pliku();
        dane = new byte[0];
    }

    public void saveTextZ() {
        dane = HexFormat.of().parseHex(getTekst_zaszyfrowany().replaceAll("\n", "656E746572"));
        zapis_do_pliku();
    }

    public void loadTextJ() {
        tekst_jawny.setText(converter(odczyt_z_pliku()));
    }
    public void loadTextZ() {
        tekst_zaszyfrowany.setText(converter(odczyt_z_pliku()).replaceAll("656E746572", "\n"));
    }

    public void podpisz() throws NoSuchAlgorithmException {
        ElGamal gamal = new ElGamal();
        kluczN = new BigInteger(HexFormat.of().parseHex(getMOD()));
        kluczA = new BigInteger(HexFormat.of().parseHex(getKeyA()));
        kluczG = new BigInteger(HexFormat.of().parseHex(getKeyG()));
        kluczNn1 = kluczN.subtract(BigInteger.ONE);
        szyfr =  gamal.sign(getTekst_jawny().getBytes(),kluczNn1,kluczG,kluczA,kluczN);
        StringBuilder result = new StringBuilder();
        for (BigInteger number : szyfr) {
            result.append(converter(number.toByteArray()));
            result.append("\n");
        }
        tekst_zaszyfrowany.setText(result.toString());
    }

    public void zweryfikuj() throws NoSuchAlgorithmException {
        kluczN = new BigInteger(HexFormat.of().parseHex(getMOD()));
        kluczH = new BigInteger(HexFormat.of().parseHex(getKeyH()));
        kluczG = new BigInteger(HexFormat.of().parseHex(getKeyG()));
        ElGamal gamal = new ElGamal();
        String[] tab = getTekst_zaszyfrowany().split("\n");
        BigInteger[] podpis = new BigInteger[2];
        podpis[0] = new BigInteger(HexFormat.of().parseHex(tab[0]));
        podpis[1] = new BigInteger(HexFormat.of().parseHex(tab[1]));
        // pomysl jak zmienic Stringa na BIgInteger,bo tak przekazuje szyfr z bufora
        boolean flag = gamal.verify(podpis, getTekst_jawny().getBytes(), kluczN, kluczG, kluczH);
        Alert alert;
        if (flag) {
            alert = new Alert(Alert.AlertType.NONE, "Podpis jest poprawny", ButtonType.OK);
        }
        else {
            alert = new Alert(Alert.AlertType.NONE, "Sfałszowano!", ButtonType.OK);
        }
        alert.setResizable(false);
        alert.showAndWait();
    }
}