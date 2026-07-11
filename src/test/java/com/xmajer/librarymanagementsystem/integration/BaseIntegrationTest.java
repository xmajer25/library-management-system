package com.xmajer.librarymanagementsystem.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.postgresql.PostgreSQLContainer;


@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @ServiceConnection
    protected static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:16-alpine")
                    .withDatabaseName("library_test_db")
                    .withUsername("test_user")
                    .withPassword("test_password");

    static {
        POSTGRES.start();
    }
}