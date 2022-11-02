package com.nimfid.commons.request;

import com.nimfid.commons.enums.Gender;
import com.nimfid.commons.validation.constraint.NameConstraint;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Data
@SuperBuilder
@RequiredArgsConstructor
public class UserUpdateDto {
    @NotEmpty(message = "Please provide firstname")
    @NameConstraint
    private String                      firstName;
    @NotNull
    @NotEmpty(message = "Please provide lastname/surname")
    @NameConstraint
    private String                      lastName;
    private String                      otherNames;
    private Gender                      gender;
    @NotNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date                        dateOfBirth;
    private Long                        phoneNumber;
    private String                      country;
    private String                      state;
    private String                      lga;
    private String                      city;
    private String                      houseNo;
    private String                      streetName;
    private String                      imageUrl;

    //Todo; Restrict update of some fields after integration with NIN api
}
