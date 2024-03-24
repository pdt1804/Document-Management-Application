package com.example.demo.modules.information;

import java.sql.Date;

import lombok.Data;

@Data
public class Information {

	private int informationID;
	private String fullName;
	private String address;
	private String nameOnCloud;
	private String image;
	private Date birthday;
}
