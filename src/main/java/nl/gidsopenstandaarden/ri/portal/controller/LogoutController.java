/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

/**
 *
 */

@Controller
public class LogoutController {


	public LogoutController() {
	}

	@RequestMapping("/logout")
	public View irmaAuth(HttpSession session) {
		session.removeAttribute("user");
		return new RedirectView("/");

	}

}
