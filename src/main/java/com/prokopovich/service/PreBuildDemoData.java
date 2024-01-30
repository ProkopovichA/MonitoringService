/**
 * утилитный класс для первичного заполнения базы данными
 * что бы легче было проверять работу приложения
 * создает Админа и трёх пользователей,
 * а так же виды приборов учета
 */

package com.prokopovich.service;

import com.prokopovich.model.*;
import com.prokopovich.repo.TypesOfIndicators;
import com.prokopovich.repo.Users;

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

        typesOfIndicators.addTypeOfIndicator(new IndicatorType("Электроэнергия"));
        typesOfIndicators.addTypeOfIndicator(new IndicatorType("Горячая вода"));
        typesOfIndicators.addTypeOfIndicator(new IndicatorType("Холодная вода"));

    }
}
