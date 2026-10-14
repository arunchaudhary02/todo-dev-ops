package com.durable.services;

import com.durable.entities.CompanyRow;
import com.durable.repositorys.CompanyRepository;

import java.sql.SQLException;
import java.util.List;

public class CompanyService {

    private CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<CompanyRow> getAllCompanies() throws SQLException {
        List<CompanyRow> rows = companyRepository.findAll();

        return rows;
    }

    public void createCompany(CompanyRow request) throws SQLException {
        companyRepository.create(request);
    }
}
