package com.example.demo.modules.logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.modules.file.SharingType;
import com.google.cloud.firestore.Firestore;

@Service
public class ActivityLoggingService {

	@Autowired
	private Firestore firestore;
	
	public List<ActivityLogging> getAllActivityLoggingOfUser(String userName) throws IOException, ExecutionException, InterruptedException	{
		List<ActivityLogging> logs = new ArrayList<>();
		for (var p : firestore.collection("StorageLogging").get().get().getDocuments())
		{
			var log = p.toObject(ActivityLogging.class);
			if (log.getUserName().equals(userName))
			{
				logs.add(log);
			}
		}
		return logs.stream().sorted((t1,t2) -> t2.getOccuredTime().compareTo(t1.getOccuredTime())).toList();
	}
	
	public void AddLoggingForLogin(String userName) {
		var log = new ActivityLogging(userName, ActivityType.Login);
		log.setContentForLogIn();
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}
	
	public void AddLoggingForChangingInformation(String userName) {
		var log = new ActivityLogging(userName, ActivityType.ChangeInformation);
		log.setContentForChangingInformation();
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}
	
	public void AddLoggingForDeletingFolder(String userName, String fileName) {
		var log = new ActivityLogging(userName, ActivityType.DeleteFolder);
		log.setContentForDeletingFolder(fileName);
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}
	
	public void AddLoggingForDeletingFolderPermanently(String userName, String fileName) {
		var log = new ActivityLogging(userName, ActivityType.DeleteFolderPermanently);
		log.setContentForDeletingFolderPermanently(fileName);
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}
	
	public void AddLoggingForUpdatingFolderName(String userName, String fileName, String previousFileName) {
		var log = new ActivityLogging(userName, ActivityType.EditFolderName);
		log.setContentForEditingFolderName(fileName, previousFileName);
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}

	public void AddLoggingForCreatingFolderName(String userName, String fileName) {
		var log = new ActivityLogging(userName, ActivityType.CreateFolder);
		log.setContentForCreatingFolder(fileName);
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}
	
	public void AddLoggingForMovingToAnotherFolder(String userName, String fileName) {
		var log = new ActivityLogging(userName, ActivityType.MoveToAnotherLocation);
		log.setContentForMovingToAnotherLocation(fileName);
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}
	
	public void AddLoggingForPostingDocument(String userName, String fileName) {
		var log = new ActivityLogging(userName, ActivityType.PostDocument);
		log.setContentForPostingDocument(fileName);
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}
	
	public void AddLoggingForDeletingDocument(String userName, String fileName) {
		var log = new ActivityLogging(userName, ActivityType.DeleteDocument);
		log.setContentForDeletingDocument(fileName);
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}
	
	public void AddLoggingForDeletingDocumentPermanently(String userName, String fileName) {
		var log = new ActivityLogging(userName, ActivityType.DeleteDocumentPermanently);
		log.setContentForDeletingDocumentPermanently(fileName);
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}
	
	public void AddLoggingForAddingSavingFile(String userName, String fileName) {
		var log = new ActivityLogging(userName, ActivityType.SavingFile);
		log.setContentForSavingFile(fileName);
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}
	
	public void AddLoggingForRemovingSavingFile(String userName, String fileName) {
		var log = new ActivityLogging(userName, ActivityType.UnsavingFile);
		log.setContentForUnsavingFile(fileName);
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}

	public void AddLoggingForAddingSharingFile(String userName, String fileName, String sharedUserName) {
		var log = new ActivityLogging(userName, ActivityType.SharingFile);
		log.setContentForSharingFile(fileName, sharedUserName);
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}
	
	public void AddLoggingForRemovingSharingFile(String userName, String fileName, String sharedUserName) {
		var log = new ActivityLogging(userName, ActivityType.UnsharingFile);
		log.setContentForUnsharingFile(fileName, sharedUserName);
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}
	
	public void AddLoggingForChangingPermission(String userName, String fileName, String sharedUserName, SharingType sharingType) {
		String type = sharingType == SharingType.View ? "vỉew" : "editable";
		var log = new ActivityLogging(userName, ActivityType.ChangePermission);
		log.setContentForChangingPermission(fileName, sharedUserName, type);
		firestore.collection("StorageLogging").document(log.getUserName() + "-" + UUID.randomUUID()).set(log);
	}
}
