package fr.univmobile.backend.domain;

import fr.univmobile.backend.domain.UsageStat.UsageType;

public class UsageStatDto {
	
	private UsageType source;
	private Long count;

	/*
	public UsageStatDto(Long count) {
		this.source = UsageType.A;
		this.count = count;
	}
	*/
	
	public UsageStatDto(UsageType source, Long count) {
		this.source = source;
		this.count = count;
	}
	
	public UsageType getSource() {
		return source;
	}
	
	public void setSource(UsageType source) {
		this.source = source;
	}
	
	public Long getCount() {
		return count;
	}
	
	public void setCount(Long count) {
		this.count = count;
	}
	
}
