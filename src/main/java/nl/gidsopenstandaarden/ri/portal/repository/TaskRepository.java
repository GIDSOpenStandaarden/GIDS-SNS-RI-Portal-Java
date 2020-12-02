/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.repository;

import nl.gidsopenstandaarden.ri.portal.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
	Optional<Task> findTaskByDefinitionReferenceAndForUser(String definitionReference, String userReference);

	Optional<Task> findTaskByIdentifier(String identifier);
}
