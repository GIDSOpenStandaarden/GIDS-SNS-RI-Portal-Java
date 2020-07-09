/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.controller;

import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import nl.gidsopenstandaarden.ri.portal.exception.NotLoggedInException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 *
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
	@RequestMapping
	public PortalUser get(HttpSession httpSession) {
		PortalUser user = (PortalUser) httpSession.getAttribute("user");
		if (user == null) {
			throw new NotLoggedInException("No active session found.");
		}
		return user;
	}
}
