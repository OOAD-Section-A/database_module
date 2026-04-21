package com.jackfruit.scm.database.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SchemaBootstrapper {

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);
    private static final Object INITIALIZATION_MONITOR = new Object();
    private static final String CORE_TABLE = "price_list";

    private SchemaBootstrapper() {
    }

    public static void ensureSchemaInitialized() {
        if (INITIALIZED.get()) {
            return;
        }

        synchronized (INITIALIZATION_MONITOR) {
            if (INITIALIZED.get()) {
                return;
            }
            initializeSchema();
            INITIALIZED.set(true);
        }
    }

    private static void initializeSchema() {
        DatabaseConfig config = DatabaseConfig.load();
        try {
            ensureDatabaseExists(config);
            try (Connection connection = DriverManager.getConnection(
                    config.getUrl(),
                    config.getUsername(),
                    config.getPassword())) {
                if (!hasTable(connection, CORE_TABLE)) {
                    applySchema(connection);
                }
            }
        } catch (SQLException | IOException exception) {
            throw new IllegalStateException("Unable to initialize schema from schema.sql", exception);
        }
    }

    private static void ensureDatabaseExists(DatabaseConfig config) throws SQLException {
        String createDatabaseSql = "CREATE DATABASE IF NOT EXISTS `" + escapeIdentifier(config.getSchemaName()) + "`";
        try (Connection connection = DriverManager.getConnection(
                config.getServerUrl(),
                config.getUsername(),
                config.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute(createDatabaseSql);
        }
    }

    private static boolean hasTable(Connection connection, String tableName) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(
                connection.getCatalog(),
                null,
                tableName,
                new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }

    private static void applySchema(Connection connection) throws IOException, SQLException {
        StringBuilder statementBuffer = new StringBuilder();
        String delimiter = ";";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(openSchemaStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.toUpperCase().startsWith("DELIMITER ")) {
                    delimiter = trimmed.substring("DELIMITER ".length()).trim();
                    continue;
                }
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }
                statementBuffer.append(line).append(System.lineSeparator());
                if (trimmed.endsWith(delimiter)) {
                    executeStatement(connection, statementBuffer.toString(), delimiter);
                    statementBuffer.setLength(0);
                }
            }
        }
    }

    private static InputStream openSchemaStream() throws IOException {
        InputStream inputStream = SchemaBootstrapper.class.getClassLoader().getResourceAsStream("schema.sql");
        if (inputStream == null) {
            throw new IOException("schema.sql is missing from the classpath");
        }
        return inputStream;
    }

    private static void executeStatement(Connection connection, String rawStatement, String delimiter) throws SQLException {
        String statementText = rawStatement.trim();
        if (statementText.endsWith(delimiter)) {
            statementText = statementText.substring(0, statementText.length() - delimiter.length()).trim();
        }
        if (statementText.isEmpty()) {
            return;
        }
        String upper = statementText.toUpperCase();
        if (upper.startsWith("CREATE DATABASE ") || upper.startsWith("USE ")) {
            return;
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(statementText);
        }
    }

    private static String escapeIdentifier(String identifier) {
        return identifier.replace("`", "``");
    }
}
