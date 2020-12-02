/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

package nl.gidsopenstandaarden.ri.portal.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 *
 */
@Entity(name = "portal_user")
public class PortalUser implements Serializable {
	@Id
	@GeneratedValue
	private Long id;
	private String identifier;
	private String subject;
	private String webId;

	public Long getId() {
		return id;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getWebId() {
		return webId;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String sub) {
		this.subject = sub;
	}

	public void setWebId(String webId) {
		this.webId = webId;
	}
}
