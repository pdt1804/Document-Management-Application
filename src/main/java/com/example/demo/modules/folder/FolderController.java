package com.example.demo.modules.folder;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.modules.jwt.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/file")
public class FolderController {

	@Autowired
	private FolderService folderService;
	
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
	
	@PostMapping("/createFolder") // location property which used to check if folder is belonged to another folder. 
	public ResponseEntity<File> CreateFolder(@RequestBody File folder, HttpServletRequest request) 
			throws ExecutionException, InterruptedException
	{
		return ResponseEntity.ok(folderService.CreateFolder(folder, extractToken(request)));
	}
	
	@PutMapping("/updateFolder") // Update Folder Name 
	public ResponseEntity<String> UpdateFolder(@RequestBody File folder, HttpServletRequest request) 
			throws ExecutionException, InterruptedException
	{
		return ResponseEntity.ok(folderService.UpdateFolderName(folder, extractToken(request)));
	}
	
	@PutMapping("/moveToAnotherFolder") // Update Folder/File Location (Copy - Paste)
	public ResponseEntity<String> MoveToAnotherFolder(@RequestParam("folderID") int folderID, @RequestParam("newFolderID") int newFolderID, HttpServletRequest request) 
			throws ExecutionException, InterruptedException
	{
		return ResponseEntity.ok(folderService.MoveToAnotherFolder(folderID, newFolderID, extractToken(request)));
	}
	
	@DeleteMapping("/deleteFolder") 
	public ResponseEntity<String> DeleteFolder(@RequestParam("folderID") int folderID, HttpServletRequest request) 
			throws ExecutionException, InterruptedException
	{
		return ResponseEntity.ok(folderService.DeleteFolder(folderID, extractToken(request)));
	}
	
	@GetMapping("/getAllFolderOfUser") 
	public ResponseEntity<List<FileDTO>> GetAllFolderOfUser(HttpServletRequest request) 
			throws ExecutionException, InterruptedException
	{
		return ResponseEntity.ok(folderService.GetAllFolderOfUser(extractToken(request)));
	}
	
}
