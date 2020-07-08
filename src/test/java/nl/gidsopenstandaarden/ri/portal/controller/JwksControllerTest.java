package nl.gidsopenstandaarden.ri.portal.controller;

import nl.gidsopenstandaarden.ri.portal.configuration.HtiConfiguration;
import nl.gidsopenstandaarden.ri.portal.util.KeyUtils;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwk.JsonWebKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwksControllerTest {

	@LocalServerPort
	private int port;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private HtiConfiguration htiConfiguration;

	@Test
	public void testGet() throws Exception {
		mockMvc.perform(get("/.well-known/jwks.json"))
				.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/json"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.keys[0].kty").value("RSA"))
		;

		HttpsJwks httpsJwks = new HttpsJwks(String.format("http://localhost:%d/.well-known/jwks.json", port));
		List<JsonWebKey> jsonWebKeys = httpsJwks.getJsonWebKeys();
		Assertions.assertEquals(1, jsonWebKeys.size());
		JsonWebKey jsonWebKey = jsonWebKeys.get(0);

		Assertions.assertEquals(htiConfiguration.getPublicKey(), KeyUtils.encodeKey(jsonWebKey.getKey()));
	}
}
