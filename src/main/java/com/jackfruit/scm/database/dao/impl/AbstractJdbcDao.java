package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.config.DatabaseConnectionManager;
import com.jackfruit.scm.database.util.RowMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractJdbcDao {

    private final DatabaseConnectionManager connectionManager = DatabaseConnectionManager.getInstance();

    protected Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    protected void releaseConnection(Connection connection) {
        connectionManager.releaseConnection(connection);
    }

    protected <T> Optional<T> queryForObject(String sql, RowMapper<T> rowMapper, SqlConsumer<PreparedStatement> binder) {
        Connection connection = null;
        try {
            connection = getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                binder.accept(statement);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(rowMapper.map(resultSet));
                    }
                    return Optional.empty();
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to execute query: " + sql, exception);
        } finally {
            releaseConnection(connection);
        }
    }

    protected <T> List<T> queryForList(String sql, RowMapper<T> rowMapper) {
        Connection connection = null;
        try {
            connection = getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(rowMapper.map(resultSet));
                }
                return results;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to execute query: " + sql, exception);
        } finally {
            releaseConnection(connection);
        }
    }

    protected <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, SqlConsumer<PreparedStatement> binder) {
        Connection connection = null;
        try {
            connection = getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                binder.accept(statement);
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<T> results = new ArrayList<>();
                    while (resultSet.next()) {
                        results.add(rowMapper.map(resultSet));
                    }
                    return results;
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to execute query: " + sql, exception);
        } finally {
            releaseConnection(connection);
        }
    }

    protected void executeUpdate(String sql, SqlConsumer<PreparedStatement> binder) {
        Connection connection = null;
        try {
            connection = getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                binder.accept(statement);
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to execute update: " + sql, exception);
        } finally {
            releaseConnection(connection);
        }
    }

    @FunctionalInterface
    protected interface SqlConsumer<T> {
        void accept(T input) throws SQLException;
    }
}
