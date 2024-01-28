package com.prokopovich.in;
/**
 * Контроллер авторизации отвечает за регистрацию
 * и авторизацию пользователей
 */

import com.prokopovich.model.User;
import com.prokopovich.model.UserRole;
import com.prokopovich.model.Users;
import com.prokopovich.model.Audit;
import com.prokopovich.service.AuditService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class UserController {
    /**
     * Запуск основной логики главного контроллера
     * @return Объект класса User если авторизация или регистрация прошла успешно, иначе null
     */
    public static User start() {

        Scanner scanner = new Scanner(System.in);
        boolean resume = true;
        User curUser = null;

        ArrayList<String> commands = new ArrayList<>();
        commands.add("Выход из приложения");
        commands.add("Войти в систему (пользователь уже есть)");
        commands.add("Зарегистрироваться (пользователя пока нету)");
        commands.add("Показать список команд");

        while (resume) {
            System.out.println("Пожалуйста, введите номер команды: ");

            for (int i = 0; i < commands.size(); i++) {
                System.out.println(i + ". " + commands.get(i));
            }

            int command = scanner.nextInt();

            switch (command) {
                case 0:
                    System.out.println("Выход из приложения.");
                    resume = false;
                    break;
                case 1:
                    curUser = singIn();
                    resume = false;
                    break;
                case 2:
                    curUser = singUp();
                    resume = false;
                    break;
                case 3:
                    for (String cmd : commands) {
                        System.out.println(cmd);
                    }
                    break;
                default:
                    System.out.println("Неверная команда. Пожалуйста, введите корректную команду.");
                    break;
            }
        }
        return curUser;
    }

    /**
     * Эндпоит авторизации пользователя
     * @return Объект класса User, если авторизация прошла успешно, иначе null
     */
    public static User singIn() {
        AuditService auditService = AuditService.getInstance();

        Scanner scanner = new Scanner(System.in);
        Users usersList = Users.getInstance();

        System.out.println("Введите имя пользователя:");
        String login = scanner.nextLine();

        User user = usersList.findUserByLogin(login);
        if (user == null) {
            System.out.println("Не правильное имя пользователя:");
            return null;
        } else {
            System.out.println("Введите пароль:");
            String password = scanner.nextLine();
            if (user.checkPassword(password)) {
                System.out.println("Вы успешно вошли в систему.");
                auditService.addAudit(new Audit(user, LocalDate.now(),"Вход в систему"));
                return user;
            } else {
                System.out.println("Не верный пароль");
                return null;
            }
        }
    }

    /**
     * Эндпоит регистрации пользователя
     * @return Объект класса User, если регистрация прошла успешно, иначе null
     */
    public static User singUp() {
        AuditService auditService = AuditService.getInstance();

        Scanner scanner = new Scanner(System.in);
        Users usersList = Users.getInstance();

        System.out.println("Введите имя пользователя:");
        String newLogin = scanner.nextLine();

        User newUser = usersList.findUserByLogin(newLogin);
        if (newUser == null) {
            System.out.println("Введите пароль:");
            String newPassword = scanner.nextLine();
            User user = new User(newLogin, newPassword, UserRole.USER);
            usersList.addUser(user);
            System.out.println("Пользователь успешно зарегистрирован");
            auditService.addAudit(new Audit(user, LocalDate.now(),"Пользователь зарегистрирован"));
            return user;
        } else {
            System.out.println("Пользователь с таким именем уже существует");
            return null;
        }
    }
}
