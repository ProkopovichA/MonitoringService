package com.prokopovich.repo.impl;

import com.prokopovich.model.IndicatorType;
import com.prokopovich.model.User;
import com.prokopovich.model.ValueOfMeteringDevices;
import com.prokopovich.repo.ValuesOfMeteringDevicesRepo;
import com.prokopovich.service.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ValuesOfMeteringDevicesRepoImpl implements ValuesOfMeteringDevicesRepo {
    public static final String URL = DatabaseConfig.getDatabaseUrlModel();
    public static final String USER_NAME = DatabaseConfig.getDatabaseUsername();
    public static final String PASSWORD = DatabaseConfig.getDatabasePassword();
    public static final String FIND_RECORDS_BY_USER_ID = "" +
            "SELECT user_id, indicator_type_id, date_of_value, value " +
            "FROM value_of_metering_devices " +
            "WHERE user_id = ?";
    public static final String GET_ALL_RECORDS = "" +
            "SELECT user_id, indicator_type_id, value, date_of_value  " +
            "FROM value_of_metering_devices ";
    public static final String INSERT_DATA_SQL = "" +
            "INSERT INTO value_of_metering_devices (user_id, indicator_type_id, date_of_value, value) VALUES (?, ?, ?, ?) RETURNING id";
    public static final String FIND_RECORDS_BY_MONTH_YEAR = "" +
            "SELECT COUNT(value) AS count " +
            "FROM value_of_metering_devices " +
            "WHERE user_id = ? " +
            "AND indicator_type_id = ? " +
            "AND EXTRACT(MONTH FROM date_of_value) = ? " +
            "AND EXTRACT(YEAR FROM date_of_value) = ?";
    public static final String FIND_RECORDS_BY_MONTH_YEAR_USER = "" +
            "SELECT user_id, indicator_type_id, value, date_of_value " +
            "FROM value_of_metering_devices " +
            "WHERE user_id = ? " +
            "AND EXTRACT(MONTH FROM date_of_value) = ? " +
            "AND EXTRACT(YEAR FROM date_of_value) = ?";

    public static final String FIND_LAST_RECORD_BY_USER_TYPE_MONTH = "" +
            "SELECT value FROM value_of_metering_devices " +
            "WHERE user_id = ? AND indicator_type_id = ? " +
            "ORDER BY date_of_value DESC " +
            "LIMIT 1";
    private static ValuesOfMeteringDevicesRepoImpl instance;

    private ValuesOfMeteringDevicesRepoImpl() {

    }

    public static ValuesOfMeteringDevicesRepoImpl getInstance() {
        if (instance == null) {
            instance = new ValuesOfMeteringDevicesRepoImpl();
        }
        return instance;
    }

    /**
     * Эндпонт просмотра показаний, возвращает список "показаний" пользователя
     * если пользователь админ, тогда по всем пользователям
     *
     * @param user - объект класа User
     * @return лист объектов "показание приборов учета"
     */
    public List<ValueOfMeteringDevices> getValueOfMeteringDevices(User user) {
        int userId = user.getId();
        List<ValueOfMeteringDevices> meteringDevicesList = new ArrayList<>();

        if (user.getRole().isAdmin()) {
            return getValueOfMeteringDevicesAll();
        }

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            ResultSet resultSet;
            try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_RECORDS_BY_USER_ID)) {
                prepareStatement.setInt(1, userId);
                resultSet = prepareStatement.executeQuery();

                while (resultSet.next()) {
                    int userIdFromRepo = resultSet.getInt("user_id");
                    int typeIdFromRepo = resultSet.getInt("indicator_type_id");
                    int valueFromRepo = resultSet.getInt("value");
                    LocalDate dateFromRepo = resultSet.getDate("date_of_value").toLocalDate();

                    User userFromRepo = UserRepoImpl.getInstance().findUserById(userIdFromRepo);
                    IndicatorType indicatorTypeFromRepo = IndicatorTypeRepoImpl.getInstance().findIndicatorTypeById(typeIdFromRepo);

                    meteringDevicesList.add(new ValueOfMeteringDevices(userFromRepo, indicatorTypeFromRepo, dateFromRepo, valueFromRepo));
                }
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception getValueOfMeteringDevices " + e.getMessage());
        }
        return meteringDevicesList;
    }

    /**
     * Эндпонт просмотра показаний, возвращает список "показаний" пользователя
     * по всем пользователям
     *
     * @return лист объектов "показание приборов учета"
     */
    public List<ValueOfMeteringDevices> getValueOfMeteringDevicesAll() {
        List<ValueOfMeteringDevices> meteringDevicesList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {

            PreparedStatement prepareStatement = connection.prepareStatement(GET_ALL_RECORDS);
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                int userIdFromRepo = resultSet.getInt("user_id");
                int typeIdFromRepo = resultSet.getInt("indicator_type_id");
                int valueFromRepo = resultSet.getInt("value");
                LocalDate dateFromRepo = resultSet.getDate("date_of_value").toLocalDate();

                User userFromRepo = UserRepoImpl.getInstance().findUserById(userIdFromRepo);
                IndicatorType indicatorTypeFromRepo = IndicatorTypeRepoImpl.getInstance().findIndicatorTypeById(typeIdFromRepo);

                meteringDevicesList.add(new ValueOfMeteringDevices(userFromRepo, indicatorTypeFromRepo, dateFromRepo, valueFromRepo));
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception getValueOfMeteringDevices " + e.getMessage());
        }
        return meteringDevicesList;
    }

    /**
     * Эндпоинт подачи показаний
     * предусмотрен запрет давать показания одинакового типа несколько раз в месяц
     *
     * @param valueOfMeteringDevice
     * @return true - в случае успеха
     */
    public boolean addValueOfMeteringDevice(ValueOfMeteringDevices valueOfMeteringDevice) {

        if (hasMeteringDevicesForMonth(valueOfMeteringDevice.getUser(), valueOfMeteringDevice.getTypeOfIndicators())) {
            System.out.println("Показания в этом месяце уже были");
            return false;
        }

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            ResultSet resultSet;
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_DATA_SQL)) {
                preparedStatement.setInt(1, valueOfMeteringDevice.getUser().getId());
                preparedStatement.setInt(2, valueOfMeteringDevice.getTypeOfIndicators().getId());
                preparedStatement.setDate(3, Date.valueOf(valueOfMeteringDevice.getDateOfValue()));
                preparedStatement.setInt(4, valueOfMeteringDevice.getValue());
                resultSet = preparedStatement.executeQuery();

                //В данной реализации мне не нужен id из базы данных, но так как есть в задании я это напечатал
                if (resultSet.next()) {
                    int generatedId = resultSet.getInt("id");
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception addValueOfMeteringDevice: " + e.getMessage());
        }
        return false;
    }

    public boolean hasMeteringDevicesForMonth(User user, IndicatorType indicatorType) {
        LocalDate month = LocalDate.now();
        int userId = user.getId();
        int indicatorTypeId = indicatorType.getId();

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {

            ResultSet resultSet;
            try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_RECORDS_BY_MONTH_YEAR)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, indicatorTypeId);
                preparedStatement.setInt(3, month.getMonthValue());
                preparedStatement.setInt(4, month.getYear());
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception hasMeteringDevicesForMonth: " + e.getMessage());
        }

        return false;
    }

    /**
     * Эндпонт просмотра показаний с фильтром по номеру года и месяца
     * возвращает список "показаний" пользователя,
     * если пользователь админ, тогда по всем пользователям
     *
     * @param user
     * @param year
     * @param month
     * @return лист объектов "показание приборов учета"
     */
    public List<ValueOfMeteringDevices> getValuesForMonthAndYear(User user, int month, int year) {
        int userId = user.getId();

        List<ValueOfMeteringDevices> meteringDevicesList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            ResultSet resultSet;
            try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_RECORDS_BY_MONTH_YEAR_USER)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, month);
                preparedStatement.setInt(3, year);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int typeIdFromRepo = resultSet.getInt("indicator_type_id");
                    int valueFromRepo = resultSet.getInt("value");
                    LocalDate dateFromRepo = resultSet.getDate("date_of_value").toLocalDate();

                    User userFromRepo = UserRepoImpl.getInstance().findUserById(userId);
                    IndicatorType indicatorTypeFromRepo = IndicatorTypeRepoImpl.getInstance().findIndicatorTypeById(typeIdFromRepo);

                    meteringDevicesList.add(new ValueOfMeteringDevices(userFromRepo, indicatorTypeFromRepo, dateFromRepo, valueFromRepo));
                }
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception getMeteringDevicesForMonth: " + e.getMessage());
        }

        return meteringDevicesList;
    }

    /**
     * Эндпоинт для получения актуальных показаний счетчика
     *
     * @param user
     * @param indicatorType
     * @return целое число, актуальное значение, если не найдено то 0
     */
    public int getLastValueForUserAndType(User user, IndicatorType indicatorType) {
        int userId = user.getId();
        int indicatorTypeId = indicatorType.getId();

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {

            ResultSet resultSet;
            try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_LAST_RECORD_BY_USER_TYPE_MONTH)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, indicatorTypeId);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getInt("value");
                }
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception getLastMeteringDeviceValue: " + e.getMessage());
        }
        return 0;
    }
}