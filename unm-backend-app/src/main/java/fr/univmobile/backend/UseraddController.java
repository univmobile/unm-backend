package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.univmobile.backend.domain.Region;
import fr.univmobile.backend.domain.RegionRepository;
import fr.univmobile.backend.domain.University;
import fr.univmobile.backend.domain.UniversityRepository;
import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.domain.UserRepository;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.Regexp;
import fr.univmobile.web.commons.View;

@Paths({ "useradd" })
public class UseraddController extends AbstractBackendController {

	public UseraddController(final UserRepository userRepository,
			final RegionRepository regionRepository,
			final UniversityRepository universityRepository,
			final UsersController usersController) {
		this.userRepository = checkNotNull(userRepository, "userRepository");
		this.regionRepository = checkNotNull(regionRepository,
				"regionRepository");
		this.universityRepository = checkNotNull(universityRepository,
				"universityRepository");
		this.usersController = checkNotNull(usersController, "usersController");
	}

	private UserRepository userRepository;
	private RegionRepository regionRepository;
	private UniversityRepository universityRepository;
	private UsersController usersController;

	// private final Encrypt encrypt = new EncryptSHA256();

	@Override
	public View action() {

		// REGIONS DATA

		Iterable<Region> allRegions = regionRepository.findAll();

		List<Region> regionsData = new ArrayList<Region>();

		for (Region r : allRegions)
			regionsData.add(r);

		setAttribute("regionsData", regionsData);

		// 1. HTTP

		final Useradd form = getHttpInputs(Useradd.class);

		setAttribute("role", getDelegationUser().getRole());
		setAttribute("userUnivId", getDelegationUser().getUniversity().getId());

		if (!form.isHttpValid()) {

			return new View("useradd.jsp");
		}

		// 2. APPLICATION VALIDATION

		return useradd(form);
	}

	private View useradd(final Useradd form) {

		User user = new User();

		boolean hasErrors = false;

		final String remoteUser = form.remoteUser();
		final String username = form.username();

		user.setUsername(username);
		if (isBlank(username)) {
			hasErrors = true;
			setAttribute("err_useradd_username", true);
			setAttribute("err_incorrectFields", true);
		}

		user.setDisplayName(form.displayName());
		if (isBlank(form.displayName())) {
			hasErrors = true;
			setAttribute("err_useradd_displayName", true);
			setAttribute("err_incorrectFields", true);
		}

		user.setRemoteUser(remoteUser);
		if (isBlank(remoteUser)) {
			hasErrors = true;
			setAttribute("err_useradd_remoteUser", true);
			setAttribute("err_incorrectFields", true);
		}

		user.setClassicLoginAllowed(form.classicLoginAllowed() != null);

		user.setPassword(form.password());
		user.setEmail(form.email());
		user.setRole(form.role());

		if (form.titleCivilite() != null)
			user.setTitleCivilite(form.titleCivilite());

		University pU = universityRepository.findOne(form.primaryUniversity());
		user.setUniversity(pU);

		University sU = universityRepository
				.findOne(form.secondaryUniversity());
		user.setSecondaryUniversity(sU);

		user.setDescription(form.description());

		final String twitterScreenName = form.twitter_screen_name().trim();
		if (!isBlank(twitterScreenName))
			user.setTwitterScreenName(twitterScreenName);

		if (!isBlank(remoteUser)) {
			if (userRepository.findByRemoteUser(remoteUser) != null) {
				hasErrors = true;
				setAttribute("err_duplicateRemoteUser", true);
			}
		}

		if (!isBlank(username)) {
			if (userRepository.findByUsername(username) != null) {
				hasErrors = true;
				setAttribute("err_duplicateUsername", true);
			}
		}

		if (hasErrors) {

			setAttribute("useradd", user); // Show the data in the view

			return new View("useradd.jsp");
		}

		// 3. SAVE DATA

		// Otherwise, we’re clear: Save the data.

		userRepository.save(user);

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
		@Regexp("[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+")
		String remoteUser();

		@HttpParameter
		@Nullable
		@Regexp("aucune|Mme|M\\.")
		String titleCivilite();

		@HttpRequired
		@HttpParameter
		@Regexp(".*")
		String displayName();

		@HttpRequired
		@HttpParameter(trim = true)
		@Regexp("[a-zA-Z0-9_-]+[a-zA-Z0-9_-].@[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+")
		String email();

		@HttpRequired
		@HttpParameter
		@Regexp(".*")
		String password();

		@HttpParameter
		String classicLoginAllowed();

		@HttpParameter
		String twitter_screen_name();

		// Added by Mauricio

		@HttpParameter
		String role();

		@HttpParameter
		String username();

		@HttpParameter
		Long primaryUniversity();

		@HttpParameter
		Long secondaryUniversity();

		@HttpParameter
		String description();
	}
}
