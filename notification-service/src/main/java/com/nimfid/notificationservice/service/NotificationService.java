package com.nimfid.notificationservice.service;


import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.nimfid.commons.enums.NotificationType;
import com.nimfid.commons.request.UserNotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationService {
    public static final String EMAIL_WEBHOOK = "https://discord.com/api/webhooks/1036643044633616424/" +
            "-oFYoHIFvrSq2RBDtSYJVmjVS_aOjQW7Qo4JznbgbwfPs0gdPLcuTK34oYcfePgCJ4SF";

    private static final String FORGOT_PSWD_WEBHOOK = "https://discord.com/api/webhooks/1036642100462227457/" +
            "LQnRq9JZUxT43uIiaEABw_yH2Xf7OPMKWlvrw1WdpjB5PZ_BGlLQT7nB5e2n5O0-LJWk";
    public static void send(final UserNotificationRequest userNotificationRequest) {
        System.out.println("......................Itworkeditworkedw./.................");

        NotificationType notificationType = userNotificationRequest.getNotificationType();
        WebhookClientBuilder builder = null;
        WebhookEmbed.EmbedTitle title = null;
        WebhookEmbed.EmbedField field1 = null;
        WebhookEmbed.EmbedField field2 = null;
        WebhookEmbed.EmbedField field3 = null;
        WebhookEmbed.EmbedField field4 = null;

        switch (notificationType) {
            case EMAIL_VERIFICATION:
                builder = new WebhookClientBuilder(EMAIL_WEBHOOK);
                title = new WebhookEmbed.EmbedTitle("NIMFID Email Verification Code", null);
                field1 = new WebhookEmbed.EmbedField(true, "RECIPIENT",
                        userNotificationRequest.getRecipientEmail());
                field2 = new WebhookEmbed.EmbedField(true, "VERIFICATION CODE",
                        userNotificationRequest.getVerificationCode());
                field3 = new WebhookEmbed.EmbedField(true, "TIME OF EVENT",
                        userNotificationRequest.getTimeOfEvent());
                field4 = new WebhookEmbed.EmbedField(true, "VALIDITY",
                        "This code will be valid for 15MINs Only!.");
                break;
            case PASSWORD_OTP:
                builder = new WebhookClientBuilder(FORGOT_PSWD_WEBHOOK);
                title = new WebhookEmbed.EmbedTitle("NIMFID FORGOT PASSWORD OTP ", null);
                field1 = new WebhookEmbed.EmbedField(true, "RECIPIENT",
                        userNotificationRequest.getRecipientEmail());
                field2 = new WebhookEmbed.EmbedField(true, "OTP",
                        userNotificationRequest.getVerificationCode());
                field3 = new WebhookEmbed.EmbedField(true, "TIME OF EVENT",
                        userNotificationRequest.getTimeOfEvent());
                field4 = new WebhookEmbed.EmbedField(true, "VALIDITY",
                        "This code will be valid for 5MINS Only!.");
                break;
            default:
                break;
        }

        WebhookClient client = builder.build();
        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("Hello");
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);
        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setTitle(title)
                .addField(field1)
                .addField(field2)
                .addField(field3)
                .addField(field4)
                .setColor(2817936)
                .build();
        client.send(embed);
    }

}
