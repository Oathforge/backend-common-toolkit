package com.backendtoolkit.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.backendtoolkit.common.enums.ExceptionEnum;
import com.backendtoolkit.common.exception.ConflictException;
import com.backendtoolkit.common.exception.IllegalArgumentException;
import com.backendtoolkit.common.exception.InternalServerErrorException;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class EncryptionUtil {

	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static final int TAG_LENGTH_BIT = 128;
	private static final int IV_SIZE = 12;

	/**
	 * Encripta los datos proporcionados utilizando una clave derivada.
	 *
	 * @param data    el texto a encriptar
	 * @param baseKey la clave base para derivar la clave de encriptación
	 * @return la cadena encriptada en Base64, que incluye el IV y el texto
	 *         encriptado
	 * @throws Exception si ocurre algún error durante la encriptación
	 */
	public String encrypt(String data, String baseKey) throws Exception {
		SecretKey key = deriveKey(baseKey);
		if (key == null) {
			throw new IllegalArgumentException(ExceptionEnum.UTL0001.name(), ExceptionEnum.UTL0001.getValue());
		}
		log.debug("Encrypting data...");
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		byte[] iv = new byte[IV_SIZE];
		new SecureRandom().nextBytes(iv);
		GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
		cipher.init(Cipher.ENCRYPT_MODE, key, spec);
		byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
		byte[] encryptedIVAndText = new byte[IV_SIZE + encrypted.length];
		System.arraycopy(iv, 0, encryptedIVAndText, 0, IV_SIZE);
		System.arraycopy(encrypted, 0, encryptedIVAndText, IV_SIZE, encrypted.length);
		log.debug("Data encrypted successfully.");
		return Base64.getEncoder().encodeToString(encryptedIVAndText);
	}

	/**
	 * Desencripta la cadena encriptada utilizando la clave base para derivar la
	 * clave.
	 *
	 * @param encryptedData la cadena encriptada en Base64 que incluye el IV y el
	 *                      texto encriptado
	 * @param baseKey       la clave base para derivar la clave de desencriptación
	 * @return el texto desencriptado
	 * @throws Exception si ocurre algún error durante la desencriptación
	 */
	public String decrypt(String encryptedData, String baseKey) throws Exception {
		SecretKey key = deriveKey(baseKey);
		if (key == null) {
			throw new IllegalArgumentException(ExceptionEnum.UTL0001.name(), ExceptionEnum.UTL0001.getValue());
		}
		log.debug("Decrypting data...");
		byte[] decodedData = Base64.getDecoder().decode(encryptedData);
		byte[] iv = new byte[IV_SIZE];
		System.arraycopy(decodedData, 0, iv, 0, iv.length);
		GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key, spec);
		byte[] encrypted = new byte[decodedData.length - IV_SIZE];
		System.arraycopy(decodedData, IV_SIZE, encrypted, 0, encrypted.length);
		byte[] decrypted = cipher.doFinal(encrypted);
		log.debug("Data decrypted successfully.");
		return new String(decrypted, StandardCharsets.UTF_8);
	}

	/**
	 * Deriva una clave secreta a partir de la clave base utilizando PBKDF2 con HMAC
	 * SHA-256.
	 *
	 * @param baseKey la clave base proporcionada por el usuario
	 * @return la clave secreta derivada para encriptar o desencriptar datos
	 */
	private SecretKey deriveKey(String baseKey) {
		String deriveKey = "PBKDF2WithHmacSHA256";
		int keySize = 256;
		int iterationCount = 65536;
		byte[] salt = "static-salt-value".getBytes();

		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance(deriveKey);
			KeySpec spec = new PBEKeySpec(baseKey.toCharArray(), salt, iterationCount, keySize);
			byte[] secretKey = factory.generateSecret(spec).getEncoded();
			return new SecretKeySpec(secretKey, "AES");
		} catch (Exception e) {
			throw new InternalServerErrorException(ExceptionEnum.UTL0002.name(), ExceptionEnum.UTL0002.getValue());
		}
	}

	/**
	 * Aplica la función de hash sobre el número de serie usando la seed
	 * proporcionada. Realiza un XOR con la seed, calcula el SHA-256, toma los 8
	 * primeros caracteres, los convierte a número y lo formatea a un string de 8
	 * dígitos.
	 *
	 * @param serialNumber el número de serie original
	 * @param seed         la seed a utilizar (valor int de 8 dígitos, ej. 20251117)
	 * @return un string de 8 dígitos representando la "contraseña" ofuscada
	 */
	public String applySerialHashAsString(long serialNumber, int seed) {
		validateSeed(seed);
		long xorResult = ((long) seed) ^ serialNumber;
		String hash = calculateSHA256(Long.toString(xorResult));
		String leftmost8 = hash.substring(0, 8);
		long parsed = Long.parseUnsignedLong(leftmost8, 16);
		long modResult = parsed % 100_000_000L;
		return String.format("%08d", modResult);
	}

	/**
	 * Calcula el hash SHA-256 de una cadena.
	 *
	 * @param input la cadena de entrada
	 * @return el hash en formato hexadecimal
	 */
	public String calculateSHA256(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			return bytesToHex(encodedhash);
		} catch (NoSuchAlgorithmException e) {
			throw new ConflictException(ExceptionEnum.UTL0007.name(), ExceptionEnum.UTL0007.getValue());
		}
	}

	/**
	 * Convierte un array de bytes a una cadena hexadecimal.
	 *
	 * @param hash el arreglo de bytes
	 * @return la representación hexadecimal
	 */
	private String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder(2 * hash.length);
		for (byte b : hash) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	/**
	 * Valida que la seed informada tenga exactamente 8 dígitos.
	 *
	 * @param seed el valor de la seed (por ejemplo, 20251117)
	 * @throws IllegalArgumentException si la seed no es un número de 8 dígitos
	 */
	public void validateSeed(int seed) {
		if (seed < 10000000 || seed > 99999999) {
			throw new ConflictException(ExceptionEnum.UTL0008.name(), ExceptionEnum.UTL0008.getValue());
		}
	}
}