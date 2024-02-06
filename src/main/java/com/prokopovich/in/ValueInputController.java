/**
 * Класс управления вводом/просмотром показаний счетчиков (ValueInputController).
 * Этот класс обеспечивает взаимодействие с пользователем и выполнение операций по добавлению и просмотру показаний.
 * Так же есть эндпоинт по добавлению новых видов показания под админом
 */

package com.prokopovich.in;

import com.prokopovich.model.*;
import com.prokopovich.model.Audit;
import com.prokopovich.repo.impl.AuditRepoImpl;
import com.prokopovich.repo.impl.IndicatorTypeRepoImpl;
import com.prokopovich.repo.IndicatorTypeRepo;
import com.prokopovich.repo.ValuesOfMeteringDevicesRepo;
import com.prokopovich.repo.AuditRepo;
import com.prokopovich.repo.impl.ValuesOfMeteringDevicesRepoImpl;
import com.prokopovich.service.IntTerminalScanner;
import com.prokopovich.service.OutputHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ValueInputController {
    /**
     * Запуск основной логики контроллера работы с показаниями приборов учета
     * @return true если работа завершена корректно
     */
    public static boolean start(User user) {
        AuditRepo auditRepo = AuditRepoImpl.getInstance();
        Scanner scanner = new Scanner(System.in);

        boolean resume = true;
        ValuesOfMeteringDevicesRepo listValuesOfMeteringDevices = ValuesOfMeteringDevicesRepoImpl.getInstance();


        ArrayList<String> commands = new ArrayList<>();
        commands.add("Выход из приложения");
        commands.add("Добавить новые показания");
        commands.add("Просмотреть показания");
        commands.add("Просмотреть показания за месяц");
        if (user.getRole().isAdmin()) {
            commands.add("Добавить новый вид показаний");
        }

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
                    if (user.getRole().isAdmin()) {
                        OutputHandler.sout("Администратор не может добавлять показания.");
                        break;
                    }
                    addValueOfMeteringDevices(user);
                    break;
                case 2:
                    List<ValueOfMeteringDevices> meteringDevicesList = listValuesOfMeteringDevices.getValueOfMeteringDevices(user);
                    if (meteringDevicesList.size() == 0) {
                        OutputHandler.sout("По данным параметрам записей не обнаружено.");
                        break;
                    }
                    meteringDevicesList.forEach(System.out::println);
                    auditRepo.addAudit(new Audit(user, LocalDate.now(),AuditAction.INPUT_VALUE));
                    break;
                case 3:
                    OutputHandler.sout("Введите год");
                    int year = IntTerminalScanner.nextInt(scanner);

                    OutputHandler.sout("Введите месяц");
                    int month = IntTerminalScanner.nextInt(scanner);

                    List<ValueOfMeteringDevices> meteringDevicesListMonth = listValuesOfMeteringDevices.getValuesForMonthAndYear(user,year,month);
                    if (meteringDevicesListMonth.size() == 0) {
                        OutputHandler.sout("По данным параметрам записей не обнаружено.");
                        break;
                    }
                    meteringDevicesListMonth.forEach(System.out::println);
                    auditRepo.addAudit(new Audit(user, LocalDate.now(),AuditAction.MONTHLY_DISPLAY_VALUE));
                    break;
                case 4:
                    if (!user.getRole().isAdmin()) {
                        OutputHandler.sout("Неверная команда. Пожалуйста, введите корректную команду.");
                        break;
                    }
                    addNewTypeOfIndicators();
                    auditRepo.addAudit(new Audit(user, LocalDate.now(),AuditAction.ADD_NEW_INDICATOR_TYPE));
                    break;
                default:
                    OutputHandler.sout("Неверная команда. Пожалуйста, введите корректную команду.");
                    break;
            }
        }
        return true;
    }

    /**
     * Эндпоит добавления показаний
     * @return true если показания успешно добавлены
     */
    public static boolean addValueOfMeteringDevices(User user) {
        AuditRepo auditRepo = AuditRepoImpl.getInstance();
        Scanner scanner = new Scanner(System.in);

        IndicatorTypeRepo typesOfIndicators = IndicatorTypeRepoImpl.getInstance();
        ArrayList<IndicatorType> listTypesOfIndicators = typesOfIndicators.getTypeOfIndicators();
        ValuesOfMeteringDevicesRepo listValuesOfMeteringDevices = ValuesOfMeteringDevicesRepoImpl.getInstance();

        OutputHandler.sout("Пожалуйста, введите номер вида показаний: ");

        for (int i = 0; i < listTypesOfIndicators.size(); i++) {
            OutputHandler.sout(i + ". " + listTypesOfIndicators.get(i));
        }
        int curNumber = IntTerminalScanner.nextInt(scanner);

        if ((curNumber < 0) || (curNumber > listTypesOfIndicators.size() - 1)) {
            OutputHandler.sout("Не правильно указан номер.");
            return false;
        }

        IndicatorType curTypeOfIndicators = listTypesOfIndicators.get(curNumber);

        OutputHandler.sout("Текущие значения " + listValuesOfMeteringDevices.getLastValueForUserAndType(user, curTypeOfIndicators));

        OutputHandler.sout("Пожалуйста, введите новые значения показаний: ");
        int value = IntTerminalScanner.nextInt(scanner);

        if (value < 0) {
            OutputHandler.sout("Показания не могут быть меньше нуля.");
            return false;
        }

        auditRepo.addAudit(new Audit(user, LocalDate.now(),AuditAction.INPUT_VALUE));

        return listValuesOfMeteringDevices.addValueOfMeteringDevice(new ValueOfMeteringDevices(user, curTypeOfIndicators, LocalDate.now(), value));

    }

    /**
     * Эндпоит добавления типа показаний
     * @return true если показания успешно добавлены
     */
    public static boolean addNewTypeOfIndicators() {
        Scanner scanner = new Scanner(System.in);
        IndicatorTypeRepo typesOfIndicators = IndicatorTypeRepoImpl.getInstance();
        ArrayList<IndicatorType> listTypesOfIndicators = typesOfIndicators.getTypeOfIndicators();
        OutputHandler.sout("Показания, которые уже есть: ");

        for (int i = 0; i < listTypesOfIndicators.size(); i++) {
            OutputHandler.sout(i + ". " + listTypesOfIndicators.get(i));
        }

        OutputHandler.sout("Пожалуйста, введите название нового вида показаний: ");

        String newName = scanner.nextLine();

        if (newName.isEmpty()) {
            OutputHandler.sout("Название не может быть пустыми");
            return false;
        } else {
            typesOfIndicators.addTypeOfIndicator(new IndicatorType(777, newName));
            return true;
        }


    }


}
