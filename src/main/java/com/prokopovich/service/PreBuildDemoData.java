/**
 * утилитный класс для первичного заполнения базы данными
 * что бы легче было проверять работу приложения
 * создает Админа и трёх пользователей,
 * а так же виды приборов учета
 */

package com.prokopovich.service;

import com.prokopovich.model.*;

public class PreBuildDemoData {
    public static void build() {

        Users usersList = Users.getInstance();
        TypesOfIndicators typesOfIndicators = TypesOfIndicators.getInstance();

        for (int i = 0; i < 4; i++) {
            String login;
            String password;
            UserRole userRole;
            if (i == 0) {
                login = "Admin";
                password = "AdmQwe";
                userRole = UserRole.ADMIN;
            } else {
                login = "User" + i;
                password = "Pass" + i;
                userRole = UserRole.USER;
            }
            usersList.addUser(new User(login, password, userRole));

        }

        typesOfIndicators.addTypeOfIndicator(new TypeOfIndicators("Электроэнергия"));
        typesOfIndicators.addTypeOfIndicator(new TypeOfIndicators("Горячая вода"));
        typesOfIndicators.addTypeOfIndicator(new TypeOfIndicators("Холодная вода"));

    }
}
