package com.example.demo.modules.file;

import java.util.Date;

import lombok.Data;

@Data
public class SharingDocument {

	private int fileID;
	private String userName;
	private Date sharingTime;
	private Date removingTime;
	private SharingType sharingType;
	private SharingStatus sharingStatus;

}
