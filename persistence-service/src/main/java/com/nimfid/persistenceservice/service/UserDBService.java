package com.nimfid.persistenceservice.service;

import com.nimfid.commons.data.UserStore;
import com.nimfid.commons.enums.AccountStatus;
import com.nimfid.commons.enums.NotificationType;
import com.nimfid.commons.enums.UserRoles;
import com.nimfid.commons.enums.UserStatus;
import com.nimfid.commons.exception.CustomException;
import com.nimfid.commons.request.*;
import com.nimfid.commons.util.CopyUtils;
import com.nimfid.commons.util.TimeUtil;
import com.nimfid.persistenceservice.data.ForgotPasswordOTP;
import com.nimfid.persistenceservice.data.VerificationCode;
import com.nimfid.persistenceservice.repo.ForgotPasswordRepository;
import com.nimfid.persistenceservice.repo.UserRepository;
import com.nimfid.persistenceservice.repo.VerificationCodeRepository;
import com.nimfid.persistenceservice.util.NotificationUtil;
import com.nimfid.persistenceservice.util.PasswordUtil;
import com.nimfid.persistenceservice.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserDBService {


    @Autowired
    private final UserRepository    userRepository;
    @Autowired
    private final VerificationCodeRepository verificationCodeRepository;
    @Autowired
    private final ForgotPasswordRepository forgotPasswordRepository;
    @Autowired
    private final NotificationUtil  notificationUtil;
    @Autowired
    private final PasswordUtil      passwordUtil;

    private final UUIDUtil          uuidUtil;

    //todo: check if account is restricted before allowing some access like, login, update...
    public void saveUser(final UserStore user) {
        userRepository.save(user);
    }

    public Optional<UserStore> getUserById(final Long id) {
        log.info("Fetching user with id: {}", id);
        return userRepository.findById(id);
    }
    public Optional<UserStore> getUserByUuid(final String uuid) {
        log.info("Fetching user with id: {}", uuid);
        return userRepository.findByUuid(uuid);
    }
    public Optional<UserStore> getUserByEmail(final String email) {
        log.info("Fetching user with email: {}", email);
        return userRepository.findByEmail(email);
    }

    public List<UserStore> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    public boolean userExistsByEmail(final String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<UserStore> getUserByIdAndUuid(final Long id, final String uuid) {
        return userRepository.findByIdAndUuid(id, uuid);
    }

    public UserStore registerUser(UserCreationDto userCreationDto) throws CustomException {
        final List<UserRoles> userRoles = new ArrayList<>();
        final ZonedDateTime zonedDateTime = TimeUtil.getZonedDateTimeOfInstant();
        final String email = userCreationDto.getEmail();
        userRoles.add(UserRoles.MEMBER_USER);
        final Optional<UserStore> userStore = getUserByEmail(email);
        if (userStore.isPresent()) {
            if (userStore.get().getAccountStatus().equals(AccountStatus.UNVERIFIED)) {
                final String message = "Please complete user registration by verifying your email";
                log.error(message);
                throw new CustomException(HttpStatus.NOT_MODIFIED, message);
            }
            final String message = "User with the email: " + email +" already exists.";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }

        UserStore user = UserStore.builder()
                .uuid(uuidUtil.assignUuid())
                .firstName(userCreationDto.getFirstName()).lastName(userCreationDto.getLastName())
                .otherNames(userCreationDto.getOtherNames()).gender(userCreationDto.getGender())
                .dateOfBirth(userCreationDto.getDateOfBirth()).phoneNumber(userCreationDto.getPhoneNumber())
                .email(userCreationDto.getEmail()).password(passwordUtil.passwordEncoder()
                        .encode(userCreationDto.getPassword().trim()))
                .country(userCreationDto.getCountry()).state(userCreationDto.getState())
                .lga(userCreationDto.getLga())
                .city(userCreationDto.getCity()).houseNo(userCreationDto.getHouseNo())
                .streetName(userCreationDto.getStreetName()).dateRegistered(zonedDateTime)
                .lastModified(zonedDateTime).passwordUpdateDate(zonedDateTime)
                .imageUrl("blank").accountStatus(AccountStatus.UNVERIFIED)
                .userStatus(UserStatus.INACTIVE).userRoles(userRoles)
                .build();
        saveUser(user);

        final String verificationCode = generateVerificationCode(user);
        final String mailContent = buildEmailForVerification(verificationCode, user.getLastName());
        sendVerificationCode(user.getEmail(), mailContent);
        return user;
    }

    private void sendVerificationCode(final String email, final String mailContent) {
        UserNotificationRequest newUserNotificationRequest = UserNotificationRequest.builder()
                .recipientEmail(email)
                .mailContent(mailContent)
                .timeOfEvent(TimeUtil.getFormattedDateTimeOfInstant())
                .notificationType(NotificationType.EMAIL_VERIFICATION)
                .build();
        notificationUtil.publishNotificationToQueue(newUserNotificationRequest);
    }

    @Transactional
    private String generateVerificationCode(final UserStore user) {
        final int SHORT_ID_LENGTH = 6;
        final String code =  RandomStringUtils.randomNumeric(SHORT_ID_LENGTH);
        VerificationCode verificationCode = VerificationCode.builder()
                .code(code)
                .createdAt(TimeUtil.getZonedDateTimeOfInstant())
                .expiresAt(TimeUtil.getZonedDateTimeOfInstant().plusMinutes(15L))
                .user(user)
                .build();
        verificationCodeRepository.save(verificationCode);
        return code;
    }

    @Transactional
    private String generateOTPForPassword(final UserStore user) {
        final int SHORT_ID_LENGTH = 5;
        final String otp =  RandomStringUtils.randomNumeric(SHORT_ID_LENGTH);
        ForgotPasswordOTP forgotPasswordOTP = ForgotPasswordOTP.builder()
                .otp(otp)
                .createdAt(TimeUtil.getZonedDateTimeOfInstant())
                .expiresAt(TimeUtil.getZonedDateTimeOfInstant().plusMinutes(5L))
                .user(user)
                .build();
        forgotPasswordRepository.save(forgotPasswordOTP);
        return otp;
    }

    @Transactional
    public void verifyUserEmail(final String email, final String code) throws CustomException {
        Optional<UserStore> userStore = userRepository.findByEmail(email);
        if (userStore.isEmpty()) {
            final String message = "User with the: " + email +" does not exists.";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }

        final Long userId = userStore.get().getId();

        if (userStore.get().getAccountStatus().equals(AccountStatus.VERIFIED)) {
            final String message = "Email has already been verified.. Proceed to login";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }

        Optional<VerificationCode> verificationCode = verificationCodeRepository
                .findByCodeAndUserId(code, userId);

        if (verificationCode.isEmpty()) {
            final String message = "Invalid token";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }

        ZonedDateTime expiresAt = verificationCode.get().getExpiresAt();

        if (expiresAt.isBefore(TimeUtil.getZonedDateTimeOfInstant())) {
            final String message = "Token is no longer valid... Please request for a new token";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }
        userRepository.updateVerificationDetails(AccountStatus.VERIFIED.getAccountStatus(),
                UserStatus.ACTIVE.getUserStatus(), email);
        verificationCodeRepository.deleteByCodeAndUserId(code, userId);
    }

    @Transactional
    public void resendVerificationCode(final String email) throws CustomException {
        Optional<UserStore> userStore = userRepository.findByEmail(email);
        if (userStore.isEmpty()) {
            final String message = "User with the: " + email +" does not exists.";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }
        final Long userId = userStore.get().getId();
        if (userStore.get().getAccountStatus().equals(AccountStatus.VERIFIED)) {
            final String message = "Email has already been verified.. Proceed to login";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }
        Optional<VerificationCode> verificationCode = verificationCodeRepository
                .findByUserId(userId);
        if (verificationCode.isPresent()) {
            verificationCodeRepository.deleteByUserId(userId);
        }
        final String newCode = generateVerificationCode(userStore.get());
        final String mailContent = buildEmailForVerification(newCode, userStore.get().getLastName());
        sendVerificationCode(email, mailContent);

    }


    /*public void deleteUserAccount(final UserDetails userDetailDto) throws CustomException {
        log.info("Deleting User account");
        final Long id = userDetailDto.getId();
        final String uuid = userDetailDto.getUuid();
        if (getUserByIdAndUuid(id, uuid).isEmpty()) {
            final String message = "User with id: " + id + " does not exist";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }
        userRepository.deleteById(id);
    }*/
    public UserStore updateUserInfo(final UserUpdateDto userUpdateDto, final UserDetails userDetail) throws CustomException {
        log.info("Updating user");
        final Long id = userDetail.getId();
        final String uuid = userDetail.getUuid();
        Optional<UserStore> oldUserModel = getUserByIdAndUuid(id, uuid);
        if (oldUserModel.isEmpty()) {
            final String message = "User does not exists in the database.";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }
        UserStore updatedUserModel = oldUserModel.get();
        CopyUtils.copyProperties(userUpdateDto,updatedUserModel);
        updatedUserModel.setLastModified(TimeUtil.getZonedDateTimeOfInstant());
        saveUser(updatedUserModel);
        log.info("User updated successfully: {}", id);
        return updatedUserModel;
    }
    @Transactional
    public void updateUserPassword(final PasswordUpdateDto passwordUpdateDto, final UserDetails userDetail) throws CustomException {
        log.info("Updating user password");
        final Long id = userDetail.getId();
        final String uuid = userDetail.getUuid();
        final String email = userDetail.getEmail();
        Optional<UserStore> oldUserModel = getUserByIdAndUuid(id, uuid);
        if (oldUserModel.isEmpty()) {
            final String message = "User does not exists in the database.";
            log.error(message);
            throw new CustomException(HttpStatus.EXPECTATION_FAILED, message);
        }
        final String userPassword = oldUserModel.get().getPassword();
        final String givenPassword = passwordUpdateDto.getCurrentPassword().trim();
        final boolean isValidCurrentPassword = passwordUtil.passwordEncoder()
                .matches(givenPassword, userPassword);
        if (!isValidCurrentPassword) {
            final String message = "Current password is not valid";
            log.error(message);
            throw new CustomException(HttpStatus.EXPECTATION_FAILED, message);
        }
        final String newPassword = passwordUpdateDto.getNewPassword().trim();
        final String encodedPassword = passwordUtil.passwordEncoder().encode(newPassword);
        ZonedDateTime passwordUpdateDate = TimeUtil.getZonedDateTimeOfInstant();
        userRepository.updatePassword(email, encodedPassword, passwordUpdateDate);
        log.info("User password updated successfully: {}", id);
    }

    public UserStore getUserProfile(final UserDetails userDetails) throws CustomException {
        log.info("Getting user profile method");
        final Long id = userDetails.getId();
        final String uuid = userDetails.getUuid();
        Optional<UserStore> userModel = getUserByIdAndUuid(id, uuid);
        if (userModel.isEmpty()) {
            final String message = "User does not exists in the database.";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_FOUND, message);
        }
        log.info("User Profile fetched successfully");
        return userModel.get();
    }

    public void findAccountAndSendOTPForPasswordReset(final String email) throws CustomException {
        Optional<UserStore> userStore = userRepository.findByEmail(email);
        if (userStore.isEmpty()) {
            final String message = "User with the: " + email +" does not exists.";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }
        final Long userId = userStore.get().getId();
        Optional<ForgotPasswordOTP> forgotPasswordOTP = forgotPasswordRepository
                .findByUserId(userId);
        if (forgotPasswordOTP.isPresent()) {
            forgotPasswordRepository.deleteByUserId(userId);
        }
        final String otp = generateOTPForPassword(userStore.get());
        final String mailContent = buildEmailForPasswordReset(otp, userStore.get().getLastName());
        UserNotificationRequest newUserNotificationRequest = UserNotificationRequest.builder()
                .recipientEmail(email)
                .mailContent(mailContent)
                .timeOfEvent(TimeUtil.getFormattedDateTimeOfInstant())
                .notificationType(NotificationType.PASSWORD_OTP)
                .build();
        notificationUtil.publishNotificationToQueue(newUserNotificationRequest);
    }

    @Transactional
    public void resetPasswordWithOTP(final ForgotPasswordRequest forgotPasswordRequest, final String email) throws CustomException {
        Optional<UserStore> userStore = userRepository.findByEmail(email);
        if (userStore.isEmpty()) {
            final String message = "User with the: " + email +" does not exists.";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }
        final Long userId = userStore.get().getId();
        final String otp = forgotPasswordRequest.getOtp();
        Optional<ForgotPasswordOTP> forgotPasswordOTP = forgotPasswordRepository
                .findByOtpAndUserId(otp, userId);

        if (forgotPasswordOTP.isEmpty()) {
            final String message = "Invalid OTP";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }

        ZonedDateTime expiresAt = forgotPasswordOTP.get().getExpiresAt();

        if (expiresAt.isBefore(TimeUtil.getZonedDateTimeOfInstant())) {
            forgotPasswordRepository.deleteByOtpAndUserId(forgotPasswordOTP.get().getOtp(),
                    userId);
            final String message = "OTP is no longer valid... Please request for a new OTP";
            log.error(message);
            throw new CustomException(HttpStatus.NOT_MODIFIED, message);
        }

        final String password = forgotPasswordRequest.getPassword().trim();
        final String encodedPassword = passwordUtil.passwordEncoder().encode(password);
        ZonedDateTime passwordUpdateTime = TimeUtil.getZonedDateTimeOfInstant();
        userRepository.updatePassword(email, encodedPassword, passwordUpdateTime);
        forgotPasswordRepository.deleteByOtpAndUserId(otp, userId);
    }

    private String buildEmailForVerification(final String code, final String name) {
        return "<div width=\"100%\" style=\"margin:0;background-color:#f0f2f3\">\n" +
                "\n" +
                "<div style=\"margin:auto;max-width:600px;padding-top:50px\" class=\"m_-8396911486253730526email-container\">\n" +
                "    \n" +
                "    \n" +
                "    \n" +
                "    <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" align=\"center\" id=\"m_-8396911486253730526logoContainer\" style=\"background:#252f3d;border-radius:3px 3px 0 0;max-width:600px\">\n" +
                "        <tbody><tr>\n" +
                "            <td style=\"background:#F4F4F4;border-radius:3px 3px 0 0;padding:20px 0 10px 0;text-align:center\">\n" +
                "                <img src=\"src/main/resources/static/NiMFID.png\" width=\"160\" height=\"45\" alt=\"NiMFID\" border=\"0\" style=\"font-family:sans-serif;font-size:15px;line-height:140%;color:#555555\" class=\"CToWUd\" data-bit=\"iit\">\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </tbody></table>\n" +
                "    \n" +
                "    \n" +
                "    <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" align=\"center\" id=\"m_-8396911486253730526emailBodyContainer\" style=\"border:0px;border-bottom:1px solid #d6d6d6;max-width:600px\">\n" +
                "        <tbody><tr>\n" +
                "            <td style=\"background-color:#fff;color:#444;font-family:'Amazon Ember','Helvetica Neue',Roboto,Arial,sans-serif;font-size:14px;line-height:140%;padding:25px 35px\">\n" +
                "                <h3 style=\"font-size:16px;font-weight:normal ;line-height:1.3;margin:0 0 15px 0\">Hi "+name+",</h3>\n" +
                "                <h1 style=\"font-size:20px;font-weight:bold;line-height:1.3;margin:0 0 15px 0\">Verify your email address</h1>\n" +
                "                <p style=\"margin:0;padding:0\">Thanks for starting the new NiMFID account creation process. We want to make sure it's really you. Please enter the following verification code when prompted. We hope to see you onboard!..</p>\n" +
                "                <p style=\"margin:0;padding:0\"></p>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td style=\"background-color:#fff;color:#444;font-family:'Amazon Ember','Helvetica Neue',Roboto,Arial,sans-serif;font-size:14px;line-height:140%;padding:25px 35px;padding-top:0;text-align:center\">\n" +
                "                <div style=\"font-weight:bold;padding-bottom:15px\">Verification code</div>\n" +
                "                <div style=\"color:#000;font-size:36px;font-weight:bold;padding-bottom:15px\">"+code+"</div>\n" +
                "                <div>(This code is valid for 15 minutes)</div>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    \n" +
                "    </tbody></table>\n" +
                "    \n" +
                "    \n" +
                "    <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" align=\"center\" id=\"m_-8396911486253730526footer\" style=\"max-width:600px\">\n" +
                "        <tbody><tr>\n" +
                "            <td style=\"color:#777;font-family:'Amazon Ember','Helvetica Neue',Roboto,Arial,sans-serif;font-size:12px;line-height:16px;padding:20px 30px;text-align:center\">\n" +
                "                This message was produced and distributed by NiMFID © 2022.All rights reserved. NiMFID is a registered trademark of <a href=\"#\" data-saferedirecturl=\"#\">nimfid.org</a>. View our <a href=\"#\"  data-saferedirecturl=\"#\">privacy policy</a>.\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </tbody></table>\n" +
                "    \n" +
                "    \n" +
                "</div>\n" +
                "\n" +
                "<img alt=\"\" src=\"https://ci6.googleusercontent.com/proxy/P21Cf0BPsMvipsGz71yLseGSngiRtp_sB65cQphtSLQyRtM4PX64eUfPDTCpRQlto-6dhQRc1ZALTnLkocoQso0q-1dM0yTfylqvsD3adN6kYjpFkBXpY2WmfnuNX_uU0ILLTHEsMFoD65v92JVZT5Tr5MtocEki_7MNLYg3NhwHHsSU0WYGUeTBaFFF158KGdhDOptWirBwu4y9=s0-d-e1-ft#https://bjdxkhre.r.us-east-1.awstrack.me/I0/01000183f4056114-367763b5-887d-4eeb-ad1d-efe90005d7d3-000000/5YjEJ7yp9wXA1e9Xk86oiNVJ-84=292\" style=\"display:none;width:1px;height:1px\" class=\"CToWUd\" data-bit=\"iit\"><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "</div></div>";
    }

    private String buildEmailForPasswordReset(final String code, final String name) {
        return "<div width=\"100%\" style=\"margin:0;background-color:#f0f2f3\">\n" +
                "\n" +
                "<div style=\"margin:auto;max-width:600px;padding-top:50px\" class=\"m_-8396911486253730526email-container\">\n" +
                "    \n" +
                "    \n" +
                "    \n" +
                "    <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" align=\"center\" id=\"m_-8396911486253730526logoContainer\" style=\"background:#252f3d;border-radius:3px 3px 0 0;max-width:600px\">\n" +
                "        <tbody><tr>\n" +
                "            <td style=\"background:#F4F4F4;border-radius:3px 3px 0 0;padding:20px 0 10px 0;text-align:center\">\n" +
                "                <img src=\"src/main/resources/static/NiMFID.png\" width=\"160\" height=\"45\" alt=\"NiMFID\" border=\"0\" style=\"font-family:sans-serif;font-size:15px;line-height:140%;color:#555555\" class=\"CToWUd\" data-bit=\"iit\">\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </tbody></table>\n" +
                "    \n" +
                "    \n" +
                "    <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" align=\"center\" id=\"m_-8396911486253730526emailBodyContainer\" style=\"border:0px;border-bottom:1px solid #d6d6d6;max-width:600px\">\n" +
                "        <tbody><tr>\n" +
                "            <td style=\"background-color:#fff;color:#444;font-family:'Amazon Ember','Helvetica Neue',Roboto,Arial,sans-serif;font-size:14px;line-height:140%;padding:25px 35px\">\n" +
                "                <h3 style=\"font-size:16px;font-weight:normal ;line-height:1.3;margin:0 0 15px 0\">Hi "+name+",</h3>\n" +
                "                <h1 style=\"font-size:20px;font-weight:bold;line-height:1.3;margin:0 0 15px 0\">Password Reset</h1>\n" +
                "                <p style=\"margin:0;padding:0\">Someone is trying to reset your password. If it is you, please enter the following verification code when prompted..</p>\n" +
                "                <p style=\"margin:0;padding:0\"></p>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td style=\"background-color:#fff;color:#444;font-family:'Amazon Ember','Helvetica Neue',Roboto,Arial,sans-serif;font-size:14px;line-height:140%;padding:25px 35px;padding-top:0;text-align:center\">\n" +
                "                <div style=\"font-weight:bold;padding-bottom:15px\">Verification code</div>\n" +
                "                <div style=\"color:#000;font-size:36px;font-weight:bold;padding-bottom:15px\">"+code+"</div>\n" +
                "                <div>(This code is valid for 5 minutes)</div>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    \n" +
                "    </tbody></table>\n" +
                "    \n" +
                "    \n" +
                "    <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" align=\"center\" id=\"m_-8396911486253730526footer\" style=\"max-width:600px\">\n" +
                "        <tbody><tr>\n" +
                "            <td style=\"color:#777;font-family:'Amazon Ember','Helvetica Neue',Roboto,Arial,sans-serif;font-size:12px;line-height:16px;padding:20px 30px;text-align:center\">\n" +
                "                This message was produced and distributed by NiMFID © 2022.All rights reserved. NiMFID is a registered trademark of <a href=\"#\" data-saferedirecturl=\"#\">nimfid.org</a>. View our <a href=\"#\"  data-saferedirecturl=\"#\">privacy policy</a>.\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </tbody></table>\n" +
                "    \n" +
                "    \n" +
                "</div>\n" +
                "\n" +
                "<img alt=\"\" src=\"https://ci6.googleusercontent.com/proxy/P21Cf0BPsMvipsGz71yLseGSngiRtp_sB65cQphtSLQyRtM4PX64eUfPDTCpRQlto-6dhQRc1ZALTnLkocoQso0q-1dM0yTfylqvsD3adN6kYjpFkBXpY2WmfnuNX_uU0ILLTHEsMFoD65v92JVZT5Tr5MtocEki_7MNLYg3NhwHHsSU0WYGUeTBaFFF158KGdhDOptWirBwu4y9=s0-d-e1-ft#https://bjdxkhre.r.us-east-1.awstrack.me/I0/01000183f4056114-367763b5-887d-4eeb-ad1d-efe90005d7d3-000000/5YjEJ7yp9wXA1e9Xk86oiNVJ-84=292\" style=\"display:none;width:1px;height:1px\" class=\"CToWUd\" data-bit=\"iit\"><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "</div></div>";
    }
}
