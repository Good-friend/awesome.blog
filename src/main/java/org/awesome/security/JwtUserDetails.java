package org.awesome.security;

import org.awesome.enums.UserStatus;
import org.awesome.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUserDetails implements UserDetails {

    private List<String> authorities;
    private User user;

    public JwtUserDetails(List<String> authorities, User user) {
        this.authorities = authorities;
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> simpleGrantedAuthorities =
                authorities.stream().map(e -> new SimpleGrantedAuthority(e)).collect(Collectors.toList());
        return simpleGrantedAuthorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getUserStatus().equals(UserStatus.EXPIRED.getValue()) ? false : true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getUserStatus().equals(UserStatus.LOCKD.getValue()) ? false : true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getUserStatus().equals(UserStatus.ENABLE.getValue()) ? true : false;
    }
}