package com.nimfid.commons.request;


import com.nimfid.commons.enums.NotificationType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@RequiredArgsConstructor
@Data
public class UserNotificationRequest {

    private String              recipientEmail;
    private String              mailContent;
    private String              timeOfEvent;
    private NotificationType    notificationType;
}
