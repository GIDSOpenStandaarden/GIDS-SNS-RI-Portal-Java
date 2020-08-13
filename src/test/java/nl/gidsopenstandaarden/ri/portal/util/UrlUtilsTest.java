/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public class UrlUtilsTest {
	@Test
	public void testGetServerUrlNormal1() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getServerPort()).thenReturn(80);
		Mockito.when(request.getScheme()).thenReturn("http");
		Mockito.when(request.getServerName()).thenReturn("example.com");
		Assertions.assertEquals("http://example.com/api/test", UrlUtils.getServerUrl("/api/test", request));
	}
	@Test
	public void testGetServerUrlNormal2() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getServerPort()).thenReturn(443);
		Mockito.when(request.getScheme()).thenReturn("https");
		Mockito.when(request.getServerName()).thenReturn("example.com");
		Assertions.assertEquals("https://example.com/api/test", UrlUtils.getServerUrl("/api/test", request));
	}

	@Test
	public void testGetServerUrlPort1() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getServerPort()).thenReturn(8443);
		Mockito.when(request.getScheme()).thenReturn("https");
		Mockito.when(request.getServerName()).thenReturn("example.com");
		Assertions.assertEquals("https://example.com:8443/api/test", UrlUtils.getServerUrl("/api/test", request));
	}

	@Test
	public void testGetServerUrlPort2() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getServerPort()).thenReturn(8080);
		Mockito.when(request.getScheme()).thenReturn("http");
		Mockito.when(request.getServerName()).thenReturn("example.com");
		Assertions.assertEquals("http://example.com:8080/api/test", UrlUtils.getServerUrl("/api/test", request));
	}

	@Test
	public void testGetServerUrlSwap1() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getServerPort()).thenReturn(80);
		Mockito.when(request.getScheme()).thenReturn("https");
		Mockito.when(request.getServerName()).thenReturn("example.com");
		Assertions.assertEquals("https://example.com:80/api/test", UrlUtils.getServerUrl("/api/test", request));
	}

	@Test
	public void testGetServerUrlSwap2() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getServerPort()).thenReturn(443);
		Mockito.when(request.getScheme()).thenReturn("http");
		Mockito.when(request.getServerName()).thenReturn("example.com");
		Assertions.assertEquals("http://example.com:443/api/test", UrlUtils.getServerUrl("/api/test", request));
	}

	@Test
	public void testIsDefault() {
		Assertions.assertTrue(UrlUtils.isDefault("http", 80));
		Assertions.assertTrue(UrlUtils.isDefault("http", -1));
		Assertions.assertTrue(UrlUtils.isDefault("https", 443));
		Assertions.assertTrue(UrlUtils.isDefault("https", -1));
		Assertions.assertFalse(UrlUtils.isDefault("http", 8080));
		Assertions.assertFalse(UrlUtils.isDefault("http", 443));
		Assertions.assertFalse(UrlUtils.isDefault("https", 80));
		Assertions.assertFalse(UrlUtils.isDefault("https", 8443));
	}
}
