package com.easy.task.resource;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.easy.task.Response;
import com.easy.task.entities.Task;
import com.easy.task.entities.User;
import com.easy.task.service.EasyTaskService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class Resource {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Resource.class);
    
	@Autowired
	private EasyTaskService easyTaskService;
	
	@PostMapping(value="/task", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Response<List<Task>>> createTask(@RequestPart(value = "file",required = false) MultipartFile file,
			@RequestPart("task") Task task, @RequestPart("userId") String userId) throws IOException {
		LOGGER.info("UserResource:- invoking task() method...");
		return ResponseEntity.ok().body(easyTaskService.createTask(file, task, userId));
	}
	
	@GetMapping("/userId/{userId}")
	public ResponseEntity<Response<List<Task>>> getTask(@PathVariable("userId") String userId){
		return ResponseEntity.ok().body(easyTaskService.fetchTask(userId));
	}
	
	@DeleteMapping("/task")
	public ResponseEntity<Response<List<Task>>> removeTask(@RequestParam("taskId") String taskId, @RequestParam("userId") String userId){
		return ResponseEntity.ok().body(easyTaskService.removeTask(taskId,userId));
	}
	
	@GetMapping("/users")
	public ResponseEntity<List<User>> getUsers(){
		return ResponseEntity.ok().body(easyTaskService.fetchUsers());
	}
	
	@PostMapping("/user")
    public ResponseEntity<List<User>> createUser(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userName") String userName) throws Exception {

        System.out.println("File name: " + file.getOriginalFilename());
        System.out.println("userName: " + userName);
        return ResponseEntity.ok().body(easyTaskService.addUser(userName, file));
    }
	
	@GetMapping("/{taskId}/{userId}")
	public ResponseEntity<org.springframework.core.io.Resource> downloadTaksDoc(@PathVariable("userId") String userId,
			@PathVariable("taskId") String taskId) throws IOException{
		
		org.springframework.core.io.Resource resource = easyTaskService.fetchTaskDocument(taskId,userId);
		return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFile().getName() + "\"")
        .body(resource);
	}
	
}
