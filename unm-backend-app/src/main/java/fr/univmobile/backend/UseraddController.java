package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.RandomStringUtils;

import fr.univmobile.backend.core.Region;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.UserBuilder;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.backend.core.impl.Encrypt;
import fr.univmobile.backend.core.impl.EncryptSHA256;
import fr.univmobile.commons.tx.Lock;
import fr.univmobile.commons.tx.TransactionException;
import fr.univmobile.commons.tx.TransactionManager;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.Regexp;
import fr.univmobile.web.commons.View;

@Paths({ "useradd" })
public class UseraddController extends AbstractBackendController {

	public UseraddController(final TransactionManager tx,
			final UserDataSource users, final UsersController usersController,
			final RegionDataSource regions) {

		this.users = checkNotNull(users, "users");
		this.tx = checkNotNull(tx, "tx");
		this.usersController = checkNotNull(usersController, "usersController");
		this.regions = checkNotNull(regions, "regions");
	}

	private final TransactionManager tx;
	private final UserDataSource users;
	private final UsersController usersController;
	private final RegionDataSource regions;

	private final Encrypt encrypt = new EncryptSHA256();

	@Override
	public View action() throws IOException, TransactionException {

		final Map<String, Region> dsRegions = regions.getAllBy(String.class,
				"uid");

		final List<Region> regionsData = new ArrayList<Region>();

		for (final Region r : dsRegions.values())
			regionsData.add(r);
		setAttribute("regionsData", regionsData);

		// 1. HTTP

		final Useradd form = getHttpInputs(Useradd.class);

		if (!form.isHttpValid()) {

			return new View("useradd.jsp");
		}

		// 2. APPLICATION VALIDATION

		final String uid = form.uid();

		final Lock lock = tx.acquireLock(5000, "users", uid);
		try {

			return useradd(lock, form);

		} finally {
			lock.release();
		}
	}

	private View useradd(final Lock lock, final Useradd form)
			throws IOException, TransactionException {

		final String uid = form.uid();

		final UserBuilder user = users.create();

		user.setAuthorName(getDelegationUser().getUid());

		final String remoteUser = form.remoteUser();

		if (form.type() != null) // gets the role
			user.setRole(form.type());

		user.setUid(uid);
		user.setRemoteUser(remoteUser);
		user.setTitle(uid);
		user.setDisplayName(form.displayName());
		user.setMail(form.mail());
		if (form.supannCivilite() != null) {
			user.setSupannCivilite(form.supannCivilite());
		}

		user.setPrimaryUniversity(form.primaryUniversity());
		user.setSecondaryUniversities(form.secondaryUniversities());

		if (form.passwordEnabled() != null) {
			user.setPasswordEnabled("true");
			user.setPasswordEncryptionAlgorithm("SHA-256");
			final String saltPrefix = RandomStringUtils.randomAlphanumeric(8);
			user.setPasswordSaltPrefix(saltPrefix);
			if (!isBlank(form.password())) {
				final String encrypted = encrypt.encrypt(saltPrefix,
						form.password());
				user.setPasswordEncrypted(encrypted);
			}
		} else user.setPasswordEnabled("false");

		final String twitterScreenName = form.twitter_screen_name().trim();

		if (!isBlank(twitterScreenName)) {
			user.setTwitterScreenName(twitterScreenName);
		}

		boolean hasErrors = false;

		if (!isBlank(uid)) {
			if (!users.isNullByUid(uid)) {
				hasErrors = true;
				setAttribute("err_duplicateUid", true);
			}
		}

		if (!isBlank(remoteUser)) {
			if (!users.isNullByRemoteUser(remoteUser)) {
				hasErrors = true;
				setAttribute("err_duplicateRemoteUser", true);
			}
		}

		if (hasErrors) {

			setAttribute("useradd", user); // Show the data in the view

			return new View("useradd.jsp");
		}

		// 3. SAVE DATA

		// Otherwise, we’re clear: Save the data.

		lock.save(user);

		lock.commit();

		return usersController.action();
	}

	/**
	 * <ol>
	 * <li>
	 * By default, all parameters are required (that is, not blank) at the
	 * application level. Use the {@link Nullable}-annotation to specify an
	 * optional parameter.
	 * <li>
	 * By default, no parameter is required at the HTTP level. Use the
	 * {@link HttpRequired}-annotation to specify that a parameter must be
	 * present in the HTTP request for the form to be valid.
	 * </ol>
	 */
	@HttpMethods("POST")
	interface Useradd extends HttpInputs {

		@HttpRequired
		@HttpParameter(trim = true)
		@Regexp("[a-zA-Z0-9_-]+")
		String uid();

		@HttpRequired
		@HttpParameter(trim = true)
		@Regexp("[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+")
		String remoteUser();

		@HttpParameter
		@Nullable
		@Regexp("aucune|Mme|M\\.")
		String supannCivilite();

		@HttpRequired
		@HttpParameter
		@Regexp(".*")
		String displayName();

		@HttpRequired
		@HttpParameter(trim = true)
		@Regexp("[a-zA-Z0-9_-]+[a-zA-Z0-9_-].@[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+")
		String mail();

		@HttpRequired
		@HttpParameter
		@Regexp(".*")
		String password();

		@HttpParameter
		String passwordEnabled();

		@HttpRequired
		@HttpParameter
		String twitter_screen_name();

		// Added by Mauricio

		@HttpParameter
		String type();

		@HttpParameter
		String primaryUniversity();

		@HttpParameter
		String secondaryUniversities();
	}
}
