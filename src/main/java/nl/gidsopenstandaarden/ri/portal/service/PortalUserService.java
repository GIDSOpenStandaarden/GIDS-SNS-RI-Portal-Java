/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.service;

import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import nl.gidsopenstandaarden.ri.portal.repository.PortalUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
@Service
public class PortalUserService {
	@Autowired
	PortalUserRepository portalUserRepository;

	public PortalUser getOrCreatePortalUser(String subject) {
		Optional<PortalUser> optional = portalUserRepository.findBySubject(subject);
		if (optional.isPresent()) {
			PortalUser portalUser = optional.get();
			// TODO: take out these 2 lines after update.
			portalUser.setType((isCareGiver(subject) ? "CareGiver" : "Patient"));
			portalUserRepository.save(portalUser);
			return portalUser;
		} else {
			PortalUser portalUser = new PortalUser();
			portalUser.setSubject(subject);
			portalUser.setIdentifier(UUID.randomUUID().toString());
			portalUser.setType((isCareGiver(subject) ? "CareGiver" : "Patient"));
			portalUserRepository.save(portalUser);
			return portalUser;
		}
	}

	public PortalUser getPortalUser(Long id) {
		return portalUserRepository.findById(id).orElse(null);
	}

	public boolean isCareGiver(String subject) {
		return StringUtils.endsWith(subject, "@edia.nl") || StringUtils.endsWith(subject, "@headease.nl");
	}

	public boolean updateWebId(PortalUser user, String webId) {
		Optional<PortalUser> optional = portalUserRepository.findById(user.getId());
		if (optional.isPresent()) {
			user = optional.get();
			user.setWebId(webId);
			portalUserRepository.save(user);
			return false;
		}
		return true;
	}

	public List<PortalUser> getPatients(PortalUser careGiver) {
		return portalUserRepository.findByType("Patient");
	}
	public List<PortalUser> getCaregivers(PortalUser patient) {
		return portalUserRepository.findByType("CareGiver");
	}
}
