package com.easy.task.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_tlb")
public class User {
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.UUID)
	private String userId;
	
	@Column(name="name")
	private String userName;
	
	@Column(name="avatar_name")
	private String userAvatar;
	
	@Column(name="avatar_full_path")
	private String avatarFullPath;
	
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id_fk",referencedColumnName = "id")
	private List<Task> tasks;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAvatar() {
		return userAvatar;
	}

	public void setUserAvatar(String userAvatar) {
		this.userAvatar = userAvatar;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public String getAvatarFullPath() {
		return avatarFullPath;
	}

	public void setAvatarFullPath(String avatarFullPath) {
		this.avatarFullPath = avatarFullPath;
	}

}
