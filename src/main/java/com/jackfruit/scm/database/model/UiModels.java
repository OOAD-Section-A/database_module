package com.jackfruit.scm.database.model;

import java.time.LocalDateTime;

public final class UiModels {

    private UiModels() {
    }

    public record UiUser(Integer userId, String username, String passwordHash, String userRole,
                         boolean accountLocked, int loginAttemptCount, LocalDateTime lastLoginTimestamp,
                         String userEmail, String displayName, String themePreference,
                         String languagePreference, LocalDateTime createdAt) {
    }

    public record UiSession(Integer sessionId, Integer userId, String jwtSessionToken,
                            String redirectPanelUrl, long sessionExpiryTime,
                            String sessionStatus, LocalDateTime createdAt) {
    }

    public record UiNotification(Integer notificationId, Integer userId, String notificationType,
                                 String notificationMessage, boolean read, LocalDateTime createdAt) {
    }

    public record UiAuditLog(Integer auditId, LocalDateTime auditTimestamp, String auditActionUser,
                             String auditActionDescription, String auditModuleName) {
    }
}
