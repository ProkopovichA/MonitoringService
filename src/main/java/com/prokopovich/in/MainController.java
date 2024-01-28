/**
 * Главный контроллер обрабатывает консольный ввод верхнего уровня
 * из него вызывается контроллер авторизации и работы с показаниями
 */

package com.prokopovich.in;

import com.prokopovich.model.User;
import com.prokopovich.model.Users;
import com.prokopovich.model.Audit;
import com.prokopovich.service.AuditService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class MainController {
    /**
     * Запуск основной логики главного контроллера
     * @return true если работа завершена корректно
     */
    public static boolean Start() {
        AuditService auditService = AuditService.getInstance();


        Users usersList = Users.getInstance();
        Scanner scanner = new Scanner(System.in);
        boolean resume = true;

        System.out.println("Добро пожаловать в сервис подачи показаний.");
        User curUser = null;

        ArrayList<String> commands = new ArrayList<>();
        commands.add("Выход из приложения");
        commands.add("Выбрать/Сменить пользователя");
        commands.add("Работа с показаниями");


        while (resume) {
            System.out.println("Пожалуйста, введите номер команды: ");

            for (int i = 0; i < commands.size(); i++) {
                System.out.println(i + ". " + commands.get(i));
            }

            int command = scanner.nextInt();

            switch (command) {
                case 0:
                    System.out.println("Выход из приложения.");
                    if (curUser != null) {
                        auditService.addAudit(new Audit(curUser, LocalDate.now(),"Выход из приложения."));
                    }
                    resume = false;
                    break;
                case 1:
                    curUser = UserController.start();
                    break;
                case 2:
                    if (curUser == null) {
                        System.out.println("Сначала надо войти в систему.");
                        break;
                    }
                    ValueInputController.start(curUser);
                    break;
                default:
                    System.out.println("Неверная команда. Пожалуйста, введите корректную команду.");
                    break;
            }
        }

        return true;
    }
}
