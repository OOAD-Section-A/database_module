package com.jackfruit.scm.database.model;

import java.time.LocalDateTime;

public class SubsystemException {

    private String exceptionId;
    private String subsystemName;
    private String referenceId;
    private String severity;
    private String exceptionMessage;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public SubsystemException() {
    }

    public SubsystemException(String exceptionId, String subsystemName, String referenceId, String severity,
                              String exceptionMessage, String status, LocalDateTime createdAt,
                              LocalDateTime resolvedAt) {
        this.exceptionId = exceptionId;
        this.subsystemName = subsystemName;
        this.referenceId = referenceId;
        this.severity = severity;
        this.exceptionMessage = exceptionMessage;
        this.status = status;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    public String getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(String exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getSubsystemName() {
        return subsystemName;
    }

    public void setSubsystemName(String subsystemName) {
        this.subsystemName = subsystemName;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
