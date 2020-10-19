/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.controller;

import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import nl.gidsopenstandaarden.ri.portal.service.PortalUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

/**
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class IndexControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	PortalUserService portalUserService;


	@Test
	public void indexLogin() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("login.html"));
	}
	@Test
	public void indexLogedin() throws Exception {
		PortalUser user = portalUserService.getOrCreatePortalUser("sub1");
		mockMvc.perform(get("/").sessionAttr("user", user))
				.andExpect(redirectedUrl("index.html"));
	}
}
