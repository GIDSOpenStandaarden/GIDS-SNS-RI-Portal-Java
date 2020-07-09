package nl.gidsopenstandaarden.ri.portal.service;

import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import nl.gidsopenstandaarden.ri.portal.repository.PortalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
			return optional.get();
		} else {
			PortalUser portalUser = new PortalUser();
			portalUser.setSubject(subject);
			portalUser.setIdentifier(UUID.randomUUID().toString());
			portalUserRepository.save(portalUser);
			return portalUser;
		}
	}
}
