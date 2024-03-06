package com.example.demo.modules.information;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.modules.email.EmailService;
import com.example.demo.modules.user.ChangePassword;
import com.example.demo.modules.user.UserService;

@RestController
@RequestMapping("/api/information")
public class InformationController {

	@Autowired
	private InformationService informationService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailService emailService;
	
	@GetMapping("/getInformation")
	public ResponseEntity<Information> GetInformation(@RequestParam("informationID") long informationID)
	{
		return ResponseEntity.ok(informationService.GetInformation(informationID)); 
	}
	
	@PostMapping("/changeInformation") // Information + Image
	public void ChangeInformation(@RequestBody Information information)
	{
		informationService.ChangeInformation(information); 
	}
	
	@PostMapping("/changePassWord")
	public ResponseEntity<String> ChangePassword(@RequestParam("userName") String userName, @RequestBody ChangePassword changePassword)
	{
		return ResponseEntity.ok(userService.ChangePassword(changePassword, userName)); 
	}
	
	@PostMapping("/changeEmail")
	public ResponseEntity<String> ChangeEmail(@RequestParam("userName") String userName, @RequestParam("email") String newEmail)
	{
		return ResponseEntity.ok(userService.ChangeEmail(userName, newEmail)); 
	}
	
	@PostMapping("/changeAuthenticatedPassWord")
	public ResponseEntity<String> ChangeAuthenticatedPassWord(@RequestParam("userName") String userName, @RequestParam("newAuthenticatedPassWord") String newAuthenticatedPassWord)
	{
		return ResponseEntity.ok(userService.ChangeAuthenticatedPassWord(userName, newAuthenticatedPassWord)); 
	}
	
}
