/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.controller;

import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import nl.gidsopenstandaarden.ri.portal.exception.NotLoggedInException;
import nl.gidsopenstandaarden.ri.portal.service.PortalUserService;
import org.apache.commons.lang3.StringUtils;
import org.gidsopenstandaarden.solid.client.OAuth2Token;
import org.gidsopenstandaarden.solid.client.SolidPodClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
	protected final PortalUserService portalUserService;
	protected final SolidPodClient solidPodClient;

	public UserController(PortalUserService portalUserService, SolidPodClient solidPodClient) {
		this.portalUserService = portalUserService;
		this.solidPodClient = solidPodClient;
	}

	@RequestMapping("caregivers")
	public List<UserWithAccessValueObject> caregivers(HttpSession session) throws IOException {
		PortalUser user = (PortalUser) session.getAttribute("user");
		if (user == null) {
			throw new NotLoggedInException("No active session found");
		}

		List<String> acls  = new ArrayList<>();
		OAuth2Token token = (OAuth2Token) session.getAttribute("token");
		if (token != null){
			acls = solidPodClient.getReadAcl(token);
		}
		List<UserWithAccessValueObject> rv = new ArrayList<>();
		List<PortalUser> caregivers = portalUserService.getCaregivers(user);
		for (PortalUser caregiver : caregivers) {
			rv.add(new UserWithAccessValueObject(caregiver, acls.contains(caregiver.getWebId())));
		}
		return rv;
	}

	@RequestMapping
	public PortalUser get(HttpSession httpSession) {
		PortalUser user = (PortalUser) httpSession.getAttribute("user");
		if (user == null) {
			throw new NotLoggedInException("No active session found.");
		}
		return user;
	}

	@RequestMapping("patients")
	public List<UserWithAccessValueObject> patients(HttpSession session) throws IOException {
		PortalUser user = (PortalUser) session.getAttribute("user");
		if (user == null) {
			throw new NotLoggedInException("No active session found.");
		}
		OAuth2Token token = (OAuth2Token) session.getAttribute("token");
		List<UserWithAccessValueObject> rv = new ArrayList<>();
		List<PortalUser> patients = portalUserService.getPatients(user);
		for (PortalUser patient : patients) {
			boolean hasReadAccess = false;
			if (token != null && StringUtils.isNotEmpty(patient.getWebId())) {
				hasReadAccess = solidPodClient.canReadPodOf(token, patient.getWebId());

			}
			rv.add(new UserWithAccessValueObject(patient, hasReadAccess));
		}
		return rv;
	}

	public class UserWithAccessValueObject {
		final PortalUser parent;
		final boolean hasReadAccess;

		public UserWithAccessValueObject(PortalUser parent, boolean hasReadAccess) {
			this.parent = parent;
			this.hasReadAccess = hasReadAccess;
		}

		public Long getId() {
			return parent.getId();
		}

		public String getIdentifier() {
			return parent.getIdentifier();
		}

		public String getSubject() {
			return parent.getSubject();
		}

		public String getType() {
			return parent.getType();
		}

		public String getWebId() {
			return parent.getWebId();
		}

		public boolean isHasReadAccess() {
			return hasReadAccess;
		}
	}
}
