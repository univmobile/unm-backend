package fr.univmobile.backend.domain;

public abstract class AuditableEntityWithLegacy extends AuditableEntity {

	public static String buildRootLegacy(int id) {
		return buildRootLegacy((long) id);
	}
	
	public static String buildRootLegacy(long id) {
		return String.format("/%d/", id);
	}
	
}
