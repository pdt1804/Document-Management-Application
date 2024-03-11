package com.example.demo.modules.folder;


import java.util.Date;

import lombok.Data;

@Data
public class Folder {

	private int folderID;
	private String folderName;
	private String createdUser;
	private Date createdTime;
	private Date updatedTime;
	private int location;
	private double size;
	
}
