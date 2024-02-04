/**
 * Класс "список"
 * хранит в себе коллекцию обектов "аудит"
 * реализован как синглтон
 */

package com.prokopovich.repo;

import com.prokopovich.model.Audit;
import com.prokopovich.model.AuditAction;
import com.prokopovich.model.IndicatorType;
import com.prokopovich.model.User;
import com.prokopovich.service.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;


public class AuditRepo {

    public static final String URL = DatabaseConfig.getDatabaseUrlService();
    public static final String USER_NAME = DatabaseConfig.getDatabaseUsername();
    public static final String PASSWORD = DatabaseConfig.getDatabasePassword();

    private static AuditRepo instance;

    private AuditRepo() {

    }

    public static AuditRepo getInstance() {
        if (instance == null) {
            instance = new AuditRepo();
        }
        return instance;
    }

    /**
     * вывести все записи по аудиту
     * @return лист объектов аудит
     */
    public ArrayList<Audit> getAuditList() {
        ArrayList<Audit> auditList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            String retriveStudentsSQL = "SELECT * FROM audit";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(retriveStudentsSQL);
            while (resultSet.next()) {
                int userIdFromRepo = resultSet.getInt("user_id");
                LocalDate dateFromRepo = resultSet.getDate("date_of_notes").toLocalDate();
                AuditAction action = AuditAction.valueOf(resultSet.getString("audit_action"));

                User userFromRepo = UsersRepo.getInstance().findUserById(userIdFromRepo);

                auditList.add(new Audit(userFromRepo,dateFromRepo,action));

            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception getTypeOfIndicators " + e.getMessage());
        }

        return auditList;
    }

    /**
     * добавляет запись в лист аудита
     * @param audit - объект типа аудит
     */
    public boolean addAudit(Audit audit) {

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            String insertDataSql = "INSERT INTO audit (user_id, date_of_notes, audit_action) VALUES (?, ?, ?) RETURNING id";
            PreparedStatement preparedStatement = connection.prepareStatement(insertDataSql);
            preparedStatement.setInt(1, audit.getUser().getId());
            preparedStatement.setDate(2, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(3, audit.getAction().toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            //В данной реализации мне не нужен id из базы данных, но так как есть в задании я это напечатал
            if (resultSet.next()) {
                int generatedId = resultSet.getInt("id");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception addValueOfMeteringDevice: " + e.getMessage());
        }
        return false;
    }
}

