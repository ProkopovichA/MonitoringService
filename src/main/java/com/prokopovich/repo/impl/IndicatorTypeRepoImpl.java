package com.prokopovich.repo.impl;

import com.prokopovich.model.IndicatorType;
import com.prokopovich.repo.IndicatorTypeRepo;
import com.prokopovich.service.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;

public class IndicatorTypeRepoImpl implements IndicatorTypeRepo{
    public static final String URL = DatabaseConfig.getDatabaseUrlModel();
    public static final String USER_NAME = DatabaseConfig.getDatabaseUsername();
    public static final String PASSWORD = DatabaseConfig.getDatabasePassword();
    public static final String GET_LIST_INDICATOR_TYPE_SQL = "SELECT id, name FROM indicator_type";
    public static final String FIND_BY_ID_SQL = "SELECT id, name FROM indicator_type WHERE id = ?";
    public static final String INSERT_DATA_SQL = "INSERT INTO indicator_type (name) VALUES (?) RETURNING id";
    private static IndicatorTypeRepoImpl instance;

    private IndicatorTypeRepoImpl() {

    }

    public static IndicatorTypeRepoImpl getInstance() {
        if (instance == null) {
            instance = new IndicatorTypeRepoImpl();
        }
        return instance;
    }

    public ArrayList<IndicatorType> getTypeOfIndicators() {
        ArrayList<IndicatorType> indicatorTypeArrayList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_LIST_INDICATOR_TYPE_SQL);
            while (resultSet.next()) {
                int idOfIndicatorType = resultSet.getInt("id");
                String nameOfIndicatorType = resultSet.getString("name");
                indicatorTypeArrayList.add(new IndicatorType(idOfIndicatorType, nameOfIndicatorType));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception getTypeOfIndicators " + e.getMessage());
        }
        return indicatorTypeArrayList;
    }

    public IndicatorType findIndicatorTypeById(int id) {

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            ResultSet resultSet;
            try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
                prepareStatement.setInt(1, id);
                resultSet = prepareStatement.executeQuery();

                if (resultSet.next()) {
                    return getIndicatorType(resultSet);
                } else {
                    throw new IllegalArgumentException("Пользователь с id " + id + " не найден");
                }
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception findUserByLogin " + e.getMessage());
        }

        return null;
    }

    public boolean addTypeOfIndicator(IndicatorType typeOfIndicator) {
        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {

            ResultSet resultSet;
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_DATA_SQL)) {
                preparedStatement.setString(1, typeOfIndicator.getTypeName());
                resultSet = preparedStatement.executeQuery();

                //В данной реализации мне не нужен id из базы данных, но так как есть в задании я это напечатал
                if (resultSet.next()) {
                    int generatedId = resultSet.getInt("id");
                    return true;
                }
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
