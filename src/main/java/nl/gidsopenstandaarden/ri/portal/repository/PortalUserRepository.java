package nl.gidsopenstandaarden.ri.portal.repository;

import nl.gidsopenstandaarden.ri.portal.entity.PortalUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *
 */
public interface PortalUserRepository extends JpaRepository<PortalUser, Long> {
	Optional<PortalUser> findBySubject(String subject);
}
