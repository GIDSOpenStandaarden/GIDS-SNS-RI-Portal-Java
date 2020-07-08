package nl.gidsopenstandaarden.ri.portal.entities;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 *
 */
@Entity
public class Treatment implements Serializable, Comparable {
	@Id
	String id;
	String url;
	String aud;
	String name;
	@Column(length = 2048)
	String description;


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (!(o instanceof Treatment)) return false;

		Treatment treatment = (Treatment) o;

		return new EqualsBuilder()
				.append(name, treatment.name)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(name)
				.toHashCode();
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof Treatment) {
			return new CompareToBuilder().append(name, ((Treatment)o).name).build();
		}
		return 0;
	}

	public String getAud() {
		return aud;
	}

	public void setAud(String aud) {
		this.aud = aud;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
