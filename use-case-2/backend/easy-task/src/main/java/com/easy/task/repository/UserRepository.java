package com.easy.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.easy.task.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
