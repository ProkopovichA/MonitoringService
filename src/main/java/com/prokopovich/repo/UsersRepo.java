/**
 * Класс "список" пользователей
 * хранит в себе коллекцию обектов "User"
 * реализован как синглтон
 */

package com.prokopovich.repo;

import com.prokopovich.model.User;
import com.prokopovich.model.UserRole;
import com.prokopovich.service.DatabaseConfig;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UsersRepo {

    public static final String URL = DatabaseConfig.getDatabaseUrlModel();
    public static final String USER_NAME = DatabaseConfig.getDatabaseUsername();
    public static final String PASSWORD = DatabaseConfig.getDatabasePassword();

    private static UsersRepo instance;

    //private Map<String, User> users;

    private UsersRepo() {
        //   this.users = new HashMap<>();
    }

    public static UsersRepo getInstance() {
        if (instance == null) {
            instance = new UsersRepo();
        }
        return instance;
    }

    /**
     * Создание пользователя
     * @param user - объект класса User
     */
    public boolean addUser(User user) {
        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            String insertDataSql = "INSERT INTO users (login, password, role) VALUES (?, ?, ?) RETURNING id";
            PreparedStatement preparedStatement = connection.prepareStatement(insertDataSql);
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getRole().toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            //В данной реализации мне не нужен id из базы данных, но так как есть в задании я это напечатал
            if (resultSet.next()) {
                int generatedId = resultSet.getInt("id");
                return true;
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
            String findUserByLogin = "SELECT * FROM users WHERE login = ?";
            PreparedStatement prepareStatement = connection.prepareStatement(findUserByLogin);
            prepareStatement.setString(1, login);
            ResultSet resultSet = prepareStatement.executeQuery();
            if (resultSet.next()) {
                return getUser(resultSet);
            } else {
                throw new IllegalArgumentException("Пользователь с логином " + login + " не найден");
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception findUserByLogin " + e.getMessage());
        }

        return null;
    }

    public User findUserById(int id) {

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            String findUserByLogin = "SELECT * FROM users WHERE id = ?";
            PreparedStatement prepareStatement = connection.prepareStatement(findUserByLogin);
            prepareStatement.setInt(1, id);
            ResultSet resultSet = prepareStatement.executeQuery();
            if (resultSet.next()) {
                return getUser(resultSet);
            } else {
                throw new IllegalArgumentException("Пользователь с id " + id + " не найден");
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
