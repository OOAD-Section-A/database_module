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
                (exception_id, exception_name, subsystem_name, severity, timestamp_utc, duration_ms,
                 exception_message, error_code, stack_trace, inner_exception, user_account,
                 handling_plan, retry_count, status, resolved_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                statement -> bindException(statement, subsystemException));
    }

    @Override
    public void update(SubsystemException subsystemException) {
        executeUpdate(
                """
                UPDATE subsystem_exceptions
                SET exception_name = ?, severity = ?, timestamp_utc = ?, duration_ms = ?, exception_message = ?,
                    error_code = ?, stack_trace = ?, inner_exception = ?, user_account = ?, handling_plan = ?,
                    retry_count = ?, status = ?, resolved_at = ?
                WHERE exception_id = ?
                """,
                statement -> {
                    statement.setString(1, subsystemException.getExceptionName());
                    statement.setString(2, subsystemException.getSeverity());
                    statement.setTimestamp(3, Timestamp.valueOf(subsystemException.getTimestampUtc()));
                    if (subsystemException.getDurationMs() == null) {
                        statement.setNull(4, java.sql.Types.BIGINT);
                    } else {
                        statement.setLong(4, subsystemException.getDurationMs());
                    }
                    statement.setString(5, subsystemException.getExceptionMessage());
                    if (subsystemException.getErrorCode() == null) {
                        statement.setNull(6, java.sql.Types.BIGINT);
                    } else {
                        statement.setLong(6, subsystemException.getErrorCode());
                    }
                    statement.setString(7, subsystemException.getStackTrace());
                    statement.setString(8, subsystemException.getInnerException());
                    statement.setString(9, subsystemException.getUserAccount());
                    statement.setString(10, subsystemException.getHandlingPlan());
                    if (subsystemException.getRetryCount() == null) {
                        statement.setNull(11, java.sql.Types.TINYINT);
                    } else {
                        statement.setShort(11, subsystemException.getRetryCount());
                    }
                    statement.setString(12, subsystemException.getStatus());
                    statement.setTimestamp(13, subsystemException.getResolvedAt() == null ? null : Timestamp.valueOf(subsystemException.getResolvedAt()));
                    statement.setString(14, subsystemException.getExceptionId());
                });
    }

    @Override
    public Optional<SubsystemException> findById(String exceptionId) {
        return queryForObject(
                "SELECT * FROM subsystem_exceptions WHERE exception_id = ?",
                resultSet -> new SubsystemException(
                        resultSet.getString("exception_id"),
                        resultSet.getString("exception_name"),
                        resultSet.getString("subsystem_name"),
                        resultSet.getString("severity"),
                        resultSet.getTimestamp("timestamp_utc").toLocalDateTime(),
                        (Long) resultSet.getObject("duration_ms"),
                        resultSet.getString("exception_message"),
                        (Long) resultSet.getObject("error_code"),
                        resultSet.getString("stack_trace"),
                        resultSet.getString("inner_exception"),
                        resultSet.getString("user_account"),
                        resultSet.getString("handling_plan"),
                        resultSet.getObject("retry_count") == null ? null : resultSet.getShort("retry_count"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("resolved_at") == null ? null : resultSet.getTimestamp("resolved_at").toLocalDateTime()),
                statement -> statement.setString(1, exceptionId));
    }

    @Override
    public List<SubsystemException> findAll() {
        return queryForList(
                "SELECT * FROM subsystem_exceptions",
                resultSet -> new SubsystemException(
                        resultSet.getString("exception_id"),
                        resultSet.getString("exception_name"),
                        resultSet.getString("subsystem_name"),
                        resultSet.getString("severity"),
                        resultSet.getTimestamp("timestamp_utc").toLocalDateTime(),
                        (Long) resultSet.getObject("duration_ms"),
                        resultSet.getString("exception_message"),
                        (Long) resultSet.getObject("error_code"),
                        resultSet.getString("stack_trace"),
                        resultSet.getString("inner_exception"),
                        resultSet.getString("user_account"),
                        resultSet.getString("handling_plan"),
                        resultSet.getObject("retry_count") == null ? null : resultSet.getShort("retry_count"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("resolved_at") == null ? null : resultSet.getTimestamp("resolved_at").toLocalDateTime()));
    }

    private void bindException(PreparedStatement statement, SubsystemException subsystemException) throws SQLException {
        statement.setString(1, subsystemException.getExceptionId());
        statement.setString(2, subsystemException.getExceptionName());
        statement.setString(3, subsystemException.getSubsystemName());
        statement.setString(4, subsystemException.getSeverity());
        statement.setTimestamp(5, Timestamp.valueOf(subsystemException.getTimestampUtc()));
        if (subsystemException.getDurationMs() == null) {
            statement.setNull(6, java.sql.Types.BIGINT);
        } else {
            statement.setLong(6, subsystemException.getDurationMs());
        }
        statement.setString(7, subsystemException.getExceptionMessage());
        if (subsystemException.getErrorCode() == null) {
            statement.setNull(8, java.sql.Types.BIGINT);
        } else {
            statement.setLong(8, subsystemException.getErrorCode());
        }
        statement.setString(9, subsystemException.getStackTrace());
        statement.setString(10, subsystemException.getInnerException());
        statement.setString(11, subsystemException.getUserAccount());
        statement.setString(12, subsystemException.getHandlingPlan());
        if (subsystemException.getRetryCount() == null) {
            statement.setNull(13, java.sql.Types.TINYINT);
        } else {
            statement.setShort(13, subsystemException.getRetryCount());
        }
        statement.setString(14, subsystemException.getStatus());
        statement.setTimestamp(15, subsystemException.getResolvedAt() == null ? null : Timestamp.valueOf(subsystemException.getResolvedAt()));
    }
}
