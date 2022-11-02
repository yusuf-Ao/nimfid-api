package com.nimfid.modelservice.data;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
@SuperBuilder
@Entity
public class ContactDetails {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(
            name = "contactdetails_id_sequence",
            sequenceName = "contactdetails_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "contactdetails_id_sequence"
    )
    private Long                    id;
    @NotEmpty
    @Column(columnDefinition = "text", nullable = false)
    private String                  officeAddress;
    @Column(columnDefinition = "text")
    private String                  village_ward;
    @NotEmpty
    @NotNull
    private String                  state;
    @NotEmpty
    @NotNull
    private String                  lga;
    private String                  city;
    @NotEmpty
    @NotNull
    private String                  officialEmail;
    @NotNull
    private Long                    officePhone;
    private String                  website;
}
