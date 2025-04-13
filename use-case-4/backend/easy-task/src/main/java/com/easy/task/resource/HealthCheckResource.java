package com.easy.task.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/")
public class HealthCheckResource {

	@GetMapping
	public ResponseEntity<Void> getHealth(){
		return ResponseEntity.ok().build();
	}
}
