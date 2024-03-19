package com.example.demo.modules.folder;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
	
	public int GetNewFileID() throws ExecutionException, InterruptedException {
		return ++com.example.demo.modules.folder.FolderService.id;
	}
	
	public String CreateDocument(MultipartFile doc, String userName, int folderID) throws IOException, InterruptedException, ExecutionException {
		Random rd = new Random();
        String fileName = doc.getName() + "-" + userName + "-" + rd.nextInt(1, 999999);
		Bucket bucket = StorageClient.getInstance().bucket();
        var blob = bucket.create(fileName, doc.getBytes(), doc.getContentType());
        
        File file = new File();
        file.setCreatedTime(new Date());
        file.setCreatedUser(userName);
        file.setFileID(GetNewFileID());
        file.setFileName(fileName);
        if (folderID != 0) file.setLocation(folderID);
        else file.setLocation(0);
        file.setSize(blob.getSize());
        file.setUrl(blob.getMediaLink());
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
		
		if (folderID != 0)
		{
			var folder = firestore.collection("File").document(String.valueOf(folderID)).get().get().toObject(File.class);
			folder.setSize(folder.getSize() + file.getSize());
			firestore.collection("File").document(String.valueOf(folderID)).set(folder);
		}
        
		return blob.getMediaLink();
	}
	
	public String DeleteDocument(int docID, String userName) throws IOException, InterruptedException, ExecutionException {
		File existingFile = firestore.collection("File").document(String.valueOf(docID)).get().get().toObject(File.class);
		
		if (existingFile.getCreatedUser().equals(userName))
		{			
			firestore.collection("File").document(String.valueOf(docID)).delete();
			
			if (existingFile.getLocation() != 0)
			{
				var folder = firestore.collection("File").document(String.valueOf(existingFile.getLocation())).get().get().toObject(File.class);
				folder.setSize(folder.getSize() - existingFile.getSize());
				firestore.collection("File").document(String.valueOf(existingFile.getLocation())).set(folder);
			}
			
			return "Success";
		}
		
		return "This folder is not belonged to your account !";
	}
	
}
