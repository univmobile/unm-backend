package fr.univmobile.backend.api.serializers;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class CommentMixIn {
	
	@JsonIgnore
	abstract String getPoi();
	
	@JsonSerialize
	abstract String getAuthor();
	
	@JsonSerialize
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:00")
	abstract Date getCreatedOn();
	
}
