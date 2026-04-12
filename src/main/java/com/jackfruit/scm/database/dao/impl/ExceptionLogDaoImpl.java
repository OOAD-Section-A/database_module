package com.jackfruit.scm.database.dao.impl;

import com.jackfruit.scm.database.dao.ExceptionLogDao;
import com.jackfruit.scm.database.model.SubsystemException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class ExceptionLogDaoImpl extends AbstractJdbcDao implements ExceptionLogDao {

    @Override
    public void save(SubsystemException subsystemException) {
        executeUpdate(
                """
                INSERT INTO subsystem_exceptions
                (exception_id, subsystem_name, reference_id, severity, exception_message, status, created_at, resolved_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> bindException(statement, subsystemException));
    }

    @Override
    public void update(SubsystemException subsystemException) {
        executeUpdate(
                """
                UPDATE subsystem_exceptions
                SET status = ?, resolved_at = ?, exception_message = ?
                WHERE exception_id = ?
                """,
                statement -> {
                    statement.setString(1, subsystemException.getStatus());
                    statement.setTimestamp(2, subsystemException.getResolvedAt() == null ? null : Timestamp.valueOf(subsystemException.getResolvedAt()));
                    statement.setString(3, subsystemException.getExceptionMessage());
                    statement.setString(4, subsystemException.getExceptionId());
                });
    }

    @Override
    public Optional<SubsystemException> findById(String exceptionId) {
        return queryForObject(
                "SELECT * FROM subsystem_exceptions WHERE exception_id = ?",
                resultSet -> new SubsystemException(
                        resultSet.getString("exception_id"),
                        resultSet.getString("subsystem_name"),
                        resultSet.getString("reference_id"),
                        resultSet.getString("severity"),
                        resultSet.getString("exception_message"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getTimestamp("resolved_at") == null ? null : resultSet.getTimestamp("resolved_at").toLocalDateTime()),
                statement -> statement.setString(1, exceptionId));
    }

    @Override
    public List<SubsystemException> findAll() {
        return queryForList(
                "SELECT * FROM subsystem_exceptions",
                resultSet -> new SubsystemException(
                        resultSet.getString("exception_id"),
                        resultSet.getString("subsystem_name"),
                        resultSet.getString("reference_id"),
                        resultSet.getString("severity"),
                        resultSet.getString("exception_message"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getTimestamp("resolved_at") == null ? null : resultSet.getTimestamp("resolved_at").toLocalDateTime()));
    }

    private void bindException(PreparedStatement statement, SubsystemException subsystemException) throws SQLException {
        statement.setString(1, subsystemException.getExceptionId());
        statement.setString(2, subsystemException.getSubsystemName());
        statement.setString(3, subsystemException.getReferenceId());
        statement.setString(4, subsystemException.getSeverity());
        statement.setString(5, subsystemException.getExceptionMessage());
        statement.setString(6, subsystemException.getStatus());
        statement.setTimestamp(7, Timestamp.valueOf(subsystemException.getCreatedAt()));
        statement.setTimestamp(8, subsystemException.getResolvedAt() == null ? null : Timestamp.valueOf(subsystemException.getResolvedAt()));
    }
}
