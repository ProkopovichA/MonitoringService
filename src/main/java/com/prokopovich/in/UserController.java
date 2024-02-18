package com.prokopovich.in;
/**
 * Контроллер авторизации отвечает за регистрацию
 * и авторизацию пользователей
 */

import com.prokopovich.model.AuditAction;
import com.prokopovich.model.User;
import com.prokopovich.model.UserRole;
import com.prokopovich.repo.impl.AuditRepoImpl;
import com.prokopovich.repo.UsersRepo;
import com.prokopovich.model.Audit;
import com.prokopovich.repo.AuditRepo;
import com.prokopovich.repo.impl.UserRepoImpl;
import com.prokopovich.service.IntTerminalScanner;
import com.prokopovich.service.OutputHandler;
import com.prokopovich.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class UserController {
    /**
     * Запуск основной логики главного контроллера
     *
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
                    singUp();
                    resume = true;
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
     *
     * @return Объект класса Optional<User>, если авторизация прошла успешно, иначе Optional.empty()
     */
    public static Optional<User> singIn() {
        AuditRepo auditRepo = AuditRepoImpl.getInstance();

        Scanner scanner = new Scanner(System.in);
        UsersRepo usersList = UserRepoImpl.getInstance();

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
            return UserService.singIn(user,password);
        } catch (Exception e) {
            OutputHandler.sout(e.getMessage());
            return Optional.empty();
        }

    }

    /**
     * Эндпоит регистрации пользователя
     *
     * @return Объект класса Optional<User>, если регистрация прошла успешно, иначе Optional.empty()
     */
    public static Optional<User> singUp() {
        Scanner scanner = new Scanner(System.in);
        UsersRepo usersList = UserRepoImpl.getInstance();

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

            return UserService.singUp(newLogin,newPassword);

        }
    }
}
