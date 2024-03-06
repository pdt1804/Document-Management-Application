package com.example.demo.modules.user;

import lombok.Data;

@Data
public class ChangePassword {

	private String currentPassWord;
	private String newPassWord;
	private String reEnterPassWord;
	
	public boolean vertification(String userPassword)
	{
		if (newPassWord.length() < 5)
		{
			return false;
		}
		
		if (currentPassWord.equals(userPassword) && newPassWord.equals(reEnterPassWord))
		{
			return true;
		}
		
		return false;
	}
}
