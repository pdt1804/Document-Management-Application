package com.example.demo.modules.file;

import java.util.Date;

import lombok.Data;

@Data
public class File {

	private int fileID;
	private String fileName;
	private String nameOnCloud;
	private String createdUser;
	private String url;
	private Date createdTime;
	private Date updatedTime;
	private int location;
	private double size;
	private FileType type;
}
