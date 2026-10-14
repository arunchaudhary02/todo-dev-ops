package com.durable;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class CompanyIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("test")
                    .withUsername("testPostgres")
                    .withPassword("password");

    private Javalin app;
    private HttpClient httpClient;
    private String baseUrl;
    private HikariDataSource dataSource;

    @BeforeEach
    void setup() {
        // Load the existing test configuration.
        Config baseConfig = ConfigFactory.load("test.conf");

        // Override database settings with the temporary Testcontainers database.
        Config containerConfig = ConfigFactory.parseMap(Map.of(
                "database.host", postgres.getHost(),
                "database.port", postgres.getMappedPort(5432),
                "database.name", postgres.getDatabaseName(),
                "database.username", postgres.getUsername(),
                "database.password", postgres.getPassword()
        ));

        Config config = containerConfig
                .withFallback(baseConfig)
                .resolve();

        // Create the application and initialize the database migrations.
        Application application = new Application();
        app = application.create(config);
        dataSource = application.getDataSource();

        // Start the application on the test port.
        app.start(8082);

        baseUrl = "http://localhost:8082";
        httpClient = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        // Stop the HTTP server.
        if (app != null) {
            app.stop();
        }

        // Close the database connection pool.
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Test
    void createCompany_shouldReturn201() throws Exception {
        String requestBody = """
                {
                    "companyId": 201,
                    "companyName": "Integration Test Company",
                    "status": true
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/create-company"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(201, response.statusCode());
    }

    @Test
    void getAllCompanies_shouldReturnCompanies() throws Exception {
        // First, create a company.
        String requestBody = """
                {
                    "companyId": 301,
                    "companyName": "Second Integration Company",
                    "status": true
                }
                """;

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/create-company"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> createResponse = httpClient.send(
                createRequest,
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(201, createResponse.statusCode());

        // Your current CompanyRoute registers /companies as POST.
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/companies"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> getResponse = httpClient.send(
                getRequest,
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, getResponse.statusCode());

        // Verify the response contains the company created above.
        assertTrue(
                getResponse.body().contains("Second Integration Company")
        );

        // Verify the company was saved in PostgreSQL.
        String sql = """
                SELECT company_name, status
                FROM company
                WHERE company_id = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, 301);

            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "Created company should exist in the database");

                assertEquals(
                        "Second Integration Company",
                        rs.getString("company_name")
                );

                assertTrue(rs.getBoolean("status"));
            }
        }
    }
}
