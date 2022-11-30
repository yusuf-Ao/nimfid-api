package com.nimfid.commons.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nimfid.commons.enums.AccountStatus;
import com.nimfid.commons.enums.Gender;
import com.nimfid.commons.enums.UserRoles;
import com.nimfid.commons.enums.UserStatus;
import com.nimfid.commons.validation.constraint.EmailConstraint;
import com.nimfid.commons.validation.constraint.NameConstraint;
import com.nimfid.commons.validation.constraint.PasswordConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


@Data
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties("password")
public class UserStore {

    @Id
    @SequenceGenerator(
            name = "user_id_sequence",
            sequenceName = "user_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_id_sequence"
    )
    private Long                        id;
    @NotEmpty
    @Column(nullable = false, unique = true)
    private String                      uuid;
    @NotNull
    @NotEmpty(message = "Please provide firstname")
    @NameConstraint
    private String                      firstName;
    @NotNull
    @NotEmpty(message = "Please provide lastname/surname")
    @NameConstraint
    private String                      lastName;
    private String                      otherNames;
    @Enumerated(EnumType.STRING)
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
    private ZonedDateTime               dateRegistered;
    private ZonedDateTime               lastModified;
    private ZonedDateTime               passwordUpdateDate;
    private String                      imageUrl;
    @Enumerated(EnumType.STRING)
    private AccountStatus               accountStatus;
    @Enumerated(EnumType.STRING)
    private UserStatus                  userStatus;
    @ElementCollection(fetch = FetchType.EAGER)
    private Collection<UserRoles>       userRoles = new ArrayList<>();

    //todo: Refactor by seggregating into chunks of tables and using joins
    //todo: add identification

}
