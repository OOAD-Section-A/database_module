package com.jackfruit.scm.database.model;

import java.time.LocalDateTime;

public class SubsystemException {

    private String exceptionId;
    private String exceptionName;
    private String subsystemName;
    private String severity;
    private LocalDateTime timestampUtc;
    private Long durationMs;
    private String exceptionMessage;
    private Long errorCode;
    private String stackTrace;
    private String innerException;
    private String userAccount;
    private String handlingPlan;
    private Short retryCount;
    private String status;
    private LocalDateTime resolvedAt;

    public SubsystemException() {
    }

    public SubsystemException(String exceptionId, String exceptionName, String subsystemName,
                              String severity, LocalDateTime timestampUtc, Long durationMs,
                              String exceptionMessage, Long errorCode, String stackTrace,
                              String innerException, String userAccount, String handlingPlan,
                              Short retryCount, String status, LocalDateTime resolvedAt) {
        this.exceptionId = exceptionId;
        this.exceptionName = exceptionName;
        this.subsystemName = subsystemName;
        this.severity = severity;
        this.timestampUtc = timestampUtc;
        this.durationMs = durationMs;
        this.exceptionMessage = exceptionMessage;
        this.errorCode = errorCode;
        this.stackTrace = stackTrace;
        this.innerException = innerException;
        this.userAccount = userAccount;
        this.handlingPlan = handlingPlan;
        this.retryCount = retryCount;
        this.status = status;
        this.resolvedAt = resolvedAt;
    }

    public String getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(String exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public String getSubsystemName() {
        return subsystemName;
    }

    public void setSubsystemName(String subsystemName) {
        this.subsystemName = subsystemName;
    }

    public LocalDateTime getTimestampUtc() {
        return timestampUtc;
    }

    public void setTimestampUtc(LocalDateTime timestampUtc) {
        this.timestampUtc = timestampUtc;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public Long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getInnerException() {
        return innerException;
    }

    public void setInnerException(String innerException) {
        this.innerException = innerException;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getHandlingPlan() {
        return handlingPlan;
    }

    public void setHandlingPlan(String handlingPlan) {
        this.handlingPlan = handlingPlan;
    }

    public Short getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Short retryCount) {
        this.retryCount = retryCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return timestampUtc;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.timestampUtc = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
