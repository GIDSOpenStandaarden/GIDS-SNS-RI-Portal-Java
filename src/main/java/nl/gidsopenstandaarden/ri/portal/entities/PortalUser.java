package nl.gidsopenstandaarden.ri.portal.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 *
 */
@Entity
public class PortalUser implements Serializable {
	@Id
	@GeneratedValue
	private Long id;
	private String identifier;
	private String subject;

	public Long getId() {
		return id;
	}

	public String getIdentifier() {
		return identifier;
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
}
