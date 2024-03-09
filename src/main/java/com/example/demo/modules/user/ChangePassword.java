package com.example.demo.modules.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Data;

@Data
public class ChangePassword {

	private String currentPassWord;
	private String newPassWord;
	private String reEnterPassWord;
	
	public boolean Vertification(String userName, AuthenticationManager authenticationManager)
	{
		try {
			if (newPassWord.length() < 5)
			{
				return false;
			}
					
			if (!newPassWord.equals(reEnterPassWord))
			{
				return false;
			}
			
			Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, currentPassWord));
			
			return authenticate.isAuthenticated();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
