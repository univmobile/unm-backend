package fr.univmobile.backend.json;

import fr.univmobile.backend.domain.Token;
import fr.univmobile.backend.domain.TokenRepository;
import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.domain.UserRepository;
import fr.univmobile.web.commons.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

@Paths("json/login")
public class LoginJSONController extends AbstractJSONController {

    UserRepository userRepository;
    TokenRepository tokenRepository;

    public LoginJSONController(UserRepository userRepository, TokenRepository tokenRepository){
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public String actionJSON(String baseURL) throws Exception {
        final Login login = getHttpInputs(Login.class);
        final JSONMap json = new JSONMap();
        if (login.isHttpValid()) {
            User user = userRepository.findByUsername(login.username());
            if (user == null || !user.getPassword().equals(login.password())){
                json.put("error", "Wrong credentials");
            } else {
                Token token = new Token();
                token.setUser(user);
                token.setToken(newUUID(40));
                tokenRepository.save(token);
                json.put("username", user.getUsername());
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
    private interface Login extends HttpInputs {
        @HttpRequired
        @HttpParameter
        String username();

        @HttpRequired
        @HttpParameter
        String password();
    }
}
