/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.controller;

import nl.gidsopenstandaarden.ri.portal.configuration.IrmaClientConfiguration;
import nl.gidsopenstandaarden.ri.portal.configuration.LoginSrvConfiguration;
import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import nl.gidsopenstandaarden.ri.portal.service.PortalUserService;
import nl.gidsopenstandaarden.ri.portal.util.KeyUtils;
import nl.gidsopenstandaarden.ri.portal.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

/**
 *
 */

@Controller
public class LoginController {

	private final IrmaClientConfiguration irmaClientConfiguration;
	private final LoginSrvConfiguration loginSrvConfiguration;

	private final PortalUserService portalUserService;

	public LoginController(IrmaClientConfiguration irmaClientConfiguration, LoginSrvConfiguration loginSrvConfiguration, PortalUserService portalUserService) {
		this.irmaClientConfiguration = irmaClientConfiguration;
		this.loginSrvConfiguration = loginSrvConfiguration;
		this.portalUserService = portalUserService;
	}

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

	@RequestMapping("/login/irma")
	public View loginIrma(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("redirect_uri", UrlUtils.getServerUrl("/irma-auth", request));
		return new RedirectView(irmaClientConfiguration.getServerUrl());
	}

	@RequestMapping("/login/{type}")
	public View loginLoginSrv(@PathVariable(value = "type", required = false) String type, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		type = StringUtils.defaultString(type, "simple");
		return new RedirectView(loginSrvConfiguration.getServerUrl() + "/" + type);
	}

	@RequestMapping(value = "/loginsrv-auth")
	public View loginSrvAuth(@CookieValue("jwt_token") String jwtToken, HttpSession session) throws InvalidJwtException, MalformedClaimException, InvalidKeySpecException, NoSuchAlgorithmException {

		final RSAPublicKey rsaPublicKey = KeyUtils.getRsaPublicKey(loginSrvConfiguration.getJwtPublicKey());
		JwtConsumer jwtConsumer = new JwtConsumerBuilder()
				.setSkipSignatureVerification()
				.setVerificationKey(rsaPublicKey)
				.build();

		final JwtClaims jwtClaims = jwtConsumer.processToClaims(jwtToken);
		final String subject = jwtClaims.getSubject();
		PortalUser portalUser = portalUserService.getOrCreatePortalUser(subject);
		session.setAttribute("user", portalUser);
		return new RedirectView("/");
	}

}
