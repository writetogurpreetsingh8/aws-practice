package com.easy.task.db.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.easy.task.enums.DBType;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
	    basePackages = {"com.easy.task.repository"},
	    entityManagerFactoryRef = "entityManagerFactory",
	    transactionManagerRef = "transactionManager"
	)
public class DataSourceConfig {
	
	@Bean(name = "rawReadDataSource")
	@ConfigurationProperties("spring.datasource-read")
	public HikariDataSource readOnlyDataSource() {
		return new HikariDataSource();
	}
	
	@Bean(name = "writeDataSource")
	@ConfigurationProperties("spring.datasource-write")
	public HikariDataSource writeOnlyDataSource() {
		return new HikariDataSource();
	}
	
	@Bean(name = "readDataSource")
	public DataSource readDataSource(@Qualifier("rawReadDataSource") HikariDataSource hikari) {
	    return new ReadOnlyDataSource(hikari);
	}
	
	
	@Primary
    @Bean
    public DataSource routingDataSource(@Qualifier("writeDataSource") DataSource writeDs,
                                         @Qualifier("readDataSource") DataSource readDs) {
		
		if (writeDs instanceof HikariDataSource hikariWrite) {
	        System.out.println("✅ Primary DB URL: " + hikariWrite.getJdbcUrl());
	    }
		if (readDs instanceof ReadOnlyDataSource roDs) {
		    DataSource delegate = roDs.getTargetDataSource();
		    if (delegate instanceof HikariDataSource hikariRead) {
		        System.out.println("✅ Read Replica DB URL: " + hikariRead.getJdbcUrl());
		    }
		}
	    
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DBType.WRITE, writeDs);
        targetDataSources.put(DBType.READ, readDs);

        RoutingDataSource routingDs = new RoutingDataSource();
        routingDs.setTargetDataSources(targetDataSources);
        routingDs.setDefaultTargetDataSource(writeDs);
        return routingDs;
    }
	
	 @Primary
	 @Bean
     public LocalContainerEntityManagerFactoryBean entityManagerFactory(
    		 JpaVendorAdapter jpaVendorAdapter,
    		 JpaProperties jpaProperties,
            @Qualifier("routingDataSource") DataSource routingDataSource) {
		 
		 LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		 bean.setDataSource(routingDataSource);
		 bean.setJpaVendorAdapter(jpaVendorAdapter);
		 bean.setPackagesToScan("com.easy.task.entities");
		 bean.setPersistenceUnitName("readWritePU");
		 bean.setJpaPropertyMap(jpaProperties.getProperties());
		 return bean;
    }
	 
	 @Bean
	 public JpaVendorAdapter jpaVendorAdapter() {
	     return new HibernateJpaVendorAdapter();
	 }

     @Primary
     @Bean
     public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory emf) {
         return new JpaTransactionManager(emf);
     }
}
