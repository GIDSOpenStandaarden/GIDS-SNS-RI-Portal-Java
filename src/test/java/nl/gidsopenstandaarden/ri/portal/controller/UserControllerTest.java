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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	PortalUserService portalUserService;

	@Test
	public void getNoSession() throws Exception {
		mockMvc.perform(get("/api/user"))
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()));
	}
	@Test
	public void getNormal() throws Exception {
		PortalUser user = portalUserService.getOrCreatePortalUser("sub1");
		mockMvc.perform(get("/api/user").sessionAttr("user", user))
				.andExpect(jsonPath("$.id").exists());
	}
}
