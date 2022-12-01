package com.nimfid.commons.request;

import com.nimfid.commons.validation.constraint.EmailConstraint;
import com.nimfid.commons.validation.constraint.PasswordConstraint;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
@SuperBuilder
@RequiredArgsConstructor
public class LoginRequest {

    @NotNull
    @NotEmpty(message = "Username cannot be empty")
    private String username;
    @NotNull
    @NotEmpty
    @PasswordConstraint(message = "Password must contain at least 1 uppercase, 1 lowercase and special character")
    private String password;
}
