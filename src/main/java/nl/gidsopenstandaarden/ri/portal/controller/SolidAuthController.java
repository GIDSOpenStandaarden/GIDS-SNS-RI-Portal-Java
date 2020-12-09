package nl.gidsopenstandaarden.ri.portal.controller;

import nl.gidsopenstandaarden.ri.portal.configuration.SolidFhirConfiguration;
import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import nl.gidsopenstandaarden.ri.portal.service.PortalUserService;
import org.gidsopenstandaarden.solid.client.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Controller
@RequestMapping("/solid")
public class SolidAuthController {
	private final SolidAuthClient solidAuthClient;
	private final SolidPodClient solidPodClient;
	private final PortalUserService portalUserService;
	private final SolidFhirClient solidFhirClient;
	private final SolidFhirConfiguration solidFhirConfiguration;

	public SolidAuthController(SolidAuthClient solidAuthClient, SolidPodClient solidPodClient, PortalUserService portalUserService, SolidFhirClient solidFhirClient, SolidFhirConfiguration solidFhirConfiguration) {
		this.solidAuthClient = solidAuthClient;
		this.solidPodClient = solidPodClient;
		this.portalUserService = portalUserService;
		this.solidFhirClient = solidFhirClient;
		this.solidFhirConfiguration = solidFhirConfiguration;
	}

	@RequestMapping("/auth")
	public View auth(HttpSession httpSession, HttpServletRequest request) throws IOException {
		String redirectUrl = getRedirectUri(request);
		final HashMap<String, Object> state = new HashMap<>();
		URL authorizeUrl = solidAuthClient.authorize(solidFhirConfiguration.getUrl(), state, redirectUrl);
		httpSession.setAttribute("state", state);
		return new RedirectView(authorizeUrl.toExternalForm());
	}

	@RequestMapping(value = "session", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	SolidSessionValueObject session(HttpSession session) throws ParseException {
		OAuth2Token token = (OAuth2Token) session.getAttribute("token");
		SolidSessionValueObject sessionValueObject = new SolidSessionValueObject();
		if (token != null) {
			sessionValueObject.setWebId(solidPodClient.getWebId(token));
			sessionValueObject.setLoggedIn(true);
		} else
			sessionValueObject.setLoggedIn(false);
		return sessionValueObject;
	}

	@RequestMapping("/solid_auth")
	@SuppressWarnings("unchecked")
	public String solidAuth(String code, HttpSession session, HttpServletRequest request) throws IOException, ParseException {
		Map<String, Object> state = (Map<String, Object>) session.getAttribute("state");
		String redirectUrl = getRedirectUri(request);
		OAuth2Token token = solidAuthClient.token(code, redirectUrl, state);
		PortalUser user = (PortalUser) session.getAttribute("user");
		if (user != null) {
			portalUserService.updateWebId(user, solidPodClient.getWebId(token));
		}
		session.setAttribute("token", token);
		solidFhirClient.ensureSolidDirectories(token);
		return "redirect:/";
	}

	private String getRedirectUri(HttpServletRequest request) throws IOException {
		final StringBuffer requestURL = request.getRequestURL();
		final String path = "/solid/solid_auth";
		return UrlUtils.getBaseUrl(requestURL.toString(), path);
	}

	public static class SolidSessionValueObject{
		String webId;
		boolean isLoggedIn;

		public String getWebId() {
			return webId;
		}

		public void setWebId(String webId) {
			this.webId = webId;
		}

		public boolean isLoggedIn() {
			return isLoggedIn;
		}

		public void setLoggedIn(boolean loggedIn) {
			isLoggedIn = loggedIn;
		}
	}

}
