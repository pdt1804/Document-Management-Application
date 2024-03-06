package com.example.demo.modules.email;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.modules.user.User;
import com.example.demo.modules.user.UserService;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender sender;
	
	@Autowired
	private UserService userService;
	
	public int getAuthenticateEmailCode(String email)
	{
		if (email.endsWith("@gmail.com") || email.endsWith("@gm.uit.edu.vn"))
		{
			int OTP = generateRandomNumber();
			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setFrom("phamduythong600@gmail.com");
			mail.setTo(email);
			mail.setText("Mã OTP của bạn để hoàn tất thủ tục tạo tài khoản là: " + OTP);
			mail.setSubject("Hệ thống Datal gửi bạn mã OTP !!!");
			
			sender.send(mail);
			
			return OTP;
		}
		
		return -1;
	}
	
	public int getForgotPasswordCode(String userName)
	{
		try 
		{
			User user = userService.GetUser(userName);
			
			if (user != null)
			{
				return getAuthenticateEmailCode(user.getEmail());
			}
			
			return -1;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return -1;
		}
	}
	
	public int generateRandomNumber() 
	{
        Random random = new Random();
        int min = 100000; 
        int max = 999999; 
        return random.nextInt(max - min + 1) + min;
    }
}
