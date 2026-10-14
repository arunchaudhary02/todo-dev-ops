package com.durable;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;

import static org.junit.Assert.assertEquals;

@Testcontainers
public class PostgresContainerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test")
            .withUsername("testPostgres")
            .withPassword("password");

    @Test
    void shouldConnectToPostgres() throws SQLException {
        try(Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        )) {
            try(
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("SELECT 1")
            ) {
                result.next();

                assertEquals(1, result.getInt(1));
            }
        }
    }
}
