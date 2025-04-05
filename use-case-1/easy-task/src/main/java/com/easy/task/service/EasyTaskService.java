package com.easy.task.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.easy.task.beans.Task;
import com.easy.task.container.DataContainer;

@Service
public class EasyTaskService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(EasyTaskService.class);


	public List<Task> createTask(Task task,String userId){
		
		LOGGER.info("EasyTaskService:- invoking createTask() method... with parameter userId {} ",userId);
		List<Task> tk;
		if(DataContainer.getDataContainer().containsKey(userId)) {
			tk = DataContainer.getDataContainer().get(userId);
			tk.add(task);
		}
		else {
			 tk = new ArrayList<>();
			 tk.add(task);
			DataContainer.getDataContainer().put(userId, tk);
		}
		return tk;
	}
	
	public List<Task> getTask(String userId){
		LOGGER.info("EasyTaskService:- invoking getTask() method... with parameter userId {} ",userId);
		if(DataContainer.getDataContainer().containsKey(userId)){
			return DataContainer.getDataContainer().get(userId);
		}else {
			return Collections.emptyList();
		}
	}
	
	public List<Task> removeTask(String taskId, String userId){
		
		List<Task> tasks = null;
		LOGGER.info("EasyTaskService:- invoking removeTask() method... with parameter userId {} and taskId {} ",userId, taskId);
		if(DataContainer.getDataContainer().containsKey(userId)) {
			tasks = DataContainer.getDataContainer().get(userId);
			tasks.removeIf(task -> task.getId().equals(taskId));
		}
		return tasks;
	}
}
