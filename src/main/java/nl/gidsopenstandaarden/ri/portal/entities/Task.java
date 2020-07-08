package nl.gidsopenstandaarden.ri.portal.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 */
@Entity(name = "task")
public class Task {
	String resourceType = "Task";

	@Id
	@GeneratedValue
	Long id;

	String identifier;
	String definitionReference;
	String status = "requested";
	String intent = "plan";
	String forUser;

	public String getDefinitionReference() {
		return definitionReference;
	}

	public void setDefinitionReference(String definitionReference) {
		this.definitionReference = definitionReference;
	}

	public String getForUser() {
		return forUser;
	}

	public void setForUser(String forUser) {
		this.forUser = forUser;
	}

	public Long getId() {
		return id;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
