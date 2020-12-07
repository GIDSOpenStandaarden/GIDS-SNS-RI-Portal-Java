/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.controller;

import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 *
 */
@Controller
@RequestMapping("/")
public class IndexController {

	@RequestMapping
	public String index(HttpSession session) {
		if (!isLoggedIn(session)) {
			return "redirect:login.html";
		}
		PortalUser user = (PortalUser)session.getAttribute("user");
		if (StringUtils.equals(user.getType(), "CareGiver"))
			return "redirect:caregiver.html";

		return "redirect:patient.html";
	}

	private boolean isLoggedIn(HttpSession session) {
		return session.getAttribute("user") != null;
	}
}
