package fr.univmobile.backend.core;

@Category("users")
@PrimaryKey("uid")
@Support(data = User.class, builder = UserBuilder.class)
public interface UserDataSource extends BackendDataSource<User, UserBuilder> {

	@SearchAttribute("remoteUser")
	User getByRemoteUser(String remoteUser);

	boolean isNullByRemoteUser(String remoteUser);

	@SearchAttribute("uid")
	User getByUid(String uid);

	boolean isNullByUid(String uid);
}
