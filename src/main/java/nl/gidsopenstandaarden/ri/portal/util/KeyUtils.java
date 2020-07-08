/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

package nl.gidsopenstandaarden.ri.portal.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;

/**
 *
 */
public class KeyUtils {

	/**
	 * Parses a public key to an instance of {@link RSAPublicKey}.
	 *
	 * @param publicKey the string representation of the public key.
	 * @return an instance of {@link RSAPublicKey}.
	 */
	public static RSAPublicKey getRsaPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return (RSAPublicKey) keyFactory.generatePublic(
				new X509EncodedKeySpec(getEncodedKey(publicKey)));
	}

	private static byte[] getEncodedKey(String key) {
		try {
			BufferedReader br = new BufferedReader(new StringReader(key));
			StringBuilder rawKey = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				if (!StringUtils.startsWith(line, "----")) {
					rawKey.append(line);
				}
			}

			return Base64.decodeBase64(rawKey.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static RSAPrivateKey getRsaPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return (RSAPrivateKey) keyFactory.generatePrivate(
				new PKCS8EncodedKeySpec(getEncodedKey(privateKey)));
	}

	public static KeyPair getRsaKeyPair(String publicKey, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return new KeyPair(getRsaPublicKey(publicKey), getRsaPrivateKey(privateKey));

	}

	public static String encodeKey(Key key) {
		return encodeBase64String(key.getEncoded());
	}

	public static String encodeKeyPem(Key key, String type) {
		String publicKeyContent = encodeKey(key);
		return encodeKeyPem(publicKeyContent, type);
	}

	public static String encodeKeyPem(String key, String type) {
		String publicKeyFormatted = "-----BEGIN " + type + " KEY-----";
		publicKeyFormatted += System.lineSeparator();
		publicKeyFormatted += chopOn80(key);
		publicKeyFormatted += System.lineSeparator();
		publicKeyFormatted += "-----END " + type + " KEY-----";
		return publicKeyFormatted;
	}

	private static String chopOn80(String key) {
		StringBuilder builder = new StringBuilder();
		int pos = 0;
		for (char c : key.toCharArray()) {
			if (pos > 0 && pos % 80 == 0) {
				builder.append(System.lineSeparator());
			}
			builder.append(c);
			pos++;
		}
		return builder.toString();
	}

	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		return generateKeyPair(2048);
	}

	public static KeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException {
		// Create a new generator
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		// Set the key size
		generator.initialize(keySize);
		// Generate a pair
		return generator.generateKeyPair();
	}
}
