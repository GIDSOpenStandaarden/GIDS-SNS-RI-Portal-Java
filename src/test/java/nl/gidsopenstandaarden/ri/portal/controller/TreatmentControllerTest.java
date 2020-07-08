package nl.gidsopenstandaarden.ri.portal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.gidsopenstandaarden.ri.portal.configuration.HtiConfiguration;
import nl.gidsopenstandaarden.ri.portal.entities.PortalUser;
import nl.gidsopenstandaarden.ri.portal.entities.Treatment;
import nl.gidsopenstandaarden.ri.portal.service.PortalUserService;
import nl.gidsopenstandaarden.ri.portal.service.TreatmentService;
import nl.gidsopenstandaarden.ri.portal.util.KeyUtils;
import nl.gidsopenstandaarden.ri.portal.valueobject.TaskValueObject;
import org.hamcrest.core.StringContains;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TreatmentControllerTest {
	@Autowired
	PortalUserService portalUserService;
	@Autowired
	TreatmentService treatmentService;
	@Autowired
	HtiConfiguration htiConfiguration;
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getNoSession() throws Exception {
		mockMvc.perform(get("/api/treatment"))
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()));
	}

	@Test
	public void getNormal() throws Exception {
		PortalUser user = portalUserService.getOrCreatePortalUser("sub1");
		mockMvc.perform(get("/api/treatment").sessionAttr("user", user))
				.andExpect(status().is(HttpStatus.OK.value()))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].id").exists());
	}

	@Test
	public void startNoSession() throws Exception {
		mockMvc.perform(get("/api/treatment/1"))
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()))
		;
	}

	@Test
	public void startNormal() throws Exception {
		PortalUser user = portalUserService.getOrCreatePortalUser("sub1");
		List<Treatment> treatments = treatmentService.getTreatmentsForUser(user);
		Assert.notEmpty(treatments, "Expecting some treatments to test with");
		Treatment treatment = treatments.get(0);
		final TaskValueObject[] tasks = {null, null};
		mockMvc.perform(get("/api/treatment/" + treatment.getId()).sessionAttr("user", user))
				.andExpect(status().is(HttpStatus.OK.value()))
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(StringContains.containsString(treatment.getUrl())))
				.andDo(mvcResult -> tasks[0] = getTaskValueObject(mvcResult, treatment));
		mockMvc.perform(get("/api/treatment/" + treatment.getId()).sessionAttr("user", user))
				.andExpect(status().is(HttpStatus.OK.value()))
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(StringContains.containsString(treatment.getUrl())))
				.andDo(mvcResult -> tasks[1] = getTaskValueObject(mvcResult, treatment));

		Assertions.assertEquals(tasks[0].getId(), tasks[1].getId());
	}

	private TaskValueObject getTaskValueObject(MvcResult mvcResult, Treatment treatment) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidJwtException {
		String content = mvcResult.getResponse().getContentAsString();
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(content)));
		XPath xpath = XPathFactory.newInstance().newXPath();
		String token = xpath.evaluate("//form/input[@name='token']/@value", document);

		JwtConsumer jwtConsumer = new JwtConsumerBuilder()
				.setExpectedIssuer(htiConfiguration.getIssuer())
				.setExpectedAudience(treatment.getAud())
				.setVerificationKey(KeyUtils.getRsaPublicKey(htiConfiguration.getPublicKey()))
				.build();
		JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
		;
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.convertValue(jwtClaims.getClaimValue("task"), TaskValueObject.class);
	}
}
