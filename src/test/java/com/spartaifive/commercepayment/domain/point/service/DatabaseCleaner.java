package com.spartaifive.commercepayment.domain.point.service;

import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {
    @Autowired
    JdbcTemplate template;

    public void deleteTables(String... tableNames) {
        JdbcClient client = JdbcClient.create(template);
        client.sql("SET REFERENTIAL_INTEGRITY FALSE").update();
        for (var name: tableNames) {
            client.sql("TRUNCATE TABLE " + name).update();
        }
        client.sql("SET REFERENTIAL_INTEGRITY TRUE").update();
    }
}
