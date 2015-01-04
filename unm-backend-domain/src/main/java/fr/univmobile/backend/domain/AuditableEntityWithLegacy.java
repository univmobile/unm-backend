package fr.univmobile.backend.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public abstract class AuditableEntityWithLegacy extends AuditableEntity {

	@JsonIgnore
	@Column(nullable = true)
	private String legacy;

	public String getLegacy() {
		return legacy;
	}

	public void setLegacy(String legacy) {
		this.legacy = legacy;
	}

	public static String buildRootLegacy(int id) {
		return buildRootLegacy((long) id);
	}
	
	public static String buildRootLegacy(long id) {
		return String.format("/%d/", id);
	}
	
	public List<Long> getLegacyIds() {
		if (this.legacy == null) {
			return new ArrayList<Long>(0);
		} else {
			String[] splittedLegacy = this.legacy.split("/");
			List<Long> ids = new ArrayList<Long>(splittedLegacy.length);

			for (String idPart : this.legacy.split("/")) {
				try {
					ids.add(Long.valueOf(idPart));
				} catch (Exception e) {
				}
			}
			
			return ids;
		}
	}
	
}
