package nl.gidsopenstandaarden.ri.portal.controller;

import nl.gidsopenstandaarden.ri.portal.configuration.IrmaClientConfiguration;
import nl.gidsopenstandaarden.ri.portal.entities.PortalUser;
import nl.gidsopenstandaarden.ri.portal.service.PortalUserService;
import nl.gidsopenstandaarden.ri.portal.util.KeyUtils;
import nl.gidsopenstandaarden.ri.portal.util.UrlUtils;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 *
 */

@Controller
public class LoginController {

	IrmaClientConfiguration irmaClientConfiguration;

	PortalUserService portalUserService;

	@RequestMapping("/irma-auth")
	public View irmaAuth(@RequestParam(name = "token") String token, HttpSession session, HttpServletRequest request) throws InvalidJwtException, MalformedClaimException, InvalidKeySpecException, NoSuchAlgorithmException {
		JwtConsumer jwtConsumer = new JwtConsumerBuilder()
				.setExpectedIssuer(irmaClientConfiguration.getIssuer())
				.setExpectedAudience(UrlUtils.getServerUrl("/irma-auth", request))
				.setRelaxVerificationKeyValidation() // Keysize of the irma pod is 2024 instead of 2042.
				.setVerificationKey(KeyUtils.getRsaPublicKey(irmaClientConfiguration.getPublicKey()))
				.build();
		JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
		String subject = jwtClaims.getSubject();

		PortalUser portalUser = portalUserService.getOrCreatePortalUser(subject);
		session.setAttribute("user", portalUser);
		return new RedirectView("/");

	}

	@RequestMapping("/login")
	public View login(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("redirect_uri", UrlUtils.getServerUrl("/irma-auth", request));
		return new RedirectView(irmaClientConfiguration.getServerUrl());
	}

	@Autowired
	public void setIrmaClientConfiguration(IrmaClientConfiguration irmaClientConfiguration) {
		this.irmaClientConfiguration = irmaClientConfiguration;

	}

	@Autowired
	public void setPortalUserService(PortalUserService portalUserService) {
		this.portalUserService = portalUserService;
	}
}
