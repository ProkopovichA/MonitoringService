package com.prokopovich.repo;

import com.prokopovich.model.User;
import com.prokopovich.model.UserRole;
import com.prokopovich.repo.impl.UserRepoImpl;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserRepoImplTest {
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
        UserRepoImpl.getInstance();
    }

    @Test
    @DisplayName("Тест добавления пользователя")
    void testAddUser() {
        User user = new User(777,"testUser", "password123", UserRole.ADMIN);

        boolean result = UserRepoImpl.getInstance().addUser(user);

        assertTrue(result, "Ожидалось успешное добавление пользователя");

        User retrievedUser = getUserByLogin(user.getLogin());
        assertNotNull(retrievedUser, "Ожидалось, что пользователь будет найден в базе данных");
        assertEquals(user.getLogin(), retrievedUser.getLogin(), "Логин не соответствует ожидаемому");
        assertEquals(user.getPassword(), retrievedUser.getPassword(), "Пароль не соответствует ожидаемому");
        assertEquals(user.getRole(), retrievedUser.getRole(), "Роль не соответствует ожидаемой");
    }

    @Test
    @DisplayName("Тест поиска пользователя по логину")
    void testFindUserByLogin() {
        User user = new User(777, "testUser", "password123", UserRole.ADMIN);
        UserRepoImpl.getInstance().addUser(user);

        User retrievedUser = UserRepoImpl.getInstance().findUserByLogin(user.getLogin());

        assertNotNull(retrievedUser, "Ожидалось, что пользователь будет найден по логину");
        assertEquals(user.getLogin(), retrievedUser.getLogin(), "Логин не соответствует ожидаемому");
        assertEquals(user.getPassword(), retrievedUser.getPassword(), "Пароль не соответствует ожидаемому");
        assertEquals(user.getRole(), retrievedUser.getRole(), "Роль не соответствует ожидаемой");
    }

    private User getUserByLogin(String login) {
        try (Connection connection = DriverManager.getConnection(
                System.getProperty("DB_URL"),
                System.getProperty("DB_USERNAME"),
                System.getProperty("DB_PASSWORD"))) {
            try (PreparedStatement prepareStatement = connection.prepareStatement(
                    "SELECT * FROM users WHERE login = ?")) {
                prepareStatement.setString(1, login);
                ResultSet resultSet = prepareStatement.executeQuery();
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("id"),
                            resultSet.getString("login"),
                            resultSet.getString("password"),
                            UserRole.valueOf(resultSet.getString("role"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }

}
