/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.*;

/**
 *
 */

@Controller
public class LogoutController {


	public LogoutController() {
	}

	@RequestMapping("/logout")
	public View irmaAuth(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		session.removeAttribute("user");
		session.removeAttribute("token");
		removeCookie("jwt_token", request, response);
		return new RedirectView("/");

	}

	private void removeCookie(String cookieName, HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (StringUtils.equals(cookieName, cookie.getName())) {
				cookie.setValue("");
				cookie.setPath("/");
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}

}
