package com.durable.routes;

import com.durable.entities.CompanyRow;
import com.durable.services.CompanyService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.sql.SQLException;

public class CompanyRoute {
    private CompanyService companyService;

    public CompanyRoute(CompanyService companyService) {
        this.companyService = companyService;
    }

    public void register(Javalin app, CompanyService companyService) {
        CompanyRoute companyRoute = new CompanyRoute(companyService);
        /*
        app.get("/companies", ctx -> {
            var response = companyService.getAllCompanies();
            ctx.status(HttpStatus.OK).json(response);
        });
         */
        app.post("/companies", companyRoute::getAllCompany);
        app.post("/create-company", companyRoute::createCompany);
    }

    public void createCompany(Context ctx) throws SQLException {
        CompanyRow request = ctx.bodyAsClass(CompanyRow.class);

        companyService.createCompany(request);

        ctx.status(HttpStatus.CREATED);
    }

    public void getAllCompany(Context ctx) throws SQLException {
        var response  = companyService.getAllCompanies();

        ctx.status(HttpStatus.OK).json(response);
    }
}
