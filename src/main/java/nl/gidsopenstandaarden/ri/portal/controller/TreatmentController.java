/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.controller;

import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import nl.gidsopenstandaarden.ri.portal.entity.Treatment;
import nl.gidsopenstandaarden.ri.portal.exception.NotLoggedInException;
import nl.gidsopenstandaarden.ri.portal.service.HtiLaunchService;
import nl.gidsopenstandaarden.ri.portal.service.TreatmentService;
import nl.gidsopenstandaarden.ri.portal.util.UrlUtils;
import nl.gidsopenstandaarden.ri.portal.valueobject.LaunchValueObject;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping("/api/treatment")
public class TreatmentController {

	private TreatmentService treatmentService;
	private HtiLaunchService htiLaunchService;

	@Autowired
	public void setHtiLaunchService(HtiLaunchService htiLaunchService) {
		this.htiLaunchService = htiLaunchService;
	}

	@Autowired
	public void setTreatmentService(TreatmentService treatmentService) {
		this.treatmentService = treatmentService;
	}

	@RequestMapping(value = "{id}", produces = MediaType.TEXT_HTML_VALUE)
	public String start(@PathVariable("id") String id, HttpSession session, HttpServletRequest request) throws JoseException, MalformedURLException {
		PortalUser portalUser = (PortalUser) session.getAttribute("user");
		if (portalUser == null) {
			throw new NotLoggedInException("No active session found");
		}
		Treatment treatment = treatmentService.getTreatment(id);

		LaunchValueObject launchValueObject = htiLaunchService.startLaunch(portalUser, treatment, UrlUtils.getServerUrl("", request));

		return "<html>\n" +
				"<head>\n" +
				"</head>\n" +
				"<body onload=\"document.forms[0].submit();\">\n" +
				"<form action=\""+ launchValueObject.getUrl()+"\" method=\"post\">\n" +
				"<input type=\"hidden\" name=\"token\" value=\""+ launchValueObject.getToken()+"\"/>\n" +
				"</form>\n" +
				"</body>\n" +
				"</html>";
	}

	@GetMapping
	public List<Treatment> treatments(HttpSession session) {
		PortalUser portalUser = (PortalUser) session.getAttribute("user");
		if (portalUser == null) {
			throw new NotLoggedInException("No active session found");
		}
		return treatmentService.getTreatmentsForUser(portalUser);
	}

	public static class Treatments {
		List<Treatment> treatments;

		public List<Treatment> getTreatments() {
			return treatments;
		}

		public void setTreatments(List<Treatment> treatments) {
			this.treatments = treatments;
		}
	}
}
