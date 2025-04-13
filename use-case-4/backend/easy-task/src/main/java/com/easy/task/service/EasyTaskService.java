package com.easy.task.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easy.task.db.config.DbContextHolder;
import com.easy.task.entities.Task;
import com.easy.task.entities.User;
import com.easy.task.enums.DBType;
import com.easy.task.repository.TaskRepository;
import com.easy.task.repository.UserRepository;

@Service
public class EasyTaskService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(EasyTaskService.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TaskRepository taskRepository;
		
	public List<Task> createTask(Task task,String userId){
		LOGGER.info("EasyTaskService:- invoking createTask() method... with parameter userId {} ",userId);
		task.setUserId(userId);
		try {
			DbContextHolder.set(DBType.WRITE);
			taskRepository.save(task);
			return fetchTask(userId);
		}
		finally {
			DbContextHolder.clear();
		}
	}
	
	public List<Task> fetchTask(String userId){
		LOGGER.info("EasyTaskService:- invoking getTask() method... with parameter userId {} ",userId);
		try {
			DbContextHolder.set(DBType.READ);
			Optional<User> user = userRepository.findById(userId);
			if(Objects.isNull(user) || user.isEmpty()){
				return Collections.emptyList();
			}else {
				return user.get().getTasks();
			}
		}
		finally {
			DbContextHolder.clear();
		}
	}
	
	public List<User> fetchUsers() {
		try {
			DbContextHolder.set(DBType.READ);
			List<User> users = userRepository.findAll();
			if(Objects.isNull(users) || users.isEmpty()) {
				return Collections.emptyList();
			}else {
				return users;
			}
		}
		finally {
			DbContextHolder.clear();
		}
	}
	
	public List<Task> removeTask(String taskId, String userId){
		LOGGER.info("EasyTaskService:- invoking removeTask() method... with parameter userId {} and taskId {} ",userId, taskId);
		try {
			DbContextHolder.set(DBType.WRITE);
			taskRepository.deleteByTaskIdAndUserId(taskId, userId);
			List<Task> tasks = fetchTask(userId);
			return tasks;
		}
		finally {
			DbContextHolder.clear();
		}
	}
	
	public List<User> addUser(String userName) {
		User user = new User();
		user.setUserName(userName);
		try {
			DbContextHolder.set(DBType.WRITE);
			userRepository.save(user);
			List<User> users = fetchUsers();
			return users;
		}
		finally {
			DbContextHolder.clear();
		}
	}
}
