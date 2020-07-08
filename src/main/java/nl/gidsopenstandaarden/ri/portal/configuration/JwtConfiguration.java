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
@ConfigurationProperties(prefix = "jwt")
public class JwtConfiguration {
	public String publicKey;
	public String privateKey;
	public String issuer;
	static final Log LOG = LogFactory.getLog(JwtConfiguration.class);

	@PostConstruct
	public void init() throws NoSuchAlgorithmException {
		if (StringUtils.isEmpty(publicKey) || StringUtils.isEmpty(privateKey)) {
			LOG.info("Public and/or private key not found, generating a new pair");
			KeyPair keyPair = KeyUtils.generateKeyPair();
			publicKey = KeyUtils.encodeKey(keyPair.getPublic());
			privateKey = KeyUtils.encodeKey(keyPair.getPrivate());
			LOG.info(String.format("Generated keypair, public key is:\n%s", KeyUtils.encodeKeyPem(keyPair.getPublic(), "PUBLIC")));
		}
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
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
}
