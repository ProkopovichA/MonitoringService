/**
 * Класс "список" пользователей
 * хранит в себе коллекцию обектов "User"
 * реализован как синглтон
 */

package com.prokopovich.model;

import java.util.ArrayList;
import java.util.Scanner;

public class Users {
    private static Users instance;
    private ArrayList<User> users;

    private Users() {
        this.users = new ArrayList<>();
    }

    public static Users getInstance() {
        if (instance == null) {
            instance = new Users();
        }
        return instance;
    }


    public void addUser(User user) {
        users.add(user);
    }

    /**
     * метод поиск пользователя по логину
     * @param login - логин
     * @return объект класса User если пользователь с таким логином найден иначе null
     */
    public User findUserByLogin(String login) {
        for (User user : users) {
            if (user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Вывод списка всех пользователей
     */
    public void displayUserLogins() {
        System.out.println("Логины всех пользователей:");

        for (User user : users) {
            System.out.println(user.getLogin());
        }
    }

    /**
     * Удаление пользователя из списка активный пользователей
     * с проверкой пароля пользователя перед удалением
     * @param login - логин пользоватлея
     * @return true- в случае успеха
     */
    public boolean removeUser(String login) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите пароль для подтверждения удаления пользователя:");
        String enteredPassword = scanner.nextLine();

        User userToRemove = findUserByLogin(login);

        if (userToRemove != null && userToRemove.checkLoginPassword(login, enteredPassword)) {
            users.remove(userToRemove);
            System.out.println("Пользователь успешно удален.");
            return true;
        } else {
            System.out.println("Неверный пароль. Пользователь не удален.");
            return false;
        }
    }
}
