package com.example.demo.modules.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.modules.logging.ActivityLoggingService;
import com.example.demo.modules.user.User;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;


@Service
public class DocumentService {

	@Autowired
	private Firestore firestore;
	
	@Autowired
	private ActivityLoggingService activityLoggingService;
	
	public int GetNewFileID() throws ExecutionException, InterruptedException {
		return ++com.example.demo.modules.file.FolderService.id;
	}
	
	public String CreateDocument(MultipartFile doc, String userName, int folderID) throws IOException, InterruptedException, ExecutionException {
		Random rd = new Random();
        String nameOnCloud = doc.getName() + "-" + userName + "-" + rd.nextInt(1, 9999999) + "-" + rd.nextInt(1, 9999999);
        String url = "https://firebasestorage.googleapis.com/v0/b/" + firestore.getOptions().getProjectId() + ".appspot.com/o/" + nameOnCloud + "?alt=media";
		Bucket bucket = StorageClient.getInstance().bucket();
        var blob = bucket.create(nameOnCloud, doc.getBytes(), doc.getContentType());
        
        File file = new File();
        file.setCreatedTime(new Date());
        file.setCreatedUser(userName);
        file.setFileID(GetNewFileID());
        file.setFileName(doc.getName());
        file.setNameOnCloud(nameOnCloud);
        if (folderID != 0) file.setLocation(folderID);
        else file.setLocation(0);
        file.setSize(blob.getSize());
        file.setUrl(url);
        file.setUpdatedTime(new Date());
        
        FileType type;
        String name = doc.getOriginalFilename();
		if (name.endsWith(".doc") || name.endsWith(".docx")) type = FileType.Word;
		else if (name.endsWith(".xls") || name.endsWith(".xlsx")) type = FileType.Excel;
		else if (name.endsWith(".ppt") || name.endsWith(".pptx")) type = FileType.Powerpoint;
		else if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) type = FileType.Image;
		else if (name.endsWith(".mp4") || name.endsWith(".mov")) type = FileType.Video;
		else if (name.endsWith(".txt")) type = FileType.Text;
		else if (name.endsWith(".pdf")) type = FileType.PDF;
		else 
		{
			System.out.println("Không định dạng được file.");
			type = FileType.Undefined;
		}
		
		file.setType(type);
				
		firestore.collection("File").document(String.valueOf(file.getFileID())).set(file);
		
		UpdateSize(file.getSize(), folderID, "+");
        activityLoggingService.AddLoggingForPostingDocument(userName, file.getFileName());
		return url;
	}
	
	public void UpdateSize(double size, int folderID, String calculation) throws IOException, InterruptedException, ExecutionException {
		
		if (folderID == 0)
		{
			return;
		}
		else
		{
			var folder = firestore.collection("File").document(String.valueOf(folderID)).get().get().toObject(File.class);
			UpdateSize(size, folder.getLocation(), calculation);
			if (calculation.equals("+")) folder.setSize(folder.getSize() + size);
			else folder.setSize(folder.getSize() - size);
			firestore.collection("File").document(String.valueOf(folder.getFileID())).set(folder);
		}
		
	}

	public String DeleteDocument(int docID, String userName) throws IOException, InterruptedException, ExecutionException {
		File existingFile = firestore.collection("File").document(String.valueOf(docID)).get().get().toObject(File.class);
		
		if (existingFile.getCreatedUser().equals(userName))
		{			
			firestore.collection("File").document(String.valueOf(docID)).delete();
			firestore.collection("DeletedFile").document(String.valueOf(docID)).set(existingFile);
			UpdateSize(existingFile.getSize(), existingFile.getLocation(), "-");
			activityLoggingService.AddLoggingForDeletingDocument(userName, existingFile.getFileName()); 
			return "Success";
		}
		return "This folder is not belonged to your account !";
	}
	
	public String DeleteDocumentPermanently(int docID, String userName) throws IOException, InterruptedException, ExecutionException {
		File existingFile = firestore.collection("DeletedFile").document(String.valueOf(docID)).get().get().toObject(File.class);
		
		if (existingFile.getCreatedUser().equals(userName))
		{			
			firestore.collection("DeletedFile").document(String.valueOf(docID)).delete();
			Bucket bucket = StorageClient.getInstance().bucket();
			Blob blob = bucket.get(existingFile.getNameOnCloud());
	        blob.delete();
			activityLoggingService.AddLoggingForDeletingDocumentPermanently(userName, existingFile.getFileName()); 
			return "Success";
		}
		return "This folder is not belonged to your account !";
	}
	
	public String AddingSavingFile(int fileID, String userName) throws IOException, InterruptedException, ExecutionException {
		var doc = firestore.collection("SavingDocuments").document(userName + "-" + String.valueOf(fileID)).get().get().toObject(SavingDocument.class);
		if (doc == null)
		{
			SavingDocument savingDoc = new SavingDocument();
			savingDoc.setFileID(fileID);
			savingDoc.setUserName(userName);
			savingDoc.setSavingTime(new Date());
			firestore.collection("SavingDocuments").document(userName + "-" + String.valueOf(fileID)).set(savingDoc);
			
			var file = firestore.collection("File").document(String.valueOf(fileID)).get().get().toObject(File.class);
			activityLoggingService.AddLoggingForAddingSavingFile(userName, file.getFileName());
			
			return "Saving successfully";
		}
		else
		{
			return "You have already saved this file.";
		}
	}
	
	public void RemovingSavingFile(int fileID, String userName) throws IOException, InterruptedException, ExecutionException {
		var doc = firestore.collection("SavingDocuments").document(userName + "-" + String.valueOf(fileID)).get().get().toObject(SavingDocument.class);
		firestore.collection("SavingDocuments").document(userName + "-" + String.valueOf(fileID)).delete();
		
		var file = firestore.collection("File").document(String.valueOf(fileID)).get().get().toObject(File.class);
		activityLoggingService.AddLoggingForRemovingSavingFile(userName, file.getFileName());
	}
	
	public List<FileDTO> GetAllSavingFile(String userName) throws IOException, InterruptedException, ExecutionException {
		List<FileDTO> savingDocs = new ArrayList<>();
		var listSavingFile = firestore.collection("SavingDocuments").get().get().getDocuments();
		for(var p : listSavingFile) 
		{
			SavingDocument doc = p.toObject(SavingDocument.class);
			if (doc.getUserName().equals(userName))
			{
				savingDocs.add(new FileDTO(firestore.collection("File").document(String.valueOf(doc.getFileID())).get().get().toObject(File.class), firestore));
			}
		}
		return savingDocs;
	}
	
	public String AddingSharingFile(int fileID, String[] sharedUserName, int permitType, String owner) throws IOException, InterruptedException, ExecutionException {
		var file = firestore.collection("File").document(String.valueOf(fileID)).get().get().toObject(File.class);
		if (file.getCreatedUser().equals(owner))
		{
			for (var p : sharedUserName)
			{
				SharingDocument doc = new SharingDocument();
				doc.setFileID(fileID);
				doc.setUserName(p);
				doc.setSharingTime(new Date());
				SharingType type;
				if (permitType == 1) type = SharingType.View;
				else type = SharingType.Editable;
				doc.setSharingType(type);
				firestore.collection("SharingDocument").document(p + "-" + fileID).set(doc);
				activityLoggingService.AddLoggingForAddingSharingFile(owner, file.getFileName(), p);
			}
			return "Successful";
		}
		return "This folder is not belonged to your account !";
	}
	
	public String RemovingSharingFile(int fileID, String[] sharedUserName, String owner) throws IOException, InterruptedException, ExecutionException {
		var file = firestore.collection("File").document(String.valueOf(fileID)).get().get().toObject(File.class);
		if (file.getCreatedUser().equals(owner))
		{
			for (var p : sharedUserName)
			{
				SharingDocument doc = firestore.collection("SharingDocument").document(p + "-" + fileID).get().get().toObject(SharingDocument.class);
				doc.setRemovingTime(new Date());
				firestore.collection("UnsharingDocument").document(p + "-" + fileID + "-" + UUID.randomUUID()).set(doc);
				firestore.collection("SharingDocument").document(p + "-" + fileID).delete();
				activityLoggingService.AddLoggingForRemovingSharingFile(owner, file.getFileName(), p);
			}
			return "Successful";
		}
		return "This folder is not belonged to your account !";	
	}
	
	public List<FileDTO> GetAllSharingFile(String userName) throws IOException, InterruptedException, ExecutionException {
		List<FileDTO> files = new ArrayList<>();
		for (var p : firestore.collection("SharingDocument").get().get().getDocuments())
		{
			SharingDocument doc = p.toObject(SharingDocument.class);
			if (doc.getUserName().equals(userName))
			{
				files.add(new FileDTO(firestore.collection("File").document(String.valueOf(doc.getFileID())).get().get().toObject(File.class), firestore));
			}
		}
		return files;
	}
	
	public String ChangePermission(int fileID, String[] sharedUserName, String owner) throws IOException, InterruptedException, ExecutionException {
		File file = firestore.collection("File").document(String.valueOf(fileID)).get().get().toObject(File.class);
		if (file.getCreatedUser().equals(owner))
		{
			for (var p : sharedUserName)
			{
				SharingDocument doc = firestore.collection("SharingDocument").document(p + "-" + fileID).get().get().toObject(SharingDocument.class);			
				if (doc.getSharingType() == SharingType.View) doc.setSharingType(SharingType.Editable);
				else doc.setSharingType(SharingType.View);
				firestore.collection("SharingDocument").document(p + "-" + fileID).set(doc);
				activityLoggingService.AddLoggingForChangingPermission(owner, file.getFileName(), p, doc.getSharingType());
			}
			return "Changed Successfully";
		}
		return "This folder is not belonged to your account !";	
	}
	
	public List<SharableUser> getAllSharableUser(int fileID, String userName) throws InterruptedException, ExecutionException, IOException {
		
		List<String> existingUserName = new ArrayList<>();
		existingUserName.add(userName);
		List<SharableUser> users = new ArrayList<>();
		
		for (var p : firestore.collection("SharingDocument").get().get().getDocuments())
		{
			var doc = p.toObject(SharingDocument.class);
			if (doc.getFileID() == fileID)
			{
				existingUserName.add(doc.getUserName());
			}
		}
		
		for (var p : firestore.collection("User").get().get().getDocuments())
		{
			var user = p.toObject(User.class);
			if (!existingUserName.stream().anyMatch(usr -> usr.equals(user.getUserName())))
			{
				users.add(new SharableUser(user, firestore));
			}
		}
		
		return users; 
	}
	
}
