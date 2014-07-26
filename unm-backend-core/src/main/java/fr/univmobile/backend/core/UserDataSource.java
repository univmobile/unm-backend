package fr.univmobile.backend.core;

public interface UserDataSource extends BackendDataSource<User> {

	@SearchAttribute("remoteUser")
	User getByRemoteUser(String remoteUser);

	@SearchAttribute("uid")
	User getByUid(String uid);
}
