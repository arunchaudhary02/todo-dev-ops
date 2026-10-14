package com.durable.entities;

public record CompanyRow(
        int companyId,
        String companyName,
        Boolean status
) {
}
