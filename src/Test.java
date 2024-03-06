import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class Test {
	static final String desPath = "output/DES/";
	static final PrintStream stdout = System.out;
	static final String tripleDesPath = "output/TripleDES/";
	static final String aesPath = "output/AES/";
	static final String rsaPath = "output/RSA/";
	static final String[] Channels = { "red", "green", "blue" };
	static final String timeTxt = "output/EncryptTime.txt";

	public static void main(String[] args) throws IOException {
		String imagePath = "input/taj.jpg";
		String en = "Encrypt/cipher_";
		String de = "Decrypt/decrypt_";

		try (FileWriter fileWriter = new FileWriter(timeTxt)) {
			fileWriter.write("Algo	Time\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Reading the Taj Mahal image...");
		readImageToArray(imagePath);

		System.out.println("Cipher text and Decrypted text files are being generated...");
		new DES();
		new TripleDES();
		new AES();
		new RSA();

		System.out.println("\nImages are being generated...");

		generateImage(desPath + en, desPath + "Encrypt/encrypted_image.jpg");
		generateImage(desPath + de, desPath + "Decrypt/decrypted_image.jpg");

		generateImage(tripleDesPath + en, tripleDesPath + "Encrypt/encrypted_image.jpg");
		generateImage(tripleDesPath + de, tripleDesPath + "Decrypt/decrypted_image.jpg");

		generateImage(aesPath + en, aesPath + "Encrypt/encrypted_image.jpg");
		generateImage(aesPath + de, aesPath + "Decrypt/decrypted_image.jpg");

		generateImage(rsaPath + en, rsaPath + "Encrypt/encrypted_image.jpg");
		generateImage(rsaPath + de, rsaPath + "Decrypt/decrypted_image.jpg");

		System.out.println("Images generated Successfully!");

	}

	// Read Image
	private static void readImageToArray(String imagePath) {
		try {
			System.out.println("Hello");
			
			
			BufferedImage image = ImageIO.read(new File(imagePath));
			int width = image.getWidth();
			int height = image.getHeight();

			int[][] redChannel = new int[height][width];
			int[][] greenChannel = new int[height][width];
			int[][] blueChannel = new int[height][width];

			for (int x = 0; x < height; x++) {
				for (int y = 0; y < width; y++) {
					Color color = new Color(image.getRGB(y, x));
					redChannel[x][y] = color.getRed();
					greenChannel[x][y] = color.getGreen();
					blueChannel[x][y] = color.getBlue();
				}
			}
			
			storeArrayToFile(redChannel, "input/taj_red.txt");
			storeArrayToFile(greenChannel, "input/taj_green.txt");
			storeArrayToFile(blueChannel, "input/taj_blue.txt");

			System.out.println("Three channels target files are obtained");

		} catch (IOException e) {
			System.out.println("Error occurred while reading the image: " + e.getMessage());
		}
	}

	// Store
	private static void storeArrayToFile(int[][] array, String filePath) {
		try {
			FileWriter writer = new FileWriter(filePath);

			for (int[] row : array) {
				for (int value : row) {
					writer.write(value + " ");
				}
				writer.write("\n");
			}

			writer.close();
		} catch (IOException e) {
			System.out.println("Error occurred while storing the array to a text file: " + e.getMessage());
		}
	}

	// Generate
	private static void generateImage(String src, String dest) throws IOException {
		Scanner[] scanners = new Scanner[3];

		for (int i = 0; i < Channels.length; i++) {
			scanners[i] = new Scanner(new File(src + Channels[i] + ".txt"));
		}

		BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < 1000; j++) {
				image.setRGB(j, i,
						(scanners[0].nextInt() << 16) | (scanners[1].nextInt() << 8) | scanners[2].nextInt());
			}
		}

		ImageIO.write(image, "jpg", new File(dest));
		System.out.println("Image generated successfully: " + dest);
	}
}