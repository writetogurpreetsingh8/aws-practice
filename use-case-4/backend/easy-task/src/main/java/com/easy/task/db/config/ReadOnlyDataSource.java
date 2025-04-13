package com.easy.task.db.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DelegatingDataSource;

public class ReadOnlyDataSource extends DelegatingDataSource{
	public ReadOnlyDataSource(DataSource targetDataSource) {
        super(targetDataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = super.getConnection();
        conn.setReadOnly(true);
        System.out.println("getConnection()........................... ");
        return conn;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection conn = super.getConnection(username, password);
        conn.setReadOnly(true);
        System.out.println("getConnection(String username, String password)........................... "+username +" "+password);
        return conn;
    }
}
