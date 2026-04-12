package com.jackfruit.scm.database.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class DatabaseConfig {

    private final String url;
    private final String username;
    private final String password;
    private final int poolSize;

    private DatabaseConfig(String url, String username, String password, int poolSize) {
        this.url = Objects.requireNonNull(url, "Database URL cannot be null");
        this.username = Objects.requireNonNull(username, "Database username cannot be null");
        this.password = Objects.requireNonNull(password, "Database password cannot be null");
        this.poolSize = poolSize;
    }

    public static DatabaseConfig load() {
        Properties properties = new Properties();
        try (InputStream inputStream = DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("database.properties is missing from the classpath");
            }
            properties.load(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load database.properties", exception);
        }

        return new DatabaseConfig(
                properties.getProperty("db.url", "jdbc:mysql://localhost:3306/OOAD"),
                properties.getProperty("db.username", "root"),
                properties.getProperty("db.password", "root"),
                Integer.parseInt(properties.getProperty("db.pool.size", "5"))
        );
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPoolSize() {
        return poolSize;
    }
}
