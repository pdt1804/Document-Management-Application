package com.example.demo;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Configuration
@SpringBootApplication
public class DocumentManagementApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(DocumentManagementApplication.class, args);
		System.out.println("Success");

	}

}
