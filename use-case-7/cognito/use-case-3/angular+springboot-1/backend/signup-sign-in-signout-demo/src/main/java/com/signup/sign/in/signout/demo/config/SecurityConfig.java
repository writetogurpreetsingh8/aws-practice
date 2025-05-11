package com.signup.sign.in.signout.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.NullSecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	 	
		@Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http
	        .cors(Customizer.withDefaults())
	        	.csrf(csrf -> csrf.disable())
	        	.securityContext(sc -> sc.securityContextRepository(new NullSecurityContextRepository()))
	        	.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            
	        	.authorizeHttpRequests(authz -> authz
	            		.requestMatchers("/").permitAll()
	            		.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
	                .anyRequest().authenticated())
	            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
	        return http.build();
	    }
}
