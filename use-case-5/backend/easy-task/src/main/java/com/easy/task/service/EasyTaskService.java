package com.easy.task.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.easy.task.entities.Task;
import com.easy.task.entities.User;
import com.easy.task.repository.TaskRepository;
import com.easy.task.repository.UserRepository;

@Service
public class EasyTaskService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(EasyTaskService.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TaskRepository taskRepository;

	public List<Task> createTask(MultipartFile file ,Task task,String userId) throws IOException{
		LOGGER.info("EasyTaskService:- invoking createTask() method... with parameter userId {} ",userId);
		task.setUserId(userId);
		try {
			if(Objects.nonNull(file)) {
				processFile(file,task);				
			}
			taskRepository.save(task);
			return fetchTask(userId);
		} catch (IOException e) {
			LOGGER.error("error {} occurred while saving task document for user: {} ",e, userId);
			throw e;
			
		}
	}
	
	private void processFile(MultipartFile file, Task task) throws IOException {
		LocalDate date = LocalDate.now();
		int year = date.getYear();
		int month = date.getMonth().getValue();
		int dayOfMonth = date.getDayOfMonth();
		String location = System.getProperty("java.io.tmpdir");		
		final String docPath = (location+File.separator+year+File.separator+month+File.separator+dayOfMonth+File.separator+task.getUserId());
		
		Path dir = Paths.get(docPath);
		if(!Files.exists(dir)) {
			Files.createDirectories(dir);			
		}
		
		Path finalFile = dir.resolve(file.getOriginalFilename());
		Files.copy(file.getInputStream(), finalFile,StandardCopyOption.REPLACE_EXISTING);
		task.setDocFullPath(finalFile.toString());
		task.setDocName(file.getOriginalFilename());
	}

	public List<Task> fetchTask(String userId){
		LOGGER.info("EasyTaskService:- invoking getTask() method... with parameter userId {} ",userId);
		Optional<User> user = userRepository.findById(userId);
		if(Objects.isNull(user) || user.isEmpty()){
			return Collections.emptyList();
		}else {
			return user.get().getTasks();
		}
	}
	
	public List<User> fetchUsers() {
		List<User> users = userRepository.findAll();
		if(Objects.isNull(users) || users.isEmpty()) {
			return Collections.emptyList();
		}else {
			return users;
		}
	}
	
	public List<Task> removeTask(String taskId, String userId){
		LOGGER.info("EasyTaskService:- invoking removeTask() method... with parameter userId {} and taskId {} ",userId, taskId);
		Task task = taskRepository.findByTaskIdAndUserId(taskId, userId);
		if(Objects.nonNull(task.getDocFullPath())) {
			deleteTaskDoc(task);
		}
		taskRepository.deleteByTaskIdAndUserId(taskId, userId);
		return fetchTask(userId);
	}
	
	private void deleteTaskDoc(Task task) {
		Path path = Paths.get(task.getDocFullPath());
		if(Files.exists(path)) {
			new File(path.toUri()).delete();
		}
	}

	public List<User> addUser(String userName) {
		User user = new User();
		user.setUserName(userName);
		userRepository.save(user);
		return fetchUsers();
	}
	
	public Resource fetchTaskDocument(String taskId, String userId) throws FileNotFoundException, MalformedURLException {
		
		Task task = taskRepository.findByTaskIdAndUserId(taskId, userId);
		Path path = Paths.get(task.getDocFullPath());
		try {
			UrlResource res = new FileUrlResource(path.toString());
			if(!res.exists()) {
				LOGGER.error("error occured while downloading task file: {} ",task.getDocName());
				throw new FileNotFoundException("File "+task.getDocName()+ "not found");
			}
			return res;
		} catch (MalformedURLException e) {
			LOGGER.error("error {} occurred while downloading task file: {} ",e, task.getDocName());
			throw e;
		}
	}
}
