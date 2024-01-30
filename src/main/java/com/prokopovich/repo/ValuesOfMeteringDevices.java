/**
 * Класс "список"
 * хранит в себе коллекцию обектов "показание приборов учета"
 * реализован как синглтон
 */
package com.prokopovich.repo;

import com.prokopovich.model.IndicatorType;
import com.prokopovich.model.User;
import com.prokopovich.model.UserRole;
import com.prokopovich.model.ValueOfMeteringDevices;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class ValuesOfMeteringDevices {
    private static ValuesOfMeteringDevices instance;
    private ArrayList<ValueOfMeteringDevices> listValueOfMeteringDevices;

    private ValuesOfMeteringDevices() {
        this.listValueOfMeteringDevices = new ArrayList<>();
    }

    public static ValuesOfMeteringDevices getInstance() {
        if (instance == null) {
            instance = new ValuesOfMeteringDevices();
        }
        return instance;
    }

    /**
     * Эндпонт просмотра показаний, возвращает список "показаний" пользователя
     * если пользователь админ, тогда по всем пользователям
     * @param user - объект класа User
     * @return лист объектов "показание приборов учета"
     */
    public List<ValueOfMeteringDevices> getValueOfMeteringDevices(User user) {
        if (user.getRole().isAdmin()) {
            return new ArrayList<>(listValueOfMeteringDevices);
        } else if (!user.getRole().isAdmin()) {
            return listValueOfMeteringDevices.stream()
                    .filter(value -> value.getUser().equals(user))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    /**
     * Эндпоинт подачи показаний
     * предусмотрен запрет давать показания одинакового типа несколько раз в месяц
     * @param valueOfMeteringDevice
     * @return true - в случае успеха
     */
    public boolean addValueOfMeteringDevice(ValueOfMeteringDevices valueOfMeteringDevice) {
        LocalDate currentDate = LocalDate.now();

        boolean alreadyAdded = listValueOfMeteringDevices.stream()
                .anyMatch(value -> value.getUser().equals(valueOfMeteringDevice.getUser())
                        && value.getTypeOfIndicators().equals(valueOfMeteringDevice.getTypeOfIndicators())
                        && value.getDateOfValue().getMonthValue() == currentDate.getMonthValue());

        if (alreadyAdded) {
            System.out.println("Для данного пользователя и данного вида показаний в текущем месяце данные уже были добавлены.");
            return false;
        } else {
            listValueOfMeteringDevices.add(valueOfMeteringDevice);
            System.out.println("Данные успешно добавлены.");
            return true;
        }
    }


    /**
     * Эндпонт просмотра показаний с фильтром по номеру года и месяца
     * возвращает список "показаний" пользователя,
     * если пользователь админ, тогда по всем пользователям
     * @param user
     * @param year
     * @param month
     * @return  лист объектов "показание приборов учета"
     */
    public List<ValueOfMeteringDevices> getValuesForMonthAndYear(User user, int year, int month) {
        return listValueOfMeteringDevices.stream()
                .filter(value -> {
                    LocalDate date = value.getDateOfValue();
                    return date.getYear() == year && date.getMonthValue() == month &&
                            (user.getRole() == UserRole.ADMIN || value.getUser().equals(user));
                })
                .collect(Collectors.toList());
    }

    /**
     * Эндпоинт для получения актуальных показаний счетчика
     * @param user
     * @param type
     * @return целое число, актуальное значение, если не найдено то 0
     */
    public int getLastValueForUserAndType(User user, IndicatorType type) {
        List<ValueOfMeteringDevices> userValues = listValueOfMeteringDevices.stream()
                .filter(value -> value.getUser().equals(user) && value.getTypeOfIndicators().equals(type))
                .sorted(Comparator.comparing(ValueOfMeteringDevices::getDateOfValue).reversed())
                .collect(Collectors.toList());

        return userValues.isEmpty() ? 0 : userValues.get(0).getValue();
    }



}