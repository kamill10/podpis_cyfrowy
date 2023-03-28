package org.example.krypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class ElGamal {
    BigInteger N;
    BigInteger a;
    BigInteger g;
    BigInteger h;
    BigInteger Nn1;
    private final Random random = new Random();
    private final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
    private final int keyLenght=2048;

    public ElGamal() throws NoSuchAlgorithmException {
    }

    public void generateKeys() {
        N=BigInteger.probablePrime(keyLenght+2,random);
        a = new BigInteger(keyLenght,random); //klucz prywatny
        g = new BigInteger(keyLenght,random);
        h=g.modPow(a,N);
        Nn1 = N.subtract(BigInteger.ONE);
        System.out.println(a.toString());
    }
    public BigInteger [] sign(byte [] text) {
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
        BigInteger rn1 = r.modInverse(r);
        BigInteger s1 = g.modPow(r,N);
        BigInteger s2 =signature.subtract(a.multiply(s1)).multiply(rn1.mod(Nn1));
        result[0]=s1;
        result[1]=s2;
        return result;
    }

    public boolean verify (BigInteger[] podpis,byte []tekst ) {
        messageDigest.update(tekst);
        BigInteger hash = new BigInteger(1, messageDigest.digest());
        BigInteger result1 = g.modPow(hash, N);
        BigInteger result2 = h.modPow(podpis[0], N).multiply(podpis[0].modPow(podpis[1], N)).mod(N);
        return result1.compareTo(result2) == 0;
    }
}
