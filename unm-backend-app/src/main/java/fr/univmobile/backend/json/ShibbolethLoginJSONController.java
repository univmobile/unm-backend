package fr.univmobile.backend.json;

import fr.univmobile.backend.domain.Token;
import fr.univmobile.backend.domain.TokenRepository;
import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.domain.UserRepository;
import fr.univmobile.web.commons.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.UUID;

@Paths("json/shibbolethLogin")
public class ShibbolethLoginJSONController extends AbstractJSONController {

	private static final Log log = LogFactory
			.getLog(ShibbolethLoginJSONController.class);

    UserRepository userRepository;
    TokenRepository tokenRepository;

    public ShibbolethLoginJSONController(UserRepository userRepository, TokenRepository tokenRepository){
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public String actionJSON(String baseURL) throws Exception {
        final ShibbolethLogin login = getHttpInputs(ShibbolethLogin.class);
        final JSONMap json = new JSONMap();
        if (login.isHttpValid()) {
            User user = userRepository.findByRemoteUser(getRemoteUser());
            if (user == null) {
            	// The user is logged in via Shibboleth. If the user is not in the DB yet, we add it automatically
            	String remoteUser = getRemoteUser();
            	user = new User();
            	user.setUsername(remoteUser);
            	user.setRole(User.STUDENT);
            	user.setRemoteUser(remoteUser);
            	user.setDisplayName(String.valueOf(checkedRequest().getAttribute("displayName")));
            	user.setEmail(remoteUser);
            	userRepository.save(user);
            	user = userRepository.findByRemoteUser(remoteUser);
            } 
            if (user != null) {
                Token token = new Token();
                token.setUser(user);
                token.setToken(newUUID(40));
                tokenRepository.save(token);
                json.put("id", user.getId().toString());
                json.put("username", user.getUsername());//TODO Maybe we should use remoteUser here
                json.put("Authentication-Token", token.getToken());
            }
        } else {
            json.put("error", "Bad Request");
        }
        return json.toJSONString();
    }

    private static String newUUID(final int length) {
        String uuid = UUID.randomUUID().toString();
        uuid += "-" + RandomStringUtils.randomAlphanumeric(length);
        return uuid.substring(0, length);
    }

    @HttpMethods("GET")
    private interface ShibbolethLogin extends HttpInputs {
    }
}
