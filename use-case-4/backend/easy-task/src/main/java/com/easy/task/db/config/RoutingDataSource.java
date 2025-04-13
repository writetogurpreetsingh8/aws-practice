package com.easy.task.db.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.easy.task.enums.DBType;

public class RoutingDataSource extends AbstractRoutingDataSource{

	@Override
	protected Object determineCurrentLookupKey() {
		DBType dbType =  DbContextHolder.get();
		System.out.println("🔄 Routing to DB: " + dbType);
		return dbType;
	}

}
