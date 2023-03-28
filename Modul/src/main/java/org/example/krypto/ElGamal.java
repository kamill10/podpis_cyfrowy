package org.example.krypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class ElGamal {

    private final int keyLenght = 2048;

    private final Random random = new Random();
    private final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");


    public ElGamal() throws NoSuchAlgorithmException {
    }

    public BigInteger [] sign(byte [] text,BigInteger Nn1,BigInteger g,BigInteger a,BigInteger N) {
        messageDigest.update(text);
        BigInteger signature = new BigInteger(1, messageDigest.digest());
       BigInteger r = BigInteger.probablePrime(keyLenght, random);
        BigInteger[] result = new BigInteger[2];
        while(true) {
            if(r.gcd(Nn1).equals(BigInteger.ONE)) {
                break;
            }
            else {
                r = r.nextProbablePrime();
            }
        }
        BigInteger rn1 = r.modInverse(Nn1);
        BigInteger s1 = g.modPow(r,N);
        BigInteger s2 =signature.subtract(a.multiply(s1)).multiply(rn1.mod(Nn1));
        result[0]=s1;
        result[1]=s2;
        return result;
    }

    public boolean verify (BigInteger[] podpis,byte []tekst,BigInteger N,BigInteger g,BigInteger h) {
        messageDigest.update(tekst);
        BigInteger hash = new BigInteger(1, messageDigest.digest());
        BigInteger result1 = g.modPow(hash, N);
        BigInteger result2 = h.modPow(podpis[0], N).multiply(podpis[0].modPow(podpis[1], N)).mod(N);
        return result1.compareTo(result2) == 0;
    }

    public boolean verify(String tekstJawny, String podpis,BigInteger N,BigInteger g,BigInteger h)
    {
        messageDigest.update(tekstJawny.getBytes());
        BigInteger hash = new BigInteger(1, messageDigest.digest());
        String tab[]=podpis.split("\n");
        BigInteger S1=new BigInteger(1,hexToBytes(tab[0]));
        BigInteger S2=new BigInteger(1,hexToBytes(tab[1]));
        BigInteger wynik1=g.modPow(hash, N);
        BigInteger wynik2=h.modPow(S1, N).multiply(S1.modPow(S2, N)).mod(N);
        if(wynik1.compareTo(wynik2)==0)return true; else return false;
    }

    private byte[] hexToBytes(String tekst) {
        if (tekst == null) { return null;}
        else if (tekst.length() < 2) { return null;}
        else { if (tekst.length()%2!=0)tekst+='0';
            int dl = tekst.length() / 2;
            byte[] wynik = new byte[dl];
            for (int i = 0; i < dl; i++)
            { try{
                wynik[i] = (byte) Integer.parseInt(tekst.substring(i * 2, i * 2 + 2), 16);
            }catch(NumberFormatException e) {
                System.out.println("blad z konwersja");
            }
            }
            return wynik;
        }
    }


}
