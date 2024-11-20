package Encryption;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
* <p>EncryptionHelper Class</p>
* 
* <p> Description: This class provides methods to encrypt and decrypt data using AES in CBC mode
* with PKCS5 padding. The BouncyCastle provider is used for enhanced cryptographic capabilities. </p>
* 
* <p>Authors: Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson</p>
*/

public class EncryptionHelper {

	// The identifier for the BouncyCastle security provider.
	private static String BOUNCY_CASTLE_PROVIDER_IDENTIFIER = "BC";	
	// The Cipher instance used for encryption and decryption operations.
	private Cipher cipher;
	
	// A predefined 128-bit AES key (16 bytes) used for encryption/decryption.
	byte[] keyBytes = new byte[] {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
	
	// The SecretKey used for AES encryption and decryption, derived from the keyBytes.
	private SecretKey key = new SecretKeySpec(keyBytes, "AES");

	/**
     * Constructor for EncryptionHelper. Initializes the cipher instance with the AES algorithm 
     * in CBC mode and PKCS5 padding, using the BouncyCastle provider.
     * 
     * @throws Exception if there is an issue with cipher initialization or provider setup.
     */
	public EncryptionHelper() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", BOUNCY_CASTLE_PROVIDER_IDENTIFIER);		
	}
	
	/**
     * Encrypts the given plaintext using AES in CBC mode with the specified initialization vector.
     * 
     * @param plainText the plaintext to be encrypted.
     * @param initializationVector the initialization vector to be used for encryption.
     * @return the encrypted ciphertext.
     * @throws Exception if an error occurs during encryption.
     */
	public byte[] encrypt(byte[] plainText, byte[] initializationVector) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(initializationVector));
		return cipher.doFinal(plainText);
	}
	
	/**
     * Decrypts the given ciphertext using AES in CBC mode with the specified initialization vector.
     * 
     * @param cipherText the ciphertext to be decrypted.
     * @param initializationVector the initialization vector to be used for decryption.
     * @return the decrypted plaintext.
     * @throws Exception if an error occurs during decryption.
     */
	public byte[] decrypt(byte[] cipherText, byte[] initializationVector) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(initializationVector));
		return cipher.doFinal(cipherText);
	}	
}
