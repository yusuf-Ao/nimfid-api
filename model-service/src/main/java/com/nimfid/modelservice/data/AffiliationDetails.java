package com.nimfid.modelservice.data;


import com.nimfid.commons.enums.AffiliationCategory;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Data
@RequiredArgsConstructor
@SuperBuilder
@Entity
public class AffiliationDetails {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(
            name = "affiliation_id_sequence",
            sequenceName = "affiliation_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "affiliation_id_sequence"
    )
    private Long                            id;
    private String                          affiliationNumber;
    @Enumerated(EnumType.STRING)
    private AffiliationCategory             affiliationCategory;
}
