/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class KeyUtilsTest {

	@Test
	public void encodeKeyPem1() {
		String tenblock = "1234567890";
		String test = String.format("%s%<s%<s%<s", tenblock);
		String encoded = KeyUtils.encodeKeyPem(test, "TEST");
		Assertions.assertEquals(String.format("-----BEGIN TEST KEY-----\n" +
				"%s%<s%<s%<s\n" +
				"-----END TEST KEY-----", tenblock), encoded);
	}
	@Test
	public void encodeKeyPem2() {
		String tenblock = "1234567890";
		String test = String.format("%s%<s%<s%<s%<s%<s%<s%<s", tenblock);
		String encoded = KeyUtils.encodeKeyPem(test, "TEST");
		Assertions.assertEquals(String.format("-----BEGIN TEST KEY-----\n" +
				"%s%<s%<s%<s%<s%<s%<s%<s\n" +
				"-----END TEST KEY-----", tenblock), encoded);
	}
	@Test
	public void encodeKeyPem3() {
		String tenblock = "1234567890";
		String test = String.format("%s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s", tenblock);
		String encoded = KeyUtils.encodeKeyPem(test, "TEST");
		Assertions.assertEquals(String.format("-----BEGIN TEST KEY-----\n" +
				"%s%<s%<s%<s%<s%<s%<s%<s\n" +
				"%<s%<s%<s%<s%<s%<s%<s%<s\n" +
				"%<s\n" +
				"-----END TEST KEY-----", tenblock), encoded);
	}
	@Test
	public void encodeKeyPem4() {
		String tenblock = "1234567890";
		String test = String.format("%s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s", tenblock);
		String encoded = KeyUtils.encodeKeyPem(test, "TEST");
		Assertions.assertEquals(String.format("-----BEGIN TEST KEY-----\n" +
				"%s%<s%<s%<s%<s%<s%<s%<s\n" +
				"%<s%<s%<s%<s%<s%<s%<s%<s\n" +
				"-----END TEST KEY-----", tenblock), encoded);
	}
}
