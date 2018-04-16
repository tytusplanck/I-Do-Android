package com.example.tyle.ido;

public class Encryption {

    private final byte[] S = new byte[256];
    private final byte[] T = new byte[256];
    private final int keylen;

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

    public byte[] encrypt(final byte[] textToEncrypt) {
        final byte[] encryptedText = new byte[textToEncrypt.length];
        int i = 0, j = 0, k, t;
        byte tmp;
        for (int counter = 0; counter < textToEncrypt.length; counter++) {
            i = (i + 1) & 0xFF;
            j = (j + S[i]) & 0xFF;
            tmp = S[j];
            S[j] = S[i];
            S[i] = tmp;
            t = (S[i] + S[j]) & 0xFF;
            k = S[t];
            encryptedText[counter] = (byte) (textToEncrypt[counter] ^ k);
        }
        return encryptedText;
    }

    public byte[] decrypt(final byte[] ciphertext) {
        return encrypt(ciphertext);
    }
}