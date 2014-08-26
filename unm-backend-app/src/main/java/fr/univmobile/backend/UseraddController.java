package fr.univmobile.backend;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;

import javax.annotation.Nullable;

import fr.univmobile.backend.core.PoiDataSource;
import fr.univmobile.backend.core.PoiTreeDataSource;
import fr.univmobile.backend.core.RegionDataSource;
import fr.univmobile.backend.core.UserBuilder;
import fr.univmobile.backend.core.UserDataSource;
import fr.univmobile.web.commons.HttpInputs;
import fr.univmobile.web.commons.HttpMethods;
import fr.univmobile.web.commons.HttpParameter;
import fr.univmobile.web.commons.HttpRequired;
import fr.univmobile.web.commons.Paths;
import fr.univmobile.web.commons.Regexp;
import fr.univmobile.web.commons.View;

@Paths({ "useradd" })
public class UseraddController extends AbstractBackendController {

	public UseraddController(final UserDataSource users,
			final RegionDataSource regions, final PoiDataSource pois,
			final PoiTreeDataSource poiTrees) {

		super(users, regions, pois, poiTrees);
	}

	@Override
	public View action() throws IOException {

		// 1. HTTP

		final Useradd form = getHttpInputs(Useradd.class);

		if (!form.isHttpValid()) {

			return new View("useradd.jsp");
		}

		// 2. APPLICATION VALIDATION

		final UserBuilder user = users.create();

		user.setAuthorName(getDelegationUser().getUid());

		final String uid = form.uid();
		final String remoteUser = form.remoteUser();

		user.setUid(uid);
		user.setRemoteUser(remoteUser);
		user.setTitle(uid);
		user.setDisplayName(form.displayName());
		user.setMail(form.mail());
		if (form.supannCivilite() != null) {
			user.setSupannCivilite(form.supannCivilite());
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

		if (!validate(form, "err_useradd") || hasErrors) {

			setAttribute("useradd", user); // Show the data in the view

			return new View("useradd.jsp");
		}

		// 3. SAVE DATA

		// Otherwise, we’re clear: Save the data.

		user.save();

		return entered();
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
	}
}
