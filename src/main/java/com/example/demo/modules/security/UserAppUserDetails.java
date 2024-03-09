package com.example.demo.modules.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.modules.user.User;

public class UserAppUserDetails implements UserDetails {
	
	private String Username;
	private String Password;
    private List<GrantedAuthority> authorities = null;
	
	
	public UserAppUserDetails(User user) {
		Username = user.getUserName();
		Password = user.getPassWord();
//		authorities = Arrays.stream(user.getRoles().split(","))
//	               .map(SimpleGrantedAuthority::new)
//	               .collect(Collectors.toList());	
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return Password;
	}

	@Override
	public String getUsername() {
		return Username;
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
