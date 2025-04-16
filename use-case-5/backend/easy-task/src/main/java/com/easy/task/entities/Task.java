package com.easy.task.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "task_tlb")
public class Task {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name="id")
	private String taskId;
	
	@Column(name="title")
    private String title;
    
	@Column(name="summary")
	private String summary;
    
	@Column(name="dueDate")
	private String dueDate;
	
	@Column(name="user_id_fk")
	private String userId;
	
	@Column(name="doc_name")
	private String docName;

	@Column(name="doc_full_path")
	private String docFullPath;	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getDocName() {
		return docName;
	}
	public void setDocName(String docName) {
		this.docName = docName;
	}
	public String getDocFullPath() {
		return docFullPath;
	}
	public void setDocFullPath(String docFullPath) {
		this.docFullPath = docFullPath;
	}
}
