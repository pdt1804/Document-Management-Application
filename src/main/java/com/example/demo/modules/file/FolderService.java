package com.example.demo.modules.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.modules.user.UserService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

import jakarta.annotation.PostConstruct;

@Service
public class FolderService {
	
	public static int id;

	@Autowired
	private Firestore firestore;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DocumentService documentService;
	
	@PostConstruct
	public void getLastFolderID() throws ExecutionException, InterruptedException
	{
		var documents = firestore.collection("File").get().get().getDocuments();
		
		if (documents.size() == 0)
		{
			id = 0;
		}
		else
		{
			for (var p : documents)
			{
				File file = p.toObject(File.class);
				if (file.getFileID() > id)
				{
					id = file.getFileID();
				}
			}
		}
	}
		
	public int GetNewFolderID() throws ExecutionException, InterruptedException {
		return ++id;
	}
	
	public String MoveToAnotherFolder(int folderID, int newFolderID, String userName) throws IOException, ExecutionException, InterruptedException {
		File existingFolder = firestore.collection("File").document(String.valueOf(folderID)).get().get().toObject(File.class);
		
		if (existingFolder.getCreatedUser().equals(userName))
		{
			documentService.UpdateSize(existingFolder.getSize(), existingFolder.getLocation(), "-");

			existingFolder.setLocation(newFolderID);
			firestore.collection("File").document(String.valueOf(existingFolder.getFileID())).set(existingFolder);
			
			documentService.UpdateSize(existingFolder.getSize(), existingFolder.getLocation(), "+");
			
			return "Success";
		}
		
		return "This folder is not belonged to your account !";
	}
	
	public File CreateFolder(File folder, String userName) throws ExecutionException, InterruptedException {
		int folderID = GetNewFolderID();
		folder.setFileID(folderID);
		folder.setCreatedUser(userName);
		folder.setSize(0);
		folder.setCreatedTime(new Date());
		folder.setUpdatedTime(new Date());
		folder.setType(FileType.Folder);
		firestore.collection("File").document(String.valueOf(folderID)).set(folder);
		return folder;
	}
	
	public String UpdateFolderName(File folder, String userName) throws ExecutionException, InterruptedException {
		File existingFolder = firestore.collection("File").document(String.valueOf(folder.getFileID())).get().get().toObject(File.class);
		
		if (userName.equals(existingFolder.getCreatedUser()))
		{
			existingFolder.setFileName(folder.getFileName());
			existingFolder.setUpdatedTime(new Date());
			firestore.collection("File").document(String.valueOf(existingFolder.getFileID())).set(existingFolder);
			return "Success";
		}
		
		return "This folder is not belonged to your account !";
	}
	
	public String DeleteFolder(int folderID, String userName) throws IOException, ExecutionException, InterruptedException {
		File existingFolder = firestore.collection("File").document(String.valueOf(folderID)).get().get().toObject(File.class);
		
		if (existingFolder.getCreatedUser().equals(userName))
		{			
			DeleteAllFileInFolder(folderID);
			
			firestore.collection("File").document(String.valueOf(folderID)).delete();
			documentService.UpdateSize(existingFolder.getSize(), existingFolder.getLocation(), "-");
			return "Success";
		}
		
		return "This folder is not belonged to your account !";
	}
	
	private void DeleteAllFileInFolder(int folderID) throws IOException, ExecutionException, InterruptedException {
		for (var document : firestore.collection("File").get().get().getDocuments())
		{
			File folder = document.toObject(File.class);
			
			if (folder.getLocation() == folderID)
			{
				if (folder.getType() == FileType.Folder)
				{
					DeleteAllFileInFolder(folder.getFileID());
					firestore.collection("File").document(String.valueOf(folder.getFileID())).delete();						
				}
				else
				{
					firestore.collection("File").document(String.valueOf(folder.getFileID())).delete();						
				}
			}
		}
	}
	
	public List<FileDTO> GetAllFolderOfUser(String userName) throws ExecutionException, InterruptedException {
		List<FileDTO> files = new ArrayList<>();
		ApiFuture<QuerySnapshot> query = firestore.collection("File").get();
		
		for (DocumentSnapshot document : query.get().getDocuments()) {
			File folder = document.toObject(File.class);
	        if (userName.equals(folder.getCreatedUser()) && folder.getLocation() == 0)
	        {
	        	files.add(new FileDTO(folder, firestore));	        	
	        }
	    }
		
		return files.stream().sorted((t1,t2) -> t2.getCreatedTime().compareTo(t1.getCreatedTime())).collect(Collectors.toList());
	}
}
