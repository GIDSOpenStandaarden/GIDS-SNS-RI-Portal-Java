package nl.gidsopenstandaarden.ri.portal.configuration;

import ca.uhn.fhir.context.FhirContext;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.apache.http.impl.client.HttpClients;
import org.gidsopenstandaarden.solid.client.HttpClientCreator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@ConfigurationProperties(prefix = "solid.pod")
public class SolidFhirConfiguration {
	String idp;

	@Bean
	public FhirContext fhirContext(){
		return FhirContext.forDstu3();
	}

	public String getIdp() {
		return idp;
	}

	public void setIdp(String idp) {
		this.idp = idp;
	}

	@Bean
	public HttpClientCreator httpClientCreator() {
		return HttpClients::createDefault;
	}

	@Bean
	public RSAKey jwtSigingKey() throws JOSEException {
		return new RSAKeyGenerator(2048).algorithm(Algorithm.parse("RS256")).keyIDFromThumbprint(true).keyUse(KeyUse.SIGNATURE).generate();
	}


}

