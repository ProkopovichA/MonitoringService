package com.prokopovich.service;

import java.util.Scanner;

/**
 * Сервисный класс для ввода номера команды в консоль
 * если значение введено корректно то возвращается оно,
 * если нет то -1
 */
public class IntTerminalScanner {
    /**
     * Сервисный метод для ввода номера команды в консоль
     * @return int если значение команды введено корректно то возвращается оно, если нет то -1
     */
    public static int nextInt(Scanner scanner) {
        try {
            int command = Integer.parseInt(scanner.nextLine());
            return command;
        } catch (Exception e) {
            return -1;
        }
    }
}
