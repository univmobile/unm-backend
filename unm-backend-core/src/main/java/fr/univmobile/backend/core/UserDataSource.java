package fr.univmobile.backend.core;

import fr.univmobile.commons.datasource.BackendDataSource;
import fr.univmobile.commons.datasource.Category;
import fr.univmobile.commons.datasource.PrimaryKey;
import fr.univmobile.commons.datasource.SearchAttribute;
import fr.univmobile.commons.datasource.Support;
import fr.univmobile.commons.datasource.Unique;

@Category("users")
@PrimaryKey("uid")
@Unique("remoteUser")
@Support(data = User.class, builder = UserBuilder.class)
public interface UserDataSource extends BackendDataSource<User, UserBuilder> {

	@SearchAttribute("remoteUser")
	User getByRemoteUser(String remoteUser);

	boolean isNullByRemoteUser(String remoteUser);

	@SearchAttribute("uid")
	User getByUid(String uid);

	boolean isNullByUid(String uid);
}
