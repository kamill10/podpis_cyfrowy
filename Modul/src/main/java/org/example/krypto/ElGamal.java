package org.example.krypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class ElGamal {

    private final Random random = new Random();
    private final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");


    public ElGamal() throws NoSuchAlgorithmException {
    }

    public BigInteger [] sign(byte[] text, BigInteger Nn1, BigInteger g, BigInteger a, BigInteger N) {
        messageDigest.update(text);
        BigInteger signature = new BigInteger(1, messageDigest.digest());
        int keyLenght = 2048;
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
        BigInteger s2 = signature.subtract(a.multiply(s1)).multiply(rn1.mod(Nn1));
        result[0]=s1;
        result[1]=s2;
        return result;
    }

    public boolean verify (BigInteger[] podpis, byte []tekst,BigInteger N,BigInteger g,BigInteger h) {
        messageDigest.update(tekst);
        BigInteger hash = new BigInteger(1, messageDigest.digest());
        BigInteger result1 = g.modPow(hash, N);
        BigInteger result2 = h.modPow(podpis[0], N).multiply(podpis[0].modPow(podpis[1], N)).mod(N);
        return result1.compareTo(result2) == 0;
    }
}
