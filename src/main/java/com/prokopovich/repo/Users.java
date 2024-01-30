/**
 * Класс "список" пользователей
 * хранит в себе коллекцию обектов "User"
 * реализован как синглтон
 */

package com.prokopovich.repo;

import com.prokopovich.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Users {
    private static Users instance;
    private Map<String, User> users;

    private Users() {

        this.users = new HashMap<>();
    }

    public static Users getInstance() {
        if (instance == null) {
            instance = new Users();
        }
        return instance;
    }


    public void addUser(User user) {
        users.put(user.getLogin(),user);
    }

    /**
     * метод поиск пользователя по логину
     * @param login - логин
     * @return объект класса User если пользователь с таким логином найден иначе IllegalArgumentException
     */

    public User findUserByLogin(String login) {
        User user = users.get(login);

        if (user == null) {
            throw new IllegalArgumentException("Пользователь с логином " + login + " не найден");
        }

        return user;

    }


}
