import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class TripleDES {

    // public static void main(String[] args) throws Exception {

        // String text = "kyle boon";

        // byte[] codedtext = new TripleDESTest().encrypt(text);
        // String decodedtext = new TripleDESTest().decrypt(codedtext);

        // System.out.println(codedtext); // this is a byte array, you'll just see a reference to an array
        // System.out.println(decodedtext); // This correctly shows "kyle boon"
    // }

	private static byte[] cryptkey = new byte[]
{

};

	private static byte[] ivBytes = new byte[] {  };
    public static byte[] encrypt(String message) throws Exception {
        final SecretKey key = new SecretKeySpec(cryptkey, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(ivBytes);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        final byte[] plainTextBytes = message.getBytes("utf-8");
        final byte[] cipherText = cipher.doFinal(plainTextBytes);
        // final String encodedCipherText = new sun.misc.BASE64Encoder()
        // .encode(cipherText);

        return cipherText;
    }

    public static String decrypt(byte[] message) throws Exception {
        final SecretKey key = new SecretKeySpec(cryptkey, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(ivBytes);
        final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        decipher.init(Cipher.DECRYPT_MODE, key, iv);

        // final byte[] encData = new
        // sun.misc.BASE64Decoder().decodeBuffer(message);
        final byte[] plainText = decipher.doFinal(message);

        return new String(plainText, "UTF-8");
    }
}