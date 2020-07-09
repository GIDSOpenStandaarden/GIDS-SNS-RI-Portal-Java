package nl.gidsopenstandaarden.ri.portal.repository;

import nl.gidsopenstandaarden.ri.portal.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 */
public interface TreatmentRepository extends JpaRepository<Treatment, String> {
	Treatment getById(String id);
}
