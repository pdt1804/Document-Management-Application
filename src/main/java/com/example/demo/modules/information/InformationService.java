package com.example.demo.modules.information;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.modules.logging.ActivityLogging;
import com.example.demo.modules.logging.ActivityLoggingService;
import com.example.demo.modules.logging.ActivityType;
import com.example.demo.modules.user.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;

@Service
public class InformationService {

	@Autowired
	private Firestore firestore;
	
	@Autowired
	private ActivityLoggingService activityLoggingService;
	
	public Information GetInformation(long informationID)
	{
		try 
		{
			DocumentReference documentRef = firestore.collection("Information").document(String.valueOf(informationID));
			ApiFuture<DocumentSnapshot> future = documentRef.get();
			DocumentSnapshot document = future.get();
			
			if(document.exists())
			{
				Information information = document.toObject(Information.class);
				return information;
			}
			
			return null;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String ChangeInformation(Information information, String userName)
	{
		try {
			var info = firestore.collection("Information").document(String.valueOf(information.getInformationID())).get().get().toObject(Information.class);
			if (information.getAddress() != null) info.setAddress(information.getAddress());
			if (information.getBirthday() != null) info.setBirthday(information.getBirthday());
			if (information.getFullName() != null) info.setFullName(information.getFullName());

			firestore.collection("Information").document(String.valueOf(information.getInformationID())).set(info);
			activityLoggingService.AddLoggingForChangingInformation(userName);
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed";
		}
	}
	
	public String ChangeAvatar(int infoID, MultipartFile file, String userName) throws IOException, InterruptedException, ExecutionException
	{
		Bucket bucket = StorageClient.getInstance().bucket();
		var info = firestore.collection("Information").document(String.valueOf(infoID)).get().get().toObject(Information.class);
      
		if (info.getImage().startsWith("https://firebasestorage.googleapis.com"))
        {
        	Blob blob = bucket.get(info.getNameOnCloud());
        	blob.delete();
        }
		
		Random rd = new Random();
		String nameOnCloud = file.getName() + "-" + userName + "-" + rd.nextInt(1, 9999999) + "-" + rd.nextInt(1, 9999999);
		String url = "https://firebasestorage.googleapis.com/v0/b/" + firestore.getOptions().getProjectId() + ".appspot.com/o/" + nameOnCloud + "?alt=media";
	    bucket.create(nameOnCloud, file.getBytes(), file.getContentType());
	    info.setNameOnCloud(nameOnCloud);
	    info.setImage(url);
	    
	    firestore.collection("Information").document(String.valueOf(infoID)).set(info);
	    
	    return "Sucessful";
	}
}

