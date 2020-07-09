/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 */
@Entity(name = "task")
public class Task {
	/**
	 * Field should always be "Task"
	 */
	String resourceType = "Task";
	String status = "requested";
	String intent = "plan";

	@Id
	@GeneratedValue
	Long id;

	String identifier;
	String definitionReference;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
