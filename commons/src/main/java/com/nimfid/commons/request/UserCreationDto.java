package com.nimfid.commons.request;

import com.nimfid.commons.enums.Gender;
import com.nimfid.commons.validation.constraint.EmailConstraint;
import com.nimfid.commons.validation.constraint.NameConstraint;
import com.nimfid.commons.validation.constraint.PasswordConstraint;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Data
@SuperBuilder
@RequiredArgsConstructor
public class UserCreationDto {

    @NotNull
    @NotEmpty(message = "Please provide firstname")
    @NameConstraint
    private String                      firstName;
    @NotNull
    @NotEmpty(message = "Please provide lastname/surname")
    @NameConstraint
    private String                      lastName;
    private String                      otherNames;
    @NotNull
    private Gender                      gender;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private Date                        dateOfBirth;
    @NotNull
    private Long                        phoneNumber;
    @Column(unique = true)
    @NotNull
    @NotEmpty(message = "email cannot be empty")
    @EmailConstraint(message = "Please enter a valid email")
    private String                      email;
    @NotNull
    @NotEmpty
    @PasswordConstraint(message = "Password must contain at least 1 uppercase, 1 lowercase and special character")
    private String                      password;
    @NotNull
    @NotEmpty
    private String                      country;
    @NotNull
    @NotEmpty
    private String                      state;
    @NotNull
    @NotEmpty
    private String                      lga;
    @NotNull
    @NotEmpty
    private String                      city;
    @NotNull
    @NotEmpty
    private String                      houseNo;
    @NotNull
    @NotEmpty
    private String                      streetName;
}
