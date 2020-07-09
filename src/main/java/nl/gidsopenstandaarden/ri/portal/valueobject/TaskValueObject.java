/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.valueobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskValueObject {
	String resourceType = "Task";
	String id;
	Reference definitionReference = new Reference();
	String status = "requested";
	String intent = "plan";
	@JsonProperty("for")
	Reference forUser = new Reference();

	public Reference getDefinitionReference() {
		return definitionReference;
	}

	public Reference getForUser() {
		return forUser;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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


	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Reference {
		String reference;

		public String getReference() {
			return reference;
		}

		public void setReference(String reference) {
			this.reference = reference;
		}
	}


}
