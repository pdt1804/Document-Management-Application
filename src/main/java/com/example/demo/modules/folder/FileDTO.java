package com.example.demo.modules.folder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import com.google.cloud.firestore.Firestore;

import lombok.Data;

@Data
public class FileDTO {

	private int fileID;
	private String fileName;
	private String createdUser;
	private String url;
	private Date createdTime;
	private Date updatedTime;
	private int location;
	private double size;
	private FileType type;
	private List<FileDTO> files;
		
	public FileDTO(File file, Firestore firestore) throws InterruptedException, ExecutionException
	{
		files = new ArrayList<>();
		this.fileID = file.getFileID();
		this.fileName = file.getFileName();
		this.createdUser = file.getCreatedUser();
		this.url = file.getUrl();
		this.createdTime = file.getCreatedTime();
		this.updatedTime = file.getUpdatedTime();
		this.location = file.getLocation();
		this.size = file.getSize();
		this.type = file.getType();
				
		if (type == FileType.Folder)
		{
			getAllFilesInFolder(firestore);
		}
	}
	
//	public void getAllFilesInFolder() throws InterruptedException, ExecutionException
//	{
//		var listFile = firestore.collection("File").get().get().getDocuments();
//		for (var rawFile : listFile)
//		{
//			File file = rawFile.toObject(File.class);
//			if (file.getLocation() == this.fileID && file.getCreatedUser().equals(createdUser))
//			{
//				var fileDTO = new FileDTO(file);
//				
//				files.add(new FileDTO(file));
//			}
//		}
//	}
	
	public void getAllFilesInFolder(Firestore firestore) throws InterruptedException, ExecutionException
	{
		var listFile = firestore.collection("File").get().get().getDocuments();
		for (var rawFile : listFile)
		{
			File file = rawFile.toObject(File.class);
			if (file.getLocation() != 0 && file.getLocation() == this.fileID)
			{
				files.add(new FileDTO(file, firestore));
			}
		}
	}
}
