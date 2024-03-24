package com.example.demo.modules.file;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	
	@DeleteMapping("/deleteFilePermanently")
	public ResponseEntity<String> DeleteFilePermanently (@RequestParam("fileID") int fileID, 
										    HttpServletRequest request) throws IOException, ExecutionException, InterruptedException 
	{
		return ResponseEntity.ok(documentService.DeleteDocumentPermanently(fileID, extractToken(request)));
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
	public ResponseEntity<List<FileDTO>> GetAllSavingFiles (HttpServletRequest request) throws IOException, ExecutionException, InterruptedException 
	{
		return ResponseEntity.ok(documentService.GetAllSavingFile(extractToken(request)));
	}
	
	@PostMapping("/addingToSharingFile")
	public ResponseEntity<String> AddingToSharingFile (@RequestParam("fileID") int fileID, 
													   @RequestParam("sharedUserName") String[] sharedUserName,
													   @RequestParam("permitType") int permitType,
													   HttpServletRequest request) 
													   throws IOException, ExecutionException, InterruptedException 
	{
		return ResponseEntity.ok(documentService.AddingSharingFile(fileID, sharedUserName, permitType, extractToken(request)));
	}
	
	@DeleteMapping("/removingSharingFile")
	public ResponseEntity<String> RemovingSharingFile (@RequestParam("fileID") int fileID, 
													   @RequestParam("sharedUserName") String[] sharedUserName,
													   HttpServletRequest request) 
													   throws IOException, ExecutionException, InterruptedException 
	{
		return ResponseEntity.ok(documentService.RemovingSharingFile(fileID, sharedUserName, extractToken(request)));
	}
	
	@PutMapping("/changePermission")
	public ResponseEntity<String> ChangePermission (@RequestParam("fileID") int fileID, 
													@RequestParam("sharedUserName") String[] sharedUserName,
													HttpServletRequest request) 
													throws IOException, ExecutionException, InterruptedException 
	{
		return ResponseEntity.ok(documentService.ChangePermission(fileID, sharedUserName, extractToken(request)));
	}
	
	@GetMapping("/getAllSharingFiles")
	public ResponseEntity<List<FileDTO>> GetAllSharingFiles (HttpServletRequest request) 
													  throws IOException, ExecutionException, InterruptedException 
	{
		return ResponseEntity.ok(documentService.GetAllSharingFile(extractToken(request)));
	}
	
	@GetMapping("/getAllSharableUsers")
	public ResponseEntity<List<SharableUser>> GetAllSharableUsers (@RequestParam("fileID") int fileID,
																   HttpServletRequest request)
														     	   throws IOException, ExecutionException, InterruptedException 
	{
		return ResponseEntity.ok(documentService.getAllSharableUser(fileID, extractToken(request)));
	}
}
