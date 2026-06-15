package com.umcsuser.carrent.security;

import com.umcsuser.carrent.repositories.impl.UserRepositoryJpaAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepositoryJpaAdapter userRepository;

    public MyUserDetailsService(UserRepositoryJpaAdapter userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        return userRepository.findByLogin(login)
                .map(user -> new User(
                        user.getLogin(),
                        user.getPasswordHash(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
    }
}
