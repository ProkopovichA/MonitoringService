package com.prokopovich.repo.impl;

import com.prokopovich.model.User;
import com.prokopovich.model.UserRole;
import com.prokopovich.repo.UsersRepo;
import com.prokopovich.service.DatabaseConfig;

import java.sql.*;

public class UserRepoImpl implements UsersRepo{
    public static final String URL = DatabaseConfig.getDatabaseUrlModel();
    public static final String USER_NAME = DatabaseConfig.getDatabaseUsername();
    public static final String PASSWORD = DatabaseConfig.getDatabasePassword();
    public static final String INSERT_DATA_SQL = "INSERT INTO users (login, password, role) VALUES (?, ?, ?) RETURNING id";
    public static final String FIND_USER_BY_LOGIN = "SELECT id, login, password, role FROM users WHERE login = ?";
    public static final String FIND_USER_BY_ID = "SELECT id, login, password, role FROM users WHERE id = ?";
    private static UserRepoImpl instance;

    private UserRepoImpl() {
    }

    public static UserRepoImpl getInstance() {
        if (instance == null) {
            instance = new UserRepoImpl();
        }
        return instance;
    }

    /**
     * Создание пользователя
     *
     * @param user - объект класса User
     */
    public boolean addUser(User user) {
        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            ResultSet resultSet;
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_DATA_SQL)) {
                preparedStatement.setString(1, user.getLogin());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, user.getRole().toString());
                resultSet = preparedStatement.executeQuery();

                //В данной реализации мне не нужен id из базы данных, но так как есть в задании я это напечатал
                if (resultSet.next()) {
                    int generatedId = resultSet.getInt("id");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception addUser: " + e.getMessage());
        }
        return false;
    }

    /**
     * метод поиск пользователя по логину
     *
     * @param login - логин
     * @return объект класса User если пользователь с таким логином найден иначе IllegalArgumentException
     */

    public User findUserByLogin(String login) {

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {

            ResultSet resultSet;
            try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_USER_BY_LOGIN)) {
                prepareStatement.setString(1, login);
                resultSet = prepareStatement.executeQuery();
                if (resultSet.next()) {
                    return getUser(resultSet);
                } else {
                    throw new IllegalArgumentException("Пользователь с логином " + login + " не найден");
                }
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception findUserByLogin " + e.getMessage());
        }

        return null;
    }

    public User findUserById(int id) {

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            ResultSet resultSet;
            try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_USER_BY_ID)) {
                prepareStatement.setInt(1, id);
                resultSet = prepareStatement.executeQuery();
                if (resultSet.next()) {
                    return getUser(resultSet);
                } else {
                    throw new IllegalArgumentException("Пользователь с id " + id + " не найден");
                }
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception findUserByLogin " + e.getMessage());
        }

        return null;
    }

    private User getUser(ResultSet resultSet) throws SQLException {
        int userId = resultSet.getInt("id");
        String userLogin = resultSet.getString("login");
        String userPassword = resultSet.getString("password");
        UserRole userRole = UserRole.valueOf(resultSet.getString("role"));
        User user = new User(userId, userLogin, userPassword, userRole);
        return user;
    }
}
