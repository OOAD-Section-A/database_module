package com.jackfruit.scm.database.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class DatabaseConfig {

    private static final String PLACEHOLDER_PREFIX = "CHANGE_ME";

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
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load database.properties", exception);
        }

        String url = resolveSetting("db.url", "DB_URL", properties, null);
        String username = resolveSetting("db.username", "DB_USERNAME", properties, null);
        String password = resolveSetting("db.password", "DB_PASSWORD", properties, null);

        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Database URL is required. Provide it via: -Ddb.url=<url>, DB_URL environment variable, or db.url in database.properties");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalStateException("Database username is required. Provide it via: -Ddb.username=<user>, DB_USERNAME environment variable, or db.username in database.properties");
        }
        if (password == null) {
            throw new IllegalStateException("Database password is required. Provide it via: -Ddb.password=<pass>, DB_PASSWORD environment variable, or db.password in database.properties");
        }

        return new DatabaseConfig(
                url,
                username,
                password,
                Integer.parseInt(resolveSetting("db.pool.size", "DB_POOL_SIZE", properties, "5"))
        );
    }

    private static String resolveSetting(String propertyKey, String envKey, Properties properties, String defaultValue) {
        String systemValue = System.getProperty(propertyKey);
        if (isConfiguredValue(systemValue)) {
            return systemValue;
        }

        String envValue = System.getenv(envKey);
        if (isConfiguredValue(envValue)) {
            return envValue;
        }

        String propertyValue = properties.getProperty(propertyKey);
        if (isConfiguredValue(propertyValue)) {
            return propertyValue;
        }

        return defaultValue;
    }

    private static boolean isConfiguredValue(String value) {
        return value != null && !value.isBlank() && !value.startsWith(PLACEHOLDER_PREFIX);
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

    public String getSchemaName() {
        String withoutPrefix = url.substring("jdbc:mysql://".length());
        int slashIndex = withoutPrefix.indexOf('/');
        if (slashIndex < 0 || slashIndex == withoutPrefix.length() - 1) {
            throw new IllegalStateException("Database URL must include a schema name, e.g. jdbc:mysql://host:3306/OOAD");
        }

        String afterSlash = withoutPrefix.substring(slashIndex + 1);
        int queryIndex = afterSlash.indexOf('?');
        String schemaName = queryIndex >= 0 ? afterSlash.substring(0, queryIndex) : afterSlash;
        if (schemaName.isBlank()) {
            throw new IllegalStateException("Database URL must include a schema name, e.g. jdbc:mysql://host:3306/OOAD");
        }
        return schemaName;
    }

    public String getServerUrl() {
        String prefix = "jdbc:mysql://";
        if (!url.startsWith(prefix)) {
            return url;
        }

        String withoutPrefix = url.substring(prefix.length());
        int slashIndex = withoutPrefix.indexOf('/');
        if (slashIndex < 0) {
            return url;
        }

        String hostPort = withoutPrefix.substring(0, slashIndex);
        String remainder = withoutPrefix.substring(slashIndex + 1);
        int queryIndex = remainder.indexOf('?');
        String query = queryIndex >= 0 ? remainder.substring(queryIndex) : "";
        return prefix + hostPort + "/" + query;
    }
}
