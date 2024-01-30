package com.prokopovich.in;
/**
 * Контроллер авторизации отвечает за регистрацию
 * и авторизацию пользователей
 */

import com.prokopovich.model.AuditAction;
import com.prokopovich.model.User;
import com.prokopovich.model.UserRole;
import com.prokopovich.repo.Users;
import com.prokopovich.model.Audit;
import com.prokopovich.service.AuditService;
import com.prokopovich.service.IntTerminalScanner;
import com.prokopovich.service.OutputHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
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

            OutputHandler.sout("Пожалуйста, введите номер команды: ");

            for (int i = 0; i < commands.size(); i++) {
                OutputHandler.sout(i + ". " + commands.get(i));
            }

            int command = IntTerminalScanner.nextInt(scanner);

            switch (command) {
                case 0:
                    OutputHandler.sout("Выход из приложения.");
                    resume = false;
                    break;
                case 1:
                    curUser = singIn().orElse(null);
                    resume = false;
                    break;
                case 2:
                    curUser = singUp().orElse(null);
                    resume = false;
                    break;
                case 3:
                    for (String cmd : commands) {
                        OutputHandler.sout(cmd);
                    }
                    break;
                default:
                    OutputHandler.sout("Неверная команда. Пожалуйста, введите корректную команду.");
                    break;
            }

        }
        return curUser;
    }

    /**
     * Эндпоит авторизации пользователя
     * @return Объект класса Optional<User>, если авторизация прошла успешно, иначе Optional.empty()
     */
    public static Optional<User> singIn() {
        AuditService auditService = AuditService.getInstance();

        Scanner scanner = new Scanner(System.in);
        Users usersList = Users.getInstance();

        OutputHandler.sout("Введите имя пользователя:");
        String login = scanner.nextLine();

        if (login.length() < 4) {
            OutputHandler.sout("Логин должен быть не пустым и содержать минимум 4 символа.");
            return Optional.empty();
        }

        try {
            User user = usersList.findUserByLogin(login);
            OutputHandler.sout("Введите пароль:");
            String password = scanner.nextLine();
            if (user.checkLoginPassword(user.getLogin(), password)) {
                OutputHandler.sout("Вы успешно вошли в систему.");
                auditService.addAudit(new Audit(user, LocalDate.now(), AuditAction.SIGN_IN));
                return Optional.of(user);
            } else {
                OutputHandler.sout("Не верный пароль");
                return Optional.empty();
            }
        } catch (Exception e) {
            OutputHandler.sout(e.getMessage());
            return Optional.empty();
        }

    }

    /**
     * Эндпоит регистрации пользователя
     * @return Объект класса Optional<User>, если регистрация прошла успешно, иначе Optional.empty()
     */
    public static Optional<User> singUp() {
        AuditService auditService = AuditService.getInstance();

        Scanner scanner = new Scanner(System.in);
        Users usersList = Users.getInstance();

        OutputHandler.sout("Введите имя пользователя, не менее 4 символа:");
        String newLogin = scanner.nextLine();

        if (newLogin.length() < 4) {
            OutputHandler.sout("Логин должен быть не пустым и содержать минимум 4 символа.");
            return Optional.empty();
        }

        try {
            usersList.findUserByLogin(newLogin);
            OutputHandler.sout("Пользователь с таким именем уже существует");
            return Optional.empty();
        } catch (Exception e) {
            OutputHandler.sout("Введите пароль, не менее 4 символа:");
            String newPassword = scanner.nextLine();
            if (newPassword.length() < 4) {
                OutputHandler.sout("пароль должен быть не пустым и содержать минимум 4 символа.");
                return Optional.empty();
            }
            User user = new User(newLogin, newPassword, UserRole.USER);
            usersList.addUser(user);
            OutputHandler.sout("Пользователь успешно зарегистрирован");
            auditService.addAudit(new Audit(user, LocalDate.now(), AuditAction.SIGN_UP));
            return Optional.of(user);
        }
    }
}
