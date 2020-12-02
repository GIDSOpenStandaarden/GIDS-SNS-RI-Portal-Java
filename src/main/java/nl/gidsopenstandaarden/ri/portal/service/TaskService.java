package nl.gidsopenstandaarden.ri.portal.service;

import nl.gidsopenstandaarden.ri.portal.entity.Task;
import nl.gidsopenstandaarden.ri.portal.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *
 */
@Service
public class TaskService {
	private final TaskRepository taskRepository;

	public TaskService(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public Task getByDefinitionReferenceAndForUser(String treatmentReference, String userReference) {
		return taskRepository.findTaskByDefinitionReferenceAndForUser(treatmentReference, userReference).orElse(null);
	}

	public Task getTask(String identifier) {
		return taskRepository.findTaskByIdentifier(identifier).orElse(null);
	}

	public void save(Task task) {
		taskRepository.save(task);
	}
}
