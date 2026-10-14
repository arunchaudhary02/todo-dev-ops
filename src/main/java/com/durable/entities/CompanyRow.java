package com.durable;

public record CompanyRow(
        int companyId,
        String companyName,
        Boolean status
) {
}
