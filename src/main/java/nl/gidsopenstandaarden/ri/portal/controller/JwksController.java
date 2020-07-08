/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

package nl.gidsopenstandaarden.ri.portal.controller;

import nl.gidsopenstandaarden.ri.portal.configuration.JwtConfiguration;
import nl.gidsopenstandaarden.ri.portal.util.KeyUtils;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 *
 */
@RestController
@RequestMapping(".well-known/jwks.json")
public class JwksController {
	private JwtConfiguration jwtConfiguration;

	@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public String get() throws InvalidKeySpecException, NoSuchAlgorithmException, JoseException {
		KeyPair rsaKeyPair = KeyUtils.getRsaKeyPair(jwtConfiguration.getPublicKey(), jwtConfiguration.getPrivateKey());
		JsonWebKey jsonWebKey = JsonWebKey.Factory.newJwk(rsaKeyPair.getPublic());
		jsonWebKey.setKeyId(jsonWebKey.calculateBase64urlEncodedThumbprint("MD5"));
		JsonWebKeySet jsonWebKeySet = new JsonWebKeySet(jsonWebKey);
		return jsonWebKeySet.toJson();
	}

	@Autowired
	public void setJwtConfiguration(JwtConfiguration jwtConfiguration) {
		this.jwtConfiguration = jwtConfiguration;
	}
}
