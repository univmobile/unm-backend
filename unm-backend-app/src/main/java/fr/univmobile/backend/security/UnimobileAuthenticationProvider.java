package fr.univmobile.backend.security;

import fr.univmobile.backend.domain.User;
import fr.univmobile.backend.domain.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.LinkedList;

public class UnimobileAuthenticationProvider implements AuthenticationProvider {

    private UserRepository userRepository;

    private Md5PasswordEncoder passwordEncoder;

    public void setUserRepository(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void setPasswordEncoder(Md5PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
//        String password = passwordEncoder.encodePassword((String) authentication.getCredentials(), null);
        String password = (String) authentication.getCredentials();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new BadCredentialsException("Username not found.");
        }

        if (!password.equals(user.getPassword())) {
            throw new BadCredentialsException("Wrong password.");
        }

        Collection<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));

        return new UsernamePasswordAuthenticationToken(user, password, authorities);
    }

    @Override
    public boolean supports(Class<?> arg0) {
        return true;
    }
}