package com.durable.repositorys;

import com.durable.entities.CompanyRow;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompanyRepository {

    private DataSource dataSource;

    public CompanyRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<CompanyRow> findAll() throws SQLException {
        String sql = """
                SELECT 
                    company_id,
                    company_name,
                    status
                FROM company
                """;

        List<CompanyRow> result = new ArrayList<>();
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
        ) {

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                result.add(new CompanyRow(
                        rs.getInt("company_id"),
                        rs.getString("company_name"),
                        rs.getBoolean("status")
                ));
            }
        }

        return result;
    }

    public void create(CompanyRow request) throws SQLException {
        String sql = """
                INSERT INTO company (company_id, company_name, status)
                VALUES (?, ?, ?)
                """;

        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setInt(1, request.companyId());
            ps.setString(2, request.companyName());
            ps.setBoolean(3, request.status());
            ps.executeUpdate();
        }
    }
}
