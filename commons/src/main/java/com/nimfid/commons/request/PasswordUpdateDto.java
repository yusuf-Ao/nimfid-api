package com.nimfid.commons.request;

import com.nimfid.commons.validation.constraint.PasswordConstraint;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class PasswordUpdateDto {

    @NotNull
    @NotEmpty
    @PasswordConstraint(message = "Password must contain at least 1 uppercase, 1 lowercase and special character")
    private String          currentPassword;
    @NotNull
    @NotEmpty
    @PasswordConstraint(message = "Password must contain at least 1 uppercase, 1 lowercase and special character")
    private String          newPassword;
}
