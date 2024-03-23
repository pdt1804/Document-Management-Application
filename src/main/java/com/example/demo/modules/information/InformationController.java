package com.example.demo.modules.information;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.modules.email.EmailService;
import com.example.demo.modules.jwt.JwtService;
import com.example.demo.modules.logging.ActivityLogging;
import com.example.demo.modules.logging.ActivityLoggingService;
import com.example.demo.modules.user.ChangePassword;
import com.example.demo.modules.user.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/information")
public class InformationController {

	@Autowired
	private InformationService informationService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired 
	private JwtService jwtService;
	
	@Autowired
	private ActivityLoggingService activityLoggingService;
	
	public String extractToken(HttpServletRequest httpRequest)
	{
		String authHeader = httpRequest.getHeader("Authorization");
		String userName = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            userName = jwtService.extractUsername(authHeader.substring(7));
        }
        return userName;
	}
	
	@GetMapping("/getInformation") /* NO SECURITY !!! Instead of utilizing this API, 
									  after authenticating successfully, 
									  we will send back a graphQL type with user, token and information of user. */
	public ResponseEntity<Information> GetInformation(@RequestParam("informationID") long informationID)
	{
		Information information = informationService.GetInformation(informationID);
		return ResponseEntity.ok(informationService.GetInformation(informationID)); 
	}
	
	@GetMapping("/getActivitiesLoggingOfUser")
	public ResponseEntity<List<ActivityLogging>> GetInformation(HttpServletRequest httpRequest) throws IOException, ExecutionException, InterruptedException
	{
		return ResponseEntity.ok(activityLoggingService.getAllActivityLoggingOfUser(extractToken(httpRequest))); 
	}
	
	@PostMapping("/changeInformation") // Information + Image
	public ResponseEntity<String> ChangeInformation(@RequestBody Information information, HttpServletRequest httpRequest)
	{
		var user = userService.GetUser(extractToken(httpRequest));
		if (user.getInformationID() != information.getInformationID())
		{
			return ResponseEntity.ok("InformationID of information isn't belonged to your username.");
		}
		
		return ResponseEntity.ok(informationService.ChangeInformation(information, extractToken(httpRequest))); 
	}
	
	@PostMapping("/changePassWord")
	public ResponseEntity<String> ChangePassword(HttpServletRequest httpRequest, @RequestBody ChangePassword changePassword)
	{
		return ResponseEntity.ok(userService.ChangePassword(changePassword, extractToken(httpRequest))); 
	}
	
	@PostMapping("/changeEmail")
	public ResponseEntity<String> ChangeEmail(HttpServletRequest httpRequest, @RequestParam("email") String newEmail)
	{
		return ResponseEntity.ok(userService.ChangeEmail(extractToken(httpRequest), newEmail)); 
	}
	
	@PostMapping("/changeAuthenticatedPassWord")
	public ResponseEntity<String> ChangeAuthenticatedPassWord(HttpServletRequest httpRequest, @RequestParam("newAuthenticatedPassWord") String newAuthenticatedPassWord)
	{
		return ResponseEntity.ok(userService.ChangeAuthenticatedPassWord(extractToken(httpRequest), newAuthenticatedPassWord)); 
	}
	
}
