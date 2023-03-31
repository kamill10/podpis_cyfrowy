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
        byte[] kluczg = new byte[257];
        byte[] kluczh = new byte[257];
        byte[] klucza = new byte[257];
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
        if(dane.length != 0)
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
        kluczH = new BigInteger(HexFormat.of().parseHex(getKeyH()));
        System.out.println(converter(kluczG.toByteArray()).equals("00D55F270219DCA9FC7ED354FE74C8A225EE81D8C5E0D157B5BA4AD5C295ABD175FCE3453D7D3AB7F3027141503E3EFE581461615F60EC266B02A6F9D3AE4ECA5CD2726423FC709A5C486D0275F8370B5A20211AD8B969A2A52BA23AEE1BD2AC5EB832A1472FD061A3E98566D04FCF4AFCE66102DA90859A7C0F0C5B29A11A728092996ECC64E5AE491D09674CC10B81007D7EB54D7C4F1D356927CE691A8436CBCBC8C49220DDD59D43C6035223496D5AC03105348AB5197E5927925BF42F8279CEF971D698B596905D8E7CBA1CEEB805CF4A9405BC5E6989E436B163B1A3DC8E8EFA5B763641DA1491440AB1A772969C8B7163B285554B797211ABA065B7D237"));
        System.out.println(converter(kluczH.toByteArray()).equals("00C2B34CCCEB38B50CF572E8955EFB31C0F2DB5A207D46AD77C08589E6065D947DBF1889063C6FB47F440C39F7FABEB273355632B1A7AE0A2DA0CA418F8A586D816A9A61B96D2BE98C7B3A360C5EC3A79749176B71463811863D879975CCA31358EB4BF6F1D4E194442DDCF7C8E4F89FE4A0CFDBFE9DE735602479D3702AA99A68BAAB189DDDBDD9252DFEE80ED25A7C7231CB2B12F90BCD68AD5BAEFA4849A7900F40BCF8FFE1D01F79641432F2F4B79C9D1431CA8E41855360BEC03C2D20C38C49411A6AD7D9C1A0311EE670453D96DA32BB64C29EAC9277D38D5193C8B1F86D2C17759FB55788F45819EA5DB7BFB020B256DB4138DA49EF16B1DFAE9F45FAFB"));
        System.out.println(converter(kluczA.toByteArray()).equals("00ED2D3B383AE40A28DAEFB649B68A558ABE567063F237AAF5E7575B85A46161AF13AD4DB1649EB17CA77497E1355AB150F1F53FE22E29437136F9587657694ADEE443BAB0F41F801094833DD7D0CB327BC6D65B9B4F95D6F084405E614BA695E0413C5D8704D75EA7B669D71EB312F87A917490FB782DA3E42702C794C32E21618E1FB886639EDF51C87450D6DDFF5B24E9F5B57DC4A66E0E5DF9E89F1B40D3CCB512514F1420F3BEA8E30AD4A368343829D158A8BEC41EACC640ACA33F8DC8B5A442F103CD07F828D866B7CB2DDD0627F08022C5E7337907E45A8A5332DD8D37CA6094D879058E21FE306CF9F5AEADD8A248F4233334143BA2CA353789F9802B"));
        System.out.println(converter(kluczN.toByteArray()).equals("02AD33F3B14EFAB7B3785EC45AF6E6E1B7CD7155B43CB36581DD606BC719522ABC8E4DD26DCD5FE9839A4A91F6C748F6B6DB36797B5F41F9706546C8718DC0DA60E9F8AC456FEBAD56A565C3FBF53E39520743772D64719DBF896D53E1BE26B3CD852E39057D87190FF03B02965AF529B7B316AEC18F611730BBCABE7E999C00E438E4C76E9091DD70811ED3098C8D4896503D666213D3C1A9B4D7AA04F2C6F047A820D1B53ED0D5A1A8EB822BCB8A6FA5E688BBEFDEBFE1B41B7D5302DD830DDD1E1330029383EDB025226AE4415D5C1CB482787A43E887FFB05FA75C555B385271A1E5F2581A170BE200F4B391A64D38D9A26FCA658298DD1073BCC932EEA037"));

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