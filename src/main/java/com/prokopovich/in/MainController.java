/**
 * Главный контроллер обрабатывает консольный ввод верхнего уровня
 * из него вызывается контроллер авторизации и работы с показаниями
 */

package com.prokopovich.in;

import com.prokopovich.model.AuditAction;
import com.prokopovich.model.User;
import com.prokopovich.repo.Users;
import com.prokopovich.model.Audit;
import com.prokopovich.service.AuditService;
import com.prokopovich.service.IntTerminalScanner;
import com.prokopovich.service.OutputHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class MainController {
    /**
     * Запуск основной логики главного контроллера
     * @return true если работа завершена корректно
     */
    public static boolean start() {
        AuditService auditService = AuditService.getInstance();
        Scanner scanner = new Scanner(System.in);

        boolean resume = true;

        OutputHandler.sout("Добро пожаловать в сервис подачи показаний.");
        User curUser = null;

        ArrayList<String> commands = new ArrayList<>();
        commands.add("Выход из приложения");
        commands.add("Выбрать/Сменить пользователя");
        commands.add("Работа с показаниями");


        while (resume) {
            OutputHandler.sout("Пожалуйста, введите номер команды: ");

            for (int i = 0; i < commands.size(); i++) {
                OutputHandler.sout(i + ". " + commands.get(i));
            }

            int command = IntTerminalScanner.nextInt(scanner);

            switch (command) {
                case 0:
                    OutputHandler.sout("Выход из приложения.");
                    if (curUser != null) {
                        auditService.addAudit(new Audit(curUser, LocalDate.now(), AuditAction.SIGN_OUT));
                    }
                    resume = false;
                    break;
                case 1:
                    curUser = UserController.start();
                    break;
                case 2:
                    if (curUser == null) {
                        OutputHandler.sout("Сначала надо войти в систему.");
                        break;
                    }
                    ValueInputController.start(curUser);
                    break;
                default:
                    OutputHandler.sout("Неверная команда. Пожалуйста, введите корректную команду.");
                    break;
            }
        }

        return true;
    }
}
