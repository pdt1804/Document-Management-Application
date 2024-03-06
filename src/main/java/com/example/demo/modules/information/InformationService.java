package com.example.demo.modules.information;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.modules.user.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

@Service
public class InformationService {

	@Autowired
	private Firestore firestore;
	
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
	
	public void ChangeInformation(Information information)
	{
		firestore.collection("Information").document(String.valueOf(information.getInformationID())).set(information);
	}
}

