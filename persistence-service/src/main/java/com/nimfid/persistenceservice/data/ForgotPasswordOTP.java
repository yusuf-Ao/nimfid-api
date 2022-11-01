package com.nimfid.persistenceservice.data;


import com.nimfid.commons.data.UserStore;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@RequiredArgsConstructor
@SuperBuilder
@Entity
public class ForgotPasswordOTP {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(
            name = "forgotpassword_id_sequence",
            sequenceName = "forgotpassword_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "forgotpassword_id_sequence"
    )
    private Long                    id;
    @Column(nullable = false)
    private String                  otp;

    @Column(nullable = false)
    private ZonedDateTime           createdAt;

    @Column(nullable = false)
    private ZonedDateTime           expiresAt;

    @ManyToOne
    @JoinColumn(nullable = false,
            name = "user_id")
    private UserStore               user;
}
