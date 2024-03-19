package com.example.demo.modules.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.modules.email.EmailService;
import com.example.demo.modules.information.InformationService;
import com.example.demo.modules.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.HttpRequest;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.servlet.http.HttpServletRequest;

	
@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired 
	private EmailService emailService;
	
	@Autowired
	private InformationService informationService;
	
	@Autowired 
	private JwtService jwtService;
	
	@GetMapping("/authenticate")
	public ResponseEntity<UserDTO> Authenticate(@RequestParam("userName") String userName, @RequestParam("passWord") String passWord)
	{
		try {
			if (!userName.isEmpty() && !userName.isBlank())
			{
				return ResponseEntity.ok(new UserDTO(userService.Authenticate(userName, passWord), jwtService.generateToken(userName), informationService));
			}
			else
			{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@PostMapping("/createUser")
	private ResponseEntity<String> CreateUser(@RequestBody User user)
	{
		return ResponseEntity.ok(userService.CreateUser(user));
	}
	
	@GetMapping("/getCodeByEmail")
	public ResponseEntity<Integer> GetCodeByEmail(@RequestParam("email") String email)
	{
		return ResponseEntity.ok(emailService.getAuthenticateEmailCode(email));
	}
	
	@GetMapping("/getCodeByUserName")
	public ResponseEntity<Integer> GetCodeByUserName(@RequestParam("userName") String userName)
	{
		return ResponseEntity.ok(emailService.getForgotPasswordCode(userName));
	}
	
	@PostMapping("/changeForgotPassWord")
	public ResponseEntity<String> ChangeForgotPassWord(@RequestParam("userName") String userName, @RequestParam("newPassWord") String newPassWord)
	{
		return ResponseEntity.ok(userService.ChangeForgotPassWord(userName, newPassWord));
	}
}
