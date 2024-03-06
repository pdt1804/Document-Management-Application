package com.example.demo.modules.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.DocumentManagementApplication;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

	@PostConstruct
	public void FirebaseInit()
	{
		try
		{
			var classLoader = DocumentManagementApplication.class.getClassLoader();
			File file = new File(Objects.requireNonNull(classLoader.getResource("serviceAccountKey.json")).getFile());
			FileInputStream serviceAccount = new FileInputStream(file.getAbsolutePath());
	
			FirebaseOptions options = new FirebaseOptions.Builder()
					  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
					  .setDatabaseUrl("https://documentmanagement-737c7-default-rtdb.firebaseio.com")
					  .build();

			FirebaseApp.initializeApp(options);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Bean
	public Firestore getFirestore()
	{
		return FirestoreClient.getFirestore();
	}
}
