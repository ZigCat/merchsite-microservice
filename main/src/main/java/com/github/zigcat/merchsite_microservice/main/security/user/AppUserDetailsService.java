package com.github.zigcat.merchsite_microservice.main.security.user;

import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import com.github.zigcat.merchsite_microservice.main.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {
    private final UserService service;

    @Autowired
    public AppUserDetailsService(UserService service) {
        this.service = service;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = service.getByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new AppUserDetails(user);
    }
}
