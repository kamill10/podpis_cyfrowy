package org.example.krypto;

import java.math.BigInteger;
import java.util.Random;

public class Keys {
    BigInteger N;
    BigInteger a;
    BigInteger g;
    BigInteger h;
    BigInteger Nn1;
    private final int keyLenght = 2048;
    private final Random random = new Random();

    public void generateKeys() {
        N=BigInteger.probablePrime(keyLenght+2,random);
        a = new BigInteger(keyLenght,random); //klucz prywatny
        g = new BigInteger(keyLenght,random);
        h=g.modPow(a,N);
        Nn1 = N.subtract(BigInteger.ONE);
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
