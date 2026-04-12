package com.jackfruit.scm.database.facade.subsystem;

import com.jackfruit.scm.database.model.UiModels.UiNotification;
import com.jackfruit.scm.database.model.UiModels.UiUser;
import com.jackfruit.scm.database.service.JdbcOperations;
import java.sql.Timestamp;
import java.util.List;

public class UiSubsystemFacade {

    private final JdbcOperations jdbcOperations;

    public UiSubsystemFacade(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void createUser(UiUser user) {
        jdbcOperations.update(
                """
                INSERT INTO ui_users
                (username, password_hash, user_role, is_account_locked, login_attempt_count, last_login_timestamp,
                 user_email, user_display_name, theme_preference, language_preference, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> {
                    statement.setString(1, user.username());
                    statement.setString(2, user.passwordHash());
                    statement.setString(3, user.userRole());
                    statement.setBoolean(4, user.accountLocked());
                    statement.setInt(5, user.loginAttemptCount());
                    statement.setTimestamp(6, user.lastLoginTimestamp() == null ? null : Timestamp.valueOf(user.lastLoginTimestamp()));
                    statement.setString(7, user.userEmail());
                    statement.setString(8, user.displayName());
                    statement.setString(9, user.themePreference());
                    statement.setString(10, user.languagePreference());
                    statement.setTimestamp(11, Timestamp.valueOf(user.createdAt()));
                });
    }

    public List<UiUser> listUsers() {
        return jdbcOperations.query(
                "SELECT * FROM ui_users",
                resultSet -> new UiUser(
                        resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password_hash"),
                        resultSet.getString("user_role"),
                        resultSet.getBoolean("is_account_locked"),
                        resultSet.getInt("login_attempt_count"),
                        resultSet.getTimestamp("last_login_timestamp") == null ? null : resultSet.getTimestamp("last_login_timestamp").toLocalDateTime(),
                        resultSet.getString("user_email"),
                        resultSet.getString("user_display_name"),
                        resultSet.getString("theme_preference"),
                        resultSet.getString("language_preference"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()));
    }

    public void createNotification(UiNotification notification) {
        jdbcOperations.update(
                "INSERT INTO ui_notifications (user_id, notification_type, notification_message, is_read, created_at) VALUES (?, ?, ?, ?, ?)",
                statement -> {
                    statement.setInt(1, notification.userId());
                    statement.setString(2, notification.notificationType());
                    statement.setString(3, notification.notificationMessage());
                    statement.setBoolean(4, notification.read());
                    statement.setTimestamp(5, Timestamp.valueOf(notification.createdAt()));
                });
    }

    public List<UiNotification> listNotifications() {
        return jdbcOperations.query(
                "SELECT * FROM ui_notifications",
                resultSet -> new UiNotification(
                        resultSet.getInt("notification_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("notification_type"),
                        resultSet.getString("notification_message"),
                        resultSet.getBoolean("is_read"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()));
    }
}
