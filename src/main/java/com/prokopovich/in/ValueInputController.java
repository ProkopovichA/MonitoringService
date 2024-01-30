/**
 * Класс управления вводом/просмотром показаний счетчиков (ValueInputController).
 * Этот класс обеспечивает взаимодействие с пользователем и выполнение операций по добавлению и просмотру показаний.
 * Так же есть эндпоинт по добавлению новых видов показания под админом
 */

package com.prokopovich.in;

import com.prokopovich.model.*;
import com.prokopovich.model.Audit;
import com.prokopovich.repo.TypesOfIndicators;
import com.prokopovich.repo.ValuesOfMeteringDevices;
import com.prokopovich.service.AuditService;
import com.prokopovich.service.IntTerminalScanner;

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
        AuditService auditService = AuditService.getInstance();
        Scanner scanner = new Scanner(System.in);

        boolean resume = true;
        ValuesOfMeteringDevices listValuesOfMeteringDevices = ValuesOfMeteringDevices.getInstance();


        ArrayList<String> commands = new ArrayList<>();
        commands.add("Выход из приложения");
        commands.add("Добавить новые показания");
        commands.add("Просмотреть показания");
        commands.add("Просмотреть показания за месяц");
        if (user.getRole().isAdmin()) {
            commands.add("Добавить новый вид показаний");
        }

        while (resume) {
            System.out.println("Пожалуйста, введите номер команды: ");

            for (int i = 0; i < commands.size(); i++) {
                System.out.println(i + ". " + commands.get(i));
            }

            int command = IntTerminalScanner.nextInt(scanner);

            switch (command) {
                case 0:
                    System.out.println("Выход из приложения.");
                    resume = false;
                    break;
                case 1:
                    if (user.getRole().isAdmin()) {
                        System.out.println("Администратор не может добавлять показания.");
                        break;
                    }
                    addValueOfMeteringDevices(user);
                    break;
                case 2:
                    List<ValueOfMeteringDevices> meteringDevicesList = listValuesOfMeteringDevices.getValueOfMeteringDevices(user);
                    if (meteringDevicesList.size() == 0) {
                        System.out.println("По данным параметрам записей не обнаружено.");
                        break;
                    }
                    meteringDevicesList.forEach(System.out::println);
                    auditService.addAudit(new Audit(user, LocalDate.now(),AuditAction.INPUT_VALUE));
                    break;
                case 3:
                    System.out.println("Введите год");
                    int year = IntTerminalScanner.nextInt(scanner);

                    System.out.println("Введите месяц");
                    int month = IntTerminalScanner.nextInt(scanner);

                    List<ValueOfMeteringDevices> meteringDevicesListMonth = listValuesOfMeteringDevices.getValuesForMonthAndYear(user,year,month);
                    if (meteringDevicesListMonth.size() == 0) {
                        System.out.println("По данным параметрам записей не обнаружено.");
                        break;
                    }
                    meteringDevicesListMonth.forEach(System.out::println);
                    auditService.addAudit(new Audit(user, LocalDate.now(),AuditAction.MONTHLY_DISPLAY_VALUE));
                    break;
                case 4:
                    if (!user.getRole().isAdmin()) {
                        System.out.println("Неверная команда. Пожалуйста, введите корректную команду.");
                        break;
                    }
                    addNewTypeOfIndicators();
                    auditService.addAudit(new Audit(user, LocalDate.now(),AuditAction.ADD_NEW_INDICATOR_TYPE));
                    break;
                default:
                    System.out.println("Неверная команда. Пожалуйста, введите корректную команду.");
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
        AuditService auditService = AuditService.getInstance();
        Scanner scanner = new Scanner(System.in);

        TypesOfIndicators typesOfIndicators = TypesOfIndicators.getInstance();
        ArrayList<IndicatorType> listTypesOfIndicators = typesOfIndicators.getTypeOfIndicators();
        ValuesOfMeteringDevices listValuesOfMeteringDevices = ValuesOfMeteringDevices.getInstance();

        System.out.println("Пожалуйста, введите номер вида показаний: ");

        for (int i = 0; i < listTypesOfIndicators.size(); i++) {
            System.out.println(i + ". " + listTypesOfIndicators.get(i));
        }
        int curNumber = IntTerminalScanner.nextInt(scanner);

        if ((curNumber < 0) || (curNumber > listTypesOfIndicators.size() - 1)) {
            System.out.println("Не правильно указан номер.");
            return false;
        }

        IndicatorType curTypeOfIndicators = listTypesOfIndicators.get(curNumber);

        System.out.println("Текущие значения " + listValuesOfMeteringDevices.getLastValueForUserAndType(user, curTypeOfIndicators));

        System.out.println("Пожалуйста, введите новые значения показаний: ");
        int value = IntTerminalScanner.nextInt(scanner);

        if (value < 0) {
            System.out.println("Показания не могут быть меньше нуля.");
            return false;
        }

        auditService.addAudit(new Audit(user, LocalDate.now(),AuditAction.INPUT_VALUE));

        return listValuesOfMeteringDevices.addValueOfMeteringDevice(new ValueOfMeteringDevices(user, curTypeOfIndicators, LocalDate.now(), value));

    }

    /**
     * Эндпоит добавления типа показаний
     * @return true если показания успешно добавлены
     */
    public static boolean addNewTypeOfIndicators() {
        Scanner scanner = new Scanner(System.in);
        TypesOfIndicators typesOfIndicators = TypesOfIndicators.getInstance();
        ArrayList<IndicatorType> listTypesOfIndicators = typesOfIndicators.getTypeOfIndicators();
        System.out.println("Показания, которые уже есть: ");

        for (int i = 0; i < listTypesOfIndicators.size(); i++) {
            System.out.println(i + ". " + listTypesOfIndicators.get(i));
        }

        System.out.println("Пожалуйста, введите название нового вида показаний: ");

        String newName = scanner.nextLine();

        if (newName.isEmpty()) {
            System.out.println("Название не может быть пустыми");
            return false;
        } else {
            listTypesOfIndicators.add(new IndicatorType(newName));
            return true;
        }


    }


}
