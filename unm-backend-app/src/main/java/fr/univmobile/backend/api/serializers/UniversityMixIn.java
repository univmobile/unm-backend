package fr.univmobile.backend.api.serializers;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class UniversityMixIn {
	
	@JsonSerialize
	abstract boolean allowBonplans();
	
}
