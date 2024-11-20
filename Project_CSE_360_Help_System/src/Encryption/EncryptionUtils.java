package Encryption;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
* <p>EncryptionUtils Class</p>
* 
* <p> Description: This class provides utility methods for character-to-byte conversions,
* generating initialization vectors (IVs), and printing character arrays. </p>
* 
* <p>Authors: Erik Camacho, Thienban Nguyen, Sarvesh Shanmugam, Ivan Mancillas, Tanis Peterson</p>
*/

public class EncryptionUtils {
	// The size of the initialization vector (IV) used for encryption and decryption.
	private static int IV_SIZE = 16;
	
	/**
     * Converts a byte array to a character array using the default charset.
     * 
     * @param bytes the byte array to convert.
     * @return a character array corresponding to the input byte array.
     */
	public static char[] toCharArray(byte[] bytes) {		
        CharBuffer charBuffer = Charset.defaultCharset().decode(ByteBuffer.wrap(bytes));
        return Arrays.copyOf(charBuffer.array(), charBuffer.limit());
	}
	
	/**
     * Converts a character array to a byte array using the default charset.
     * 
     * @param chars the character array to convert.
     * @return a byte array corresponding to the input character array.
     */
	static byte[] toByteArray(char[] chars) {		
        ByteBuffer byteBuffer = Charset.defaultCharset().encode(CharBuffer.wrap(chars));
        return Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
	}
		
	/**
     * Generates an initialization vector (IV) from a given text input.
     * The IV is constructed by cycling through the input text until the required size is reached.
     * 
     * @param text the text used to generate the IV.
     * @return a byte array representing the initialization vector.
     */
	public static byte[] getInitializationVector(char[] text) {
		char iv[] = new char[IV_SIZE];
		
		int textPointer = 0;
		int ivPointer = 0;
		while(ivPointer < IV_SIZE) {
			iv[ivPointer++] = text[textPointer++ % text.length];
		}		
		return toByteArray(iv);
	}
	
	 /**
     * Prints the characters of a character array to the standard output.
     * 
     * @param chars the character array to print.
     */
	public static void printCharArray(char[] chars) {
		for(char c : chars) {
			System.out.print(c);
		}
	}
}
