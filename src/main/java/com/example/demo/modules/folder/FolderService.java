package com.example.demo.modules.folder;

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

@Service
public class FolderService {

	@Autowired
	private Firestore firestore;
	
	@Autowired
	private UserService userService;
		
	public int GetNewFolderID() throws ExecutionException, InterruptedException {
		var documents = firestore.collection("Folder").get().get().getDocuments();
		
		if (documents.size() == 0)
		{
			return 1;
		}
		else
		{
			var lastDocument = documents.get(documents.size() - 1);
			return lastDocument.toObject(Folder.class).getFolderID() + 1;
		}
	}
	
	public Folder CreateFolder(Folder folder, String userName) throws ExecutionException, InterruptedException {
		int folderID = GetNewFolderID();
		folder.setFolderID(folderID);
		folder.setCreatedUser(userName);
		folder.setSize(0);
		folder.setCreatedTime(new Date());
		folder.setUpdatedTime(new Date());
		firestore.collection("Folder").document(String.valueOf(folderID)).set(folder);
		return folder;
	}
	
	public String UpdateFolderName(Folder folder, String userName) throws ExecutionException, InterruptedException {
		Folder existingFolder = firestore.collection("Folder").document(String.valueOf(folder.getFolderID())).get().get().toObject(Folder.class);
		
		if (userName.equals(existingFolder.getCreatedUser()))
		{
			existingFolder.setFolderName(folder.getFolderName());
			existingFolder.setUpdatedTime(new Date());
			firestore.collection("Folder").document(String.valueOf(existingFolder.getFolderID())).set(existingFolder);
			return "Success";
		}
		
		return "This folder is not belonged to your account !";
	}
	
	public String DeleteFolder(int folderID, String userName) throws ExecutionException, InterruptedException {
		Folder existingFolder = firestore.collection("Folder").document(String.valueOf(folderID)).get().get().toObject(Folder.class);
		
		if (existingFolder.getCreatedUser().equals(userName))
		{			
			for (var document : firestore.collection("Folder").get().get().getDocuments())
			{
				Folder folder = document.toObject(Folder.class);
				
				if (folder.getLocation() == folderID)
				{
					firestore.collection("Folder").document(String.valueOf(folder.getFolderID())).delete();
				}
			}
			
			firestore.collection("Folder").document(String.valueOf(folderID)).delete();
			return "Success";
		}
		
		return "This folder is not belonged to your account !";
	}
	
	public List<Folder> GetAllFolderOfUser(String userName) throws ExecutionException, InterruptedException {
		List<Folder> folders = new ArrayList<>();
		ApiFuture<QuerySnapshot> query = firestore.collection("Folder").get();
		
		for (DocumentSnapshot document : query.get().getDocuments()) {
	        Folder folder = document.toObject(Folder.class);
	        if (userName.equals(folder.getCreatedUser()))
	        {
	        	folders.add(folder);	        	
	        }
	    }
		
		return folders.stream().sorted((t1,t2) -> t2.getCreatedTime().compareTo(t1.getCreatedTime())).collect(Collectors.toList());
	}
}
