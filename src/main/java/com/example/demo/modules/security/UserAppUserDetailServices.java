package com.example.demo.modules.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.demo.modules.user.User;
import com.example.demo.modules.user.UserService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

@Component
public class UserAppUserDetailServices implements UserDetailsService {

	@Autowired
	private Firestore firestore;
	
	public User GetUser(String userName)
	{
		try 
		{
			DocumentReference documentRef = firestore.collection("User").document(userName);
			ApiFuture<DocumentSnapshot> future = documentRef.get();
			DocumentSnapshot document = future.get();
			if (document.exists())
			{
				User user = document.toObject(User.class);
				return user;
			}
			
			return null;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> userApp = Optional.of(GetUser(username));
		return userApp.map(UserAppUserDetails::new)
	              .orElseThrow(() -> new UsernameNotFoundException("user not found " + username));	
	}
}
	
