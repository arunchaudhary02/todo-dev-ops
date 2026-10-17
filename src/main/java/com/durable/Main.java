package com.durable;

import com.durable.repositorys.CompanyRepository;
import com.durable.routes.CompanyRoute;
import com.durable.services.CompanyService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.javalin.Javalin;
import org.flywaydb.core.Flyway;

// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

    /*
           //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");



//        String env  = System.getenv("APP_ENV");
//        System.out.println("env = " + env);
//        Config config = ConfigFactory.load(env);
//        System.out.println(config.getString("env"));
//
//        HikariConfig hikariConfig = new HikariConfig();
//
//        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/moonrise");
//        hikariConfig.setUsername("postgres");
//        hikariConfig.setPassword("password");
//
//        hikariConfig.setMaximumPoolSize(10);
//        hikariConfig.setMinimumIdle(2);
//
//        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
//
//        System.out.println(dataSource);





        String configResource = System.getProperty("config.resource");
        System.out.println("configResource = " + configResource);
        Config config = ConfigFactory.load(configResource);

        String host = config.getString("database.host");
        int port = config.getInt("database.port");
        String database = config.getString("database.name");
        String username = config.getString("database.username");
        String password = config.getString("database.password");

        // dattabase client
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(
                "jdbc:postgresql://" + host + ":" + port + "/" + database
        );

        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        System.out.println(dataSource);

        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:postgresql://" + host + ":" + port + "/" + database,
                        username,
                        password
                )
                .load();

        // read database table
        // setup javalin http server - /getEmployee
        // best practices of hikari - connection pool, etc
        // flyway migration best practices

        flyway.migrate();


        // Http javalin server

        Javalin app = Javalin.create();

        app.get("/", ctx -> {
            ctx.result("Hello Javalin");
        });

        app.get("/hello", ctx -> {
            ctx.result("Welcome In Mathura");
        });

        CompanyRepository companyRepository = new CompanyRepository(dataSource);
        CompanyService companyService = new CompanyService(companyRepository);
        CompanyRoute companyRoute = new CompanyRoute(companyService);

        companyRoute.register(app, companyService);

        app.start(8081);

    */

       // Integration setup / test.conf
      // command to run integration test
     // use test container to start actual PG database
    // how to debug / how we can check data present in table of test container

        String configResource = System.getProperty("config.resource");

        if(configResource == null || configResource.isBlank()) {
            configResource = "application.conf";
        }

        System.out.println("Using config: " + configResource);

        Config config = ConfigFactory.load(configResource);

        Application application = new Application();
        Javalin app = application.create(config);

        app.start(8082);
    }
}