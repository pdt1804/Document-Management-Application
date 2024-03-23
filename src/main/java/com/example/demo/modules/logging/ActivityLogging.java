package com.example.demo.modules.logging;

import java.util.Date;

import com.example.demo.modules.file.SharingType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActivityLogging {

	private String userName;
	private String content;
	private Date occuredTime;
	private ActivityType type;
	
	public ActivityLogging(String userName, ActivityType type)
	{
		this.userName = userName;
		this.type = type;
		occuredTime = new Date();
	}
	
	public void setContentForLogIn()
	{
		content = "You logged in.";
	}
	
	public void setContentForLogOut()
	{
		content = "You logged out.";
	}
	
	public void setContentForMovingToAnotherLocation(String fileName)
	{
        content = "You moved a file/folder " + fileName + " to another location.";
	}
	
	public void setContentForUnsavingFile(String fileName)
	{
        content = "You unsaved a file " + fileName + ".";
	}
	
	public void setContentForUnsharingFile(String fileName, String sharedUserName)
	{
        content = "You stopped sharing a file " + fileName + " with user " + sharedUserName + ".";
	}
	
	public void setContentForChangingInformation()
	{
        content = "You changed your information.";
	}
	
	public void setContentForSavingFile(String fileName)
	{
        content = "You saved a file " + fileName + ".";
	}
	
	public void setContentForSharingFile(String fileName, String sharedUserName)
	{
        content = "You shared a file " + fileName + " with user " + sharedUserName + ".";
	}
	
	public void setContentForEditingFolderName(String fileName, String oldFileName)
	{
        content = "You edited a folder's name from " + oldFileName + " to " + fileName + ".";
	}
	
	public void setContentForChangingPermission(String fileName, String sharedUserName, String sharingType)
	{
        content = "You changed permissions of document " + fileName + " shared with user " + sharedUserName + " to status " + sharingType + ".";
	}
	
	public void setContentForDeletingFolder(String fileName)
	{
        content = "You deleted a folder " + fileName + ".";
	}
	
	public void setContentForCreatingFolder(String fileName)
	{
        content = "You created a folder " + fileName + ".";
	}
	
	public void setContentForDeletingDocument(String fileName)
	{
        content = "You deleted a document " + fileName + ".";
	}
	
	public void setContentForPostingDocument(String fileName)
	{
        content = "You posted a document " + fileName + ".";
	}
	
}
