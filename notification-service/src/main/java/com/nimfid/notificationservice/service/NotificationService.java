package com.nimfid.notificationservice.service;


import com.nimfid.commons.enums.NotificationType;
import com.nimfid.commons.request.UserNotificationRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailConfiguration emailConfiguration;
    @Autowired
    private JavaMailSender mailSender;
    public void send(final UserNotificationRequest userNotificationRequest) {

        final NotificationType notificationType = userNotificationRequest.getNotificationType();
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            final String recipient = userNotificationRequest.getRecipientEmail();
            helper.setText(userNotificationRequest.getMailContent(), true);
            helper.setTo(recipient);
            helper.setFrom(emailConfiguration.getUsername());

            if (notificationType.equals(NotificationType.EMAIL_VERIFICATION)) {
                helper.setSubject("NiMFID Email Verification");
            }
            if (notificationType.equals(NotificationType.PASSWORD_OTP)) {
                helper.setSubject("NiMFID Password Reset Code");
            }
            mailSender.send(mimeMessage);
        } catch (final MessagingException e) {
            log.error("Error occurred while sending "+notificationType+" to "+userNotificationRequest.getRecipientEmail());
        }

    }

}
