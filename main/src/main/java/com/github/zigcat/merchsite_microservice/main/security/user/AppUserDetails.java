package com.github.zigcat.merchsite_microservice.main.security.user;

import com.github.zigcat.merchsite_microservice.main.entity.enums.Role;
import com.github.zigcat.merchsite_microservice.main.entity.AppUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

@Getter
public class AppUserDetails implements UserDetails {
    private AppUser user;

    public AppUserDetails(AppUser user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(user.getRole().equals(Role.ADMIN)){
            return Arrays.asList(
                    new SimpleGrantedAuthority("ROLE_"+user.getRole().toString()),
                    new SimpleGrantedAuthority("ROLE_"+Role.USER.toString()));
        } else {
            return Arrays.asList(
                    new SimpleGrantedAuthority("ROLE_"+user.getRole().toString())
            );
        }
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
