package nl.gidsopenstandaarden.ri.portal.controller;

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
			return "/login";
		}
		return "redirect:index.html";
	}

	private boolean isLoggedIn(HttpSession session) {
		return session.getAttribute("user") != null;
	}
}
