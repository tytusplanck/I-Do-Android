package com.example.tyle.ido;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Developed by Kyle Rossman to use an algorithm similar to a RC4 stream cipher to encrypt and decrypt strings.
 */
public class Encryption {
    private static final int S_LENGTH = 256;

    /**
     * Key array
     */
    private byte[] key = new byte[S_LENGTH - 1];

    /**
     * Initialization key stream
     */
    private int[] s = new int[S_LENGTH];

    /**
     * Instantiates an instance of the Encryption class with the specified key.
     * @param key is typically the same throughout all classes in the project so that decryption can happen.
     */
    public Encryption(String key) {
        this();
        this.key = key.getBytes();
    }

    /**
     *
     */
    private void emptyArrays() {
        Arrays.fill(key, (byte) 0);
        Arrays.fill(s, 0);
    }

    public Encryption() {
        emptyArrays();
    }

    /**
     * Encrypt given message String with given Charset and key
     *
     * @param message message to be encrypted
     * @param key     key
     * @return encrypted message
     */
    public byte[] encryptText(String message, String key) {

        //First, clear out what was previously in the key stream and the key array
        emptyArrays();

        //set the bytes of the current key as the associated key variable
        this.key = key.getBytes();

        //calls the cipher that takes the plain text message and encrypts it.
        byte[] crypt = cipher(message.getBytes());
        emptyArrays();

        //returns the XOR'd product of the cipher method
        return crypt;
    }

    /**
     * Decrypts the cipher text byte array using the given string.
     *
     * @param message message to be decrypted
     * @param key     key
     * @return string in given charset
     */
    public String decryptText(byte[] message, String key) {

        //Clear previous contents of array and replace with all zeros
        emptyArrays();

        //set the current key as the bytes associated with string key
        this.key = key.getBytes();

        //call the cipher class to take encrypted stream and decrypt back to original message
        byte[] msg = cipher(message);

        emptyArrays();

        //returns the equivalent string of the returned decrypted byte array
        return new String(msg);
    }

    /**
     * Crypt given byte array. Be aware, that you must init key, before using
     * crypt.
     *
     * @param msg array to be crypt
     **/
    public byte[] cipher(final byte[] msg) {

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
        byte[] xOR = new byte[msg.length]; //creates byte to hold the result of the xor operation
        int i = 0;
        int j2 = 0; //used j2 to keep with the syntax of the algorithm

        //loops for each spot in the byte array and find the xor value of the corresponding message byte and the random temp array
        for (int n = 0; n < msg.length; n++) {
            i = (i + 1) % S_LENGTH; //increment i continuously
            j2 = (j2 + s[i]) % S_LENGTH; //creates a "random" value for j

            //flips the values of i and j2 in s to disrupt the initialized order
            flip(i, j2, s);
            int pseudoRand = s[(s[i] * 2 + s[j2] * 3) % S_LENGTH];

            //Finally casts the xor operation between the random value and the spot in the message as a byte
            xOR[n] = (byte) (pseudoRand ^ msg[n]);
        }
        return xOR;
    }

    /**
     * As we iterate over s, we swap values of each location with a random index j.
     *
     * @param i
     * @param j
     * @param s
     */
    private void flip(int i, int j, int[] s) {
        int temp = s[i];
        s[i] = s[j];
        s[j] = temp;
    }
}