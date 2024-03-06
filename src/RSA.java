import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Base64;
import java.util.Scanner;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class RSA {
    static final String Dir = "output/RSA";
    static final PrintStream stdout = System.out;
    static final String[] Channels = { "red", "green", "blue" };
    static final String timeTxt = "output/EncryptTime.txt";

    static int size = 0;
    static byte[] buff = null;

    static {
        try {
            File directory = new File(Dir);
            directory.mkdirs();

            File encryptDir = new File(Dir + "/Encrypt");
            encryptDir.mkdirs();

            File decryptDir = new File(Dir + "/Decrypt");
            decryptDir.mkdirs();

            String publicKeyPath = Dir + "/rsaPublicKey.txt";
            String privateKeyPath = Dir + "/rsaPrivateKey.txt";

            KeyPair keyPair = generateRSAKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            savePublicKeyToFile(publicKey, publicKeyPath);

            PrivateKey privateKey = keyPair.getPrivate();
            savePrivateKeyToFile(privateKey, privateKeyPath);

            long startTime = System.nanoTime();

            for (String channel : Channels) {
                String targetImagePath = "input/taj_" + channel + ".txt";
                String cipherImagePath = encryptDir + "/cipher_" + channel + ".txt";

                encrypt(publicKey, targetImagePath, cipherImagePath);
            }

            long endTime = System.nanoTime();

            System.out.println("\nRSA data encryption done Successfully!");

            long executionTime = endTime - startTime;

            System.out.println("RSA Encryption Time: " + executionTime + " ns");

            try (PrintWriter out = new PrintWriter(new FileWriter(timeTxt, true))) {
                out.println("RSA	" + executionTime + " ns");
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("RSA Decryption is being done...");

            for (String channel : Channels) {
                String targetImagePath = "input/taj_" + channel + ".txt";
                String decryptImagePath = decryptDir + "/decrypt_" + channel + ".txt";

                decrypt(privateKey, publicKey, targetImagePath, decryptImagePath);
            }

            System.out.println("RSA data decryption done Successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void encrypt(PublicKey publicKey, String src, String dest)
            throws NoSuchAlgorithmException, IOException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, NoSuchPaddingException, ShortBufferException {
        try (Scanner scanner = new Scanner(new File(src));) {

            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            byte[] buffer = new byte[1000 * 1000];

            int count = 0;
            while (scanner.hasNext()) {
                byte[] tmp = new byte[32];

                size = tmp.length;

                for (int j = 0; j < size; j++) {
                    tmp[j] = (byte) scanner.nextInt();
                }

                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                buff = cipher.doFinal(tmp);

                for (int j = 0; j < size; j++) {
                    buffer[size * count + j] = buff[j];
                }

                count++;
            }

            saveToFile(dest, buffer);

            return;
        }
    }

    private static void decrypt(PrivateKey privateKey, PublicKey publicKey, String src, String dest)
            throws NoSuchAlgorithmException, IOException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, NoSuchPaddingException, ShortBufferException {
        try (Scanner scanner = new Scanner(new File(src));) {

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            byte[] buffer = new byte[1000 * 1000];

            int count = 0;
            while (scanner.hasNext()) {
                byte[] tmp = new byte[32];

                size = tmp.length;

                for (int j = 0; j < size; j++) {
                    tmp[j] = (byte) scanner.nextInt();
                }

                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                buff = cipher.doFinal(tmp);

                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                buff = cipher.doFinal(buff);

                for (int j = 0; j < size; j++) {
                    buffer[size * count + j] = buff[j];
                }

                count++;
            }

            saveToFile(dest, buffer);

            return;
        }
    }

    private static void saveToFile(String dest, byte[] buffer) throws IOException {
        System.setOut(new PrintStream(new File(dest)));
        for (byte i : buffer) {
            System.out.print((i & 255) + " ");
        }
        System.setOut(stdout);
    }

    private static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(512, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    private static void savePrivateKeyToFile(PrivateKey key, String filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filePath))) {
            byte[] encodedKey = key.getEncoded();
            String encodedKeyString = Base64.getEncoder().encodeToString(encodedKey);
            writer.write(encodedKeyString);
        }
    }

    private static void savePublicKeyToFile(PublicKey key, String filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filePath))) {
            byte[] encodedKey = key.getEncoded();
            String encodedKeyString = Base64.getEncoder().encodeToString(encodedKey);
            writer.write(encodedKeyString);
        }
    }

}