/**
 * Класс "список"
 * хранит в себе коллекцию обектов "показание приборов учета"
 * реализован как синглтон
 */
package com.prokopovich.repo;

import com.prokopovich.model.IndicatorType;
import com.prokopovich.model.User;
import com.prokopovich.model.ValueOfMeteringDevices;
import com.prokopovich.repo.impl.IndicatorTypeRepoImpl;
import com.prokopovich.repo.impl.UserRepoImpl;
import com.prokopovich.service.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public interface ValuesOfMeteringDevicesRepo {
    List<ValueOfMeteringDevices> getValueOfMeteringDevices(User user);

    List<ValueOfMeteringDevices> getValueOfMeteringDevicesAll();

    boolean addValueOfMeteringDevice(ValueOfMeteringDevices valueOfMeteringDevice);

    boolean hasMeteringDevicesForMonth(User user, IndicatorType indicatorType);

    List<ValueOfMeteringDevices> getValuesForMonthAndYear(User user, int month, int year);

    int getLastValueForUserAndType(User user, IndicatorType indicatorType);

}