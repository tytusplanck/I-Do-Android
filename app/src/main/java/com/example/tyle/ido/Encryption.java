package com.example.tyle.ido;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class Encryption {
    private static final int S_LENGTH = 256;

    /**
     * Key array
     */
    private byte[] key = new byte[S_LENGTH - 1];

    private int[] s = new int[S_LENGTH];

    public Encryption() {
        emptyArrays();
    }

    public Encryption(String key) {
        this();
        this.key = key.getBytes();
    }

    private void emptyArrays() {
        Arrays.fill(key, (byte) 0);
        Arrays.fill(s, 0);
    }

    /**
     * Encrypt given message String with given Charset and key
     *
     * @param message message to be encrypted
     * @param key     key
     * @return encrypted message
     */
    public byte[] encryptText(String message, String key) {
        emptyArrays();
        this.key = key.getBytes();
        byte[] crypt = crypt(message.getBytes());
        emptyArrays();
        return crypt;
    }

    /**
     * Decrypt given byte[] message array with given charset and key
     *
     * @param message message to be decrypted
     * @param charset charset of message
     * @param key     key
     * @return string in given charset
     */
    public String decryptText(byte[] message, Charset charset, String key) {
        emptyArrays();
        this.key = key.getBytes();
        byte[] msg = crypt(message);
        emptyArrays();
        return new String(msg);
    }

    /**
     * Decrypt given byte[] message array with given key and pre-defined UTF-8
     * charset
     *
     * @param message message to be decrypted
     * @param key     key
     * @return string in given charset
     * @see StandardCharsets
     */
    public String decryptText(byte[] message, String key) {
        return decryptText(message, StandardCharsets.UTF_8, key);
    }

    /**
     * Crypt given byte array. Be aware, that you must init key, before using
     * crypt.
     *
     * @param msg array to be crypt
     **/
    public byte[] crypt(final byte[] msg) {

        //Generates the Key Stream
        s = new int[S_LENGTH];
        int j = 0;

        //Instantiates s in numerical order
        for (int i = 0; i < S_LENGTH; i++) {
            s[i] = i;
        }

        //Pseudo-Randomness algorithm for RC4
        for (int i = 0; i < S_LENGTH; i++) {
            j = (j + s[i] + (key[i % key.length]) & 0xFF) % S_LENGTH;
            flip(i, j, s);
        }

        //XOR values and create the encrypted/decrypted byte array
        byte[] code = new byte[msg.length];
        int i = 0;
        int j2 = 0; //used j2 to keep with the syntax of the algorithm
        for (int n = 0; n < msg.length; n++) {
            i = (i + 1) % S_LENGTH;
            j2 = (j2 + s[i]) % S_LENGTH;
            flip(i, j2, s);
            int rand = s[(s[i] + s[j2]) % S_LENGTH];
            code[n] = (byte) (rand ^ msg[n]);
        }
        return code;
    }

    private void flip(int i, int j, int[] s) {
        int temp = s[i];
        s[i] = s[j];
        s[j] = temp;
    }
}