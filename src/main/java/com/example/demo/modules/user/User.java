package com.example.demo.modules.user;

import org.checkerframework.common.value.qual.MinLen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

import lombok.Data;

@Data
@JsonIgnoreType
public class User {

	private String userName;
	private String passWord;
	private String authenticatedPassWord;
	private String email;
	private int informationID;
	
}
