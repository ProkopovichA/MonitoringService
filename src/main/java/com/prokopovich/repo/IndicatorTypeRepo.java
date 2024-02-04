/**
 * Класс "список" типов приборов учета
 * хранит в себе коллекцию обектов "типы показаний"
 * реализован как синглтон
 */

package com.prokopovich.repo;

import com.prokopovich.model.IndicatorType;
import com.prokopovich.model.User;
import com.prokopovich.service.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;

public class IndicatorTypeRepo {

    public static final String URL = DatabaseConfig.getDatabaseUrlModel();
    public static final String USER_NAME = DatabaseConfig.getDatabaseUsername();
    public static final String PASSWORD = DatabaseConfig.getDatabasePassword();
    private static IndicatorTypeRepo instance;

    private IndicatorTypeRepo() {

    }

    public static IndicatorTypeRepo getInstance() {
        if (instance == null) {
            instance = new IndicatorTypeRepo();
        }
        return instance;
    }

    public ArrayList<IndicatorType> getTypeOfIndicators() {
        ArrayList<IndicatorType> indicatorTypeArrayList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            String retriveStudentsSQL = "SELECT * FROM indicator_type";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(retriveStudentsSQL);
            while (resultSet.next()) {
                int idOfIndicatorType = resultSet.getInt("id");
                String nameOfIndicatorType = resultSet.getString("name");
                indicatorTypeArrayList.add(new IndicatorType(idOfIndicatorType,nameOfIndicatorType));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception getTypeOfIndicators " + e.getMessage());
        }
        return indicatorTypeArrayList;
    }

    public IndicatorType findIndicatorTypeById(int id) {

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            String findById = "SELECT * FROM indicator_type WHERE id = ?";
            PreparedStatement prepareStatement = connection.prepareStatement(findById);
            prepareStatement.setInt(1, id);
            ResultSet resultSet = prepareStatement.executeQuery();
            if (resultSet.next()) {
                return getIndicatorType(resultSet);
            } else {
                throw new IllegalArgumentException("Пользователь с id " + id + " не найден");
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception findUserByLogin " + e.getMessage());
        }

        return null;
    }

    public boolean addTypeOfIndicator(IndicatorType typeOfIndicator) {
        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {

            String insertDataSql = "INSERT INTO indicator_type (name) VALUES (?) RETURNING id";
            PreparedStatement preparedStatement = connection.prepareStatement(insertDataSql);
            preparedStatement.setString(1, typeOfIndicator.getTypeName());
            ResultSet resultSet = preparedStatement.executeQuery();

            //В данной реализации мне не нужен id из базы данных, но так как есть в задании я это напечатал
            if (resultSet.next()) {
                int generatedId = resultSet.getInt("id");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception addTypeOfIndicator: " + e.getMessage());
        }
        return false;
    }

    private IndicatorType getIndicatorType(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        return new IndicatorType(id, name);
    }

}