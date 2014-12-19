package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static fr.univmobile.commons.DataBeans.instantiate;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.domain.UserRepository;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.View;

@Paths({ "users", "users/" })
public class UsersController extends AbstractBackendController {

	public UsersController(final UserRepository userRepository) {
		this.userRepository = checkNotNull(userRepository, "userRepository");
	}

	private UserRepository userRepository;

	@Override
	public View action() {

		User dUser = getDelegationUser();

		// 1. USERS DATA

		Iterable<User> allUsers;
		if (dUser.getRole().equals(User.ADMIN))
			allUsers = userRepository.findByUniversityAndRole(
					dUser.getUniversity(), User.STUDENT);
		else
			allUsers = userRepository.findAll();

		List<User> users = new ArrayList<User>();

		for (User u : allUsers)
			users.add(u);

		// ADD THE ADMIN
		if (dUser.getRole().equals(User.ADMIN))
			users.add(dUser);

		setAttribute("users", users);

		// CURRENT USER ROLE

		setAttribute("role", dUser.getRole());

		// 2. USERS INFO

		final UsersInfo usersInfo = instantiate(UsersInfo.class) //
				.setCount(users.size()) //
				.setContext("Tous les utilisateurs") //
				.setResultCount(users.size());

		setAttribute("usersInfo", usersInfo);

		// 3. END

		return new View("users.jsp");
	}
}

interface UsersInfo {

	/**
	 * Total count of users in the DataBase.
	 */
	int getCount();

	UsersInfo setCount(int count);

	/**
	 * e.g. "Tous les utilisateurs"
	 */
	@Nullable
	String getContext();

	UsersInfo setContext(String context);

	/**
	 * Count of users returned by the search.
	 */
	int getResultCount();

	UsersInfo setResultCount(int count);
}
