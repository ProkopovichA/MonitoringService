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
import com.prokopovich.service.DatabaseConfig;
import com.prokopovich.service.OutputHandler;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class ValuesOfMeteringDevicesRepo {
    public static final String URL = DatabaseConfig.getDatabaseUrlModel();
    public static final String USER_NAME = DatabaseConfig.getDatabaseUsername();
    public static final String PASSWORD = DatabaseConfig.getDatabasePassword();
    private static ValuesOfMeteringDevicesRepo instance;

    private ValuesOfMeteringDevicesRepo() {

    }

    public static ValuesOfMeteringDevicesRepo getInstance() {
        if (instance == null) {
            instance = new ValuesOfMeteringDevicesRepo();
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
        int userId = user.getId();
        List<ValueOfMeteringDevices> meteringDevicesList = new ArrayList<>();

        if (user.getRole().isAdmin()) {
            return getValueOfMeteringDevicesAll();
        }

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            String findUserByLogin = "SELECT * FROM value_of_metering_devices WHERE user_id = ?";
            PreparedStatement prepareStatement = connection.prepareStatement(findUserByLogin);
            prepareStatement.setInt(1, userId);
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                int userIdFromRepo = resultSet.getInt("user_id");
                int typeIdFromRepo = resultSet.getInt("indicator_type_id");
                int valueFromRepo = resultSet.getInt("value");
                LocalDate dateFromRepo = resultSet.getDate("date_of_value").toLocalDate();

                User userFromRepo = UsersRepo.getInstance().findUserById(userIdFromRepo);
                IndicatorType indicatorTypeFromRepo = IndicatorTypeRepo.getInstance().findIndicatorTypeById(typeIdFromRepo);

                meteringDevicesList.add(new ValueOfMeteringDevices(userFromRepo,indicatorTypeFromRepo,dateFromRepo,valueFromRepo));
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception getValueOfMeteringDevices " + e.getMessage());
        }
        return meteringDevicesList;
    }

    /**
     * Эндпонт просмотра показаний, возвращает список "показаний" пользователя
     * по всем пользователям
     * @return лист объектов "показание приборов учета"
     */
    public List<ValueOfMeteringDevices> getValueOfMeteringDevicesAll() {
        List<ValueOfMeteringDevices> meteringDevicesList = new ArrayList<>();



        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            String findUserByLogin = "SELECT * FROM value_of_metering_devices ";
            PreparedStatement prepareStatement = connection.prepareStatement(findUserByLogin);
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                int userIdFromRepo = resultSet.getInt("user_id");
                int typeIdFromRepo = resultSet.getInt("indicator_type_id");
                int valueFromRepo = resultSet.getInt("value");
                LocalDate dateFromRepo = resultSet.getDate("date_of_value").toLocalDate();

                User userFromRepo = UsersRepo.getInstance().findUserById(userIdFromRepo);
                IndicatorType indicatorTypeFromRepo = IndicatorTypeRepo.getInstance().findIndicatorTypeById(typeIdFromRepo);

                meteringDevicesList.add(new ValueOfMeteringDevices(userFromRepo,indicatorTypeFromRepo,dateFromRepo,valueFromRepo));
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception getValueOfMeteringDevices " + e.getMessage());
        }
        return meteringDevicesList;
    }



    /**
     * Эндпоинт подачи показаний
     * предусмотрен запрет давать показания одинакового типа несколько раз в месяц
     * @param valueOfMeteringDevice
     * @return true - в случае успеха
     */
    public boolean addValueOfMeteringDevice(ValueOfMeteringDevices valueOfMeteringDevice) {

        if (hasMeteringDevicesForMonth(valueOfMeteringDevice.getUser(),valueOfMeteringDevice.getTypeOfIndicators())) {
            System.out.println("Показания в этом месяце уже были");
            return false;
        }

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            String insertDataSql = "INSERT INTO value_of_metering_devices (user_id, indicator_type_id, date_of_value, value) VALUES (?, ?, ?, ?) RETURNING id";
            PreparedStatement preparedStatement = connection.prepareStatement(insertDataSql);
            preparedStatement.setInt(1, valueOfMeteringDevice.getUser().getId());
            preparedStatement.setInt(2, valueOfMeteringDevice.getTypeOfIndicators().getId());
            preparedStatement.setDate(3, Date.valueOf(valueOfMeteringDevice.getDateOfValue()));
            preparedStatement.setInt(4, valueOfMeteringDevice.getValue());
            ResultSet resultSet = preparedStatement.executeQuery();

            //В данной реализации мне не нужен id из базы данных, но так как есть в задании я это напечатал
            if (resultSet.next()) {
                int generatedId = resultSet.getInt("id");
                return true;
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
            String findMeteringDevicesQuery = "SELECT COUNT(*) AS count FROM value_of_metering_devices " +
                    "WHERE user_id = ? AND indicator_type_id = ? AND EXTRACT(MONTH FROM date_of_value) = ? AND EXTRACT(YEAR FROM date_of_value) = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(findMeteringDevicesQuery);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, indicatorTypeId);
            preparedStatement.setInt(3, month.getMonthValue());
            preparedStatement.setInt(4, month.getYear());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
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
     * @param user
     * @param year
     * @param month
     * @return  лист объектов "показание приборов учета"
     */
    public List<ValueOfMeteringDevices> getValuesForMonthAndYear(User user, int month, int year) {
        int userId = user.getId();

        List<ValueOfMeteringDevices> meteringDevicesList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            String findMeteringDevicesQuery = "SELECT * FROM value_of_metering_devices " +
                    "WHERE user_id = ? AND EXTRACT(MONTH FROM date_of_value) = ? AND EXTRACT(YEAR FROM date_of_value) = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(findMeteringDevicesQuery);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, month);
            preparedStatement.setInt(3, year);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int typeIdFromRepo = resultSet.getInt("indicator_type_id");
                int valueFromRepo = resultSet.getInt("value");
                LocalDate dateFromRepo = resultSet.getDate("date_of_value").toLocalDate();

                User userFromRepo = UsersRepo.getInstance().findUserById(userId);
                IndicatorType indicatorTypeFromRepo = IndicatorTypeRepo.getInstance().findIndicatorTypeById(typeIdFromRepo);

                meteringDevicesList.add(new ValueOfMeteringDevices(userFromRepo, indicatorTypeFromRepo, dateFromRepo, valueFromRepo));
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception getMeteringDevicesForMonth: " + e.getMessage());
        }

        return meteringDevicesList;
    }

    /**
     * Эндпоинт для получения актуальных показаний счетчика
     * @param user
     * @param indicatorType
     * @return целое число, актуальное значение, если не найдено то 0
     */
    public int getLastValueForUserAndType(User user, IndicatorType indicatorType) {
        int userId = user.getId();
        int indicatorTypeId = indicatorType.getId();

        try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD)) {
            String findLastMeteringDeviceQuery = "SELECT value FROM value_of_metering_devices " +
                    "WHERE user_id = ? AND indicator_type_id = ? " +
                    "ORDER BY date_of_value DESC " +
                    "LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(findLastMeteringDeviceQuery);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, indicatorTypeId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("value");
            }

        } catch (SQLException e) {
            System.out.println("Got SQL Exception getLastMeteringDeviceValue: " + e.getMessage());
        }

        // Возвращаем значение по умолчанию (может быть 0 или другое значение в зависимости от логики вашего приложения)
        return 0;
    }





}