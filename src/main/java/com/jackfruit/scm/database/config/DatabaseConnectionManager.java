package com.jackfruit.scm.database.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class DatabaseConnectionManager {

    private static volatile DatabaseConnectionManager instance;
    private final DatabaseConfig config;
    private final BlockingQueue<Connection> pool;

    private DatabaseConnectionManager(DatabaseConfig config) {
        this.config = config;
        this.pool = new ArrayBlockingQueue<>(config.getPoolSize());
    }

    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionManager.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionManager(DatabaseConfig.load());
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        Connection pooled = pool.poll();
        if (pooled != null && !pooled.isClosed()) {
            return pooled;
        }
        return DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
    }

    public void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            if (connection.isClosed()) {
                return;
            }
            if (!pool.offer(connection)) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }
}
