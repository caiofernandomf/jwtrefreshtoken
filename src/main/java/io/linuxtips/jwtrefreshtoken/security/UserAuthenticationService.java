package io.linuxtips.jwtrefreshtoken.security;

import io.linuxtips.jwtrefreshtoken.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import io.linuxtips.jwtrefreshtoken.model.User;

@Service
public class UserAuthenticationService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserAuthenticationService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User inDB = userRepository.findByUsername(username);
        if(inDB==null){
            throw new UsernameNotFoundException("user not found");
        }
        return new AppUser(inDB);
    }
}
