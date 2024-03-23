package com.example.demo.modules.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.modules.information.Information;
import com.example.demo.modules.jwt.JwtService;
import com.example.demo.modules.logging.ActivityLogging;
import com.example.demo.modules.logging.ActivityLoggingService;
import com.example.demo.modules.logging.ActivityType;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class UserService {
	
	@Autowired
	private Firestore firestore;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired 
	private JwtService jwtService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ActivityLoggingService activityLoggingService;
	
	public User Authenticate(String userName, String passWord)
	{
		try 
		{
			Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, passWord));
			
			if (authenticate.isAuthenticated())
			{
				//activityLoggingService.AddLoggingForLogin(userName);
				return GetUser(userName);
			}
			
			return null;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
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
	
	public String CreateUser(User user)
	{
		try 
		{
			user.setPassWord(passwordEncoder.encode(user.getPassWord()));
			int informationID = (int)(firestore.collection("Information").count().get().get().getCount() + 1);
			Information information = new Information();
			information.setInformationID(informationID);
			information.setAddress("None");
			information.setFullName("None");
			information.setImage("https://res.cloudinary.com/ddsbj1z4x/image/upload/v1709654383/q7husgn76ewahtbwcgpz.jpg");
			ApiFuture<WriteResult> InsertInformation = firestore.collection("Information").document(String.valueOf(information.getInformationID())).set(information);

			user.setInformationID(informationID);
			ApiFuture<WriteResult> CreateUser = firestore.collection("User").document(user.getUserName()).set(user);
			return CreateUser.get().getUpdateTime().toString();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String ChangePassword(ChangePassword changePassword, String userName)
	{		
		try {
			var user = GetUser(userName);

			if (changePassword.Vertification(userName, authenticationManager))
			{
				user.setPassWord(passwordEncoder.encode(changePassword.getNewPassWord()));
				firestore.collection("User").document(user.getUserName()).set(user);
				return "Success";
			}
			else
			{
				return "Failed";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed";
		}
	}
	
	public String ChangeEmail(String userName, String newEmail)
	{
		try 
		{
			var user = GetUser(userName);
			user.setEmail(newEmail);
			firestore.collection("User").document(user.getUserName()).set(user);
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed";
		}
	}
	
	public String ChangeForgotPassWord(String userName, String newPassWord)
	{
		try 
		{
			var user = GetUser(userName);
			user.setPassWord(passwordEncoder.encode(newPassWord));
			firestore.collection("User").document(user.getUserName()).set(user);
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed";
		}
	}
	
	public String ChangeAuthenticatedPassWord(String userName, String newAuthenticatedPassWord)
	{
		try 
		{
			var user = GetUser(userName);
			user.setAuthenticatedPassWord(newAuthenticatedPassWord);
			firestore.collection("User").document(user.getUserName()).set(user);
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed";
		}
	}
}
