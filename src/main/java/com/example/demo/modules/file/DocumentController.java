package com.example.demo.modules.file;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.modules.jwt.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/file")
public class DocumentController {

	@Autowired
	private DocumentService documentService;
	
	@Autowired
	private JwtService jwtService;
	
	public String extractToken(HttpServletRequest httpRequest)
	{
		String authHeader = httpRequest.getHeader("Authorization");
		String userName = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            userName = jwtService.extractUsername(authHeader.substring(7));
        }
        return userName;
	}
	
	@PostMapping("/postFile")
	public ResponseEntity<String> PostFile (@RequestParam("file") MultipartFile file, 
										    @RequestParam("folderID") int folderID, 
										    HttpServletRequest request) throws IOException, ExecutionException, InterruptedException 
	{
		return ResponseEntity.ok(documentService.CreateDocument(file, extractToken(request), folderID));
	}
	
	@DeleteMapping("/deleteFile")
	public ResponseEntity<String> DeleteFile (@RequestParam("fileID") int fileID, 
										    HttpServletRequest request) throws IOException, ExecutionException, InterruptedException 
	{
		return ResponseEntity.ok(documentService.DeleteDocument(fileID, extractToken(request)));
	}
	
	@PostMapping("/addingToSavingFile")
	public ResponseEntity<String> AddingToSavingFIle (@RequestParam("fileID") int fileID, 
													   HttpServletRequest request) 
													   throws IOException, ExecutionException, InterruptedException 
	{
		return ResponseEntity.ok(documentService.AddingSavingFile(fileID, extractToken(request)));
	}
	
	@DeleteMapping("/removingSavingFile")
	public void RemovingSavingFile (@RequestParam("fileID") int fileID, 
										    HttpServletRequest request) throws IOException, ExecutionException, InterruptedException 
	{
		documentService.RemovingSavingFile(fileID, extractToken(request));
	}
	
	@GetMapping("/getAllSavingFiles")
	public ResponseEntity<List<File>> GetAllSavingFiles (HttpServletRequest request) throws IOException, ExecutionException, InterruptedException 
	{
		return ResponseEntity.ok(documentService.GetAllSavingFile(extractToken(request)));
	}
}
