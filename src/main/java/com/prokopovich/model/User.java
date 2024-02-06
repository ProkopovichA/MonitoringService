/**
 * Класс пользователь, описывает пользователя.
 * Содержит логин, пароль и права доступа
 */


package com.prokopovich.model;

import lombok.Getter;


public class User {
    @Getter
    private int id;
    @Getter
    private String login;
    @Getter
    private String password;
    @Getter
    private UserRole role;

    //Я решил то в данной версии приложения
    //не хочу учитывать пробелы в login и password

    public User(int id, String login, String password, UserRole role) {
        this.id = id;
        this.login = login.trim();
        this.password = password.trim();
        this.role = role;
    }

    /**
     * Эндпоинт проверки связки пользователь - пароль,
     * позволяет не светить пароль вне класса
     *
     * @param inLogin    - логин пользователя
     * @param inPassword - пароль пользователя
     * @return true - в случае успешной проверки
     */

    public boolean checkLoginPassword(String inLogin, String inPassword) {
        inLogin = inLogin.trim();
        inPassword = inPassword.trim();

        boolean loginMatch = this.login.equals(inLogin);
        boolean passwordMatch = this.password.equals(inPassword);

        return loginMatch && passwordMatch;
    }

    @Override
    public String toString() {
        return login;
    }
}
