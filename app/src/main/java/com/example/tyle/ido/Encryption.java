package com.example.tyle.ido;

public class Encryption {

    private final int keylen;

    //The byte array representing the key stream
    private final byte[] S = new byte[256];

    //The Pseudo-random
    private final byte[] T = new byte[256];

    public Encryption(final byte[] key) {

            keylen = key.length;
            for (int i = 0; i < 256; i++) {
                S[i] = (byte) i;
                T[i] = key[i % keylen];
            }
            int j = 0;
            byte storage;
            for (int i = 0; i < 256; i++) {
                j = (j + S[i] + T[i]) & 0xFF;
                storage = S[j];
                S[j] = S[i];
                S[i] = storage;
            }
    }

    public byte[] encryptText(String textToEncrypt) {
        byte[] textInBytes = textToEncrypt.getBytes();
        final byte[] encryptedText = new byte[textInBytes.length];
        int i = 0, j = 0, k, t;
        byte tmp;
        for (int counter = 0; counter < textInBytes.length; counter++) {
            i = (i + 1) & 0xFF;
            j = (j + S[i]) & 0xFF;
            tmp = S[j];
            S[j] = S[i];
            S[i] = tmp;
            t = (S[i] + S[j]) & 0xFF;
            k = S[t];
            encryptedText[counter] = (byte) (textInBytes[counter] ^ k);
        }
        return encryptedText;
    }

    public String decryptText(final byte[] encryptedText) {
        return encryptText(encryptedText.toString()).toString();
    }
}