package com.durable;

import com.durable.repositorys.CompanyRepository;
import com.durable.routes.CompanyRoute;
import com.durable.services.CompanyService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import com.typesafe.config.Config;
import org.flywaydb.core.Flyway;

public class Application {

    HikariDataSource dataSource;

    public Javalin create(Config config) {
        String host = config.getString("database.host");
        int port = config.getInt("database.port");
        String database = config.getString("database.name");
        String username = config.getString("database.username");
        String password = config.getString("database.password");

        // Hikari
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(
                "jdbc:postgresql://" + host + ":" + port + "/" + database
        );

        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        this.dataSource =
                new HikariDataSource(hikariConfig);

        // Flyway
        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:postgresql://" + host + ":" + port + "/" + database,
                        username,
                        password
                )
                .load();

        flyway.migrate();

        // Javalin
        Javalin app = Javalin.create();

        app.get("/", ctx -> {
            ctx.result("Hello Javalin");
        });

        app.get("/hello", ctx -> {
            ctx.result("Welcome In Mathura");
        });

        // Company
        CompanyRepository companyRepository =
                new CompanyRepository(dataSource);

        CompanyService companyService =
                new CompanyService(companyRepository);

        CompanyRoute companyRoute =
                new CompanyRoute(companyService);

        companyRoute.register(app, companyService);

        return app;
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }
}
