package com.prokopovich.service;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LiquibasePreBuildDemoDataService {
    public static final String URL = DatabaseConfig.getDatabaseUrlService();
    public static final String USER_NAME = DatabaseConfig.getDatabaseUsername();
    public static final String PASSWORD = DatabaseConfig.getDatabasePassword();
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase =
                    new Liquibase("db/changelog/changelogService.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("Migration service is completed successfully");

        } catch (SQLException | LiquibaseException e) {
            System.out.println("Got SQL Exception in migration" + e.getMessage());
        }
    }
}

