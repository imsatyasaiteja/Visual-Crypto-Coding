import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.BufferedWriter;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Base64;
import java.util.Scanner;

import java.security.SecureRandom;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class TripleDES {
	static final String Dir = "output/TripleDES";
	static final PrintStream stdout = System.out;
	static final String[] Channels = { "red", "green", "blue" };
	static final String timeTxt = "output/EncryptTime.txt";

	static {
		try {
			File directory = new File(Dir);
			directory.mkdirs();

			File encryptDir = new File(Dir + "/Encrypt");
			encryptDir.mkdirs();

			File decryptDir = new File(Dir + "/Decrypt");
			decryptDir.mkdirs();

			String keyFilePath = Dir + "/tripleDesKey.txt";

			SecretKey secretKey = generateDESKey();
			saveKeyToFile(secretKey, keyFilePath);

			Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");

			long startTime = System.nanoTime();

			for (String channel : Channels) {
				String targetImagePath = "input/taj_" + channel + ".txt";
				String cipherImagePath = encryptDir + "/cipher_" + channel + ".txt";

				crypt(secretKey, cipher, Cipher.ENCRYPT_MODE, targetImagePath, cipherImagePath);
			}

			long endTime = System.nanoTime();

			System.out.println("\nTriple DES data encryption done Successfully!");

			long executionTime = endTime - startTime;
			System.out.println("Triple DES Encryption Time: " + executionTime + " ns");

			try (PrintWriter out = new PrintWriter(new FileWriter(timeTxt, true))) {
				out.println("3DES	" + executionTime + " ns");
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (String channel : Channels) {
				String cipherImagePath = encryptDir + "/cipher_" + channel + ".txt";
				String decryptImagePath = decryptDir + "/decrypt_" + channel + ".txt";

				crypt(secretKey, cipher, Cipher.DECRYPT_MODE, cipherImagePath, decryptImagePath);
			}

			System.out.println("Triple DES data decryption done Successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void crypt(SecretKey secretKey, Cipher cipher, int mode, String src, String dest)
			throws NoSuchAlgorithmException, IOException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException {
		Scanner scanner = new Scanner(new File(src));

		byte[] buffer = new byte[1000 * 1000];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (byte) scanner.nextInt();
		}

		cipher.init(mode, secretKey);
		buffer = cipher.doFinal(buffer);

		System.setOut(new PrintStream(new File(dest)));
		for (byte i : buffer) {
			System.out.print((i & 255) + " ");
		}

		System.setOut(stdout);
	}

	private static SecretKey generateDESKey() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
		keyGenerator.init(168, new SecureRandom());
		return keyGenerator.generateKey();
	}

	private static void saveKeyToFile(SecretKey key, String filePath) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filePath))) {
			byte[] encodedKey = key.getEncoded();
			String encodedKeyString = Base64.getEncoder().encodeToString(encodedKey);
			writer.write(encodedKeyString);
		}
	}
}