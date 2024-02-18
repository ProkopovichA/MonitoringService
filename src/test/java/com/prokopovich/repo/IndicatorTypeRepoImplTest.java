package com.prokopovich.repo;

import com.prokopovich.model.IndicatorType;
import com.prokopovich.repo.impl.IndicatorTypeRepoImpl;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class IndicatorTypeRepoImplTest {

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
        IndicatorTypeRepoImpl.getInstance();
    }


    @Test
    @DisplayName("Тест получения списка типов индикаторов")
    void testGetTypeOfIndicators() {
        IndicatorTypeRepoImpl repo = IndicatorTypeRepoImpl.getInstance();

        IndicatorType indicatorType = new IndicatorType(1, "Тип 1");
        repo.addTypeOfIndicator(indicatorType);

        ArrayList<IndicatorType> indicatorTypes = repo.getTypeOfIndicators();

        assertNotNull(indicatorTypes, "Список типов индикаторов не должен быть null");
        assertFalse(indicatorTypes.isEmpty(), "Список типов индикаторов не должен быть пустым");
        assertEquals(1, indicatorTypes.size(), "Ожидался один тип индикатора в списке");
        assertEquals(indicatorType, indicatorTypes.get(0), "Полученный тип индикатора не соответствует ожидаемому");
    }

    @Test
    @DisplayName("Тест поиска типа индикатора по ID")
    void testFindIndicatorTypeById() {
        IndicatorTypeRepoImpl repo = IndicatorTypeRepoImpl.getInstance();

        IndicatorType indicatorType = new IndicatorType(1, "Тип 1");
        repo.addTypeOfIndicator(indicatorType);

        IndicatorType foundType = repo.findIndicatorTypeById(1);

        assertNotNull(foundType, "Ожидался не null результат поиска по ID");
        assertEquals(indicatorType, foundType, "Найденный тип индикатора не соответствует ожидаемому");
    }

    @Test
    @DisplayName("Тест добавления типа индикатора")
    void testAddTypeOfIndicator() {
        IndicatorTypeRepoImpl repo = IndicatorTypeRepoImpl.getInstance();

        IndicatorType newType = new IndicatorType(2, "Новый тип");

        boolean result = repo.addTypeOfIndicator(newType);

        assertTrue(result, "Ожидалось успешное добавление типа индикатора");
        assertEquals(1, repo.getTypeOfIndicators().size(), "Ожидался один тип индикатора после добавления");
        assertEquals(newType, repo.getTypeOfIndicators().get(0), "Добавленный тип индикатора не соответствует ожидаемому");
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }
}

