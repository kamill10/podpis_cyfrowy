package org.example.krypto;

import java.math.BigInteger;
import java.util.Random;

public class Keys {
    BigInteger N;
    BigInteger a;
    BigInteger g;
    BigInteger h;
    BigInteger Nn1;
    private final Random random = new Random();

    public void generateKeys() {
        int keyLenght = 2046;
        N = BigInteger.probablePrime(keyLenght + 4, random);
//        a = new BigInteger(keyLenght, random); //klucz prywatny
//        g = new BigInteger(keyLenght, random);
        a = BigInteger.probablePrime(keyLenght + 2, random);
        g = BigInteger.probablePrime(keyLenght + 2, random);
        h = g.modPow(a,N);
        Nn1 = N.subtract(BigInteger.ONE);
//        System.out.println(N.bitLength());
//        System.out.println(a.bitLength());
//        System.out.println(g.bitLength());
//        System.out.println(h.bitLength());


    }

    public BigInteger getN() {
        return N;
    }

    public BigInteger getA() {
        return a;
    }

    public BigInteger getG() {
        return g;
    }

    public BigInteger getH() {
        return h;
    }

    public BigInteger getNn1() {
        return Nn1;
    }
}
