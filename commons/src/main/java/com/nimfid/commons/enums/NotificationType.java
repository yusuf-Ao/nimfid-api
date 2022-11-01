package com.nimfid.commons.enums;


public enum NotificationType {

    PASSWORD_OTP("PASSWORD_OTP"),
    EMAIL_VERIFICATION("EMAIL_VERIFICATION");

    private final String notificationType;

    NotificationType(final String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationType() {
        return notificationType;
    }
}
