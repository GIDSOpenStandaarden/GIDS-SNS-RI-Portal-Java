/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.controller;

import nl.gidsopenstandaarden.ri.portal.configuration.IrmaClientConfiguration;
import nl.gidsopenstandaarden.ri.portal.service.PortalUserService;
import nl.gidsopenstandaarden.ri.portal.util.KeyUtils;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;
import java.security.KeyPair;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

/**
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginControllerTest {

	@Autowired
	PortalUserService portalUserService;
	@Autowired
	IrmaClientConfiguration irmaClientConfiguration;
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void irmaAuth() throws Exception {
		String redirectUri = "http://localhost/irma-auth";
		KeyPair keyPair = KeyUtils.generateKeyPair();
		irmaClientConfiguration.setPublicKey(KeyUtils.encodeKey(keyPair.getPublic()));

		JsonWebSignature jws = new JsonWebSignature();
		JwtClaims claims = new JwtClaims();
		claims.setIssuer(irmaClientConfiguration.getIssuer());
		claims.setAudience(redirectUri);
		jws.setPayload(claims.toJson());
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);
		jws.setKey(keyPair.getPrivate());

		MockHttpSession session = new MockHttpSession();
		mockMvc.perform(get("/irma-auth").session(session).param("token", jws.getCompactSerialization()))
				.andExpect(redirectedUrl("/"));

		mockMvc.perform(get("/").session(session)).andExpect(redirectedUrl("index.html"));
	}

	@Test
	public void login() throws Exception {
		String redirectUri = "http://localhost/irma-auth";

		mockMvc.perform(get("/login/irma"))
				.andExpect(redirectedUrl(irmaClientConfiguration.getServerUrl() + "?redirect_uri=" + URLEncoder.encode(redirectUri, "ascii")));
	}

}
