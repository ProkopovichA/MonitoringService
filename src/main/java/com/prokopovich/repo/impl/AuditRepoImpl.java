package com.prokopovich.repo.impl;

/**
 * Класс "список"
 * хранит в себе коллекцию обектов "аудит"
 * реализован как синглтон
 */

import com.prokopovich.model.Audit;
import com.prokopovich.model.AuditAction;
import com.prokopovich.model.User;
import com.prokopovich.repo.AuditRepo;
import com.prokopovich.repo.UsersRepo;
import com.prokopovich.service.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class AuditRepoImpl implements AuditRepo {
    public static final String URL = DatabaseConfig.getDatabaseUrlService();
    public static final String USER_NAME = DatabaseConfig.getDatabaseUsername();
    public static final String PASSWORD = DatabaseConfig.getDatabasePassword();
    private static final String GET_AUDIT_LIST_SQL_QUERY = "SELECT user_id, date_of_notes, audit_action FROM audit";
    private static final String INSERT_DATA_SQL = "INSERT INTO audit (user_id, date_of_notes, audit_action) VALUES (?, ?, ?) RETURNING id";
    private static AuditRepoImpl instance;

    private AuditRepoImpl() {

    }

    public static AuditRepoImpl getInstance() {
        if (instance == null) {
            instance = new AuditRepoImpl();
        }
        return instance;
    }

    /**
     * вывести все записи по аудиту
     *
     * @return лист объектов аудит
     */
    public ArrayList<Audit> getAuditList() {
        ArrayList<Audit> auditList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {

            try (Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery(GET_AUDIT_LIST_SQL_QUERY);

                while (resultSet.next()) {
                    int userIdFromRepo = resultSet.getInt("user_id");
                    LocalDate dateFromRepo = resultSet.getDate("date_of_notes").toLocalDate();
                    AuditAction action = AuditAction.valueOf(resultSet.getString("audit_action"));
                    User userFromRepo = UserRepoImpl.getInstance().findUserById(userIdFromRepo);
                    auditList.add(new Audit(userFromRepo, dateFromRepo, action));
                }
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception getTypeOfIndicators " + e.getMessage());
        }

        return auditList;
    }

    /**
     * добавляет запись в лист аудита
     *
     * @param audit - объект типа аудит
     */
    public boolean addAudit(Audit audit) {

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {

            ResultSet resultSet;
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_DATA_SQL)) {
                preparedStatement.setInt(1, audit.getUser().getId());
                preparedStatement.setDate(2, Date.valueOf(LocalDate.now()));
                preparedStatement.setString(3, audit.getAction().toString());
                resultSet = preparedStatement.executeQuery();

                //В данной реализации мне не нужен id из базы данных, но так как есть в задании я это напечатал
                if (resultSet.next()) {
                    int generatedId = resultSet.getInt("id");
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception addValueOfMeteringDevice: " + e.getMessage());
        }
        return false;
    }
}
