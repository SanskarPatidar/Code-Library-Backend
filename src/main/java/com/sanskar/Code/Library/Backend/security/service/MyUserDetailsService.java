package com.sanskar.Code.Library.Backend.security.service;


import com.sanskar.Code.Library.Backend.exception.NotFoundException;
import com.sanskar.Code.Library.Backend.security.model.UserPrincipal;
import com.sanskar.Code.Library.Backend.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MyUserDetailsService implements UserDetailsService { // for mapping User to UserPrincipal
    @Autowired
    private UserRepository repo;
    @Override
    public UserDetails loadUserByUsername(String loginString) throws UsernameNotFoundException {
        // since this function requires UserDetails so we give it
        return repo.findByUsername(loginString)
                .or(() -> repo.findByEmail(loginString))
                .map(UserPrincipal::new) // same as map(it -> new UserPrincipal(it)), ::UserPrincipal in Kotlin
                .orElseThrow(() -> new NotFoundException("User not found"));// in java, class has to be functional interface to have its constructor reference
    }
}
