import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

public class DecryptProject {

	static String pwd = "";

	public static void main(String[] args) {
		String in = args[0];
		String outputPath = args[1];
		pwd = args[2];
		try {
			System.out.println("Begin Decryption");
			(new DecryptProject()).beginDecrypt(in, compressPassword(pwd),
					outputPath);
			System.out.println("Decryption Complete");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String compressPassword(String pass) {
		char[] tmp = new char[8];
		if (pass.length() > 8) {
			for (int i = 0; i < pass.toCharArray().length; i++) {
				tmp[i % 7] += pass.toCharArray()[i];
			}
		}
		pass = new String(tmp);
		return pass;
	}

	public void beginDecrypt(String in, String pass, String output)
			throws Exception {
		try {
			processDecrypt(in, output, compressPassword(pass));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private String decryptName(String str) {
		String decrypted = "";

		int keyLength = pwd.length();
		for (int i = 0; i < str.length(); i++) {
			int c = str.charAt(i);
			if (Character.isUpperCase(c)) {
				c = c - (keyLength % 26);
				if (c < 'A')
					c = c + 26;
			} else if (Character.isLowerCase(c)) {
				c = c - (keyLength % 26);
				if (c < 'a')
					c = c + 26;
			}
			decrypted += (char) c;
		}
		return decrypted;
	}

	private void processDecrypt(String in, String out, String pass)
			throws Exception {

		File fin = new File(in);
		File fout = new File(out);

		if (!fin.exists()) {
			return;
		}

		if (!fout.exists() && fin.isDirectory()) {
			fout.mkdirs();
			fout.setWritable(true);
		} else if (!fin.isDirectory()) {
			try {
				// out = out.substring(0, out.length()
				// - encryptName(".enc").length());

				decrypt(fin, new File(out), pass);

			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		if (fin.isDirectory()) {
			for (int i = 0; i < fin.list().length; i++) {
				processDecrypt(in + "/" + fin.list()[i], out + "/"
						+ decryptName(fin.list()[i]), pass);
			}
		}
	}

	private void decrypt(File fileIn, File fileOut, String pass)
			throws Exception {
		fileOut.setWritable(true);
		fileOut.setReadable(true);
		fileIn.setReadable(true);
		final String algo = "DES/ECB/PKCS5Padding";
		FileInputStream fis = new FileInputStream(fileIn);
		FileOutputStream fos = new FileOutputStream(fileOut);
		byte k[] = pass.getBytes();
		SecretKeySpec key = new SecretKeySpec(k, algo.split("/")[0]);
		Cipher decrypt = Cipher.getInstance(algo);
		decrypt.init(Cipher.DECRYPT_MODE, key);
		CipherInputStream cin = new CipherInputStream(fis, decrypt);

		byte[] buf = new byte[1024];
		int read = 0;
		while ((read = cin.read(buf)) != -1) {
			fos.write(buf, 0, read);
			System.out.print(".");
		}
		System.out.println("." + fileOut.getName() + " decrypted!");
		cin.close();
		fos.flush();
		fos.close();
	}
}
