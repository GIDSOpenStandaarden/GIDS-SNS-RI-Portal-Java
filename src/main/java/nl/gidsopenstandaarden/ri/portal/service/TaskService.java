package nl.gidsopenstandaarden.ri.portal.service;

import nl.gidsopenstandaarden.ri.portal.entity.Task;
import nl.gidsopenstandaarden.ri.portal.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
		List<Task> list = taskRepository.findTasksByDefinitionReferenceAndForUser(treatmentReference, userReference);
		if (list.size() == 0) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		}
		throw new RuntimeException("Multiple results for getByDefinitionReferenceAndForUser");
	}

	public Task getTask(String identifier) {
		return taskRepository.findTaskByIdentifier(identifier).orElse(null);
	}

	public void save(Task task) {
		taskRepository.save(task);
	}
}
