package com.example.demo.modules.file;

import java.io.IOException;
import java.sql.Date;
import java.util.concurrent.ExecutionException;

import com.example.demo.modules.information.Information;
import com.example.demo.modules.user.User;
import com.google.cloud.firestore.Firestore;

import lombok.Data;

@Data
public class SharableUser {

	private String userName;
	private String email;
	private String fullName;
	private String image;
	
	public SharableUser(User user, Firestore firestore) throws ExecutionException, IOException, InterruptedException {
		this.userName = user.getUserName();
		this.email = user.getEmail();
		
		var information = firestore.collection("Information").document(String.valueOf(user.getInformationID())).get().get().toObject(Information.class);
		
		this.fullName = information.getFullName();
		this.image = information.getImage();
	}
}
