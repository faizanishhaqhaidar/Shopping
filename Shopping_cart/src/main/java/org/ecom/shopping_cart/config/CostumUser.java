package org.ecom.shopping_cart.config;

import org.ecom.shopping_cart.model.UserDtls;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CostumUser implements UserDetails {
    private UserDtls user;
    public CostumUser( UserDtls user) {
        super();
        this.user = user;
    }
    @Override
    public String getUsername() {
        return   user.getEmail();
    }

    @Override
    public String getPassword() {
        return  user.getPassword();
    }

    @Override
    public boolean isEnabled() {
        return  user.getIsEnable();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        return List.of(authority);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return  true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountNonLocked();
    }

}
