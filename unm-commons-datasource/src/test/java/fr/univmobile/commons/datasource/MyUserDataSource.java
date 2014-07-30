package fr.univmobile.commons.datasource;

import fr.univmobile.commons.datasource.BackendDataSource;
import fr.univmobile.commons.datasource.Category;
import fr.univmobile.commons.datasource.PrimaryKey;
import fr.univmobile.commons.datasource.SearchAttribute;
import fr.univmobile.commons.datasource.Support;
import fr.univmobile.commons.datasource.Unique;

@Category("users")
@PrimaryKey("uid")
@Unique("remoteUser")
@Support(data = MyUser.class, builder = MyUserBuilder.class)
public interface MyUserDataSource extends BackendDataSource<MyUser, MyUserBuilder> {

	@SearchAttribute("remoteUser")
	MyUser getByRemoteUser(String remoteUser);

	boolean isNullByRemoteUser(String remoteUser);

	@SearchAttribute("uid")
	MyUser getByUid(String uid);

	boolean isNullByUid(String uid);
}
