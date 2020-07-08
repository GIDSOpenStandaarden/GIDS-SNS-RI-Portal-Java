package nl.gidsopenstandaarden.ri.portal.repository;

import nl.gidsopenstandaarden.ri.portal.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
	Optional<Task> findTaskByDefinitionReferenceAndForUser(String definitionReference, String userReference);
}
