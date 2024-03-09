package com.example.demo.modules.user;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.modules.information.Information;
import com.example.demo.modules.information.InformationService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import lombok.Data;

@Data
public class UserDTO {

	private String token;
	private String userName;
	private String passWord;
	private String authenticatedPassWord;
	private String email;
	private Information information;
	
	public UserDTO(User user, String token, InformationService informationService)
	{
		this.token = token;
		userName = user.getUserName();
		passWord = user.getPassWord();
		authenticatedPassWord = user.getAuthenticatedPassWord();
		email = user.getEmail();
		information = informationService.GetInformation(user.getInformationID());
	}
}
