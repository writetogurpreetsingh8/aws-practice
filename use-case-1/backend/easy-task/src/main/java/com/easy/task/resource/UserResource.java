package com.easy.task.resource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.easy.task.beans.Task;
import com.easy.task.service.EasyTaskService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UserResource {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(UserResource.class);
    
	@Autowired
	private EasyTaskService easyTaskService;
	
	
	@GetMapping("/healthy")
	public ResponseEntity<Void> getHealth(){
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/task")
	public ResponseEntity<List<Task>> createTask(@RequestBody Task task, @RequestParam("userId") String userId) {
		LOGGER.info("UserResource:- invoking task() method...");
		return ResponseEntity.ok().body(easyTaskService.createTask(task, userId));
	}
	
	@GetMapping("/userId/{userId}")
	public ResponseEntity<List<Task>> getTask(@PathVariable("userId") String userId){
		return ResponseEntity.ok().body(easyTaskService.getTask(userId));
	}
	
	@DeleteMapping("/task")
	public ResponseEntity<List<Task>> removeTask(@RequestParam("taskId") String taskId, @RequestParam("userId") String userId){
		return ResponseEntity.ok().body(easyTaskService.removeTask(taskId,userId));
	}
}
