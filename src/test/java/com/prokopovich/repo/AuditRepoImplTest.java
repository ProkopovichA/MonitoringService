package com.prokopovich.repo;

import com.prokopovich.model.Audit;
import com.prokopovich.model.AuditAction;
import com.prokopovich.model.User;
import com.prokopovich.model.UserRole;
import com.prokopovich.repo.impl.AuditRepoImpl;
import com.prokopovich.repo.impl.UserRepoImpl;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class AuditRepoImplTest {

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
        AuditRepoImpl.getInstance();
    }


    @Test
    @DisplayName("Тест получения списка аудита")
    void testGetAuditList() {
        User user = new User(777,"testUser", "password123", UserRole.ADMIN);
        UserRepoImpl.getInstance().addUser(user);

        Audit audit = new Audit(user, LocalDate.now(), AuditAction.SIGN_IN);
        AuditRepoImpl.getInstance().addAudit(audit);

        ArrayList<Audit> auditList = AuditRepoImpl.getInstance().getAuditList();

        assertNotNull(auditList, "Список аудита не должен быть null");
        assertFalse(auditList.isEmpty(), "Список аудита не должен быть пустым");
        assertEquals(1, auditList.size(), "Ожидалась одна запись в списке аудита");
        assertEquals(user.getId(), auditList.get(0).getUser().getId(), "ID пользователя не соответствует ожидаемому");
        assertEquals(AuditAction.SIGN_IN, auditList.get(0).getAction(), "Действие аудита не соответствует ожидаемому");
    }

    @Test
    @DisplayName("Тест добавления записи аудита")
    void testAddAudit() {
        User user = new User(777,"testUser", "password123", UserRole.ADMIN);
        UserRepoImpl.getInstance().addUser(user);

        Audit audit = new Audit(user, LocalDate.now(), AuditAction.SIGN_IN);

        boolean result = AuditRepoImpl.getInstance().addAudit(audit);

        assertTrue(result, "Ожидалось успешное добавление записи аудита");
        ArrayList<Audit> auditList = AuditRepoImpl.getInstance().getAuditList();
        assertNotNull(auditList, "Список аудита не должен быть null после добавления записи");
        assertFalse(auditList.isEmpty(), "Список аудита не должен быть пустым после добавления записи");
        assertEquals(1, auditList.size(), "Ожидалась одна запись в списке аудита после добавления записи");
        assertEquals(user.getId(), auditList.get(0).getUser().getId(), "ID пользователя не соответствует ожидаемому");
        assertEquals(AuditAction.SIGN_IN, auditList.get(0).getAction(), "Действие аудита не соответствует ожидаемому");
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }
}
