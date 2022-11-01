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
public class VerificationCode {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(
            name = "verification_id_sequence",
            sequenceName = "verification_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "verification_id_sequence"
    )
    private Long                    id;
    @Column(nullable = false)
    private String                  code;

    @Column(nullable = false)
    private ZonedDateTime           createdAt;

    @Column(nullable = false)
    private ZonedDateTime           expiresAt;

    @ManyToOne
    @JoinColumn(nullable = false,
            name = "user_id")
    private UserStore               user;
}
