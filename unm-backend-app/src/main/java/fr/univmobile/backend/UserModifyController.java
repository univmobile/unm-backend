package fr.univmobile.backend;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
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
import fr.univmobile.web.commons.PathVariable;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.Regexp;
import fr.univmobile.web.commons.View;

@Paths({ "usermodify/${id}" })
public class UserModifyController extends AbstractBackendController {

	@PathVariable("${id}")
	private long getUserId() {

		return getPathLongVariable("${id}");
	}

	public UserModifyController(final UserRepository userRepository,
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
	public View action() throws IOException {

		if (getDelegationUser().isLibrarian() && getUserId() != getDelegationUser().getId()) {
			return sendError403("FORBIDDEN");
		}

		// REGIONS DATA

		Iterable<Region> allRegions = regionRepository.findAll();

		List<Region> regionsData = new ArrayList<Region>();

		for (Region r : allRegions)
			regionsData.add(r);

		setAttribute("regionsData", regionsData);

		// 1.1 USER

		User user = userRepository.findOne(getUserId());

		setAttribute("usermodify", user);

		// 1.2 HTTP

		final Usermodify form = getHttpInputs(Usermodify.class);

		setAttribute("role", getDelegationUser().getRole());
		setAttribute("userUnivId", getDelegationUser().getUniversity().getId());

		if (!form.isHttpValid()) {

			return new View("usermodify.jsp");
		}

		// 2. APPLICATION VALIDATION
		return usermodify(form);
	}

	private View usermodify(final Usermodify form) throws IOException {

		User user = userRepository.findOne(getUserId());

		boolean hasErrors = false;

		user.setUsername(form.username());
		if (isBlank(form.username())) {
			hasErrors = true;
			setAttribute("err_usermodify_username", true);
			setAttribute("err_incorrectFields", true);
		}

		user.setDisplayName(form.displayName());
		if (isBlank(form.displayName())) {
			hasErrors = true;
			setAttribute("err_usermodify_displayName", true);
			setAttribute("err_incorrectFields", true);
		}

		user.setClassicLoginAllowed(form.classicLoginAllowed() != null);

		user.setPassword(form.password());
		user.setEmail(form.email());
		
		if (getDelegationUser().isSuperAdmin()) {
			user.setRole(form.role());
		}

		if (form.titleCivilite() != null)
			user.setTitleCivilite(form.titleCivilite());

		University pU;
		if (getDelegationUser().isSuperAdmin()) {
			pU = universityRepository.findOne(form.university());
			user.setUniversity(pU);
		}

		University sU = universityRepository
				.findOne(form.secondaryUniversity());
		user.setSecondaryUniversity(sU);

		user.setDescription(form.description());

		final String twitterScreenName = form.twitter_screen_name().trim();
		if (!isBlank(twitterScreenName)) {
			user.setTwitterScreenName(twitterScreenName);
		}

		if (hasErrors) {

			setAttribute("usermodify", user); // Show the data in the view

			return new View("usermodify.jsp");
		}

		// 3. SAVE DATA

		// Otherwise, we’re clear: Save the data.

		userRepository.save(user);

		return sendRedirect(getDelegationUser().isLibrarian() ? getContextBaseUrl() : String.format("%sapi/app#/manage/users", getContextBaseUrl()));
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
	interface Usermodify extends HttpInputs {

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
		Long university();

		@HttpParameter
		Long secondaryUniversity();

		@HttpParameter
		String description();
	}
}
