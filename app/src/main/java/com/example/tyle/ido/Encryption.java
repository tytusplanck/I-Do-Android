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

    // The type of encryption you want to use to encrypt data
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    // Used for getting an instance of the android KeyStore
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    // The name of the alias used to retrieve the encryption key
    private static final String KEY_ALIAS = "BadgerAlias";

    // Array to hold the encrypted data in bytes
    private byte[] encryption;

    // Initiliazation Vector
    public byte[] iv;

    KeyStore keyStore;

    public Encryption() { }

    /**
     * Creates a cipher and encrypts the String passed according to that cipher.
     *
     * @param textToEncrypt
     * @return
     *
     */
    public byte[] encryptText(final String textToEncrypt)
            throws Exception {

//        //Makes sure that if no key exists yet it will create one with specified alias and store in KeyStore
//        generateSecretKey();
//
//        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//
//
//        //set the cipher to Encrypt mode and call method
//        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
//
//        //generate an initialization vector to randomize encryption and avoid repeated occurrences
//        iv = cipher.getIV();
//
//        // Convert String input to a sequence of bytes, then encrypt the data according to the
//        // encryption algorithm specified in the cipher
//        byte[] encryptedData = cipher.doFinal(textToEncrypt.getBytes("UTF-8"));
//
//        // Append the IV to the end of the byte array with the encrypted bytes
//        encryption = appendIVToData(encryptedData, iv);
        encryption = textToEncrypt.getBytes();

        return encryption;
    }

    /**
     * Creates the AES key to encrypt data and stores the key in the keystore under the Alias
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidAlgorithmParameterException
     * @throws KeyStoreException
     * @throws IOException
     * @throws CertificateException
     */
    @NonNull
    private void generateSecretKey() throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException, KeyStoreException, IOException, CertificateException {
//
//        //Retrieve the current KeyStore
//        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
//        keyStore.load(null);
//
//        //If the current alias is not recognized by the KeyStore it generates a key.
//        if (!keyStore.containsAlias(KEY_ALIAS)){
//            final KeyGenerator keyGenerator = KeyGenerator
//                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
//
//            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
//                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
//                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
//                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
//                    .build());
//
//            // Generate a key
//            Key k = keyGenerator.generateKey();
//
//            // Store the key in the keystore
//            keyStore.setKeyEntry(KEY_ALIAS, k, null, null);
//        }
    }

    /**
     * Returns the encryption key from the Keystore under the alias
     *
     * @return - the encryption key
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    private Key getSecretKey() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return keyStore.getKey(KEY_ALIAS, null);
    }

    byte[] getEncryption() {
        return encryption;
    }

    byte[] getIv() {
        return iv;
    }


    /**
     * Adds the IV to the end of the byte array so that you can decrypt it
     *
     * @param encryptedData
     * @param iv
     * @return
     * @throws Exception
     */
    private static byte[] appendIVToData(byte[] encryptedData, byte[] iv) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //Write the encrypted data and the IV to the Stream
        output.write(encryptedData);
        output.write(iv);

        // Return the stream as a byte array
        return output.toByteArray();
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
    public String decryptText(final byte[] encryptedDataWithIV)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, CertificateException {

//        // Return the current keystore
//        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
//        keyStore.load(null);
//
//        // Get an instance of the cipher
//        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//
//        // Get the initialization vector from the byte array
//        ByteArrayOutputStream encryptedIV = new ByteArrayOutputStream(12);
//        encryptedIV.write(encryptedDataWithIV, encryptedDataWithIV.length -12, 12);
//        iv = encryptedIV.toByteArray();
//
//        // Get the original string from the byte array
//        ByteArrayOutputStream onlydata = new ByteArrayOutputStream();
//        onlydata.write(encryptedDataWithIV, 0, encryptedDataWithIV.length-12);
//        byte[] encryptedData = onlydata.toByteArray();
//
//        // Initialize the cipher and return the decrypted string
//        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), new GCMParameterSpec(128, iv));
//        return new String(cipher.doFinal(encryptedData), "UTF-8");
        return new String(encryptedDataWithIV, "UTF-8");
    }
}