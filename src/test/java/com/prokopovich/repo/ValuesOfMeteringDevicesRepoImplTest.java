package com.prokopovich.repo;

import com.prokopovich.model.IndicatorType;
import com.prokopovich.model.User;
import com.prokopovich.model.UserRole;
import com.prokopovich.model.ValueOfMeteringDevices;
import com.prokopovich.repo.impl.IndicatorTypeRepoImpl;
import com.prokopovich.repo.impl.UserRepoImpl;
import com.prokopovich.repo.impl.ValuesOfMeteringDevicesRepoImpl;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ValuesOfMeteringDevicesRepoImplTest {

    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
        System.setProperty("DB_URL", postgresContainer.getJdbcUrl());
        System.setProperty("DB_USERNAME", postgresContainer.getUsername());
        System.setProperty("DB_PASSWORD", postgresContainer.getPassword());
    }

    @BeforeEach
    void setUp() {
        ValuesOfMeteringDevicesRepoImpl.getInstance();
    }

    @Test
    @DisplayName("Тест получения значений приборов учета пользователя")
    void testGetValueOfMeteringDevices() {
        ValuesOfMeteringDevicesRepoImpl repo = ValuesOfMeteringDevicesRepoImpl.getInstance();
        User user = new User(777, "testUser", "password123", UserRole.USER);
        UserRepoImpl.getInstance().addUser(user);

        List<ValueOfMeteringDevices> values = repo.getValueOfMeteringDevices(user);

        assertNotNull(values, "Список значений не должен быть null");
        assertTrue(values.isEmpty(), "Список значений должен быть пустым для нового пользователя");
    }

    @Test
    @DisplayName("Тест добавления значения прибора учета")
    void testAddValueOfMeteringDevice() {
        ValuesOfMeteringDevicesRepoImpl repo = ValuesOfMeteringDevicesRepoImpl.getInstance();
        User user = new User(777,"testUser", "password123", UserRole.USER);
        UserRepoImpl.getInstance().addUser(user);
        IndicatorType type = new IndicatorType(1, "Тип 1");
        IndicatorTypeRepoImpl.getInstance().addTypeOfIndicator(type);
        LocalDate date = LocalDate.now();
        int value = 100;

        ValueOfMeteringDevices valueOfMeteringDevices = new ValueOfMeteringDevices(user, type, date, value);
        boolean result = repo.addValueOfMeteringDevice(valueOfMeteringDevices);

        assertTrue(result, "Ожидалось успешное добавление значения прибора учета");
        List<ValueOfMeteringDevices> values = repo.getValueOfMeteringDevices(user);
        assertFalse(values.isEmpty(), "Список значений не должен быть пустым после добавления");
        assertEquals(1, values.size(), "Ожидалось одно значение в списке после добавления");
        assertEquals(value, values.get(0).getValue(), "Добавленное значение прибора учета не соответствует ожидаемому");
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }

}

