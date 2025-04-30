package com.easy.task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.easy.task.entities.Task;

import jakarta.transaction.Transactional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
	
	@Transactional
	void deleteByTaskIdAndUserId(@Param("taskId") String taskId, @Param("userId") String userId);
	
	Task findByTaskIdAndUserId(@Param("taskId") String taskId, @Param("userId") String userId);
	
	@Query("select t from Task as t where t.userId=:userId")
	List<Task> findByUserId(@Param("userId") String userId);
	

}
