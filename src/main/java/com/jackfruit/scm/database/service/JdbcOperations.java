package com.jackfruit.scm.database.service;

import com.jackfruit.scm.database.config.DatabaseConnectionManager;
import com.scm.subsystems.DatabaseDesignSubsystem;
import com.jackfruit.scm.database.util.RowMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcOperations {

    private volatile DatabaseConnectionManager connectionManager;
    private final DatabaseDesignSubsystem exceptions = DatabaseDesignSubsystem.INSTANCE;

    public JdbcOperations() {
    }

    public void update(String sql, SqlConsumer<PreparedStatement> binder) {
        Connection connection = null;
        try {
            connection = connectionManager().getConnection();
            update(connection, sql, binder);
        } catch (SQLException exception) {
            handleSqlException(exception, sql);
            throw new IllegalStateException("Failed to execute update: " + sql, exception);
        } finally {
            connectionManager().releaseConnection(connection);
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> mapper) {
        Connection connection = null;
        try {
            connection = connectionManager().getConnection();
            return query(connection, sql, mapper, statement -> {
            });
        } catch (SQLException exception) {
            handleSqlException(exception, sql);
            throw new IllegalStateException("Failed to execute query: " + sql, exception);
        } finally {
            connectionManager().releaseConnection(connection);
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> mapper, SqlConsumer<PreparedStatement> binder) {
        Connection connection = null;
        try {
            connection = connectionManager().getConnection();
            return query(connection, sql, mapper, binder);
        } catch (SQLException exception) {
            handleSqlException(exception, sql);
            throw new IllegalStateException("Failed to execute query: " + sql, exception);
        } finally {
            connectionManager().releaseConnection(connection);
        }
    }

    public <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, SqlConsumer<PreparedStatement> binder) {
        List<T> results = query(sql, mapper, binder);
        return results.stream().findFirst();
    }

    public void update(Connection connection, String sql, SqlConsumer<PreparedStatement> binder) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.accept(statement);
            statement.executeUpdate();
        }
    }

    public <T> List<T> query(Connection connection, String sql, RowMapper<T> mapper,
                             SqlConsumer<PreparedStatement> binder) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.accept(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(mapper.map(resultSet));
                }
                return results;
            }
        }
    }

    public <T> Optional<T> queryOne(Connection connection, String sql, RowMapper<T> mapper,
                                    SqlConsumer<PreparedStatement> binder) throws SQLException {
        return query(connection, sql, mapper, binder).stream().findFirst();
    }

    public void inTransaction(SqlConsumer<Connection> work) {
        Connection connection = null;
        boolean originalAutoCommit = true;
        try {
            connection = connectionManager().getConnection();
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            work.accept(connection);
            connection.commit();
        } catch (SQLException exception) {
            handleSqlException(exception, "TRANSACTION");
            rollbackQuietly(connection, exception);
            throw new IllegalStateException("Failed to execute transaction", exception);
        } finally {
            restoreAutoCommit(connection, originalAutoCommit);
            connectionManager().releaseConnection(connection);
        }
    }

    @FunctionalInterface
    public interface SqlConsumer<T> {
        void accept(T input) throws SQLException;
    }

    private DatabaseConnectionManager connectionManager() {
        DatabaseConnectionManager local = connectionManager;
        if (local == null) {
            local = DatabaseConnectionManager.getInstance();
            connectionManager = local;
        }
        return local;
    }

    private void rollbackQuietly(Connection connection, SQLException originalException) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            exceptions.onTransactionRollbackFailed(extractKeyValue(originalException.getMessage()));
        }
    }

    private void restoreAutoCommit(Connection connection, boolean originalAutoCommit) {
        if (connection == null) {
            return;
        }
        try {
            connection.setAutoCommit(originalAutoCommit);
        } catch (SQLException ignored) {
            // Let the connection manager retire the connection if needed.
        }
    }

    private void handleSqlException(SQLException exception, String sql) {
        String message = exception.getMessage();
        if (message == null) {
            return;
        }

        if (message.contains("Duplicate entry") || message.contains("Duplicate key")) {
            exceptions.onDuplicatePrimaryKey("Entity", extractKeyValue(message));
        } else if (message.contains("foreign key constraint") || message.contains("FOREIGN KEY")) {
            exceptions.onForeignKeyViolation("ChildTable", "ParentTable", extractKeyValue(message));
        } else if (message.contains("UNIQUE constraint failed")) {
            exceptions.onUniqueConstraintViolation("field_name", extractKeyValue(message));
        } else if (message.contains("Deadlock") || message.contains("deadlock")) {
            exceptions.onTransactionDeadlock("entity_id", extractTableName(sql));
        } else if (message.contains("doesn't exist") || message.contains("Table") || message.contains("table")) {
            exceptions.onTableNotFound(extractTableName(sql));
        } else if (message.contains("timeout") || message.contains("Timeout") || message.contains("timed out")) {
            exceptions.onQueryTimeout(sql, 30000);
        } else if (message.contains("NOT NULL") || message.contains("null constraint")) {
            exceptions.onNullConstraintViolation(extractFieldName(message));
        } else if (message.contains("Check constraint") || message.contains("check constraint")) {
            exceptions.onCheckConstraintViolation("field_name", extractConstraintName(message));
        } else if (message.contains("Invalid") || message.contains("Data type")) {
            exceptions.onInvalidDataType(extractFieldName(message), message);
        } else if (message.contains("Price") || message.contains("price")) {
            exceptions.onPriceDataInconsistency(extractKeyValue(message));
        }
    }

    private String extractTableName(String sql) {
        if (sql == null) {
            return "unknown";
        }
        String upper = sql.toUpperCase();
        String[] patterns = {"FROM ", "INTO ", "UPDATE ", "DELETE FROM "};
        for (String pattern : patterns) {
            int index = upper.indexOf(pattern);
            if (index != -1) {
                int start = index + pattern.length();
                int end = Math.min(start + 50, sql.length());
                return sql.substring(start, end).trim().split("[\\s(,]")[0];
            }
        }
        return "unknown";
    }

    private String extractKeyValue(String message) {
        if (message == null) {
            return "unknown";
        }
        String[] parts = message.split("'");
        return parts.length >= 2 ? parts[1] : "unknown";
    }

    private String extractFieldName(String message) {
        if (message == null) {
            return "unknown_field";
        }
        if (message.contains("Column")) {
            String[] parts = message.split("Column");
            if (parts.length > 1) {
                return parts[1].trim().split("[\\s,.]")[0];
            }
        }
        if (message.contains("field")) {
            String[] parts = message.split("field");
            if (parts.length > 1) {
                return parts[1].trim().split("[\\s,.]")[0];
            }
        }
        return "unknown_field";
    }

    private String extractConstraintName(String message) {
        if (message == null) {
            return "unknown_constraint";
        }
        if (message.contains("constraint")) {
            String[] parts = message.split("constraint");
            if (parts.length > 1) {
                return parts[1].trim().split("[\\s,.]")[0];
            }
        }
        return "unknown_constraint";
    }
}
