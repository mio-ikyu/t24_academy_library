package jp.co.metateam.library.service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jp.co.metateam.library.model.Account;

public class AccountPrincipal implements UserDetails {

    public static final int AUTH_TYPE_NORMAL = 0;
    public static final int AUTH_TYPE_ADMIN = 1;

    private transient Account account;

    public AccountPrincipal(Account account) {
        this.account = account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Integer authType = account.getAuthorizationType();

        if (authType == AUTH_TYPE_NORMAL) {
            return Collections.singleton(new SimpleGrantedAuthority("USER"));
        } else {
            return Collections.singleton(new SimpleGrantedAuthority("ADMIN"));
        }
    }

    @Override
    public String getPassword() {
        return this.account.getPassword();
    }

    @Override
    public String getUsername() {
        return this.account.getEmail();
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

