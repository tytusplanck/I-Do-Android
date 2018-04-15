package com.example.tyle.ido;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;

/**
 *
 *
 * This class contains methods to encrypt Strings,
 * generates and retrieves the key to encrypt with AES 256.
 */

public class Encryption {


    private static final int SBOX_LENGTH = 256;
    private static final int KEY_MIN_LENGTH = 5;
    /**
     * Key array
     */
    private byte[] key = new byte[SBOX_LENGTH - 1];
    /**
     * Sbox
     */
    private int[] sbox = new int[SBOX_LENGTH];


    /**
     * Creates a cipher and encrypts the String passed according to that cipher.
     *
     * @param textToEncrypt
     * @return
     *
     */
    public byte[] encryptText(final String textToEncrypt)
            throws Exception {

        return new byte[0];
    }


    /**
     * Decrypts a string of encrypted data
     *
     * @param encryptedDataWithIV
     * @return - the decrypted string
     * @throws UnrecoverableEntryException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws NoSuchProviderException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws CertificateException
     */
    public String decryptText(final byte[] encryptedDataWithIV) {
        return "skrrt";
    }
}