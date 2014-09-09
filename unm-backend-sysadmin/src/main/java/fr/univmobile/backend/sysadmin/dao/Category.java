package fr.univmobile.backend.sysadmin.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import org.joda.time.DateTime;

public class Category {

	public final String id;
	public final String path;

	@Nullable
	public final DateTime lockedSince;

	public Category(final String id, final String path,
			@Nullable final DateTime lockedSince) {

		this.id = checkNotNull(id, "id");
		this.path = checkNotNull(path, "path");

		this.lockedSince = lockedSince;
	}
}
