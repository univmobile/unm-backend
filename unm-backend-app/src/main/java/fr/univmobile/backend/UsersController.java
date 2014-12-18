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

		fr.univmobile.backend.core.User dUser = getDelegationUser();

		// 1. USERS DATA

		Iterable<User> allUsers = userRepository.findAll();

		List<User> users = new ArrayList<User>();

		int size = 0;
		for (User u : allUsers) {
			if (dUser.getRole().equals("admin")) {
				if (u.getRole().equals("student")
						&& u.getUniversity().getId()
								.equals(dUser.getPrimaryUniversity()))
					users.add(u);
				size++;
			}
		}

		setAttribute("users", users);

		// CURRENT USER ROLE

		setAttribute("role", dUser.getRole());

		// 2. USERS INFO

		final UsersInfo usersInfo = instantiate(UsersInfo.class) //
				.setCount(size) //
				.setContext("Tous les utilisateurs") //
				.setResultCount(size);

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
