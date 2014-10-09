package fr.univmobile.backend.client;


public interface SSOConfiguration {

	/**
	 * The URL used to initiate the SSO process, with placeholders for 
	 * callback URLs.
	 * <p>
	 * e.g.
	 * "https://univmobile-dev.univ-paris1.fr/Shibboleth.sso/Login?target=${target.url}&entityID=${shibboleth.entityProvider}"
	 */
	String getURL();

	/**
	 * The URL to be targetted after the SSO process succeeds, with placeholders for 
	 * callback URLs and login-conversation information. 
	 * <p>
	 * e.g.
	 * "https://univmobile-dev.univ-paris1.fr/testSP/?loginToken=${loginToken}&callback=${callback.url}"
	 */
	String getTargetURL();

	/**
	 * The default callback URL to be used after the Shibboleth authentication
	 * succeeds. But clients are free to use any other callback URL.
	 * <p>
	 * e.g. "https://univmobile-dev.univ-paris1.fr/testSP/success"
	 */
	String getCallbackURL();
}
