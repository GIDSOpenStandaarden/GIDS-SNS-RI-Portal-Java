/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.configuration;

import nl.gidsopenstandaarden.ri.portal.util.KeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
@Configuration
@ConfigurationProperties(prefix = "hti")
public class HtiConfiguration {
	static final Log LOG = LogFactory.getLog(HtiConfiguration.class);
	public String publicKey;
	public String privateKey;
	public String issuerOverride;

	public String getIssuerOverride() {
		return issuerOverride;
	}

	public void setIssuerOverride(String issuerOverride) {
		this.issuerOverride = issuerOverride;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@PostConstruct
	public void init() throws NoSuchAlgorithmException {
		if (StringUtils.isEmpty(publicKey) || StringUtils.isEmpty(privateKey)) {
			LOG.info("HTI public and/or private key not found, generating a new pair");
			KeyPair keyPair = KeyUtils.generateKeyPair();
			publicKey = KeyUtils.encodeKey(keyPair.getPublic());
			privateKey = KeyUtils.encodeKey(keyPair.getPrivate());
			LOG.info(String.format("Generated HTI keypair, public key is:%n%s", KeyUtils.encodeKeyPem(keyPair.getPublic(), "PUBLIC")));
		} else {
			LOG.info(String.format("Public key is:%n%s", KeyUtils.encodeKeyPem(publicKey, "PUBLIC")));
		}

		if (StringUtils.isNotEmpty(getIssuerOverride())) {
			LOG.info(String.format("Issuer override is set to: %S", getIssuerOverride()));
		}
	}
}
